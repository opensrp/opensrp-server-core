package org.opensrp.repository;

import java.util.Date;
import java.util.List;

import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.Organization;

/**
 * Created by Samuel Githengi on 8/30/19.
 */
public interface OrganizationRepository extends BaseRepository<Organization> {

	/**
	 * Assign jurisdiction and or plan to a organization
	 * 
	 * @param organizationId   the organization identifier
	 * @param planIdentifier 
	 * @param jurisdictionIdentifier 
	 * @param jurisdiction id of jurisdiction being assigned
	 * @param planId       id of the plan being assigned
	 * @param fromDate with effect from
	 * @param toDate with effect to
	 */
	void assignLocationAndPlan(Long organizationId, String jurisdictionIdentifier, Long jurisdictionId, String planIdentifier, Long planId,Date fromDate, Date toDate);

	/**
	 * Gets the plans and jurisdictions that an organization is assigned to
	 * 
	 * @param organizationId Id of organization
	 */
	List<AssignedLocations> findAssignedLocations(Long organizationId);

}
