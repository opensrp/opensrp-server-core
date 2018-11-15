package org.opensrp.repository.postgres.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.opensrp.domain.PhysicalLocation;
import org.postgresql.util.PGobject;

public class LocationTypeHandler extends BaseTypeHandler implements TypeHandler<PhysicalLocation> {

	@Override
	public void setParameter(PreparedStatement ps, int i, PhysicalLocation parameter, JdbcType jdbcType)
			throws SQLException {
		try {
			if (parameter != null) {
				String jsonString = mapper.writeValueAsString(parameter);
				PGobject jsonObject = new PGobject();
				jsonObject.setType("jsonb");
				jsonObject.setValue(jsonString);
				ps.setObject(i, jsonObject);
			}
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public PhysicalLocation getResult(ResultSet rs, String columnName) throws SQLException {
		return getResult(rs.getString(columnName));
	}

	@Override
	public PhysicalLocation getResult(ResultSet rs, int columnIndex) throws SQLException {
		return getResult(rs.getString(columnIndex));
	}

	@Override
	public PhysicalLocation getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getResult(cs.getString(columnIndex));
	}

	private PhysicalLocation getResult(String jsonString) throws SQLException {
		try {
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return mapper.readValue(jsonString, PhysicalLocation.class);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

}
