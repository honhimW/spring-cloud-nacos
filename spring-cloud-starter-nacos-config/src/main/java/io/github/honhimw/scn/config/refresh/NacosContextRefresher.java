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

package io.github.honhimw.scn.config.refresh;

import io.github.honhimw.nacos.api.config.listener.AbstractSharedListener;
import io.github.honhimw.nacos.api.config.listener.Listener;
import io.github.honhimw.nacos.api.exception.NacosException;
import io.github.honhimw.scn.config.NacosConfigManager;
import io.github.honhimw.scn.config.NacosConfigProperties;
import io.github.honhimw.scn.config.NacosPropertySourceRepository;
import io.github.honhimw.scn.config.client.NacosPropertySource;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * On application start up, NacosContextRefresher add nacos listeners to all application
 * level dataIds, when there is a change in the data, listeners will refresh
 * configurations.
 *
 * @author juven.xuxb
 * @author pbting
 * @author freeman
 */
public class NacosContextRefresher
	implements ApplicationListener<@NonNull ApplicationReadyEvent>, ApplicationEventPublisherAware {

	private final static Logger log = LoggerFactory
		.getLogger(NacosContextRefresher.class);

	private static final AtomicLong REFRESH_COUNT = new AtomicLong(0);
	private final boolean isRefreshEnabled;
	private final NacosRefreshHistory nacosRefreshHistory;
	private final NacosConfigProperties nacosConfigProperties;
	private final NacosConfigManager nacosConfigManager;

	private ApplicationEventPublisher publisher;

	private final AtomicBoolean ready = new AtomicBoolean(false);

	private final Map<String, Listener> listenerMap = new ConcurrentHashMap<>(16);

	public NacosContextRefresher(NacosConfigManager nacosConfigManager,
								 NacosRefreshHistory refreshHistory) {
		this.nacosConfigManager = nacosConfigManager;
		this.nacosConfigProperties = nacosConfigManager.getNacosConfigProperties();
		this.nacosRefreshHistory = refreshHistory;
		this.isRefreshEnabled = this.nacosConfigProperties.isRefreshEnabled();
	}

	public static long getRefreshCount() {
		return REFRESH_COUNT.get();
	}

	public static void refreshCountIncrement() {
		REFRESH_COUNT.incrementAndGet();
	}

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		// many Spring context
		if (this.ready.compareAndSet(false, true)) {
			this.registerNacosListenersForApplications();
		}
	}

	@Override
	public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}

	/**
	 * register Nacos Listeners.
	 */
	private void registerNacosListenersForApplications() {
		if (isRefreshEnabled()) {
			for (NacosPropertySource propertySource : NacosPropertySourceRepository
				.getAll()) {
				if (!propertySource.isRefreshable()) {
					continue;
				}
				String dataId = propertySource.getDataId();
				registerNacosListener(propertySource.getGroup(), dataId);
			}
		}
	}

	private void registerNacosListener(final String groupKey, final String dataKey) {
		String key = NacosPropertySourceRepository.getMapKey(dataKey, groupKey);
		Listener listener = listenerMap.computeIfAbsent(key,
			lst -> new AbstractSharedListener() {
				@Override
				public void innerReceive(String dataId, String group,
										 String configInfo) {

					log.info("[Nacos Config] Receive Nacos config change: dataId={}, group={}", dataKey,
						groupKey);
					refreshCountIncrement();
					nacosRefreshHistory.addRefreshRecord(dataId, group, configInfo);
					NacosSnapshotConfigManager.putConfigSnapshot(dataId, group, configInfo);
					NacosConfigRefreshEvent event = new NacosConfigRefreshEvent(this, null, "Refresh Nacos config");
					event.setDataId(dataId);
					event.setGroup(group);
					publisher.publishEvent(event);
					if (log.isDebugEnabled()) {
						log.debug("Publish Nacos config Refresh Event group={},dataId={},configInfo={}",
							group, dataId, configInfo);
					}
				}
			});
		try {
			nacosConfigManager.getConfigService().addListener(dataKey, groupKey, listener);
			log.info("[Nacos Config] Listening config: dataId={}, group={}", dataKey, groupKey);
		} catch (NacosException e) {
			log.warn("register fail for nacos listener ,dataId=[{}],group=[{}]", dataKey,
				groupKey, e);
		}
	}

	public NacosConfigProperties getNacosConfigProperties() {
		return nacosConfigProperties;
	}

	public boolean isRefreshEnabled() {
		if (null == nacosConfigProperties) {
			return isRefreshEnabled;
		}
		// Compatible with older configurations
		if (nacosConfigProperties.isRefreshEnabled() && !isRefreshEnabled) {
			return false;
		}
		return isRefreshEnabled;
	}

}
