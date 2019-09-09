package org.opensrp.repository;

import org.opensrp.domain.Organization;

/**
 * Created by Samuel Githengi on 8/30/19.
 */
public interface OrganizationRepository extends BaseRepository<Organization> {

	/**
	 * @param organizationIdentifier
	 * @param jurisdiction
	 * @param planId
	 */
	void assignLocationAndPlan(String organizationIdentifier, Long jurisdictionId, Long planId);

	/**
	 * 
	 * @param organizationIdentifier
	 */
	void findAssignedLocations(String organizationIdentifier);

}
