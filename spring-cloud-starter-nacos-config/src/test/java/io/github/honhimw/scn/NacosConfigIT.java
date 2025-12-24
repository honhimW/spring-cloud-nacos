package io.github.honhimw.scn;

import io.github.honhimw.scn.config.NacosConfigManager;
import io.github.honhimw.scn.config.refresh.NacosContextRefresher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;

import java.util.Map;

/// @author honhimW
/// @since 2025-12-22
public class NacosConfigIT extends AbstractIntegrationTests {

	@Override
	protected void setUp() {
		withProperties(Map.of(
			"spring.application.name", "nacos-starter-test",
			"spring.config.import[0]", "optional:nacos:nacos-starter-test",
			"spring.config.import[1]", "optional:nacos:extend-config",
			"spring.cloud.nacos.config.server-addr", "10.37.1.132:8848",
			"spring.cloud.nacos.config.namespace", "dev"
		));
	}

	@Override
	protected void run(ConfigurableApplicationContext context) throws Exception {
		NacosConfigManager bean = context.getBean(NacosConfigManager.class);
		System.out.println(bean.getConfigService().getServerStatus());
		NacosContextRefresher refresher = context.getBean(NacosContextRefresher.class);
		System.out.println(refresher.isRefreshEnabled());
		MutablePropertySources propertySources = context.getEnvironment().getPropertySources();
		for (int i = 0; i < 60; i++) {
			String hello = context.getEnvironment().getProperty("hello");
			System.out.println(hello);
			Thread.sleep(4000);
		}
	}
}
