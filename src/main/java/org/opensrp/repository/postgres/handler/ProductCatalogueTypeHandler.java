package org.opensrp.repository.postgres.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.smartregister.domain.ProductCatalogue;
import org.opensrp.util.DateTypeConverter;
import org.postgresql.util.PGobject;
import org.smartregister.utils.TaskDateTimeTypeConverter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductCatalogueTypeHandler extends BaseTypeHandler implements TypeHandler<ProductCatalogue> {


	public Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new TaskDateTimeTypeConverter())
			.registerTypeAdapter(LocalDate.class, new DateTypeConverter()).create();

	@Override
	public void setParameter(PreparedStatement ps, int i, ProductCatalogue parameter, JdbcType jdbcType) throws SQLException {
		try {
			if (parameter != null) {
				String jsonString = gson.toJson(parameter);
				PGobject jsonObject = new PGobject();
				jsonObject.setType("jsonb");
				jsonObject.setValue(jsonString);
				ps.setObject(i, jsonObject);
			}
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public ProductCatalogue getResult(ResultSet rs, String columnName) throws SQLException {
		try {
			String jsonString = rs.getString(columnName);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			ProductCatalogue result =  gson.fromJson(jsonString, ProductCatalogue.class);
			return result;
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public ProductCatalogue getResult(ResultSet rs, int columnIndex) throws SQLException {
		try {
			String jsonString = rs.getString(columnIndex);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			ProductCatalogue result = gson.fromJson(jsonString, ProductCatalogue.class);
			return result;
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public ProductCatalogue getResult(CallableStatement cs, int columnIndex) throws SQLException {
		try {
			String jsonString = cs.getString(columnIndex);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			ProductCatalogue result = gson.fromJson(jsonString, ProductCatalogue.class);
			return result;
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}

}
