/**
 * 
 */
package org.opensrp.repository.postgres.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.domain.postgres.DateRange;
import org.postgresql.util.PGobject;

/**
 * @author Samuel Githengi created on 09/25/20
 */
public class DateRangeTypeHandler extends BaseTypeHandler implements TypeHandler<DateRange> {
	
	private static final Logger logger = LogManager.getLogger(DateRangeTypeHandler.class);
	
	private SimpleDateFormat dateFormat;
	
	public DateRangeTypeHandler() {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Override
	public void setParameter(PreparedStatement ps, int i, DateRange parameter, JdbcType jdbcType) throws SQLException {
		try {
			if (parameter != null) {
				String[] rangeString = new String[] { dateFormat.format(parameter.getFromdate()), "" };
				boolean empty = false;
				if (parameter.getFromdate().equals(parameter.getToDate())) {
					empty = true;
				} else if (parameter.getToDate() != null) {
					rangeString[1] = dateFormat.format(parameter.getToDate());
				}
				String range = empty ? "empty" : String.format("[%s,%s]", rangeString[0], rangeString[1]);
				PGobject jsonObject = new PGobject();
				jsonObject.setType("daterange");
				jsonObject.setValue(range);
				ps.setObject(i, jsonObject);
			}
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
		
	}
	
	private DateRange readResult(String range) {
		if (range != null && !"empty".equals(range)) {
			String[] dates = range.substring(1, range.length() - 1).split(":");
			Date fromDate = null;
			Date toDate = null;
			if (StringUtils.isNotBlank(dates[0])) {
				try {
					fromDate = dateFormat.parse(dates[0]);
				}
				catch (ParseException e) {
					logger.error("Error reading duration", e);
				}
			}
			if (dates.length == 2 && StringUtils.isNotBlank(dates[1])) {
				try {
					toDate = dateFormat.parse(dates[1]);
				}
				catch (ParseException e) {
					logger.error("Error reading duration", e);
				}
			}
			return new DateRange(fromDate, toDate);
		} else {
			return null;
		}
	}
	
	@Override
	public DateRange getResult(ResultSet rs, String columnName) throws SQLException {
		return readResult(rs.getString(columnName));
		
	}
	
	@Override
	public DateRange getResult(ResultSet rs, int columnIndex) throws SQLException {
		return readResult(rs.getString(columnIndex));
	}
	
	@Override
	public DateRange getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return readResult(cs.getString(columnIndex));
	}
	
}
