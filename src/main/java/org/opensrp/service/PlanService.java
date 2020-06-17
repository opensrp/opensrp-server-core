package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Vincent Karuri on 06/05/2019
 */

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.PlanDefinition;
import org.opensrp.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class PlanService {
	
	private PlanRepository planRepository;
	
	private OrganizationService organizationService;
	
	private PractitionerService practitionerService;
	
	@Autowired
	public PlanService(PlanRepository planRepository, OrganizationService organizationService,
	    PractitionerService practitionerService) {
		this.planRepository = planRepository;
		this.organizationService = organizationService;
		this.practitionerService = practitionerService;
	}
	
	public PlanRepository getPlanRepository() {
		return planRepository;
	}
	
	@PreAuthorize("hasRole('PLAN_VIEW')")
	@PostFilter("hasPermission(filterObject, 'PLAN_VIEW')")
	public List<PlanDefinition> getAllPlans() {
		return getPlanRepository().getAll();
	}
	
	@PreAuthorize("(hasPermission(#plan,'PlanDefinition', 'PLAN_CREATE') and "
	        + "hasPermission(#plan,'PlanDefinition', 'PLAN_UPDATE'))")
	public void addOrUpdatePlan(PlanDefinition plan) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		plan.setServerVersion(System.currentTimeMillis());
		if (getPlan(plan.getIdentifier()) != null) {
			updatePlan(plan);
		} else {
			addPlan(plan);
		}
	}
	
	/* @formatter:off */
	@PreAuthorize("hasPermission(#plan,'PlanDefinition', 'PLAN_CREATE')")
	/* @formatter:on */
	public PlanDefinition addPlan(PlanDefinition plan) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		plan.setServerVersion(System.currentTimeMillis());
		getPlanRepository().add(plan);
		
		return plan;
	}
	
	/* @formatter:off */
	@PreAuthorize("hasPermission(#plan,'PlanDefinition', 'PLAN_UPDATE') ")
	/* @formatter:on */
	public PlanDefinition updatePlan(PlanDefinition plan) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		plan.setServerVersion(System.currentTimeMillis());
		getPlanRepository().update(plan);
		
		return plan;
	}
	
	@PreAuthorize("hasRole('PLAN_VIEW')")
	@PostAuthorize("hasPermission(returnObject,'PlanDefinition', 'PLAN_VIEW')")
	public PlanDefinition getPlan(String identifier) {
		return StringUtils.isBlank(identifier) ? null : getPlanRepository().get(identifier);
	}
	
	@PreAuthorize("hasPermission(#operationalAreaIds,'Jurisdiction', 'PLAN_VIEW')")
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
	@PreAuthorize("hasRole('PLAN_VIEW')")
	@PostAuthorize("hasPermission(returnObject,'PlanDefinition', 'PLAN_VIEW')")
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
	@PreAuthorize("hasPermission(#organizationIds,'Organization', 'PLAN_VIEW')")
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
	 * Gets the plan identifiers using organization Ids
	 *
	 * @param organizationIds the list of organization Ids
	 * @return the plan identifiers matching the above
	 */
	@PreAuthorize("hasPermission(#organizationIds,'Organization', 'PLAN_VIEW')")
	public List<String> getPlanIdentifiersByOrganizations(List<Long> organizationIds) {
		
		List<AssignedLocations> assignedPlansAndLocations = organizationService
		        .findAssignedLocationsAndPlans(organizationIds);
		List<String> planIdentifiers = new ArrayList<>();
		for (AssignedLocations assignedLocation : assignedPlansAndLocations) {
			planIdentifiers.add(assignedLocation.getPlanId());
		}
		return planIdentifiers;
	}
	
	/**
	 * Gets the plans that a user has access to according to the plan location assignment that have
	 * server version >= the server version param
	 *
	 * @param username the username of user
	 * @param serverVersion the server version to filter plans with
	 * @return the plans a user has access to
	 */
	@PreAuthorize("hasPermission(#username,'User', 'PLAN_VIEW')")
	public List<PlanDefinition> getPlansByUsernameAndServerVersion(String username, long serverVersion) {
		
		List<Long> organizationIds = practitionerService.getOrganizationIdsByUserName(username);
		if (organizationIds != null) {
			return getPlansByOrganizationsAndServerVersion(organizationIds, serverVersion);
		}
		return null;
	}
	
	/**
	 * Gets the plan identifiers that a user has access to according to the plan location assignment
	 *
	 * @param username the username of user
	 * @return the plans a user has access to
	 */
	@PreAuthorize("hasRole('PLAN_ADMIN') or hasPermission(#username,'User', 'PLAN_VIEW')")
	public List<String> getPlanIdentifiersByUsername(String username) {
		List<Long> organizationIds = practitionerService.getOrganizationIdsByUserName(username);
		if (organizationIds != null) {
			return getPlanIdentifiersByOrganizations(organizationIds);
		}
		return null;
	}
	
	/**
	 * This method searches for plans ordered by serverVersion ascending
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of plans to fetch
	 * @return list of plan identifiers
	 */
	@PreAuthorize("hasRole('PLAN_ADMIN')")
	public List<PlanDefinition> getAllPlans(Long serverVersion, int limit) {
		return getPlanRepository().getAllPlans(serverVersion, limit);
	}
	
	/**
	 * This method searches for all location ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of plans to fetch
	 * @param isDeleted whether to return deleted plan ids
	 * @return a list of location ids and the last server version
	 */
	@PreAuthorize("hasRole('PLAN_ADMIN')")
	public Pair<List<String>, Long> findAllIds(Long serverVersion, int limit, boolean isDeleted) {
		return planRepository.findAllIds(serverVersion, limit, isDeleted);
	}

	/**
	 * Gets the count of plans using organization Ids that have server version >= the server version param
	 *
	 * @param organizationIds the list of organization Ids
	 * @param serverVersion the server version to filter plans with
	 * @return the count plans matching the above
	 */
	public Long countPlansByOrganizationsAndServerVersion(List<Long> organizationIds, long serverVersion) {

		List<AssignedLocations> assignedPlansAndLocations = organizationService
				.findAssignedLocationsAndPlans(organizationIds);
		List<String> planIdentifiers = assignedPlansAndLocations
				.stream()
				.map(a-> a.getPlanId())
				.collect(Collectors.toList());
		return planRepository.countPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion);
	}

	/**
	 * Gets the count of plans that a user has access to according to the plan location assignment that have
	 * server version >= the server version param
	 *
	 * @param username the username of user
	 * @param serverVersion the server version to filter plans with
	 * @return the count of plans a user has access to
	 */
	public Long countPlansByUsernameAndServerVersion(String username, long serverVersion) {

		List<Long> organizationIds = getOrganizationIdsByUserName(username);
		if (organizationIds != null) {
			return countPlansByOrganizationsAndServerVersion(organizationIds, serverVersion);
		}
		return 0l;
	}
}
