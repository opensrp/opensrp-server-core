package org.opensrp.repository.postgres.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.opensrp.domain.setting.Setting;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Vincent Karuri on 27/04/2020
 */
public class SettingMetadataTypeHandler extends BaseTypeHandler implements TypeHandler<Setting> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Setting parameter, JdbcType jdbcType) throws SQLException {
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
    public Setting getResult(ResultSet rs, String columnName) throws SQLException {
        try {
            String jsonString = rs.getString(columnName);
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            return mapper.readValue(jsonString, Setting.class);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Setting getResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            String jsonString = rs.getString(columnIndex);
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            return mapper.readValue(jsonString, Setting.class);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Setting getResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            String jsonString = cs.getString(columnIndex);
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            return mapper.readValue(jsonString, Setting.class);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
