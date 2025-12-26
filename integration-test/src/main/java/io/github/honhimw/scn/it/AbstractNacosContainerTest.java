package io.github.honhimw.scn.it;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/// @author honhimW
/// @since 2025-12-26
public abstract class AbstractNacosContainerTest {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	protected RNacosContainer nacos;

	/// After Test context started: [org.junit.jupiter.api.BeforeAll] [org.junit.jupiter.api.BeforeEach]
	/// Before application start
	protected void setUp() throws Exception {
		nacos = TestUtils.rNacosContainer();
		nacos.start();
		log.info("Console url: {}", nacos.consoleUrl());
		Integer httpPort = nacos.httpPort();
		Integer grpcPort = nacos.grpcPort();
		int offset = grpcPort - httpPort;
		System.setProperty("nacos.server.grpc.port.offset", String.valueOf(offset));
	}

	/// After application close
	/// Before Test context destroy: [org.junit.jupiter.api.AfterAll] [org.junit.jupiter.api.AfterEach]
	protected void tearDown() throws Exception {
		if (nacos != null) {
			nacos.stop();
		}
	}

}
