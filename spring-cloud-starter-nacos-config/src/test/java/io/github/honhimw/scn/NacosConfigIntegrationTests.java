package io.github.honhimw.scn;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/// @author honhimW
/// @since 2025-12-22
public class NacosConfigIntegrationTests {

	private static ApplicationContextRunner contextRunner;

	@BeforeAll
	public static void setup() {
		contextRunner = new ApplicationContextRunner().withPropertyValues(
			"spring.application.name=integrationTests",
			"spring.config.import=nacos:integrationTests",
			"spring.cloud.nacos.config.server-addr=10.37.1.132:8848"
		);
	}

	@Test
	void test() throws Exception {
	}

}
