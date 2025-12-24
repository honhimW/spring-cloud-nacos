/*
 * Copyright 2013-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.honhimw.scn.discovery.endpoint;

import io.github.honhimw.nacos.api.naming.NamingService;
import io.github.honhimw.nacos.api.naming.pojo.Instance;
import io.github.honhimw.nacos.api.naming.pojo.ServiceInfo;
import io.github.honhimw.scn.discovery.NacosDiscoveryProperties;
import io.github.honhimw.scn.discovery.NacosServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoint for nacos discovery, get nacos properties and subscribed services.
 *
 * @author xiaojing
 */
@Endpoint(id = "nacosdiscovery")
public class NacosDiscoveryEndpoint {

	private static final Logger log = LoggerFactory
			.getLogger(NacosDiscoveryEndpoint.class);

	private NacosServiceManager nacosServiceManager;

	private NacosDiscoveryProperties nacosDiscoveryProperties;

	public NacosDiscoveryEndpoint(NacosServiceManager nacosServiceManager,
								  NacosDiscoveryProperties nacosDiscoveryProperties) {
		this.nacosServiceManager = nacosServiceManager;
		this.nacosDiscoveryProperties = nacosDiscoveryProperties;
	}

	/**
	 * @return nacos discovery endpoint
	 */
	@ReadOperation
	public Map<String, Object> nacosDiscovery() {
		Map<String, Object> result = new HashMap<>();
		result.put("NacosDiscoveryProperties", nacosDiscoveryProperties);

		NamingService namingService = nacosServiceManager.getNamingService();
		List<ServiceInfo> subscribe = Collections.emptyList();

		try {
			subscribe = namingService.getSubscribeServices();
			for (ServiceInfo serviceInfo : subscribe) {
				List<Instance> instances = namingService.getAllInstances(
						serviceInfo.getName(), serviceInfo.getGroupName());
				serviceInfo.setHosts(instances);
			}
		}
		catch (Exception e) {
			log.error("get subscribe services from nacos fail,", e);
		}
		result.put("subscribe", subscribe);
		return result;
	}

}
