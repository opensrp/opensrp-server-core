package org.opensrp.repository.postgres;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.common.AllConstants.BaseEntity;
import org.smartregister.domain.BaseDataEntity;

public abstract class BaseRepositoryImpl<T> {

    public static int DEFAULT_FETCH_SIZE = 1000;

    public static int FETCH_SIZE_LIMIT = 5000;

    public static String REVISION_PREFIX = "v";

    public static String SERVER_VERSION = "server_version";

    public static String VERSION = "version";

    public static String ASCENDING = "asc";

    public static String DESCENDING = "desc";

    protected static Logger logger = LogManager.getLogger(BaseRepositoryImpl.class.toString());

    protected abstract Object retrievePrimaryKey(T t);

    protected abstract Object getUniqueField(T t);

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

}
