package io.github.honhimw.scn.discovery;

import io.github.honhimw.nacos.api.NacosFactory;
import io.github.honhimw.nacos.api.naming.NamingService;
import io.github.honhimw.nacos.api.naming.pojo.Instance;
import io.github.honhimw.scn.it.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.Map;

/// @author honhimW
/// @since 2025-12-25
public class NacosDiscoveryIT extends AbstractIntegrationTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		appBuilder.web(WebApplicationType.SERVLET);
		withProperties(Map.of(
			"spring.application.name", "nacos-starter-test",
			"spring.cloud.nacos.discovery.server-addr", nacos.serverAddr(),
			"spring.cloud.nacos.discovery.namespace", "public",
			"spring.cloud.config.discovery.enabled", "true"
		));
	}

	@Override
	protected void run(ConfigurableApplicationContext context) throws Exception {
		NamingService namingService = NacosFactory.createNamingService(nacos.serverAddr());
		List<Instance> allInstances = namingService.getAllInstances("nacos-starter-test");
		Assertions.assertEquals(1, allInstances.size());
	}
}
