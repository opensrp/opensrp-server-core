/**
 * 
 */
package org.opensrp.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.Organization;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.OrganizationRepository;
import org.opensrp.repository.PlanRepository;
import org.opensrp.search.OrganizationSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * @author Samuel Githengi created on 09/09/19
 */
@Service
public class OrganizationService {

	private OrganizationRepository organizationRepository;

	private LocationRepository locationRepository;

	private PlanRepository planRepository;

	@Autowired
	public OrganizationService(OrganizationRepository organizationRepository, LocationRepository locationRepository,
	    PlanRepository planRepository) {
		this.organizationRepository = organizationRepository;
		this.locationRepository = locationRepository;
		this.planRepository = planRepository;
	}

	/**
	 * Get all organizations
	 * 
	 * @return all organizations
	 */
	@PreAuthorize("hasRole('ORGANIZATION_VIEW')")
	@PostFilter("hasPermission(filterObject, 'ORGANIZATION_VIEW')")
	public List<Organization> getAllOrganizations() {
		return organizationRepository.getAll();
	}

	/**
	 * Returns all organizations filtered by locations
	 *
	 * @return all organizations
	 */
	@PreAuthorize("hasRole('ORGANIZATION_VIEW')")
	@PostFilter("hasPermission(filterObject, 'ORGANIZATION_VIEW')")
	public List<Organization> selectOrganizationsEncompassLocations(String location_id) {
		return organizationRepository.selectOrganizationsEncompassLocations(location_id, new Date());
	}

	/**
	 * Get the organization that has the identifier
	 * 
	 * @param identifier
	 * @return organization with matching identifier
	 */
	@PreAuthorize("hasRole('ORGANIZATION_VIEW')")
	@PostAuthorize("hasPermission(returnObject, 'ORGANIZATION_VIEW')")
	public Organization getOrganization(String identifier) {
		return organizationRepository.get(identifier);
	}

	/**
	 * Get the organization that has the identifier
	 * 
	 * @param identifier
	 * @return organization with matching identifier
	 */
	@PreAuthorize("hasRole('ORGANIZATION_VIEW')")
	@PostAuthorize("hasPermission(returnObject, ORGANIZATION_VIEW')")
	public Organization getOrganization(Long id) {
		return organizationRepository.getByPrimaryKey(id);
	}

	private void validateIdentifier(Organization organization) {
		validateIdentifier(organization.getIdentifier());
	}

	public void validateIdentifier(String identifier) {
		if (StringUtils.isBlank(identifier))
			throw new IllegalArgumentException("Organization Identifier not specified");
	}

	/**
	 * Adds or Updates an Organization
	 * 
	 * @param organization to add on update
	 */
	@PreAuthorize("hasPermission(#organization,'Organization', 'ORGANIZATION_CREATE') and hasPermission(#organization,'Organization', 'ORGANIZATION_UPDATE')")
	public void addOrUpdateOrganization(Organization organization) {
		validateIdentifier(organization);
		Organization entity = organizationRepository.get(organization.getIdentifier());
		if (entity != null) {
			organizationRepository.update(entity);
		} else {
			organizationRepository.add(organization);
		}

	}

	/**
	 * Adds an Organization
	 * 
	 * @param organization to add
	 */
	@PreAuthorize("hasPermission(#organization,'Organization', 'ORGANIZATION_CREATE')")
	public void addOrganization(Organization organization) {
		validateIdentifier(organization);
		organizationRepository.add(organization);

	}

	/**
	 * Updates an Organization
	 * 
	 * @param organization to update
	 */
	@PreAuthorize("hasPermission(#organization,'Organization', 'ORGANIZATION_UPDATE')")
	public void updateOrganization(Organization organization) {
		validateIdentifier(organization);
		organizationRepository.update(organization);

	}

