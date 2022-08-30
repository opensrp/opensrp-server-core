package org.opensrp;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class TestDriverManagerDataSource extends DriverManagerDataSource {

    @Override
    public void setUrl(String url) {
        super.setUrl(String.format("jdbc:postgresql://localhost:%d/%s",
                TestPostgresInstance.postgresContainer.getMappedPort(TestPostgresInstance.DOCKER_EXPOSE_PORT), TestPostgresInstance.POSTGRES_DB));
    }
}
