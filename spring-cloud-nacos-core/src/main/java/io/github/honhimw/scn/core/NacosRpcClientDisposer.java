package io.github.honhimw.scn.core;

import io.github.honhimw.nacos.common.remote.client.RpcClientFactory;
import org.springframework.beans.factory.DisposableBean;

/// Gracefully shutdown all rpc clients.
///
/// @author honhimW
/// @since 2025-12-25
public class NacosRpcClientDisposer implements DisposableBean {

	@Override
	public void destroy() throws Exception {
		RpcClientFactory.destroyAllClients();
	}
}
