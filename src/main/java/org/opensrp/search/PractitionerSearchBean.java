package org.opensrp.search;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PractitionerSearchBean {

	public enum OrderByType {
		ASC, DESC
	};

	public enum FieldName {
		id
	};

	private Integer pageNumber = 0;

	private Integer pageSize = 0;

	private OrderByType orderByType = OrderByType.DESC;

	private FieldName orderByFieldName = FieldName.id;

}
