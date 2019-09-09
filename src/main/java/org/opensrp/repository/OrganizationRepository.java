package org.opensrp.repository;

import java.util.Date;
import java.util.List;

import org.opensrp.domain.Organization;
import org.opensrp.domain.postgres.OrganizationLocation;

/**
 * Created by Samuel Githengi on 8/30/19.
 */
public interface OrganizationRepository extends BaseRepository<Organization> {

	/**
	 * Assign jurisdiction and or plan to a organization
	 * 
	 * @param organizationId   the organization identifier
	 * @param jurisdiction id of jurisdiction being assigned
	 * @param planId       id of the plan being assigned
	 * @param fromDate with effect from
	 * @param toDate with effect to
	 */
	void assignLocationAndPlan(Long organizationId, Long jurisdictionId, Long planId,Date fromDate, Date toDate);

	/**
	 * Gets the plans and jurisdictions that an organization is assigned to
	 * 
	 * @param organizationId Id of organization
	 */
	List<OrganizationLocation> findAssignedLocations(Long organizationId);

}
