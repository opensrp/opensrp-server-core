package org.opensrp.repository.postgres.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.opensrp.domain.Campaign;
import org.opensrp.util.DateTypeConverter;
import org.opensrp.util.TaskDateTimeTypeConverter;
import org.postgresql.util.PGobject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CampaignTypeHandler extends BaseTypeHandler implements TypeHandler<Campaign> {

	private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new TaskDateTimeTypeConverter())
			.registerTypeAdapter(LocalDate.class, new DateTypeConverter()).create();

	@Override
	public void setParameter(PreparedStatement ps, int i, Campaign parameter, JdbcType jdbcType) throws SQLException {
		try {
			if (parameter != null) {
				String jsonString = gson.toJson(parameter);
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
	public Campaign getResult(ResultSet rs, String columnName) throws SQLException {
		return getResult(rs.getString(columnName));
	}

	@Override
	public Campaign getResult(ResultSet rs, int columnIndex) throws SQLException {
		return getResult(rs.getString(columnIndex));
	}

	@Override
	public Campaign getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return getResult(cs.getString(columnIndex));
	}

	private Campaign getResult(String jsonString) throws SQLException {
		try {
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return gson.fromJson(jsonString, Campaign.class);
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

}
