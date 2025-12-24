/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

package io.github.honhimw.nacos.client.naming.remote;

import io.github.honhimw.nacos.api.ability.constant.AbilityKey;
import io.github.honhimw.nacos.api.exception.NacosException;
import io.github.honhimw.nacos.api.naming.pojo.Instance;
import io.github.honhimw.nacos.api.naming.pojo.ListView;
import io.github.honhimw.nacos.api.naming.pojo.Service;
import io.github.honhimw.nacos.api.naming.pojo.ServiceInfo;
import io.github.honhimw.nacos.api.naming.utils.NamingUtils;
import io.github.honhimw.nacos.api.selector.AbstractSelector;
import io.github.honhimw.nacos.client.env.NacosClientProperties;
import io.github.honhimw.nacos.client.naming.cache.NamingFuzzyWatchServiceListHolder;
import io.github.honhimw.nacos.client.naming.cache.ServiceInfoHolder;
import io.github.honhimw.nacos.client.naming.core.NamingServerListManager;
import io.github.honhimw.nacos.client.naming.core.ServiceInfoUpdateService;
import io.github.honhimw.nacos.client.naming.event.InstancesChangeNotifier;
import io.github.honhimw.nacos.client.naming.remote.gprc.NamingGrpcClientProxy;
import io.github.honhimw.nacos.client.naming.remote.http.NamingHttpClientManager;
import io.github.honhimw.nacos.client.naming.remote.http.NamingHttpClientProxy;
import io.github.honhimw.nacos.client.security.SecurityProxy;
import io.github.honhimw.nacos.common.executor.NameThreadFactory;
import io.github.honhimw.nacos.common.utils.CollectionUtils;
import io.github.honhimw.nacos.common.utils.ThreadUtils;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.github.honhimw.nacos.client.constant.Constants.Security.SECURITY_INFO_REFRESH_INTERVAL_MILLS;
import static io.github.honhimw.nacos.client.utils.LogUtils.NAMING_LOGGER;

/**
 * Delegate of naming client proxy.
 *
 * @author xiweng.yy
 */
public class NamingClientProxyDelegate implements NamingClientProxy {
    
    private final NamingServerListManager serverListManager;
    
    private final ServiceInfoUpdateService serviceInfoUpdateService;
    
    private final ServiceInfoHolder serviceInfoHolder;
    
    private final NamingHttpClientProxy httpClientProxy;
    
    private final NamingGrpcClientProxy grpcClientProxy;
    
    private final SecurityProxy securityProxy;
    
    private ScheduledExecutorService executorService;
    
    public NamingClientProxyDelegate(String namespace, ServiceInfoHolder serviceInfoHolder,
            NacosClientProperties properties, InstancesChangeNotifier changeNotifier,
            NamingFuzzyWatchServiceListHolder namingFuzzyWatchServiceListHolder) throws NacosException {
        this.serviceInfoUpdateService = new ServiceInfoUpdateService(properties, serviceInfoHolder, this,
                changeNotifier);
        this.serverListManager = new NamingServerListManager(properties, namespace);
        this.serverListManager.start();
        this.serviceInfoHolder = serviceInfoHolder;
        this.securityProxy = new SecurityProxy(this.serverListManager,
                NamingHttpClientManager.getInstance().getNacosRestTemplate());
        initSecurityProxy(properties);
        this.httpClientProxy = new NamingHttpClientProxy(namespace, securityProxy, serverListManager, properties);
        this.grpcClientProxy = new NamingGrpcClientProxy(namespace, securityProxy, serverListManager, properties,
                serviceInfoHolder, namingFuzzyWatchServiceListHolder);
    }
    
