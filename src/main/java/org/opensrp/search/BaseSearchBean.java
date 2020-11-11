package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseSearchBean {

	public enum OrderByType {
		ASC, DESC
	}

	public enum FieldName {
		id
	}

	private Integer pageNumber = 0;

	private Integer pageSize = 0;

	private BaseSearchBean.OrderByType orderByType = BaseSearchBean.OrderByType.DESC;

	private BaseSearchBean.FieldName orderByFieldName = BaseSearchBean.FieldName.id;

}
