package io.github.honhimw.scn.core;

import io.github.honhimw.nacos.common.remote.client.RpcClient;
import io.github.honhimw.nacos.common.remote.client.RpcClientFactory;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.Set;

/// Gracefully shutdown all rpc clients.
///
/// @author honhimW
/// @since 2025-12-25
public class NacosRpcClientDisposer implements DisposableBean {

	@Override
	public void destroy() throws Exception {
		Set<Map.Entry<String, RpcClient>> allClientEntries = RpcClientFactory.getAllClientEntries();
		for (Map.Entry<String, RpcClient> entry : allClientEntries) {
			RpcClientFactory.destroyClient(entry.getKey());
		}
	}
}
