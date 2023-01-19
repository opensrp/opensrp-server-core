package org.opensrp.migrations;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.ibatis.migration.DataSourceConnectionProvider;
import org.apache.ibatis.migration.FileMigrationLoader;
import org.apache.ibatis.migration.operations.UpOperation;
import org.apache.ibatis.migration.options.DatabaseOperationOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
public class MybatisMigration {

	private static Logger logger = LogManager.getLogger(MybatisMigration.class.getName());

	@Autowired
	private DataSource dataSource;

	private static final String CONFIG_FILE = "mybatis/environments/deployment.properties";

	private static final String SCRIPTS_FOLDER = "mybatis/scripts";

	@PostConstruct
	public void initializeMybatisMigration() {
		logger.info("Running migrations.");
		Resource resource = new ClassPathResource(CONFIG_FILE);
		try {
			Properties props = PropertiesLoaderUtils.loadProperties(resource);
			Gson gson = new GsonBuilder()
					.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
					.create();

			JSONObject dbOperationOptionObject = new JSONObject(gson.toJson(new DatabaseOperationOption()));
			for(String key : dbOperationOptionObject.keySet()){
				if(props.containsKey(key)){
					dbOperationOptionObject.put(key, props.getProperty(key));
				}
			}

			DatabaseOperationOption databaseOperationOption = gson.fromJson(dbOperationOptionObject.toString(), DatabaseOperationOption.class);

			runMigrationOperation(props, databaseOperationOption);

			logger.info("Migration done.");
		}
		catch (IOException e) {
			logger.error(e);
		}
	}

	protected void runMigrationOperation(Properties props, DatabaseOperationOption databaseOperationOption) throws IOException {
		new UpOperation().operate(
				new DataSourceConnectionProvider(dataSource),
				new FileMigrationLoader(new ClassPathResource(SCRIPTS_FOLDER).getFile(), StandardCharsets.UTF_8.toString(),
						props), databaseOperationOption, System.out);
	}

}
