package io.github.honhimw.scn;

import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.stream.Stream;

/// @author honhimW
/// @since 2025-12-24
public class TestUtils {

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
		Iterable<Object> objects = _yaml.loadAll(yaml);
		for (Object object : objects) {

		}
		return new String[0];
	}

}
