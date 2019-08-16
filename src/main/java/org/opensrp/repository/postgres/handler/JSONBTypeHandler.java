package org.opensrp.repository.postgres.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.codehaus.jackson.map.ObjectMapper;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JSONBTypeHandler extends BaseTypeHandler<Object>{

	private static final PGobject jsonObject = new PGobject();

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		jsonObject.setType("json");

		try {
			jsonObject.setValue(new ObjectMapper().writeValueAsString(parameter));  //Converting java objects to json strings
		} catch (IOException e) {
			e.printStackTrace();
		}

		ps.setObject(i, jsonObject);
	}

	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);                                 // Return to String
	}

	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getString(columnIndex);
	}

	
	/*@Override
	public void setParameter(PreparedStatement ps, int i, Client parameter, JdbcType jdbcType) throws SQLException {
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
	public Client getResult(ResultSet rs, String columnName) throws SQLException {
		try {
			String jsonString = rs.getString(columnName);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return mapper.readValue(jsonString, Client.class);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public Client getResult(ResultSet rs, int columnIndex) throws SQLException {
		try {
			String jsonString = rs.getString(columnIndex);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return mapper.readValue(jsonString, Client.class);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public Client getResult(CallableStatement cs, int columnIndex) throws SQLException {
		try {
			String jsonString = cs.getString(columnIndex);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return mapper.readValue(jsonString, Client.class);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}*/

	
}
