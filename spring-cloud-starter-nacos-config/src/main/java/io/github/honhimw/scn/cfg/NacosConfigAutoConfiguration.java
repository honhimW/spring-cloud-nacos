package io.github.honhimw.scn.cfg;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.Properties;

/// @author honhimW
/// @since 2025-12-22
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "spring.cloud.nacos.config.enabled", matchIfMissing = true)
@EnableConfigurationProperties(NacosConfigProperties.class)
public class NacosConfigAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	ConfigService nacosConfigService(NacosConfigProperties properties) {
		try {
			Properties props = new Properties();

			props.put(PropertyKeyConst.SERVER_ADDR, Objects.toString(properties.getServerAddr(), ""));
			props.put(PropertyKeyConst.USERNAME, Objects.toString(properties.getUsername(), ""));
			props.put(PropertyKeyConst.PASSWORD, Objects.toString(properties.getPassword(), ""));
			props.put(PropertyKeyConst.ENCODE, Objects.toString(properties.getEncode(), ""));
			props.put(PropertyKeyConst.NAMESPACE, Objects.toString(properties.getNamespace(), ""));
			props.put(PropertyKeyConst.ACCESS_KEY, Objects.toString(properties.getAccessKey(), ""));
			props.put(PropertyKeyConst.SECRET_KEY, Objects.toString(properties.getSecretKey(), ""));
			props.put(PropertyKeyConst.RAM_ROLE_NAME, Objects.toString(properties.getRamRoleName(), ""));
			props.put(PropertyKeyConst.ENDPOINT_CLUSTER_NAME, Objects.toString(properties.getClusterName(), ""));
			props.put(PropertyKeyConst.MAX_RETRY, Objects.toString(properties.getMaxRetry(), ""));
			props.put(PropertyKeyConst.CONFIG_LONG_POLL_TIMEOUT, Objects.toString(properties.getConfigLongPollTimeout(), ""));
			props.put(PropertyKeyConst.CONFIG_RETRY_TIME, Objects.toString(properties.getConfigRetryTime(), ""));
			props.put(PropertyKeyConst.ENABLE_REMOTE_SYNC_CONFIG, Objects.toString(properties.isEnableRemoteSyncConfig(), ""));
			String endpoint = Objects.toString(properties.getEndpoint(), "");
			String[] endpointPart = endpoint.split(":", 2);
			if (endpointPart.length == 2) {
				props.put(PropertyKeyConst.ENDPOINT, endpointPart[0]);
				props.put(PropertyKeyConst.ENDPOINT_PORT, endpointPart[1]);
			} else {
				props.put(PropertyKeyConst.ENDPOINT, endpoint);
			}

			return NacosFactory.createConfigService(props);
		} catch (NacosException e) {
			throw new IllegalStateException("cannot create config-server: %s".formatted(properties.getServerAddr()), e);
		}
	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnProperty(value = "spring.cloud.nacos.config.refresh-enabled", matchIfMissing = true)
	protected static class NacosRefreshConfiguration {

		@Bean
		@ConditionalOnBean(ConfigService.class)
		ConfigWatch configWatch(NacosConfigProperties configProperties, ConfigService configService) {
			return new ConfigWatch(configProperties, configService);
		}

	}

}
