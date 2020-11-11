package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
