package org.opensrp.repository;

import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.Organization;
import org.opensrp.search.OrganizationSearchBean;

import java.util.Date;
import java.util.List;

/**
 * Created by Samuel Githengi on 8/30/19.
 */
public interface OrganizationRepository extends BaseRepository<Organization> {

	/**
	 * Returns all organizations that encompass the given location
	 * Organizations can be fetched by child locations
	 *
	 * @return
	 */
	List<Organization> selectOrganizationsEncompassLocations(String location_id, Date activeDate);

	/**
	 * Assign jurisdiction and or plan to a organization
	 * 
	 * @param organizationId         the organization identifier
	 * @param planIdentifier
	 * @param jurisdictionIdentifier
	 * @param jurisdiction           id of jurisdiction being assigned
	 * @param planId                 id of the plan being assigned
	 * @param fromDate               with effect from
	 * @param toDate                 with effect to
	 */
	void assignLocationAndPlan(Long organizationId, String jurisdictionIdentifier, Long jurisdictionId,
			String planIdentifier, Long planId, Date fromDate, Date toDate);

	/**
	 * Gets the plans and jurisdictions that an organization is assigned to an
	 * organization
	 * 
	 * @param organizationId Id of organization
	 */
	List<AssignedLocations> findAssignedLocations(Long organizationId);

	/**
	 * Gets the plans and jurisdictions that an organization is assigned to a list
	 * organizations
	 * 
	 * @param organizationIds
	 * @return
	 */
	List<AssignedLocations> findAssignedLocations(List<Long> organizationIds);

	/**
	 * @param id
	 * @return
	 */
	Organization getByPrimaryKey(Long id);

	/**
	 * Gets the plans and jurisdictions using filtered by plan identifier
	 *
	 * @param planId the Plan Id
	 *
	 * @return list of assigned locations and plans
	 */
	List<AssignedLocations> findAssignedLocationsByPlanId(Long planId);
	
	List<Organization> findSearchOrganizations(OrganizationSearchBean organizationSearchBean);
	
	int findOrganizationCount(OrganizationSearchBean organizationSearchBean);
}
