package io.github.honhimw.scn;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.concurrent.Executor;

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
		Listener listener = new Listener() {
			@Override
			public Executor getExecutor() {
				return null;
			}

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
