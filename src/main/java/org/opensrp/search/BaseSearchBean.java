package org.opensrp.search;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseSearchBean {

	public enum OrderByType {
		ASC, DESC
	}

	public enum FieldName {
		id
	}

	private Integer pageNumber = 0;

	private Integer pageSize = 0;

	private BaseSearchBean.OrderByType orderByType;

	private BaseSearchBean.FieldName orderByFieldName;

}
