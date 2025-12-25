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

import io.github.honhimw.scn.discovery.loadbalancer.ConditionalOnLoadBalancerNacos;
import io.github.honhimw.scn.discovery.loadbalancer.DefaultLoadBalancerAlgorithm;
import io.github.honhimw.scn.discovery.loadbalancer.LoadBalancerAlgorithm;
import io.github.honhimw.scn.discovery.loadbalancer.NacosLoadBalancerClientConfiguration;
import io.github.honhimw.scn.discovery.util.InetIPv6Utils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:echooy.mxq@gmail.com">echooymxq</a>
 **/
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnNacosDiscoveryEnabled
@EnableConfigurationProperties(NacosDiscoveryProperties.class)
public class NacosDiscoveryAutoConfiguration {

	@Bean
	public NacosServiceManager nacosServiceManager(NacosDiscoveryProperties nacosDiscoveryProperties) {
		return new NacosServiceManager(nacosDiscoveryProperties);
	}

	@Bean
	@ConditionalOnMissingBean
	public InetIPv6Utils inetIPv6Utils(InetUtilsProperties properties) {
		return new InetIPv6Utils(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	public NacosServiceDiscovery nacosServiceDiscovery(
			NacosDiscoveryProperties discoveryProperties,
			NacosServiceManager nacosServiceManager) {
		return new NacosServiceDiscovery(discoveryProperties, nacosServiceManager);
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnLoadBalancerNacos
	@ConditionalOnNacosDiscoveryEnabled
	@LoadBalancerClients(defaultConfiguration = NacosLoadBalancerClientConfiguration.class)
	protected static class NacosLoadBalancerConfiguration {

		@Bean
		public LoadBalancerAlgorithm defaultLoadBalancerAlgorithm() {
			return new DefaultLoadBalancerAlgorithm();
		}

	}

}
