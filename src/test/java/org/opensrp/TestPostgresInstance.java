package org.opensrp;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class TestPostgresInstance {

	private static final String POSTGRES_USER = "opensrp_admin";
	private static final String POSTGRES_PASSWORD = "admin";
	private static final String POSTGRES_USER_CONFIG = "POSTGRES_USER";

	private static final String POSTGRES_PASSWORD_CONFIG = "POSTGRES_PASSWORD";

	protected static final String POSTGRES_DB_CONFIG = "POSTGRES_DB";
	protected static final String POSTGRES_DB = "opensrp_test";
	private static final String DOCKER_IMAGE_NAME = "postgis/postgis:14-3.3-alpine";

	protected static final int DOCKER_EXPOSE_PORT = 5432;

	public static final GenericContainer<?> postgresContainer = new GenericContainer<>(DockerImageName.parse(DOCKER_IMAGE_NAME))
			.withEnv(POSTGRES_PASSWORD_CONFIG, POSTGRES_PASSWORD)
			.withEnv(POSTGRES_USER_CONFIG, POSTGRES_USER)
			.withEnv(POSTGRES_DB_CONFIG, POSTGRES_DB)
		.withExposedPorts(DOCKER_EXPOSE_PORT);

	static {
		postgresContainer.start();
	}
}
