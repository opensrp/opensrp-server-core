package org.opensrp.repository.postgres.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.postgresql.util.PGobject;
import org.smartregister.domain.Client;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientTypeHandler extends BaseTypeHandler implements TypeHandler<Client> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Client parameter, JdbcType jdbcType) throws SQLException {
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
    public Client getResult(ResultSet rs, String columnName) throws SQLException {
        try {
            String jsonString = rs.getString(columnName);
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            return mapper.readValue(jsonString, Client.class);
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

}
