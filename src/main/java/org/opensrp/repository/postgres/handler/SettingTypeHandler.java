package org.opensrp.repository.postgres.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.json.JSONArray;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.util.JsonArrayDeserializer;
import org.opensrp.util.JsonArraySerializer;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingTypeHandler extends BaseTypeHandler implements TypeHandler<SettingConfiguration> {

	public SettingTypeHandler() {
		super();
		SimpleModule jsonArrayDeserializerModule = new SimpleModule("JsonArrayDeserializerModule", new Version(0, 0, 0, null));
		jsonArrayDeserializerModule.addDeserializer(JSONArray.class, new JsonArrayDeserializer());
		mapper.registerModule(jsonArrayDeserializerModule);

		SimpleModule jsonArraySerializerModule = new SimpleModule("JsonArraySerializerModule", new Version(0, 0, 0, null));
		jsonArraySerializerModule.addSerializer(JSONArray.class, new JsonArraySerializer());
		mapper.registerModule(jsonArraySerializerModule);
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, SettingConfiguration parameter, JdbcType jdbcType) throws SQLException {
		try {
			if (parameter != null) {
				String jsonString = mapper.writeValueAsString(parameter);
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
	public SettingConfiguration getResult(ResultSet rs, String columnName) throws SQLException {
		try {
			String jsonString = rs.getString(columnName);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return mapper.readValue(jsonString, SettingConfiguration.class);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public SettingConfiguration getResult(ResultSet rs, int columnIndex) throws SQLException {
		try {
			String jsonString = rs.getString(columnIndex);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return mapper.readValue(jsonString, SettingConfiguration.class);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public SettingConfiguration getResult(CallableStatement cs, int columnIndex) throws SQLException {
		try {
			String jsonString = cs.getString(columnIndex);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return mapper.readValue(jsonString, SettingConfiguration.class);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}

}
