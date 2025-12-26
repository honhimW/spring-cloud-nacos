package io.github.honhimw.scn.it;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/// @author honhimW
/// @since 2025-12-26
public class RNacosContainer extends GenericContainer<RNacosContainer> {

	public static final String IMAGE = "qingpan/rnacos:v0.7.10-alpine";

	public static final DockerImageName DOCKER_IMAGE_NAME = DockerImageName.parse(IMAGE);

	public static final int HTTP_PORT = 8848;
	public static final int GRPC_PORT = 9848;
	public static final int CONSOLE_PORT = 10848;

	public RNacosContainer() {
		super(DOCKER_IMAGE_NAME);
		this.withExposedPorts(8848, 9848, 10848)
			.waitingFor(Wait.forLogMessage(".*rnacos started.*", 1));
	}

	public int httpPort() {
		return getMappedPort(HTTP_PORT);
	}

	public int grpcPort() {
		return getMappedPort(GRPC_PORT);
	}

	public int consolePort() {
		return getMappedPort(CONSOLE_PORT);
	}

	public String serverAddr() {
		return "%s:%d".formatted(getHost(), httpPort());
	}

	public String consoleUrl() {
		return "http://%s:%d/rnacos".formatted(getHost(), consolePort());
	}

}
