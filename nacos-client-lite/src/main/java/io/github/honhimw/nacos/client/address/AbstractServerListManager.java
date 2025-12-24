/*
 * Copyright 1999-2025 Alibaba Group Holding Ltd.
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

package io.github.honhimw.nacos.client.address;

import io.github.honhimw.nacos.api.PropertyKeyConst;
import io.github.honhimw.nacos.api.common.Constants;
import io.github.honhimw.nacos.api.exception.NacosException;
import io.github.honhimw.nacos.client.env.NacosClientProperties;
import io.github.honhimw.nacos.common.JustForTest;
import io.github.honhimw.nacos.common.http.client.NacosRestTemplate;
import io.github.honhimw.nacos.common.lifecycle.Closeable;
import io.github.honhimw.nacos.common.remote.client.ServerListFactory;
import io.github.honhimw.nacos.common.spi.NacosServiceLoader;
import io.github.honhimw.nacos.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Server list Manager.
 *
 * @author totalo
 */
public abstract class AbstractServerListManager implements ServerListFactory, Closeable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServerListManager.class);
    
    protected ServerListProvider serverListProvider;
    
    protected NacosClientProperties properties;
    
    public AbstractServerListManager(NacosClientProperties properties) {
        this(properties, null);
    }
    
    public AbstractServerListManager(NacosClientProperties properties, String namespace) {
        // To avoid set operation affect the original properties.
        NacosClientProperties tmpProperties = properties.derive();
        if (StringUtils.isNotBlank(namespace)) {
            tmpProperties.setProperty(PropertyKeyConst.NAMESPACE, namespace);
        }
        tmpProperties.setProperty(Constants.CLIENT_MODULE_TYPE, getModuleName());
        this.properties = tmpProperties;
    }
    
    @Override
    public List<String> getServerList() {
        return serverListProvider.getServerList();
    }
    
    @Override
    public void shutdown() throws NacosException {
        String className = this.getClass().getName();
        LOGGER.info("{} do shutdown begin", className);
        if (null != serverListProvider) {
            serverListProvider.shutdown();
        }
        serverListProvider = null;
        LOGGER.info("{} do shutdown stop", className);
    }
    
    /**
     * Start server list manager.
     *
     * @throws NacosException during start and initialize.
     */
    public void start() throws NacosException {
        Collection<ServerListProvider> serverListProviders = NacosServiceLoader.load(ServerListProvider.class);
        Collection<ServerListProvider> sorted = serverListProviders.stream()
                .sorted((a, b) -> b.getOrder() - a.getOrder()).collect(Collectors.toList());
        for (ServerListProvider each : sorted) {
            boolean matchResult = each.match(properties);
            LOGGER.info("Load and match ServerListProvider {}, match result: {}", each.getClass().getCanonicalName(),
                    matchResult);
            if (matchResult) {
                this.serverListProvider = each;
                LOGGER.info("Will use {} as ServerListProvider", this.serverListProvider.getClass().getCanonicalName());
                break;
            }
        }
        if (null == serverListProvider) {
            LOGGER.error("No server list provider found, SPI load size: {}", sorted.size());
            throw new NacosException(NacosException.CLIENT_INVALID_PARAM, "No server list provider found.");
        }
        this.serverListProvider.init(properties, getNacosRestTemplate());
    }
    
    public String getServerName() {
        return getModuleName() + "-" + serverListProvider.getServerName();
    }
    
    public String getContextPath() {
        return serverListProvider.getContextPath();
    }
    
    public String getNamespace() {
        return serverListProvider.getNamespace();
    }
    
    public String getAddressSource() {
        return serverListProvider.getAddressSource();
    }
    
    public boolean isFixed() {
        return serverListProvider.isFixed();
    }
    
    /**
     * get module name.
     *
     * @return module name
     */
    protected abstract String getModuleName();
    
    /**
     * get nacos rest template.
     *
     * @return nacos rest template
     */
    protected abstract NacosRestTemplate getNacosRestTemplate();
    
    @JustForTest
    NacosClientProperties getProperties() {
        return properties;
    }
}
