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

package io.github.honhimw.nacos.api.remote.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServerCheckResponseTest {
    
    ObjectMapper mapper;
    
    @BeforeEach
    void setUp() throws Exception {
		mapper = JsonMapper.builder()
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_NULL))
			.build();
    }
    
    @Test
    void testSerialization() {
        ServerCheckResponse response = new ServerCheckResponse("35643245_1.1.1.1_3306", false);
        String actual = mapper.writeValueAsString(response);
        assertTrue(actual.contains("\"connectionId\":\"35643245_1.1.1.1_3306\""));
        assertTrue(actual.contains("\"supportAbilityNegotiation\":false"));
    }
    
    @Test
    void testDeserialization() {
        String json = "{\"resultCode\":200,\"errorCode\":0,\"connectionId\":\"35643245_1.1.1.1_3306\",\"success\":true,"
                + "\"supportAbilityNegotiation\":true}";
        ServerCheckResponse response = mapper.readValue(json, ServerCheckResponse.class);
        assertEquals("35643245_1.1.1.1_3306", response.getConnectionId());
        assertTrue(response.isSupportAbilityNegotiation());
    }
}
