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

package io.github.honhimw.nacos.api.selector;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.github.honhimw.nacos.api.exception.NacosException;
import io.github.honhimw.nacos.api.naming.pojo.Instance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.NamedType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoneSelectorTest {
    
    ObjectMapper mapper = new ObjectMapper();
    
    @BeforeEach
    void setUp() throws Exception {
		mapper = JsonMapper.builder()
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.changeDefaultPropertyInclusion(value -> value.withValueInclusion(JsonInclude.Include.NON_NULL))
			.registerSubtypes(new NamedType(NoneSelector.class, SelectorType.none.name()))
			.build();
    }
    
    @Test
    void testSerialization() {
        NoneSelector selector = new NoneSelector();
        String actual = mapper.writeValueAsString(selector);
        assertTrue(actual.contains("\"type\":\"" + SelectorType.none.name() + "\""));
    }
    
    @Test
    void testDeserialization() {
        String json = "{\"type\":\"none\"}";
        AbstractSelector actual = mapper.readValue(json, AbstractSelector.class);
        assertEquals(SelectorType.none.name(), actual.getType());
    }
    
    @Test
    void testCommandMethod() throws NacosException {
        NoneSelector selector = new NoneSelector();
        assertNull(selector.parse(""));
        List<Instance> instances = new ArrayList<>();
        assertEquals(instances, selector.select(instances));
    }
}
