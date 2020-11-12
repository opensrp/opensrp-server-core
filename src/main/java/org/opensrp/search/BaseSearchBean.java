package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
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

	public BaseSearchBean(Integer pageNumber, Integer pageSize, OrderByType orderByType,
			FieldName orderByFieldName) {
		this.pageNumber = pageNumber != null ? pageNumber : 0;
		this.pageSize = pageSize != null ? pageSize : 0;
		this.orderByType = orderByType != null ? orderByType : BaseSearchBean.OrderByType.DESC;
		this.orderByFieldName = orderByFieldName != null ? orderByFieldName : BaseSearchBean.FieldName.id;
	}
}
