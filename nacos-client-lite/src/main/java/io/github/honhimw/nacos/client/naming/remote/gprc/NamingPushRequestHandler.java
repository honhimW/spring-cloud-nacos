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

package io.github.honhimw.nacos.client.naming.remote.gprc;

import io.github.honhimw.nacos.api.naming.remote.request.NotifySubscriberRequest;
import io.github.honhimw.nacos.api.naming.remote.response.NotifySubscriberResponse;
import io.github.honhimw.nacos.api.remote.request.Request;
import io.github.honhimw.nacos.api.remote.response.Response;
import io.github.honhimw.nacos.client.naming.cache.ServiceInfoHolder;
import io.github.honhimw.nacos.common.remote.client.Connection;
import io.github.honhimw.nacos.common.remote.client.ServerRequestHandler;

/**
 * Naming push request handler.
 *
 * @author xiweng.yy
 */
public class NamingPushRequestHandler implements ServerRequestHandler {
    
    private final ServiceInfoHolder serviceInfoHolder;
    
    public NamingPushRequestHandler(ServiceInfoHolder serviceInfoHolder) {
        this.serviceInfoHolder = serviceInfoHolder;
    }
    
    @Override
    public Response requestReply(Request request, Connection connection) {
        if (request instanceof NotifySubscriberRequest) {
            NotifySubscriberRequest notifyRequest = (NotifySubscriberRequest) request;
            serviceInfoHolder.processServiceInfo(notifyRequest.getServiceInfo());
            return new NotifySubscriberResponse();
        }
        return null;
    }
}