    private void initSecurityProxy(NacosClientProperties properties) {
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new NameThreadFactory("io.github.honhimw.nacos.client.naming.security"));
        final Properties nacosClientPropertiesView = properties.asProperties();
        this.securityProxy.login(nacosClientPropertiesView);
        this.executorService.scheduleWithFixedDelay(() -> securityProxy.login(nacosClientPropertiesView), 0,
                SECURITY_INFO_REFRESH_INTERVAL_MILLS, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void registerService(String serviceName, String groupName, Instance instance) throws NacosException {
        getExecuteClientProxy(instance).registerService(serviceName, groupName, instance);
    }
    
    @Override
    public void batchRegisterService(String serviceName, String groupName, List<Instance> instances)
            throws NacosException {
        NAMING_LOGGER.info("batchRegisterInstance instances: {} ,serviceName: {} begin.", instances, serviceName);
        if (CollectionUtils.isEmpty(instances)) {
            NAMING_LOGGER.warn("batchRegisterInstance instances is Empty:{}", instances);
        }
        grpcClientProxy.batchRegisterService(serviceName, groupName, instances);
        NAMING_LOGGER.info("batchRegisterInstance instances: {} ,serviceName: {} finish.", instances, serviceName);
    }
    
    @Override
    public void batchDeregisterService(String serviceName, String groupName, List<Instance> instances)
            throws NacosException {
        NAMING_LOGGER.info("batch DeregisterInstance instances: {} ,serviceName: {} begin.", instances, serviceName);
        if (CollectionUtils.isEmpty(instances)) {
            NAMING_LOGGER.warn("batch DeregisterInstance instances is Empty:{}", instances);
        }
        grpcClientProxy.batchDeregisterService(serviceName, groupName, instances);
        NAMING_LOGGER.info("batch DeregisterInstance instances: {} ,serviceName: {} finish.", instances, serviceName);
    }
    
    @Override
    public void deregisterService(String serviceName, String groupName, Instance instance) throws NacosException {
        getExecuteClientProxy(instance).deregisterService(serviceName, groupName, instance);
    }
    
    @Override
    public void updateInstance(String serviceName, String groupName, Instance instance) throws NacosException {
    
    }
    
    @Override
    public ServiceInfo queryInstancesOfService(String serviceName, String groupName, String clusters,
            boolean healthyOnly) throws NacosException {
        return grpcClientProxy.queryInstancesOfService(serviceName, groupName, clusters, healthyOnly);
    }
    
    @Override
    public Service queryService(String serviceName, String groupName) throws NacosException {
        return null;
    }
    
    @Override
    public void createService(Service service, AbstractSelector selector) throws NacosException {
    
    }
    
    @Override
    public boolean deleteService(String serviceName, String groupName) throws NacosException {
        return false;
    }
    
    @Override
    public void updateService(Service service, AbstractSelector selector) throws NacosException {
    
    }
    
    @Override
    public ListView<String> getServiceList(int pageNo, int pageSize, String groupName, AbstractSelector selector)
            throws NacosException {
        return grpcClientProxy.getServiceList(pageNo, pageSize, groupName, selector);
    }
    
    @Override
    public ServiceInfo subscribe(String serviceName, String groupName, String clusters) throws NacosException {
        NAMING_LOGGER.info("[SUBSCRIBE-SERVICE] service:{}, group:{}, clusters:{} ", serviceName, groupName, clusters);
        String serviceNameWithGroup = NamingUtils.getGroupedName(serviceName, groupName);
        String serviceKey = ServiceInfo.getKey(serviceNameWithGroup, clusters);
        serviceInfoUpdateService.scheduleUpdateIfAbsent(serviceName, groupName, clusters);
        ServiceInfo result = serviceInfoHolder.getServiceInfoMap().get(serviceKey);
        if (null == result || !isSubscribed(serviceName, groupName, clusters)) {
            result = grpcClientProxy.subscribe(serviceName, groupName, clusters);
        }
        serviceInfoHolder.processServiceInfo(result);
        return result;
    }
    
    @Override
    public void unsubscribe(String serviceName, String groupName, String clusters) throws NacosException {
        NAMING_LOGGER.debug("[UNSUBSCRIBE-SERVICE] service:{}, group:{}, cluster:{} ", serviceName, groupName,
                clusters);
        serviceInfoUpdateService.stopUpdateIfContain(serviceName, groupName, clusters);
        grpcClientProxy.unsubscribe(serviceName, groupName, clusters);
    }
    
    @Override
    public boolean isSubscribed(String serviceName, String groupName, String clusters) throws NacosException {
        return grpcClientProxy.isSubscribed(serviceName, groupName, clusters);
    }
    
    @Override
    public boolean serverHealthy() {
        return grpcClientProxy.serverHealthy() || httpClientProxy.serverHealthy();
    }
    
    private NamingClientProxy getExecuteClientProxy(Instance instance) {
        if (instance.isEphemeral() || grpcClientProxy.isAbilitySupportedByServer(
                AbilityKey.SERVER_PERSISTENT_INSTANCE_BY_GRPC)) {
            return grpcClientProxy;
        }
        return httpClientProxy;
    }
    
    @Override
    public void shutdown() throws NacosException {
        String className = this.getClass().getName();
        NAMING_LOGGER.info("{} do shutdown begin", className);
        serviceInfoUpdateService.shutdown();
        serverListManager.shutdown();
        httpClientProxy.shutdown();
        grpcClientProxy.shutdown();
        securityProxy.shutdown();
        ThreadUtils.shutdownThreadPool(executorService, NAMING_LOGGER);
        NAMING_LOGGER.info("{} do shutdown stop", className);
    }
}
