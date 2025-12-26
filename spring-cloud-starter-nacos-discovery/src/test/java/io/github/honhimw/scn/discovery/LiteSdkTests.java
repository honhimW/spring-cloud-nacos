package io.github.honhimw.scn.discovery;

import io.github.honhimw.nacos.api.NacosFactory;
import io.github.honhimw.nacos.api.naming.NamingService;
import io.github.honhimw.nacos.api.naming.pojo.Instance;
import io.github.honhimw.nacos.common.remote.client.RpcClientFactory;
import io.github.honhimw.scn.it.AbstractNacosContainerTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/// @author honhimW
/// @since 2025-12-26
public class LiteSdkTests extends AbstractNacosContainerTest {

	@Test
	@SneakyThrows
	void namingService() {
		setUp();
		NamingService namingService = NacosFactory.createNamingService(nacos.serverAddr());
		Instance instance = new Instance();
		instance.setIp("127.0.0.1");
		instance.setPort(8080);
		instance.setClusterName("");
		instance.addMetadata("preserved.register.source", "SPRING_CLOUD");
		namingService.registerInstance("nacos-cloud-test", "DEFAULT_GROUP", instance);

		List<Instance> allInstances = namingService.getAllInstances("nacos-cloud-test");
		Assertions.assertEquals(1, allInstances.size());
		Instance instance1 = allInstances.get(0);
		Assertions.assertEquals("DEFAULT_GROUP@@nacos-cloud-test", instance1.getServiceName());
		Assertions.assertEquals("127.0.0.1", instance1.getIp());
		Assertions.assertEquals(8080, instance1.getPort());
		Assertions.assertEquals("SPRING_CLOUD", instance1.getMetadata().get("preserved.register.source"));

		namingService.deregisterInstance("nacos-cloud-test", nacos.getHost(), nacos.httpPort());

		RpcClientFactory.destroyAllClients();
		tearDown();
	}

}
