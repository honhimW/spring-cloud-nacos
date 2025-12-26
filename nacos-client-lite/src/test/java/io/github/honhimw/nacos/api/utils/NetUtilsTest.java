/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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

package io.github.honhimw.nacos.api.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NetUtilsTest {
    
    @AfterEach
    void tearDown() throws Exception {
        Class<?> clazz = Class.forName("io.github.honhimw.nacos.api.utils.NetUtils");
        Field field = clazz.getDeclaredField("localIp");
        field.setAccessible(true);
        field.set(null, "");
        System.clearProperty("io.github.honhimw.nacos.client.local.ip");
        System.clearProperty("io.github.honhimw.nacos.client.local.preferHostname");
        System.clearProperty("java.net.preferIPv6Addresses");
    }
    
    @Test
    void testLocalIpWithSpecifiedIp() {
        System.setProperty("io.github.honhimw.nacos.client.local.ip", "10.2.8.8");
        assertEquals("10.2.8.8", NetUtils.localIp());
        System.setProperty("io.github.honhimw.nacos.client.local.ip", "10.2.8.9");
        assertEquals("10.2.8.8", NetUtils.localIp());
    }
    
    @Test
    void testLocalIpWithPreferHostname() throws Exception {
        InetAddress inetAddress = invokeGetInetAddress();
        String hostname = inetAddress.getHostName();
        System.setProperty("io.github.honhimw.nacos.client.local.preferHostname", "true");
        assertEquals(hostname, NetUtils.localIp());
    }
    
    @Test
    void testLocalIpWithoutPreferHostname() throws Exception {
        InetAddress inetAddress = invokeGetInetAddress();
        String ip = inetAddress.getHostAddress();
        assertEquals(ip, NetUtils.localIp());
    }
    
    private InetAddress invokeGetInetAddress() throws Exception {
        Class<?> clazz = Class.forName("io.github.honhimw.nacos.api.utils.NetUtils");
        Method method = clazz.getDeclaredMethod("findFirstNonLoopbackAddress");
        method.setAccessible(true);
        return (InetAddress) method.invoke(null);
    }
    
}
