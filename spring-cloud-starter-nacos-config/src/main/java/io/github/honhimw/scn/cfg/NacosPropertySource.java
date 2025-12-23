package io.github.honhimw.scn.cfg;

import com.alibaba.nacos.api.config.ConfigService;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.env.EnumerablePropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

/// @author honhimW
/// @since 2025-12-22
public class NacosPropertySource extends EnumerablePropertySource<@NonNull ConfigService> {

	private final Map<String, Object> properties = new LinkedHashMap<>();

	private final NacosConfigProperties configProperties;

	private final NacosConfigProperties.Config config;

	private long timestamp;

	public NacosPropertySource(NacosConfigProperties.Config config, ConfigService source, NacosConfigProperties configProperties) {
		super("%s:%s".formatted(config.getGroup(), config.getDataId()), source);
		this.configProperties = configProperties;
		this.config = config;
	}

	public void init() {
		this.timestamp = System.currentTimeMillis();
	}

	@Override
	public String @NonNull [] getPropertyNames() {
		return new String[0];
	}

	@Override
	public @Nullable Object getProperty(@NonNull String name) {
		return null;
	}
}