	/**
	 * Assigns the jurisdiction and /or plan to the organization with organizationId
	 * 
	 * @param organizationId the id of the organization
	 * @param jurisdictionId the identifier of the jurisdiction
	 * @param planId the identifier of the plan
	 * @param fromDate
	 * @param toDate
	 */
	@PreAuthorize("hasPermission(#identifier,'OrganizationLocation','ORGANIZATION_ASSIGN_LOCATION')")
	public void assignLocationAndPlan(String identifier, String jurisdictionId, String planId, Date fromDate, Date toDate) {
		validateIdentifier(identifier);
		if (StringUtils.isBlank(identifier))
			throw new IllegalArgumentException("identifier cannot be null or empty");
		if (StringUtils.isBlank(jurisdictionId) && StringUtils.isBlank(planId))
			throw new IllegalArgumentException("jurisdictionId and planId cannot be null");
		Organization organization = getOrganization(identifier);
		if (organization == null)
			throw new IllegalArgumentException("Organization not found");

		organizationRepository.assignLocationAndPlan(organization.getId(), jurisdictionId,
		    locationRepository.retrievePrimaryKey(jurisdictionId, true, 0), planId, planRepository.retrievePrimaryKey(planId),
		    fromDate == null ? new Date() : fromDate, toDate);

	}

	/**
	 * Gets the locations and Plans assigned to an organization
	 * 
	 * @param identifier the organization identifier
	 * @return the assigned locations and plans
	 */
	@PreAuthorize("hasRole('ORGANIZATION_VIEW_LOCATIONS')")
	@PostFilter("hasPermission(filterObject ,'ORGANIZATION_VIEW_LOCATIONS')")
	public List<AssignedLocations> findAssignedLocationsAndPlans(String identifier) {
		validateIdentifier(identifier);
		Organization organization = getOrganization(identifier);
		if (organization == null)
			throw new IllegalArgumentException("Organization not found");
		return organizationRepository.findAssignedLocations(organization.getId());

	}

	/**
	 * Gets the locations and Plans assigned to a list of organizations
	 * 
	 * @param organizationIds the organization ids
	 * @return the assigned locations and plans
	 */
	@PreAuthorize("hasRole('ORGANIZATION_VIEW_LOCATIONS')")
	@PostFilter("hasPermission(filterObject,'ORGANIZATION_VIEW_LOCATIONS')")
	public List<AssignedLocations> findAssignedLocationsAndPlans(List<Long> organizationIds) {
		return organizationRepository.findAssignedLocations(organizationIds);

	}

	/**
	 * Gets the locations and Plans using the Plan Identifier
	 *
	 * @param planIdentifier the plan identifier
	 * @return the assigned locations and plans
	 */
	@PreAuthorize("hasRole('ORGANIZATION_VIEW_LOCATIONS')") 
	@PostFilter("hasPermission(filterObject,'ORGANIZATION_VIEW_LOCATIONS')")
	public List<AssignedLocations> findAssignedLocationsAndPlansByPlanIdentifier(String planIdentifier) {
		if (StringUtils.isBlank(planIdentifier))
			throw new IllegalArgumentException("PlanIdentifier Identifier not specified");

		Long planId = planRepository.retrievePrimaryKey(planIdentifier);

		if (planId == null)
			throw new IllegalArgumentException("Plan not found");

		return organizationRepository.findAssignedLocationsByPlanId(planId);

	}

	/**
	 * Set the Organization repository
	 * 
	 * @param organizationRepository the organizationRepository to set
	 */
	@Autowired
	public void setOrganizationRepository(OrganizationRepository organizationRepository) {
		this.organizationRepository = organizationRepository;
	}

	/**
	 * set the location repository
	 * 
	 * @param locationRepository the locationRepository to set
	 */
	@Autowired
	/**
	 * @param locationRepository the locationRepository to set
	 */
	public void setLocationRepository(LocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}

	/**
	 * set the plan Repository
	 * 
	 * @param planRepository the planRepository to set
	 */
	@Autowired
	public void setPlanRepository(PlanRepository planRepository) {
		this.planRepository = planRepository;
	}

	@PreAuthorize("hasRole('ORGANIZATION_VIEW')")
	@PostFilter("hasPermission(filterObject, 'ORGANIZATION_VIEW')")
	public List<Organization> getSearchOrganizations(OrganizationSearchBean organizationSearchBean) {
		return organizationRepository.findSearchOrganizations(organizationSearchBean);
	}

	public Integer findOrganizationCount(OrganizationSearchBean organizationSearchBean) {
		return organizationRepository.findOrganizationCount(organizationSearchBean);
	}

}
