/*
 * Copyright 2013-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.honhimw.scn.config.configdata;

import io.github.honhimw.scn.config.NacosConfigManager;
import io.github.honhimw.scn.config.NacosConfigProperties;
import io.github.honhimw.scn.config.NacosPropertiesPrefixer;
import org.apache.commons.logging.Log;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.bootstrap.BootstrapRegistry;
import org.springframework.boot.bootstrap.ConfigurableBootstrapContext;
import org.springframework.boot.context.config.*;
import org.springframework.boot.context.properties.bind.BindHandler;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static io.github.honhimw.scn.config.configdata.NacosConfigDataResource.NacosItemConfig;

/**
 * Implementation of {@link ConfigDataLocationResolver}, load Nacos
 * {@link ConfigDataResource}.
 *
 * @author freeman
 * @since 2021.0.1.0
 */
public class NacosConfigDataLocationResolver
	implements ConfigDataLocationResolver<@NonNull NacosConfigDataResource>, Ordered {
	/**
	 * Prefix for Config Server imports.
	 */
	public static final String PREFIX = "nacos:";
	private static final String GROUP = "group";

	// support params
	private static final String REFRESH_ENABLED = "refreshEnabled";
	private static final String PREFERENCE = "preference";
	private final Log log;

	public NacosConfigDataLocationResolver(DeferredLogFactory logFactory) {
		this.log = logFactory.getLog(getClass());
	}

	@Override
	public int getOrder() {
		return -1;
	}

	protected NacosConfigProperties loadProperties(
		ConfigDataLocationResolverContext context) {
		Binder binder = context.getBinder();
		BindHandler bindHandler = getBindHandler(context);

		NacosConfigProperties nacosConfigProperties;
		if (context.getBootstrapContext().isRegistered(NacosConfigProperties.class)) {
			nacosConfigProperties = context.getBootstrapContext()
				.get(NacosConfigProperties.class);
		} else {
			String nacosPrefix = NacosPropertiesPrefixer.PREFIX;

			String nacosConfigPrefix = nacosPrefix + ".config";

			nacosConfigProperties = binder
				.bind(nacosPrefix, Bindable.of(NacosConfigProperties.class),
					bindHandler)
				.map(properties -> binder
					.bind(nacosConfigPrefix,
						Bindable.ofInstance(properties), bindHandler)
					.orElse(properties))
				.orElseGet(() -> binder
					.bind(nacosConfigPrefix,
						Bindable.of(NacosConfigProperties.class), bindHandler)
					.orElseGet(NacosConfigProperties::new));
		}

		return nacosConfigProperties;
	}

	private BindHandler getBindHandler(ConfigDataLocationResolverContext context) {
		return context.getBootstrapContext().getOrElse(BindHandler.class, null);
	}

	protected Log getLog() {
		return this.log;
	}

	@Override
	public boolean isResolvable(@NonNull ConfigDataLocationResolverContext context,
								ConfigDataLocation location) {
		if (!location.hasPrefix(getPrefix())) {
			return false;
		}
		return context.getBinder()
			.bind(NacosPropertiesPrefixer.PREFIX + ".config.enabled", Boolean.class)
			.orElse(true);
	}

	protected String getPrefix() {
		return PREFIX;
	}

	@NonNull
	@Override
	public List<NacosConfigDataResource> resolve(
		@NonNull ConfigDataLocationResolverContext context, @NonNull ConfigDataLocation location)
		throws ConfigDataLocationNotFoundException,
		ConfigDataResourceNotFoundException {
		return Collections.emptyList();
	}

	@NonNull
	@Override
	public List<NacosConfigDataResource> resolveProfileSpecific(
		@NonNull ConfigDataLocationResolverContext resolverContext,
		@NonNull ConfigDataLocation location, @NonNull Profiles profiles)
		throws ConfigDataLocationNotFoundException {
		NacosConfigProperties properties = loadProperties(resolverContext);

		ConfigurableBootstrapContext bootstrapContext = resolverContext
			.getBootstrapContext();

		bootstrapContext.registerIfAbsent(NacosConfigProperties.class,
			BootstrapRegistry.InstanceSupplier.of(properties));

		registerConfigManager(properties, bootstrapContext, resolverContext);

		return loadConfigDataResources(location, profiles, properties);
	}

	private List<NacosConfigDataResource> loadConfigDataResources(
		ConfigDataLocation location, Profiles profiles,
		NacosConfigProperties properties) {
		List<NacosConfigDataResource> result = new ArrayList<>();
		URI uri = getUri(location, properties);

		if (!StringUtils.hasText(dataIdFor(uri))) {
			throw new IllegalArgumentException("dataId must be specified");
		}

		NacosConfigDataResource resource = new NacosConfigDataResource(properties,
			location.isOptional(), profiles, log,
			new NacosItemConfig().setGroup(groupFor(uri, properties))
				.setDataId(dataIdFor(uri)).setSuffix(suffixFor(uri, properties))
				.setRefreshEnabled(refreshEnabledFor(uri, properties))
				.setPreference(preferenceFor(uri)));
		result.add(resource);

		return result;
	}

	private String preferenceFor(URI uri) {
		return getQueryMap(uri).get(PREFERENCE);
	}

	private void registerConfigManager(NacosConfigProperties properties,
									   ConfigurableBootstrapContext bootstrapContext,
									   ConfigDataLocationResolverContext resolverContext) {
		List<?> springConfigImportProperties = resolverContext.getBinder()
			.bind("spring.config.import", List.class).get();
		if (!springConfigImportProperties.isEmpty() && !bootstrapContext.isRegistered(NacosConfigManager.class)) {
			NacosConfigManager configManager = NacosConfigManager.getInstance(properties);
			bootstrapContext.register(NacosConfigManager.class, BootstrapRegistry.InstanceSupplier.of(configManager));
//			bootstrapContext.addCloseListener(event -> {
//				try {
//					configManager.destroy();
//				} catch (Exception e) {
//					log.warn("Unable to destroy bootstrap NacosConfigManager.", e);
//				}
//			});
		}
	}

	private URI getUri(ConfigDataLocation location, NacosConfigProperties properties) {
		String path = location.getNonPrefixedValue(getPrefix());
		if (!StringUtils.hasText(path)) {
			path = "/";
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		String uri = properties.getServerAddr() + path;
		return getUri(uri);
	}

	private URI getUri(String uris) {
		if (!uris.startsWith("http://") && !uris.startsWith("https://")) {
			uris = "http://" + uris;
		}
		URI uri;
		try {
			uri = new URI(uris);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("illegal URI: " + uris);
		}
		return uri;
	}

	private String groupFor(URI uri, NacosConfigProperties properties) {
		Map<String, String> queryMap = getQueryMap(uri);
		return queryMap.containsKey(GROUP) ? queryMap.get(GROUP) : properties.getGroup();
	}

	private Map<String, String> getQueryMap(URI uri) {
		String query = uri.getQuery();
		if (!StringUtils.hasText(query)) {
			return Collections.emptyMap();
		}
		Map<String, String> result = new HashMap<>(4);
		for (String entry : query.split("&")) {
			String[] kv = entry.split("=");
			if (kv.length == 2) {
				result.put(kv[0], kv[1]);
			}
		}
		return result;
	}

	private String suffixFor(URI uri, NacosConfigProperties properties) {
		String dataId = dataIdFor(uri);
		if (dataId != null && dataId.contains(".")) {
			return dataId.substring(dataId.lastIndexOf('.') + 1);
		}
		return properties.getFileExtension();
	}

	private boolean refreshEnabledFor(URI uri, NacosConfigProperties properties) {
		Map<String, String> queryMap = getQueryMap(uri);
		return queryMap.containsKey(REFRESH_ENABLED)
			? Boolean.parseBoolean(queryMap.get(REFRESH_ENABLED))
			: properties.isRefreshEnabled();
	}

	private String dataIdFor(URI uri) {
		String path = uri.getPath();
		// notice '/'
		if (path == null || path.length() <= 1) {
			return "";
		}
		String[] parts = path.substring(1).split("/");
		if (parts.length != 1) {
			throw new IllegalArgumentException("illegal dataId");
		}
		return parts[0];
	}

}
