package org.opensrp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent Karuri on 06/05/2019
 */

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.PlanDefinition;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanService {
	
	private PlanRepository planRepository;
	
	private PractitionerService practitionerService;
	
	private PractitionerRoleService practitionerRoleService;
	
	private OrganizationService organizationService;
	
	@Autowired
	public PlanService(PlanRepository planRepository, PractitionerService practitionerService,
	    PractitionerRoleService practitionerRoleService, OrganizationService organizationService) {
		this.planRepository = planRepository;
		this.practitionerService = practitionerService;
		this.practitionerRoleService = practitionerRoleService;
		this.organizationService = organizationService;
	}

	public PlanRepository getPlanRepository() {
		return planRepository;
	}
	
	public List<PlanDefinition> getAllPlans() {
		return getPlanRepository().getAll();
	}
	
	public void addOrUpdatePlan(PlanDefinition plan) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		plan.setServerVersion(System.currentTimeMillis());
		if (getPlanRepository().get(plan.getIdentifier()) != null) {
			getPlanRepository().update(plan);
		} else {
			getPlanRepository().add(plan);
		}
	}
	
	public PlanDefinition addPlan(PlanDefinition plan) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		plan.setServerVersion(System.currentTimeMillis());
		getPlanRepository().add(plan);
		
		return plan;
	}
	
	public PlanDefinition updatePlan(PlanDefinition plan) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		plan.setServerVersion(System.currentTimeMillis());
		getPlanRepository().update(plan);
		
		return plan;
	}
	
	public PlanDefinition getPlan(String identifier) {
		return StringUtils.isBlank(identifier) ? null : getPlanRepository().get(identifier);
	}
	
	public List<PlanDefinition> getPlansByServerVersionAndOperationalArea(long serverVersion,
	        List<String> operationalAreaIds) {
		return getPlanRepository().getPlansByServerVersionAndOperationalAreas(serverVersion, operationalAreaIds);
	}
	
	/**
	 * This method searches for plans using a list of provided plan identifiers and returns a subset
	 * of fields determined by the list of provided fields If no plan identifier(s) are provided the
	 * method returns all available plans If no fields are provided the method returns all the
	 * available fields
	 * 
	 * @param ids list of plan identifiers
	 * @param fields list of fields to return
	 * @return plan definitions whose identifiers match the provided params
	 */
	public List<PlanDefinition> getPlansByIdsReturnOptionalFields(List<String> ids, List<String> fields) {
		return getPlanRepository().getPlansByIdsReturnOptionalFields(ids, fields);
	}
	
	/**
	 * Gets the plans using organization Ids that have server version >= the server version param
	 * 
	 * @param organizationIds the list of organization Ids
	 * @param serverVersion the server version to filter plans with
	 * @return the plans matching the above
	 */
	public List<PlanDefinition> getPlansByOrganizationsAndServerVersion(List<Long> organizationIds, long serverVersion) {
		
		List<AssignedLocations> assignedPlansAndLocations = organizationService
		        .findAssignedLocationsAndPlans(organizationIds);
		List<String> planIdentifiers = new ArrayList<>();
		for (AssignedLocations assignedLocation : assignedPlansAndLocations) {
			planIdentifiers.add(assignedLocation.getPlanId());
		}
		return planRepository.getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion);
	}
	
	/**
	 * Gets the plans that a user has access to according to the plan location assignment that have
	 * server version >= the server version param
	 * 
	 * @param username the username of user
	 * @param serverVersion the server version to filter plans with
	 * @return the plans a user has access to
	 */
	public List<PlanDefinition> getPlansByUsernameAndServerVersion(String username, long serverVersion) {
		org.opensrp.domain.Practitioner practitioner = practitionerService.getPractionerByUsername(username);
		if (practitioner != null) {
			List<PractitionerRole> roles = practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier());
			if (roles.isEmpty())
				return null;
			List<Long> organizationIds = new ArrayList<>();
			for (PractitionerRole role : roles)
				organizationIds.add(role.getOrganizationId());
			return getPlansByOrganizationsAndServerVersion(organizationIds, serverVersion);
		}
		
		return null;
	}

	/**
	 *  This method searches for plans ordered by serverVersion ascending
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of plas to fetch
	 * @return list of plan identifiers
	 */
	public List<PlanDefinition> getAllPlans(Long serverVersion, int limit) {
		return getPlanRepository().getAllPlans(serverVersion, limit);	}

	/**
	 * This method searches for all location ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of plans to fetch
	 * @param dateDeleted date  on or after which deleted event ids should be returned
	 * @return a list of location ids and the last server version
	 */
	public Pair<List<String>, Long> findAllIds(Long serverVersion, int limit, Date dateDeleted ) {
		return planRepository.findAllIds(serverVersion, limit, dateDeleted);
	}
}
