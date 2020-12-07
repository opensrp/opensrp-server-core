package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AssignedLocationAndPlanSearchBean extends BaseSearchBean{

	private String planIdentifier;

	private String organizationIdentifier;

	private Long planId;

	private Long organizationId;

	private boolean returnFutureAssignments;

}
