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

import io.github.honhimw.scn.config.client.NacosPropertySourceLocator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

/**
 * @author xiaojing
 * @author freeman
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnNacosConfigEnabled
public class NacosConfigBootstrapConfiguration {

	@Configuration(proxyBeanMethods = false)
	@EnableConfigurationProperties
	@Import(NacosConfigAutoConfiguration.class)
	protected static class NacosPropertySourceConfiguration {

		@Bean
		@ConditionalOnMissingBean
		public NacosConfigProperties nacosConfigProperties(Environment env) {
			NacosConfigProperties properties = new NacosConfigProperties();
			if (ObjectUtils.isEmpty(properties.getName())) {
				properties.setName(env.getProperty("spring.application.name", "application"));
			}
			return properties;
		}

		@Bean
		@ConditionalOnMissingBean
		NacosConfigManager nacosConfigManager(NacosConfigProperties properties) {
			return NacosConfigManager.getInstance(properties);
		}

		@Bean
		public NacosPropertySourceLocator nacosPropertySourceLocator(NacosConfigManager nacosConfigManager) {
			return new NacosPropertySourceLocator(nacosConfigManager);
		}

	}

}
