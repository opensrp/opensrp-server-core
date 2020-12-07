package org.opensrp.util;

import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.search.BaseSearchBean;

import static org.opensrp.repository.postgres.BaseRepositoryImpl.DEFAULT_FETCH_SIZE;

public class RepositoryUtil {

	public static Pair<Integer, Integer> getPageSizeAndOffset(BaseSearchBean baseSearchBean) {

		Integer pageSize;
		Integer offset = 0;
		if (baseSearchBean.getPageSize() == null || baseSearchBean.getPageSize() == 0) {
			pageSize = DEFAULT_FETCH_SIZE;
		} else {
			pageSize = baseSearchBean.getPageSize();
		}

		if (baseSearchBean.getPageNumber() != null && baseSearchBean.getPageNumber() != 0) {
			offset = (baseSearchBean.getPageNumber() - 1) * pageSize;
		}

		return Pair.of(pageSize, offset);
	}
}
