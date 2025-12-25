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

package io.github.honhimw.scn.discovery.registry;

import io.github.honhimw.scn.discovery.ConditionalOnNacosDiscoveryEnabled;
import io.github.honhimw.scn.discovery.NacosDiscoveryAutoConfiguration;
import io.github.honhimw.scn.discovery.NacosDiscoveryProperties;
import io.github.honhimw.scn.discovery.NacosServiceManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationConfiguration;
import org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.util.List;

/**
 * @author xiaojing
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
@ConditionalOnNacosDiscoveryEnabled
@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled",
		matchIfMissing = true)
@AutoConfigureAfter({ AutoServiceRegistrationConfiguration.class,
		AutoServiceRegistrationAutoConfiguration.class,
		NacosDiscoveryAutoConfiguration.class })
public class NacosServiceRegistryAutoConfiguration {

	@Bean
	public NacosServiceRegistry nacosServiceRegistry(
			NacosServiceManager nacosServiceManager,
			NacosDiscoveryProperties nacosDiscoveryProperties) {
		return new NacosServiceRegistry(nacosServiceManager, nacosDiscoveryProperties);
	}

	@Bean
	@ConditionalOnBean(AutoServiceRegistrationProperties.class)
	public NacosRegistration nacosRegistration(
			ObjectProvider<List<NacosRegistrationCustomizer>> registrationCustomizers,
			NacosDiscoveryProperties nacosDiscoveryProperties,
			ApplicationContext context) {
		return new NacosRegistration(registrationCustomizers.getIfAvailable(),
				nacosDiscoveryProperties, context);
	}

	@Bean
	@ConditionalOnBean(AutoServiceRegistrationProperties.class)
	public NacosAutoServiceRegistration nacosAutoServiceRegistration(
			ApplicationContext context,
			NacosServiceRegistry registry,
			AutoServiceRegistrationProperties autoServiceRegistrationProperties,
			NacosRegistration registration) {
		return new NacosAutoServiceRegistration(context, registry,
				autoServiceRegistrationProperties, registration);
	}

	@Bean
	@DependsOn("nacosAutoServiceRegistration")
	public NacosDiscoveryInfoChangedListener nacosDiscoveryInfoChangedListener(NacosAutoServiceRegistration nacosAutoServiceRegistration) {
		return new NacosDiscoveryInfoChangedListener(nacosAutoServiceRegistration);
	}

	@Bean
	@ConditionalOnBean(NacosAutoServiceRegistration.class)
	public NacosGracefulShutdownDelegate nacosGracefulShutdownDelegate(
			NacosAutoServiceRegistration nacosAutoServiceRegistration,
			NacosDiscoveryProperties nacosDiscoveryProperties) {
		return new NacosGracefulShutdownDelegate(nacosAutoServiceRegistration, nacosDiscoveryProperties);
	}
}
