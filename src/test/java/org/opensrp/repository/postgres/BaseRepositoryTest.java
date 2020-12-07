package org.opensrp.repository.postgres;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
@ActiveProfiles(profiles = { "jedis"})
public abstract class BaseRepositoryTest {
	
	private static String TEST_SCRIPTS_ROOT_DIRECTORY = "test-scripts/";
	
	@Autowired
	private DataSource openSRPDataSource;
	protected static List<String> tableNames = new ArrayList<>();

	/**
	 * Populates the configured {@link DataSource} with data from passed scripts
	 * 
	 * @param the scripts to load
	 * @throws SQLException
	 */
	@Before
	public void populateDatabase() throws SQLException {
		truncateTables();
		if (getDatabaseScripts() == null || getDatabaseScripts().isEmpty())
			return;
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		for (String script : getDatabaseScripts()) {
			populator.addScript(new ClassPathResource(TEST_SCRIPTS_ROOT_DIRECTORY + script));
		}
		
		Connection connection = null;
		
		try {
			connection = DataSourceUtils.getConnection(openSRPDataSource);
			populator.populate(connection);
		}
		finally {
			if (connection != null) {
				DataSourceUtils.releaseConnection(connection, openSRPDataSource);
			}
		}
	}

	private void truncateTables() {
		try {
			for (String tableName : tableNames) {
				Connection connection = DataSourceUtils.getConnection(openSRPDataSource);
				Statement statement = connection.createStatement();
				statement.executeUpdate("TRUNCATE " + tableName +" CASCADE");
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected abstract Set<String> getDatabaseScripts();
}
