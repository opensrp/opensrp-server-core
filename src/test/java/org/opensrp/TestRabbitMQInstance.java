package org.opensrp;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class TestRabbitMQInstance {
	private static final String DOCKER_IMAGE_NAME = "rabbitmq:3-alpine";

	private static final String RABBITMQ_USERNAME = "rabbitmq.username";

	private static final String RABBITMQ_PASSWORD = "rabbitmq.password";

	private static final String RABBITMQ_PORT = "rabbitmq.port";

	private static final int DOCKER_EXPOSE_PORT = 5672;

	private static final GenericContainer<?> rabbitMQContainer = new GenericContainer<>(DockerImageName.parse(DOCKER_IMAGE_NAME))
			.withExposedPorts(DOCKER_EXPOSE_PORT);

	static {
		rabbitMQContainer.start();
	}

	@DynamicPropertySource
	private static void registerRabbitMQProperties(DynamicPropertyRegistry registry) {
		registry.add(RABBITMQ_PORT, () -> rabbitMQContainer.getMappedPort(DOCKER_EXPOSE_PORT));
		registry.add(RABBITMQ_USERNAME, () -> "guest");
		registry.add(RABBITMQ_PASSWORD, () -> "guest");
	}
}
