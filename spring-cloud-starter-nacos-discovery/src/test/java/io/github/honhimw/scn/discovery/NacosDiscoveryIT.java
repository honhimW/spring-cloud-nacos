package io.github.honhimw.scn.discovery;

import io.github.honhimw.nacos.common.remote.client.grpc.GrpcConstants;
import io.github.honhimw.scn.it.AbstractIntegrationTests;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/// @author honhimW
/// @since 2025-12-25
public class NacosDiscoveryIT extends AbstractIntegrationTests {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		String host = container.getHost();
		Integer httpPort = container.getMappedPort(8848);
		Integer grpcPort = container.getMappedPort(9848);
		String serverAddr = "%s:%d".formatted(host, httpPort);
		int offset = grpcPort - httpPort;
		System.setProperty(GrpcConstants.NACOS_SERVER_GRPC_PORT_OFFSET_KEY, String.valueOf(offset));
		withProperties(Map.of(
			"spring.application.name", "nacos-starter-test",
			"spring.cloud.nacos.discovery.server-addr", serverAddr,
			"spring.cloud.nacos.discovery.namespace", "public"
		));
	}

	@Override
	protected void run(ConfigurableApplicationContext context) throws Exception {

	}
}
