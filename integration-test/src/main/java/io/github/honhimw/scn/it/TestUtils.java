package io.github.honhimw.scn.it;

import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.*;
import java.util.stream.Stream;

/// @author honhimW
/// @since 2025-12-24
public class TestUtils {

	public static RNacosContainer rNacosContainer() {
		return new RNacosContainer()
			.withEnv("RNACOS_INIT_ADMIN_USERNAME", "admin")
			.withEnv("RNACOS_INIT_ADMIN_PASSWORD", "admin")
			.withEnv("RNACOS_CONSOLE_ENABLE_CAPTCHA", "false");
	}

	public static String[] properties2Pairs(Map<?, ?> properties) {
		return properties.entrySet().stream().flatMap(entry -> {
			Object key = entry.getKey();
			Object value = entry.getValue();
			if (key == null) {
				return Stream.empty();
			}
			if (value == null) {
				value = "";
			}
			return Stream.of("%s=%s".formatted(key, value));
		}).toArray(String[]::new);
	}

	public static String[] yaml2Pairs(String yaml) {
		Yaml _yaml = new Yaml();
		Map<String, Object> map = _yaml.loadAs(yaml, Map.class);
		Map<String, String> flatten = flatten(map);
		return flatten.entrySet().stream().map(entry -> "%s=%s".formatted(entry.getKey(), entry.getValue())).toArray(String[]::new);
	}

	public static Map<String, String> flatten(Map<?, ?> map) {
		Map<String, String> current = new LinkedHashMap<>();
		map.forEach((key, value) -> {
			if (value instanceof Map<?, ?> next) {
				Map<String, String> flatten = flatten(next);
				flatten.forEach((nextKey, nextValue) -> current.put(key + "." + nextKey, nextValue));
			} else if (value instanceof Collection<?> next) {
				List<?> list = new ArrayList<>(next);
				for (int i = 0; i < list.size(); i++) {
					Object item = list.get(i);
					String path = key + "[" + i + "]";
					if (item instanceof Map<?, ?> next2) {
						Map<String, String> flatten = flatten(next2);
						flatten.forEach((nextKey, nextValue) -> current.put(path + nextKey, nextValue));
					} else {
						current.put(path, String.valueOf(item));
					}
				}
			} else {
				current.put(String.valueOf(key), String.valueOf(value));
			}
		});
		return current;
	}

	@Test
	@SneakyThrows
	void yamlFlatten() {
		@Language("yaml")
		String yaml = """
			spring:
			  config:
			    import:
			    - a.yml
			    - b.yml
			  cloud:
			    config:
			      enabled: true
			    discovery:
			      enabled: false
			""";
		String[] strings = yaml2Pairs(yaml);
		Assertions.assertEquals("spring.config.import[0]=a.yml", strings[0]);
		Assertions.assertEquals("spring.config.import[1]=b.yml", strings[1]);
		Assertions.assertEquals("spring.cloud.config.enabled=true", strings[2]);
		Assertions.assertEquals("spring.cloud.discovery.enabled=false", strings[3]);
	}

}
