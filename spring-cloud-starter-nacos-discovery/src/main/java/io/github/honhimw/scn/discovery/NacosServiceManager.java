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

package io.github.honhimw.scn.discovery;

import io.github.honhimw.nacos.api.NacosFactory;
import io.github.honhimw.nacos.api.exception.NacosException;
import io.github.honhimw.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;

/**
 * @author yuhuangbin
 */
public class NacosServiceManager {

	private static final Logger log = LoggerFactory.getLogger(NacosServiceManager.class);

	private final NacosDiscoveryProperties nacosDiscoveryProperties;

	private volatile NamingService namingService;

	public NacosServiceManager(NacosDiscoveryProperties nacosDiscoveryProperties) {
		this.nacosDiscoveryProperties = nacosDiscoveryProperties;
	}

	public NamingService getNamingService() {
		if (Objects.isNull(this.namingService)) {
			buildNamingService(nacosDiscoveryProperties.getNacosProperties());
		}
		return namingService;
	}

	@Deprecated
	public NamingService getNamingService(Properties properties) {
		if (Objects.isNull(this.namingService)) {
			buildNamingService(properties);
		}
		return namingService;
	}

	private NamingService buildNamingService(Properties properties) {
		if (Objects.isNull(namingService)) {
			synchronized (NacosServiceManager.class) {
				if (Objects.isNull(namingService)) {
					namingService = createNewNamingService(properties);
				}
			}
		}
		return namingService;
	}

	private NamingService createNewNamingService(Properties properties) {
		try {
			return NacosFactory.createNamingService(properties);
		}
		catch (NacosException e) {
			throw new RuntimeException(e);
		}
	}

	public void nacosServiceShutDown() throws NacosException {
		if (Objects.nonNull(this.namingService)) {
			this.namingService.shutDown();
			this.namingService = null;
		}
	}

}
