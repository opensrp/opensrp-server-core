package org.opensrp;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class TestDriverManagerDataSource extends DriverManagerDataSource {

	@Override
	public void setUrl(String url) {
		super.setUrl(String.format("jdbc:postgresql://localhost:%d/%s",
				TestPostgresInstance.postgresContainer.getMappedPort(5432), TestPostgresInstance.POSTGRES_DB));
	}
}
