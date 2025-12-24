/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.honhimw.nacos.client.lock.remote.grpc;

import io.github.honhimw.nacos.api.ability.constant.AbilityKey;
import io.github.honhimw.nacos.api.ability.constant.AbilityStatus;
import io.github.honhimw.nacos.api.common.Constants;
import io.github.honhimw.nacos.api.exception.NacosException;
import io.github.honhimw.nacos.api.exception.runtime.NacosRuntimeException;
import io.github.honhimw.nacos.api.lock.constant.PropertyConstants;
import io.github.honhimw.nacos.api.lock.model.LockInstance;
import io.github.honhimw.nacos.api.lock.remote.AbstractLockRequest;
import io.github.honhimw.nacos.api.lock.remote.LockOperationEnum;
import io.github.honhimw.nacos.api.lock.remote.request.LockOperationRequest;
import io.github.honhimw.nacos.api.lock.remote.response.LockOperationResponse;
import io.github.honhimw.nacos.api.remote.RemoteConstants;
import io.github.honhimw.nacos.api.remote.response.Response;
import io.github.honhimw.nacos.api.remote.response.ResponseCode;
import io.github.honhimw.nacos.client.env.NacosClientProperties;
import io.github.honhimw.nacos.client.lock.remote.AbstractLockClient;
import io.github.honhimw.nacos.client.security.SecurityProxy;
import io.github.honhimw.nacos.client.utils.AppNameUtils;
import io.github.honhimw.nacos.common.remote.ConnectionType;
import io.github.honhimw.nacos.common.remote.client.RpcClient;
import io.github.honhimw.nacos.common.remote.client.RpcClientFactory;
import io.github.honhimw.nacos.common.remote.client.RpcClientTlsConfigFactory;
import io.github.honhimw.nacos.common.remote.client.ServerListFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * lock grpc client.
 *
 * @author 985492783@qq.com
 * @description LockGrpcClient
 * @date 2023/6/28 17:35
 */
public class LockGrpcClient extends AbstractLockClient {
    
    private final String uuid;
    
    private final Long requestTimeout;
    
    private final RpcClient rpcClient;
    
    public LockGrpcClient(NacosClientProperties properties, ServerListFactory serverListFactory,
            SecurityProxy securityProxy) throws NacosException {
        super(securityProxy);
        this.uuid = UUID.randomUUID().toString();
        this.requestTimeout = Long.parseLong(properties.getProperty(PropertyConstants.LOCK_REQUEST_TIMEOUT, "-1"));
        Map<String, String> labels = new HashMap<>();
        labels.put(RemoteConstants.LABEL_SOURCE, RemoteConstants.LABEL_SOURCE_SDK);
        labels.put(RemoteConstants.LABEL_MODULE, RemoteConstants.LABEL_MODULE_LOCK);
        labels.put(Constants.APPNAME, AppNameUtils.getAppName());
        this.rpcClient = RpcClientFactory.createClient(uuid, ConnectionType.GRPC, labels,
                RpcClientTlsConfigFactory.getInstance().createSdkConfig(properties.asProperties()));
        start(serverListFactory);
    }
    
    private void start(ServerListFactory serverListFactory) throws NacosException {
        rpcClient.serverListFactory(serverListFactory);
        rpcClient.start();
    }
    
    @Override
    public Boolean lock(LockInstance instance) throws NacosException {
        if (!isAbilitySupportedByServer()) {
            throw new NacosRuntimeException(NacosException.SERVER_NOT_IMPLEMENTED,
                    "Request Nacos server version is too low, not support lock feature.");
        }
        LockOperationRequest request = new LockOperationRequest();
        request.setLockInstance(instance);
        request.setLockOperationEnum(LockOperationEnum.ACQUIRE);
        LockOperationResponse acquireLockResponse = requestToServer(request, LockOperationResponse.class);
        return (Boolean) acquireLockResponse.getResult();
    }
    
    @Override
    public Boolean unLock(LockInstance instance) throws NacosException {
        if (!isAbilitySupportedByServer()) {
            throw new NacosRuntimeException(NacosException.SERVER_NOT_IMPLEMENTED,
                    "Request Nacos server version is too low, not support lock feature.");
        }
        LockOperationRequest request = new LockOperationRequest();
        request.setLockInstance(instance);
        request.setLockOperationEnum(LockOperationEnum.RELEASE);
        LockOperationResponse acquireLockResponse = requestToServer(request, LockOperationResponse.class);
        return (Boolean) acquireLockResponse.getResult();
    }
    
    @Override
    public void shutdown() throws NacosException {
        rpcClient.shutdown();
    }
    
    private <T extends Response> T requestToServer(AbstractLockRequest request, Class<T> responseClass)
            throws NacosException {
        try {
            request.putAllHeader(getSecurityHeaders());
            Response response =
                    requestTimeout < 0 ? rpcClient.request(request) : rpcClient.request(request, requestTimeout);
            if (ResponseCode.SUCCESS.getCode() != response.getResultCode()) {
                throw new NacosException(response.getErrorCode(), response.getMessage());
            }
            if (responseClass.isAssignableFrom(response.getClass())) {
                return (T) response;
            }
        } catch (NacosException e) {
            throw e;
        } catch (Exception e) {
            throw new NacosException(NacosException.SERVER_ERROR, "Request nacos server failed: ", e);
        }
        throw new NacosException(NacosException.SERVER_ERROR, "Server return invalid response");
    }
    
    private boolean isAbilitySupportedByServer() {
        return rpcClient.getConnectionAbility(AbilityKey.SERVER_DISTRIBUTED_LOCK) == AbilityStatus.SUPPORTED;
    }
}
