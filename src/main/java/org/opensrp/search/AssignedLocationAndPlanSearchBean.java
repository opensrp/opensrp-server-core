package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignedLocationAndPlanSearchBean extends BaseSearchBean{

	private String planIdentifier;

	private String organizationIdentifier;

	private Long planId;

	private Long organizationId;

	private boolean returnFutureAssignments;

	@Builder
	public AssignedLocationAndPlanSearchBean(Integer pageNumber, Integer pageSize, OrderByType orderByType,
			FieldName orderByFieldName, String planIdentifier, String organizationIdentifier,
			Long planId, Long organizationId, boolean returnFutureAssignments) {
		super(pageNumber, pageSize, orderByType, orderByFieldName);
		this.planIdentifier = planIdentifier;
		this.organizationIdentifier = organizationIdentifier;
		this.planId = planId;
		this.organizationId = organizationId;
		this.returnFutureAssignments = returnFutureAssignments;
	}
}
