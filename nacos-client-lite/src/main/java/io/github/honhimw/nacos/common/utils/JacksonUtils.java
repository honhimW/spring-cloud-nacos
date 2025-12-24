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

package io.github.honhimw.nacos.common.utils;

import io.github.honhimw.nacos.api.exception.runtime.NacosDeserializationException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.NamedType;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Json utils implement by Jackson.
 *
 * @author <a href="mailto:liaochuntao@live.com">liaochuntao</a>
 */
public final class JacksonUtils {

	private JacksonUtils() {
	}

	static ObjectMapper mapper;

	static {
		mapper = JsonMapper.builder()
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.changeDefaultPropertyInclusion(value -> value.withValueInclusion(Include.NON_NULL))
			.build();
	}

	/**
	 * Object to json string.
	 *
	 * @param obj obj
	 * @return json string
	 */
	public static String toJson(Object obj) {
		return mapper.writeValueAsString(obj);
	}

	/**
	 * Object to json string byte array.
	 *
	 * @param obj obj
	 * @return json string byte array
	 */
	public static byte[] toJsonBytes(Object obj) {
		return mapper.writeValueAsBytes(obj);
	}

	/**
	 * Json string deserialize to Object.
	 *
	 * @param json json string
	 * @param cls  class of object
	 * @param <T>  General type
	 * @return object
	 */
	public static <T> T toObj(byte[] json, Class<T> cls) {
		return mapper.readValue(json, cls);
	}

	/**
	 * Json string deserialize to Object.
	 *
	 * @param json json string
	 * @param cls  {@link Type} of object
	 * @param <T>  General type
	 * @return object
	 */
	public static <T> T toObj(byte[] json, Type cls) {
		return mapper.readValue(json, mapper.constructType(cls));
	}

	/**
	 * Json string deserialize to Object.
	 *
	 * @param inputStream json string input stream
	 * @param cls         class of object
	 * @param <T>         General type
	 * @return object
	 */
	public static <T> T toObj(InputStream inputStream, Class<T> cls) {
		return mapper.readValue(inputStream, cls);
	}

	/**
	 * Json string deserialize to Object.
	 *
	 * @param json          json string byte array
	 * @param typeReference {@link TypeReference} of object
	 * @param <T>           General type
	 * @return object
	 */
	public static <T> T toObj(byte[] json, TypeReference<T> typeReference) {
		return mapper.readValue(json, typeReference);
	}

	/**
	 * Json string deserialize to Object.
	 *
	 * @param json json string
	 * @param cls  class of object
	 * @param <T>  General type
	 * @return object
	 * @throws NacosDeserializationException if deserialize failed
	 */
	public static <T> T toObj(String json, Class<T> cls) {
		return mapper.readValue(json, cls);
	}

	/**
	 * Json string deserialize to Object.
	 *
	 * @param json json string
	 * @param type {@link Type} of object
	 * @param <T>  General type
	 * @return object
	 */
	public static <T> T toObj(String json, Type type) {
		return mapper.readValue(json, mapper.constructType(type));
	}

	/**
	 * Json string deserialize to Object.
	 *
	 * @param json          json string
	 * @param typeReference {@link TypeReference} of object
	 * @param <T>           General type
	 * @return object
	 */
	public static <T> T toObj(String json, TypeReference<T> typeReference) {
		return mapper.readValue(json, typeReference);
	}

	/**
	 * Json string deserialize to Object.
	 *
	 * @param inputStream json string input stream
	 * @param type        {@link Type} of object
	 * @param <T>         General type
	 * @return object
	 */
	public static <T> T toObj(InputStream inputStream, Type type) {
		return mapper.readValue(inputStream, mapper.constructType(type));
	}

	/**
	 * Json string deserialize to Jackson {@link JsonNode}.
	 *
	 * @param json json string
	 * @return {@link JsonNode}
	 */
	public static JsonNode toObj(String json) {
		return mapper.readTree(json);
	}

	/**
	 * Java object deserialize to Jackson {@link JsonNode}.
	 *
	 * @param obj java object
	 * @return {@link JsonNode}
	 */
	public static <T extends JsonNode> T toTree(Object obj) {
		return mapper.valueToTree(obj);
	}

	/**
	 * Register sub type for child class.
	 *
	 * @param clz  child class
	 * @param type type name of child class
	 */
	public static void registerSubtype(Class<?> clz, String type) {
		mapper = mapper.rebuild().registerSubtypes(new NamedType(clz, type)).build();
	}

	/**
	 * Create a new empty Jackson {@link ObjectNode}.
	 *
	 * @return {@link ObjectNode}
	 */
	public static ObjectNode createEmptyJsonNode() {
		return new ObjectNode(mapper.getNodeFactory());
	}

	/**
	 * Create a new empty Jackson {@link ArrayNode}.
	 *
	 * @return {@link ArrayNode}
	 */
	public static ArrayNode createEmptyArrayNode() {
		return new ArrayNode(mapper.getNodeFactory());
	}

	/**
	 * Parse object to Jackson {@link JsonNode}.
	 *
	 * @param obj object
	 * @return {@link JsonNode}
	 */
	public static JsonNode transferToJsonNode(Object obj) {
		return mapper.valueToTree(obj);
	}

	/**
	 * construct java type -> Jackson Java Type.
	 *
	 * @param type java type
	 * @return JavaType {@link JavaType}
	 */
	public static JavaType constructJavaType(Type type) {
		return mapper.constructType(type);
	}
}
