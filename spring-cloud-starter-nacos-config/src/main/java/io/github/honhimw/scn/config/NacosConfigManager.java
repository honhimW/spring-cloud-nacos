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

package io.github.honhimw.scn.config;

import io.github.honhimw.nacos.api.NacosFactory;
import io.github.honhimw.nacos.api.config.ConfigService;
import io.github.honhimw.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @author zkzlx
 */
public class NacosConfigManager {

	private static final Logger log = LoggerFactory.getLogger(NacosConfigManager.class);

	private ConfigService service;

	private final NacosConfigProperties nacosConfigProperties;

	public NacosConfigManager(NacosConfigProperties nacosConfigProperties) {
		this.nacosConfigProperties = nacosConfigProperties;
	}

	public static NacosConfigManager getInstance(NacosConfigProperties properties) {
		NacosConfigManager manager = new NacosConfigManager(properties);
		manager.createConfigService(properties);
		return manager;
	}

	/**
	 * Compatible with old design,It will be perfected in the future.
	 */
	private void createConfigService(
		NacosConfigProperties nacosConfigProperties) {
		try {
			if (Objects.isNull(service)) {
				service = NacosFactory.createConfigService(
					nacosConfigProperties.assembleConfigServiceProperties());
			}
		} catch (NacosException e) {
			log.error(e.getMessage());
			throw new IllegalStateException(nacosConfigProperties.getServerAddr(), e);
		}
	}

	public ConfigService getConfigService() {
		if (Objects.isNull(service)) {
			createConfigService(this.nacosConfigProperties);
		}
		return service;
	}

	public NacosConfigProperties getNacosConfigProperties() {
		return nacosConfigProperties;
	}

}
