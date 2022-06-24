package org.opensrp.repository.postgres.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.postgresql.util.PGobject;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.PhysicalLocationAndStocks;
import org.smartregister.utils.PropertiesConverter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationAndStockTypeHandler extends BaseTypeHandler implements TypeHandler<PhysicalLocationAndStocks> {

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HHmm")
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();

    @Override
    public void setParameter(PreparedStatement ps, int i, PhysicalLocationAndStocks parameter, JdbcType jdbcType)
            throws SQLException {
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
    public PhysicalLocationAndStocks getResult(ResultSet rs, String columnName) throws SQLException {
        return getResult(rs.getString(columnName));
    }

    @Override
    public PhysicalLocationAndStocks getResult(ResultSet rs, int columnIndex) throws SQLException {
        return getResult(rs.getString(columnIndex));
    }

    @Override
    public PhysicalLocationAndStocks getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getResult(cs.getString(columnIndex));
    }

    private PhysicalLocationAndStocks getResult(String jsonString) throws SQLException {
        try {
            if (StringUtils.isBlank(jsonString)) {
                return null;
            }
            return gson.fromJson(jsonString, PhysicalLocationAndStocks.class);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

}

