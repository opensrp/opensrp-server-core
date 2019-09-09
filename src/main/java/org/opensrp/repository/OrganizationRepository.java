package org.opensrp.repository;

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
	 * @param identifier   the organization identifier
	 * @param jurisdiction id of jurisdiction being assigned
	 * @param planId       id of the plan being assigned
	 */
	void assignLocationAndPlan(String identifier, Long jurisdictionId, Long planId);

	/**
	 * Gets the plans and jurisdictions that an organization is assigned to
	 * 
	 * @param organization Identifier
	 */
	List<OrganizationLocation> findAssignedLocations(String organization);

}
