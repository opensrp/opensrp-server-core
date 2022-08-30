package org.opensrp.repository.postgres.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.opensrp.scheduler.Action;
import org.postgresql.util.PGobject;

public class ActionTypeHandler extends BaseTypeHandler implements TypeHandler<Action> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Action parameter, JdbcType jdbcType) throws SQLException {
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
    public Action getResult(ResultSet rs, String columnName) throws SQLException {
        try {
            String jsonString = rs.getString(columnName);
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            return mapper.readValue(jsonString, Action.class);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Action getResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            String jsonString = rs.getString(columnIndex);
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            return mapper.readValue(jsonString, Action.class);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Action getResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            String jsonString = cs.getString(columnIndex);
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            return mapper.readValue(jsonString, Action.class);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
