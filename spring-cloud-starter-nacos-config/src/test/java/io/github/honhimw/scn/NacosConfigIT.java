package io.github.honhimw.scn;

import io.github.honhimw.nacos.common.remote.client.grpc.GrpcConstants;
import io.github.honhimw.scn.config.NacosConfigManager;
import io.github.honhimw.scn.config.refresh.NacosContextRefresher;
import io.github.honhimw.scn.it.AbstractIntegrationTest;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Assertions;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/// @author honhimW
/// @since 2025-12-22
public class NacosConfigIT extends AbstractIntegrationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		withProperties(Map.of(
			"spring.application.name", "nacos-starter-test",
			"spring.config.import[0]", "optional:nacos:nacos-starter-test",
			"spring.config.import[1]", "optional:nacos:extend-config",
			"spring.cloud.nacos.config.server-addr", nacos.serverAddr(),
			"spring.cloud.nacos.config.namespace", "public"
		));
	}

	@Override
	protected void tearDown() {
		if (nacos != null) {
			nacos.close();
		}
	}

	@Override
	protected void run(ConfigurableApplicationContext context) throws Exception {
		NacosConfigManager manager = context.getBean(NacosConfigManager.class);
		Assertions.assertEquals("UP", manager.getConfigService().getServerStatus());
		NacosContextRefresher refresher = context.getBean(NacosContextRefresher.class);
		Assertions.assertTrue(refresher.isRefreshEnabled());

		@Language("yaml")
		String yaml = """
			foo: bar
			hello: world
			""";
		manager.getConfigService().publishConfig("nacos-starter-test", "DEFAULT_GROUP", yaml, "yaml");

		Thread.sleep(1000);
		Assertions.assertEquals("bar", context.getEnvironment().getProperty("foo"));
		Assertions.assertEquals("world", context.getEnvironment().getProperty("hello"));

		yaml = """
			foo: bar1
			# hello: world
			""";

		manager.getConfigService().publishConfig("nacos-starter-test", "DEFAULT_GROUP", yaml, "yaml");

		Thread.sleep(1000);
		Assertions.assertEquals("bar1", context.getEnvironment().getProperty("foo"));
		Assertions.assertFalse(context.getEnvironment().containsProperty("hello"));

	}
}
