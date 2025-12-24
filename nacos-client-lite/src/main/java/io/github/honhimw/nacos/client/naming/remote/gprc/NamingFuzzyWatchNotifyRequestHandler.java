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

import io.github.honhimw.nacos.api.common.Constants;
import io.github.honhimw.nacos.api.naming.remote.request.NamingFuzzyWatchChangeNotifyRequest;
import io.github.honhimw.nacos.api.naming.remote.request.NamingFuzzyWatchSyncRequest;
import io.github.honhimw.nacos.api.naming.remote.response.NamingFuzzyWatchChangeNotifyResponse;
import io.github.honhimw.nacos.api.naming.utils.NamingUtils;
import io.github.honhimw.nacos.api.remote.request.Request;
import io.github.honhimw.nacos.api.remote.response.Response;
import io.github.honhimw.nacos.client.naming.cache.NamingFuzzyWatchContext;
import io.github.honhimw.nacos.client.naming.cache.NamingFuzzyWatchServiceListHolder;
import io.github.honhimw.nacos.client.naming.event.NamingFuzzyWatchNotifyEvent;
import io.github.honhimw.nacos.common.notify.NotifyCenter;
import io.github.honhimw.nacos.common.remote.client.Connection;
import io.github.honhimw.nacos.common.remote.client.ServerRequestHandler;
import io.github.honhimw.nacos.common.utils.FuzzyGroupKeyPattern;

import java.util.Collection;

import static io.github.honhimw.nacos.api.common.Constants.FUZZY_WATCH_RESOURCE_CHANGED;

/**
 * handle fuzzy watch request from server.
 *
 * @author shiyiyue
 */
public class NamingFuzzyWatchNotifyRequestHandler implements ServerRequestHandler {
    
    NamingFuzzyWatchServiceListHolder namingFuzzyWatchServiceListHolder;
    
    public NamingFuzzyWatchNotifyRequestHandler(NamingFuzzyWatchServiceListHolder namingFuzzyWatchServiceListHolder) {
        this.namingFuzzyWatchServiceListHolder = namingFuzzyWatchServiceListHolder;
        NotifyCenter.registerToPublisher(NamingFuzzyWatchNotifyEvent.class, 1000);
    }
    
    @Override
    public Response requestReply(Request request, Connection connection) {
        
        if (request instanceof NamingFuzzyWatchSyncRequest) {
            NamingFuzzyWatchSyncRequest watchNotifySyncRequest = (NamingFuzzyWatchSyncRequest) request;
            NamingFuzzyWatchContext namingFuzzyWatchContext = namingFuzzyWatchServiceListHolder.getFuzzyMatchContextMap()
                    .get(watchNotifySyncRequest.getGroupKeyPattern());
            if (namingFuzzyWatchContext != null) {
                Collection<NamingFuzzyWatchSyncRequest.Context> serviceKeys = watchNotifySyncRequest.getContexts();
                if (watchNotifySyncRequest.getSyncType().equals(Constants.FUZZY_WATCH_INIT_NOTIFY)
                        || watchNotifySyncRequest.getSyncType().equals(Constants.FUZZY_WATCH_DIFF_SYNC_NOTIFY)) {
                    for (NamingFuzzyWatchSyncRequest.Context serviceKey : serviceKeys) {
                        // may have a 'change event' sent to client before 'init event'
                        if (namingFuzzyWatchContext.addReceivedServiceKey(serviceKey.getServiceKey())) {
                            NotifyCenter.publishEvent(NamingFuzzyWatchNotifyEvent.build(
                                    namingFuzzyWatchServiceListHolder.getNotifierEventScope(),
                                    watchNotifySyncRequest.getGroupKeyPattern(), serviceKey.getServiceKey(),
                                    serviceKey.getChangedType(), watchNotifySyncRequest.getSyncType()));
                        }
                    }
                } else if (watchNotifySyncRequest.getSyncType().equals(Constants.FINISH_FUZZY_WATCH_INIT_NOTIFY)) {
                    namingFuzzyWatchContext.markInitializationComplete();
                }
            }
            
            return new NamingFuzzyWatchChangeNotifyResponse();
            
        } else if (request instanceof NamingFuzzyWatchChangeNotifyRequest) {
            NamingFuzzyWatchChangeNotifyRequest notifyChangeRequest = (NamingFuzzyWatchChangeNotifyRequest) request;
            String[] serviceKeyItems = NamingUtils.parseServiceKey(notifyChangeRequest.getServiceKey());
            String namespace = serviceKeyItems[0];
            String groupName = serviceKeyItems[1];
            String serviceName = serviceKeyItems[2];
            
            Collection<String> matchedPattern = FuzzyGroupKeyPattern.filterMatchedPatterns(
                    namingFuzzyWatchServiceListHolder.getFuzzyMatchContextMap().keySet(), serviceName, groupName,
                    namespace);
            String serviceChangeType = notifyChangeRequest.getChangedType();
            
            switch (serviceChangeType) {
                case Constants.ServiceChangedType.ADD_SERVICE:
                case Constants.ServiceChangedType.INSTANCE_CHANGED:
                    for (String pattern : matchedPattern) {
                        NamingFuzzyWatchContext namingFuzzyWatchContext = namingFuzzyWatchServiceListHolder.getFuzzyMatchContextMap()
                                .get(pattern);
                        if (namingFuzzyWatchContext != null && namingFuzzyWatchContext.addReceivedServiceKey(
                                ((NamingFuzzyWatchChangeNotifyRequest) request).getServiceKey())) {
                            //publish local service add event
                            NotifyCenter.publishEvent(NamingFuzzyWatchNotifyEvent.build(
                                    namingFuzzyWatchServiceListHolder.getNotifierEventScope(), pattern,
                                    notifyChangeRequest.getServiceKey(), Constants.ServiceChangedType.ADD_SERVICE,
                                    FUZZY_WATCH_RESOURCE_CHANGED));
                        }
                    }
                    break;
                case Constants.ServiceChangedType.DELETE_SERVICE:
                    for (String pattern : matchedPattern) {
                        NamingFuzzyWatchContext namingFuzzyWatchContext = namingFuzzyWatchServiceListHolder.getFuzzyMatchContextMap()
                                .get(pattern);
                        if (namingFuzzyWatchContext != null && namingFuzzyWatchContext.removeReceivedServiceKey(
                                notifyChangeRequest.getServiceKey())) {
                            NotifyCenter.publishEvent(NamingFuzzyWatchNotifyEvent.build(
                                    namingFuzzyWatchServiceListHolder.getNotifierEventScope(), pattern,
                                    notifyChangeRequest.getServiceKey(), Constants.ServiceChangedType.DELETE_SERVICE,
                                    FUZZY_WATCH_RESOURCE_CHANGED));
                        }
                    }
                    break;
                default:
                    break;
            }
            return new NamingFuzzyWatchChangeNotifyResponse();
        }
        return null;
    }
}
