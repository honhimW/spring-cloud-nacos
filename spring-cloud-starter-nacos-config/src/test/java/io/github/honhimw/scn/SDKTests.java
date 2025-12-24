package io.github.honhimw.scn;

import io.github.honhimw.nacos.api.NacosFactory;
import io.github.honhimw.nacos.api.PropertyKeyConst;
import io.github.honhimw.nacos.api.config.ConfigService;
import io.github.honhimw.nacos.api.config.listener.AbstractListener;
import io.github.honhimw.nacos.api.config.listener.Listener;
import org.junit.jupiter.api.Test;

import java.util.Properties;

/// @author honhimW
/// @since 2025-12-22
public class SDKTests {

	@Test
	void listener() throws Exception {
		Properties properties = new Properties();
		properties.put(PropertyKeyConst.SERVER_ADDR, "10.37.1.132:8848");
		properties.put(PropertyKeyConst.USERNAME, "nacos");
		properties.put(PropertyKeyConst.PASSWORD, "nacos");
		properties.put(PropertyKeyConst.NAMESPACE, "dev");
		ConfigService configService = NacosFactory.createConfigService(properties);
		Listener listener = new AbstractListener() {
			@Override
			public void receiveConfigInfo(String configInfo) {
				System.out.println(configInfo);
			}
		};
		String config = configService.getConfigAndSignListener("nacos-starter-test", "DEFAULT_GROUP", 1000, listener);
		System.out.println(config);

		Thread.sleep(6000000);

	}

}
