package org.opensrp.repository.postgres;

import java.sql.Types;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.common.AllConstants.BaseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.BaseDataEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public abstract class BaseRepositoryImpl<T> {
	
	public static int DEFAULT_FETCH_SIZE = 1000;
	
	public static int FETCH_SIZE_LIMIT = 5000;
	
	public static String REVISION_PREFIX = "v";
	
	public static String SERVER_VERSION = "server_version";
	
	public static String VERSION = "version";
	
	public static String ASCENDING = "asc";
	
	public static String DESCENDING = "desc";
	
	protected static Logger logger = LoggerFactory.getLogger(BaseRepositoryImpl.class.toString());
	
	@Autowired
	private JdbcTemplate jdbcTemplate;  
	  
	
	protected abstract Object retrievePrimaryKey(T t);
	
	protected abstract Object getUniqueField(T t);
	
	protected  String getSequenceName() {
		return null;
	};
	
	protected String getOrderByClause(String sortBy, String sortOrder) {
		String orderByClause = sortBy == null || sortBy == BaseEntity.SERVER_VERSIOIN ? SERVER_VERSION : sortBy;
		orderByClause += " " + ((sortOrder == null || !sortOrder.toLowerCase().matches("(asc)|(desc)")) ? "asc" : sortOrder);
		return orderByClause;
	}
	
	protected void setRevision(BaseDataEntity entity) {
		if (entity.isNew())
			entity.setRevision(REVISION_PREFIX + 1);
		else if (entity.getRevision().startsWith(REVISION_PREFIX))
			entity.setRevision(
			    REVISION_PREFIX + (Integer.parseInt(entity.getRevision().substring(REVISION_PREFIX.length())) + 1));
		else {
			String[] revision = entity.getRevision().split("-");
			entity.setRevision((Integer.parseInt(revision[0]) + 1) + "-" + revision[1]);
		}
	}
	
	public long getNextServerVersion() {
		return getNextServerVersion(getSequenceName());
	}
	
	public long getNextServerVersion(String sequenceName) {
		if (StringUtils.isBlank(sequenceName)) {
			return System.currentTimeMillis();
		} else {
			return jdbcTemplate.queryForObject(String.format("SELECT nextval('%s')",sequenceName), Long.class);
		}
		
	}
	
}
