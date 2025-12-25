/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
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

package io.github.honhimw.nacos.api.naming.remote.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.honhimw.nacos.api.naming.remote.NamingRemoteConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstanceResponseTest {
    
    protected static ObjectMapper mapper;
    
    @BeforeAll
    static void setUp() throws Exception {
		mapper = JsonMapper.builder()
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_NULL))
			.build();
    }
    
    @Test
    void testSerialize() {
        InstanceResponse response = new InstanceResponse(NamingRemoteConstants.REGISTER_INSTANCE);
        String json = mapper.writeValueAsString(response);
        assertTrue(json.contains("\"type\":\"" + NamingRemoteConstants.REGISTER_INSTANCE + "\""));
    }
    
    @Test
    void testDeserialize() {
        String json = "{\"resultCode\":200,\"errorCode\":0,\"type\":\"deregisterInstance\",\"success\":true}";
        InstanceResponse response = mapper.readValue(json, InstanceResponse.class);
        assertEquals(NamingRemoteConstants.DE_REGISTER_INSTANCE, response.getType());
    }
}
