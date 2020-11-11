package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignedLocationAndPlanSearchBean {

	public enum OrderByType {
		ASC, DESC
	}

	public enum FieldName {
		id
	}

	private Integer pageNumber = 0;

	private Integer pageSize = 0;

	private AssignedLocationAndPlanSearchBean.OrderByType orderByType = AssignedLocationAndPlanSearchBean.OrderByType.DESC;

	private AssignedLocationAndPlanSearchBean.FieldName orderByFieldName = AssignedLocationAndPlanSearchBean.FieldName.id;

	String planIdentifier;

	String organizationIdentifier;

	Long planId;

	Long organizationId;

	boolean returnFutureAssignments;

}
