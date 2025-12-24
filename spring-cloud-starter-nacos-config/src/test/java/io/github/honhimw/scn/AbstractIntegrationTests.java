package io.github.honhimw.scn;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/// @author honhimW
/// @since 2025-12-24
public abstract class AbstractIntegrationTests {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	protected SpringApplicationBuilder appBuilder = new SpringApplicationBuilder(Config.class)
		.web(WebApplicationType.NONE);

	protected final void withProperties(Map<?, ?> properties) {
		appBuilder.properties(TestUtils.properties2Pairs(properties));
	}

	protected final void withProperties(String yaml) {
		appBuilder.properties(TestUtils.yaml2Pairs(yaml));
	}

	protected void setUp() {

	}

	@Test
	@SneakyThrows
	public final void run() {
		setUp();
		SpringApplication app = appBuilder.build();
		ConfigurableApplicationContext context = app.run();
		run(context);
	}

	protected abstract void run(ConfigurableApplicationContext context) throws Exception;

	@SpringBootConfiguration
	@EnableAutoConfiguration
	public static class Config {

	}

}
