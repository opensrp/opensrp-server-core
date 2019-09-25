package org.opensrp.repository.postgres.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;
import org.opensrp.domain.postgres.Manifest;
import org.opensrp.util.DateTimeTypeConverter;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManifestTypeHandler extends BaseTypeHandler implements TypeHandler<Manifest> {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

    @Override
    public void setParameter(PreparedStatement ps, int i, Manifest parameter, JdbcType jdbcType) throws SQLException {
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
    public Manifest getResult(ResultSet rs, String columnName) throws SQLException {
        return getResult(rs.getString(columnName));
    }

    @Override
    public Manifest getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getResult(rs.getString(columnIndex));
    }

    @Override
    public Manifest getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getResult(cs.getString(columnIndex));
    }

    private Manifest getResult(String jsonString) throws SQLException {
        try {
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            return gson.fromJson(jsonString, Manifest.class);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

}
