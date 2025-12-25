package io.github.honhimw.scn;

import io.github.honhimw.nacos.api.NacosFactory;
import io.github.honhimw.nacos.api.PropertyKeyConst;
import io.github.honhimw.nacos.api.config.ConfigService;
import io.github.honhimw.nacos.api.config.listener.AbstractListener;
import io.github.honhimw.nacos.api.config.listener.Listener;
import io.github.honhimw.scn.it.TestUtils;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.util.Properties;

/// @author honhimW
/// @since 2025-12-22
public class SDKTests {

	@Test
	void listener() throws Exception {
		try (GenericContainer<?> container = TestUtils.testContainers()) {
			container.start();
			String host = container.getHost();
			Integer mappedPort = container.getMappedPort(8848);
			String serverAddr = "%s:%d".formatted(host, mappedPort);
			Properties properties = new Properties();
			properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
			properties.put(PropertyKeyConst.USERNAME, "admin");
			properties.put(PropertyKeyConst.PASSWORD, "admin");
			properties.put(PropertyKeyConst.NAMESPACE, "public");
			ConfigService configService = NacosFactory.createConfigService(properties);
			Listener listener = new AbstractListener() {
				@Override
				public void receiveConfigInfo(String configInfo) {
					System.out.println(configInfo);
				}
			};
			String config = configService.getConfigAndSignListener("nacos-starter-test", "DEFAULT_GROUP", 1000, listener);
			System.out.println(config);
		}

	}

}
