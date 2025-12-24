package io.github.honhimw.scn.config;

import io.github.honhimw.nacos.api.NacosFactory;
import io.github.honhimw.scn.config.health.NacosConfigEndpoint;
import io.github.honhimw.scn.config.health.NacosConfigHealthIndicator;
import io.github.honhimw.scn.config.refresh.NacosConfigRefreshEventListener;
import io.github.honhimw.scn.config.refresh.NacosContextRefresher;
import io.github.honhimw.scn.config.refresh.NacosRefreshHistory;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.health.autoconfigure.contributor.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// @author honhimW
/// @since 2025-12-22
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(NacosFactory.class)
@ConditionalOnNacosConfigEnabled
@EnableConfigurationProperties(NacosConfigProperties.class)
public class NacosConfigAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	NacosConfigManager nacosConfigManager(NacosConfigProperties properties) {
		return NacosConfigManager.getInstance(properties);
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(value = "spring.cloud.nacos.config.refresh-enabled", matchIfMissing = true)
	protected static class NacosRefreshConfiguration {

		@Bean
		public NacosRefreshHistory nacosRefreshHistory() {
			return new NacosRefreshHistory();
		}

		@Bean
		public NacosContextRefresher nacosContextRefresher(NacosConfigManager nacosConfigManager,
														   NacosRefreshHistory nacosRefreshHistory) {
			// Consider that it is not necessary to be compatible with the previous
			// configuration
			// and use the new configuration if necessary.
			return new NacosContextRefresher(nacosConfigManager, nacosRefreshHistory);
		}

		@Bean
		public NacosConfigRefreshEventListener nacosConfigRefreshEventListener() {
			return new NacosConfigRefreshEventListener();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({Endpoint.class, Health.class})
	protected static class NacosHealthConfig {

		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnAvailableEndpoint
		public NacosConfigEndpoint consulEndpoint(NacosConfigProperties properties,
												  ObjectProvider<@NonNull NacosRefreshHistory> refreshHistory) {
			return new NacosConfigEndpoint(properties, refreshHistory.stream().findAny().orElse(new NacosRefreshHistory()));
		}

		@Bean
		@ConditionalOnMissingBean
		@ConditionalOnProperty(name = "spring.nacos.config.health-indicator.enabled", havingValue = "true")
		@ConditionalOnEnabledHealthIndicator("nacos-config")
		public NacosConfigHealthIndicator consulHealthIndicator(NacosConfigManager nacosConfigManager) {
			return new NacosConfigHealthIndicator(nacosConfigManager.getConfigService());
		}

	}

}
