package org.opensrp.repository.postgres.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.*;
import org.postgresql.util.PGobject;
import org.smartregister.domain.Geometry;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.smartregister.utils.PropertiesConverter.gson;

public class GeometryTypeHandler extends BaseTypeHandler implements TypeHandler<Geometry> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Geometry parameter, JdbcType jdbcType) throws SQLException {
		try {
			if (parameter != null) {
				String jsonString = gson.toJson(parameter);
				PGobject jsonObject = new PGobject();
				jsonObject.setType("jsonb");
				jsonObject.setValue(jsonString);
				ps.setObject(i, jsonObject, jdbcType.TYPE_CODE);
			}
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}

	@Override
	public Geometry getResult(ResultSet rs, String columnName) throws SQLException {
		return getResult(rs.getString(columnName));
	}

	@Override
	public Geometry getResult(ResultSet rs, int columnIndex) throws SQLException {
		return getResult(rs.getString(columnIndex));
	}

	@Override
	public Geometry getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getResult(cs.getString(columnIndex));
	}

	private Geometry getResult(String jsonString) throws SQLException {
		try {
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return gson.fromJson(jsonString, Geometry.class);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
}
