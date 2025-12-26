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

package io.github.honhimw.scn.discovery.reactive;

import io.github.honhimw.scn.discovery.ConditionalOnNacosDiscoveryEnabled;
import io.github.honhimw.scn.discovery.NacosDiscoveryAutoConfiguration;
import io.github.honhimw.scn.discovery.NacosServiceDiscovery;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.ReactiveCommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.composite.reactive.ReactiveCompositeDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:echooy.mxq@gmail.com">echooymxq</a>
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnReactiveDiscoveryEnabled
@ConditionalOnNacosDiscoveryEnabled
@AutoConfiguration(
	after = {NacosDiscoveryAutoConfiguration.class, ReactiveCompositeDiscoveryClientAutoConfiguration.class},
	before = ReactiveCommonsClientAutoConfiguration.class
)
public class NacosReactiveDiscoveryClientAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public NacosReactiveDiscoveryClient nacosReactiveDiscoveryClient(
		NacosServiceDiscovery nacosServiceDiscovery) {
		return new NacosReactiveDiscoveryClient(nacosServiceDiscovery);
	}

}
