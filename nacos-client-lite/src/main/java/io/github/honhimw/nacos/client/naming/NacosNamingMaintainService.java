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

package io.github.honhimw.nacos.client.naming;

import io.github.honhimw.nacos.api.PropertyKeyConst;
import io.github.honhimw.nacos.api.common.Constants;
import io.github.honhimw.nacos.api.exception.NacosException;
import io.github.honhimw.nacos.api.naming.NamingMaintainService;
import io.github.honhimw.nacos.api.naming.pojo.Instance;
import io.github.honhimw.nacos.api.naming.pojo.Service;
import io.github.honhimw.nacos.api.selector.AbstractSelector;
import io.github.honhimw.nacos.api.selector.ExpressionSelector;
import io.github.honhimw.nacos.api.selector.NoneSelector;
import io.github.honhimw.nacos.client.env.NacosClientProperties;
import io.github.honhimw.nacos.client.naming.core.NamingServerListManager;
import io.github.honhimw.nacos.client.naming.remote.http.NamingHttpClientManager;
import io.github.honhimw.nacos.client.naming.remote.http.NamingHttpClientProxy;
import io.github.honhimw.nacos.client.naming.utils.InitUtils;
import io.github.honhimw.nacos.client.security.SecurityProxy;
import io.github.honhimw.nacos.client.utils.ValidatorUtils;
import io.github.honhimw.nacos.common.executor.NameThreadFactory;
import io.github.honhimw.nacos.common.utils.ThreadUtils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.github.honhimw.nacos.client.constant.Constants.Security.SECURITY_INFO_REFRESH_INTERVAL_MILLS;
import static io.github.honhimw.nacos.client.utils.LogUtils.NAMING_LOGGER;

/**
 * Nacos naming maintain service.
 *
 * @author liaochuntao
 * @since 1.0.1
 * @deprecated Use {@link io.github.honhimw.nacos.api.naming.maintain.NamingMaintainService} in nacos-maintainer-client article tp replaced.
 */
@SuppressWarnings("PMD.ServiceOrDaoClassShouldEndWithImplRule")
@Deprecated
public class NacosNamingMaintainService implements NamingMaintainService {
    
    private String namespace;
    
    private NamingHttpClientProxy serverProxy;
    
    private NamingServerListManager serverListManager;
    
    private SecurityProxy securityProxy;
    
    private ScheduledExecutorService executorService;
    
    public NacosNamingMaintainService(String serverList) throws NacosException {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverList);
        init(properties);
    }
    
    public NacosNamingMaintainService(Properties properties) throws NacosException {
        init(properties);
    }
    
    private void init(Properties properties) throws NacosException {
        final NacosClientProperties nacosClientProperties = NacosClientProperties.PROTOTYPE.derive(properties);
        ValidatorUtils.checkInitParam(nacosClientProperties);
        namespace = InitUtils.initNamespaceForNaming(nacosClientProperties);
        InitUtils.initSerialization();
        InitUtils.initWebRootContext(nacosClientProperties);
        serverListManager = new NamingServerListManager(nacosClientProperties, namespace);
        serverListManager.start();
        securityProxy = new SecurityProxy(serverListManager,
                NamingHttpClientManager.getInstance().getNacosRestTemplate());
        initSecurityProxy(properties);
        serverProxy = new NamingHttpClientProxy(namespace, securityProxy, serverListManager, nacosClientProperties);
    }
    
    private void initSecurityProxy(Properties properties) {
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new NameThreadFactory("io.github.honhimw.nacos.client.naming.maintainService.security"));
        this.securityProxy.login(properties);
        this.executorService
                .scheduleWithFixedDelay(() -> securityProxy.login(properties), 0, SECURITY_INFO_REFRESH_INTERVAL_MILLS,
                        TimeUnit.MILLISECONDS);
        
    }
    
    @Override
    public void updateInstance(String serviceName, Instance instance) throws NacosException {
        updateInstance(serviceName, Constants.DEFAULT_GROUP, instance);
    }
    
    @Override
    public void updateInstance(String serviceName, String groupName, Instance instance) throws NacosException {
        serverProxy.updateInstance(serviceName, groupName, instance);
    }
    
    @Override
    public Service queryService(String serviceName) throws NacosException {
        return queryService(serviceName, Constants.DEFAULT_GROUP);
    }
    
    @Override
    public Service queryService(String serviceName, String groupName) throws NacosException {
        return serverProxy.queryService(serviceName, groupName);
    }
    
    @Override
    public void createService(String serviceName) throws NacosException {
        createService(serviceName, Constants.DEFAULT_GROUP);
    }
    
    @Override
    public void createService(String serviceName, String groupName) throws NacosException {
        createService(serviceName, groupName, Constants.DEFAULT_PROTECT_THRESHOLD);
    }
    
    @Override
    public void createService(String serviceName, String groupName, float protectThreshold) throws NacosException {
        Service service = new Service();
        service.setName(serviceName);
        service.setGroupName(groupName);
        service.setProtectThreshold(protectThreshold);
        
        createService(service, new NoneSelector());
    }
    
    @Override
    public void createService(String serviceName, String groupName, float protectThreshold, String expression)
            throws NacosException {
        Service service = new Service();
        service.setName(serviceName);
        service.setGroupName(groupName);
        service.setProtectThreshold(protectThreshold);
        
        ExpressionSelector selector = new ExpressionSelector();
        selector.setExpression(expression);
        
        createService(service, selector);
    }
    
    @Override
    public void createService(Service service, AbstractSelector selector) throws NacosException {
        serverProxy.createService(service, selector);
    }
    
    @Override
    public boolean deleteService(String serviceName) throws NacosException {
        return deleteService(serviceName, Constants.DEFAULT_GROUP);
    }
    
    @Override
    public boolean deleteService(String serviceName, String groupName) throws NacosException {
        return serverProxy.deleteService(serviceName, groupName);
    }
    
    @Override
    public void updateService(String serviceName, String groupName, float protectThreshold) throws NacosException {
        Service service = new Service();
        service.setName(serviceName);
        service.setGroupName(groupName);
        service.setProtectThreshold(protectThreshold);
        
        updateService(service, new NoneSelector());
    }
    
    @Override
    public void updateService(String serviceName, String groupName, float protectThreshold,
            Map<String, String> metadata) throws NacosException {
        Service service = new Service();
        service.setName(serviceName);
        service.setGroupName(groupName);
        service.setProtectThreshold(protectThreshold);
        service.setMetadata(metadata);
        
        updateService(service, new NoneSelector());
    }
    
    @Override
    public void updateService(Service service, AbstractSelector selector) throws NacosException {
        serverProxy.updateService(service, selector);
    }
    
    @Override
    public void shutDown() throws NacosException {
        String className = this.getClass().getName();
        NAMING_LOGGER.info("{} do shutdown begin", className);
        serverListManager.shutdown();
        serverProxy.shutdown();
        ThreadUtils.shutdownThreadPool(executorService, NAMING_LOGGER);
        NAMING_LOGGER.info("{} do shutdown stop", className);
    }
}
