package org.opensrp.repository.postgres;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.opensrp.TestPostgresInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.utils.DbAccessUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
@ActiveProfiles(profiles = {"jedis"})
public abstract class BaseRepositoryTest extends TestPostgresInstance {
    protected static List<String> tableNames = new ArrayList<>();
    private static String TEST_SCRIPTS_ROOT_DIRECTORY = "test-scripts/";
    @Autowired
    private DataSource openSRPDataSource;

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
        } finally {
            if (connection != null) {
                DataSourceUtils.releaseConnection(connection, openSRPDataSource);
            }
        }
    }

    protected void truncateTables() {
        for (String tableName : tableNames) {
            DbAccessUtils.truncateTable(tableName, openSRPDataSource);
        }
    }

    protected abstract Set<String> getDatabaseScripts();
}
