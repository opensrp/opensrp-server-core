package org.opensrp.search;

import lombok.Setter;
import lombok.Getter;
import lombok.Builder;

@Getter
@Setter
public class PractitionerRoleSearchBean extends BaseSearchBean{

	@Builder
	public PractitionerRoleSearchBean(Integer pageNumber, Integer pageSize,
			OrderByType orderByType, FieldName orderByFieldName) {
		super(pageNumber, pageSize, orderByType, orderByFieldName);
	}

	public PractitionerRoleSearchBean() {
	}
}
