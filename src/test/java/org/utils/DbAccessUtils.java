package org.utils;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.opensrp.repository.BaseRepository;
import org.smartregister.domain.BaseDataEntity;
import org.springframework.jdbc.datasource.DataSourceUtils;

public final class DbAccessUtils {
	
	private DbAccessUtils() {
		
	}
	
	public static <T extends BaseDataEntity, R extends BaseRepository<T>> void addObjectToRepository(List<T> objectList,
	        R repository) {
		for (T object : objectList) {
			repository.add(object);
		}
	}
	
	public static void truncateTable(String tableName, DataSource openSRPDataSource) {
		try {
			
			Connection connection = DataSourceUtils.getConnection(openSRPDataSource);
			Statement statement = connection.createStatement();
			statement.executeUpdate("TRUNCATE " + tableName + " CASCADE");
			connection.close();
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
