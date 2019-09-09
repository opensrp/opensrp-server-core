/**
 * 
 */
package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Organization;
import org.opensrp.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Samuel Githengi created on 09/09/19
 */
@Service
public class OrganizationService {

	private OrganizationRepository organizationRepository;

	/**
	 * Get all organizations
	 * 
	 * @return all organizations
	 */
	public List<Organization> getAllOrganizations() {
		return organizationRepository.getAll();
	}

	/**
	 * Get the organization that has the identifier
	 * 
	 * @param identifier
	 * @return organization with matching identifier
	 */
	public Organization getOrganization(String identifier) {
		return organizationRepository.get(identifier);
	}

	/**
	 * Adds or Updates an Organization
	 * 
	 * @param organization to add on update
	 */
	public void addOrUpdateOrganization(Organization organization) {
		String identifier = organization.getIdentifier();
		if (StringUtils.isBlank(identifier))
			throw new IllegalArgumentException("Identifier not specified");
		Organization entity = organizationRepository.get(identifier);
		if (entity != null) {
			organizationRepository.update(entity);
		} else {
			organizationRepository.add(entity);
		}

	}

	/**
	 * Adds an Organization
	 * 
	 * @param organization to add
	 */
	public void addOrganization(Organization organization) {
		String identifier = organization.getIdentifier();
		if (StringUtils.isBlank(identifier))
			throw new IllegalArgumentException("Identifier not specified");
		organizationRepository.add(organization);

	}

	/**
	 * Updates an Organization
	 * 
	 * @param organization to update
	 */
	public void updateOrganization(Organization organization) {
		String identifier = organization.getIdentifier();
		if (StringUtils.isBlank(identifier))
			throw new IllegalArgumentException("Identifier not specified");
		organizationRepository.update(organization);

	}

	/**
	 * Assigns the jurisdiction and /or plan to the organization with organizationId
	 * 
	 * @param organizationId the id of the organization
	 * @param jurisdictionId the identifier of the jurisdiction
	 * @param planId         the identifier of the plan
	 */
	public void assignLocationAndPlan(Long organizationId, String jurisdictionId, String planId) {

	}

	/**
	 * 
	 * @param organizationId
	 */
	public void findAssignedLocationsAndPlans(Long organizationId) {

	}
	
	/** Set the Organization repository
	 * @param organizationRepository the organizationRepository to set
	 */
	@Autowired
	public void setOrganizationRepository(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

}
