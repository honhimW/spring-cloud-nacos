package io.github.honhimw.scn.core;

import io.github.honhimw.nacos.api.NacosFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// @author honhimW
/// @since 2025-12-25
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(NacosFactory.class)
public class NacosAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public NacosRpcClientDisposer nacosRpcClientDisposer() {
		return new NacosRpcClientDisposer();
	}

}
