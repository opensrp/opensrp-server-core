package org.opensrp.util;

import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.search.BaseSearchBean;

import static org.opensrp.repository.postgres.BaseRepositoryImpl.DEFAULT_FETCH_SIZE;

public class RepositoryUtil {

    public static Pair<Integer, Integer> getPageSizeAndOffset(BaseSearchBean baseSearchBean) {
        return getPageSizeAndOffset(baseSearchBean.getPageNumber(), baseSearchBean.getPageSize());
    }

    public static Pair<Integer, Integer> getPageSizeAndOffset(Integer pageNumber, Integer pageSize) {
        int currentPageSize;
        int offset = 0;

        if (pageSize == null || pageSize == 0) {
            currentPageSize = DEFAULT_FETCH_SIZE;
        } else {
            currentPageSize = pageSize;
        }

        if (pageNumber != null && pageNumber != 0) {
            offset = (pageNumber - 1) * currentPageSize;
        }

        return Pair.of(currentPageSize, offset);
    }
}
