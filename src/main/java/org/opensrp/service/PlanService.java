package org.opensrp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.repository.PlanRepository;
import org.opensrp.search.PlanSearchBean;
import org.smartregister.domain.PlanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PlanService {
	
	private PlanRepository planRepository;
	
	private PractitionerService practitionerService;
	
	private PractitionerRoleService practitionerRoleService;
	
	private OrganizationService organizationService;
	
	private TaskGenerator taskGenerator;
	
	@Autowired
	public PlanService(PlanRepository planRepository, PractitionerService practitionerService,
	    PractitionerRoleService practitionerRoleService, OrganizationService organizationService,
	    TaskGenerator taskGenerator) {
		this.planRepository = planRepository;
		this.practitionerService = practitionerService;
		this.practitionerRoleService = practitionerRoleService;
		this.organizationService = organizationService;
		this.taskGenerator = taskGenerator;
	}
	
	public PlanRepository getPlanRepository() {
		return planRepository;
	}
	
	public List<PlanDefinition> getAllPlans(PlanSearchBean planSearchBean) {
		return getPlanRepository().getAllPlans(planSearchBean);
	}
	
	public void addOrUpdatePlan(PlanDefinition plan, String username) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		plan.setServerVersion(System.currentTimeMillis());
		if (getPlan(plan.getIdentifier()) != null) {
			updatePlan(plan, username);
		} else {
			addPlan(plan, username);
		}
	}
	
	@CachePut(value = "plans", key = "#plan.identifier")
	public PlanDefinition addPlan(PlanDefinition plan, String username) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		getPlanRepository().add(plan);
		taskGenerator.processPlanEvaluation(plan, null, username);
		return plan;
	}
	
	@CachePut(value = "plans", key = "#plan.identifier")
	public PlanDefinition updatePlan(PlanDefinition plan, String username) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		PlanDefinition existing = getPlan(plan.getIdentifier());
		getPlanRepository().update(plan);
		taskGenerator.processPlanEvaluation(plan, existing, username);
		return plan;
	}
	
	@Cacheable(value = "plans", key = "#identifier")
	public PlanDefinition getPlan(String identifier) {
		return StringUtils.isBlank(identifier) ? null : getPlanRepository().get(identifier);
	}
	
	public List<PlanDefinition> getPlansByServerVersionAndOperationalArea(long serverVersion,
	        List<String> operationalAreaIds, boolean experimental) {
		return getPlanRepository().getPlansByServerVersionAndOperationalAreas(serverVersion, operationalAreaIds,
		    experimental);
	}
	
	/**
	 * This method searches for plans using a list of provided plan identifiers and returns a subset of
	 * fields determined by the list of provided fields If no plan identifier(s) are provided the method
	 * returns all available plans If no fields are provided the method returns all the available fields
	 *
	 * @param ids list of plan identifiers
	 * @param fields list of fields to return
	 * @return plan definitions whose identifiers match the provided params
	 */
	public List<PlanDefinition> getPlansByIdsReturnOptionalFields(List<String> ids, List<String> fields,
	        boolean experimental) {
		return getPlanRepository().getPlansByIdsReturnOptionalFields(ids, fields, experimental);
	}
	
	/**
	 * Gets the plans using organization Ids that have server version >= the server version param
	 *
	 * @param organizationIds the list of organization Ids
	 * @param serverVersion the server version to filter plans with
	 * @return the plans matching the above
	 */
	public List<PlanDefinition> getPlansByOrganizationsAndServerVersion(List<Long> organizationIds, long serverVersion,
	        boolean experimental) {
		
		List<AssignedLocations> assignedPlansAndLocations = organizationService
		        .findAssignedLocationsAndPlans(organizationIds);
		List<String> planIdentifiers = new ArrayList<>();
		for (AssignedLocations assignedLocation : assignedPlansAndLocations) {
			planIdentifiers.add(assignedLocation.getPlanId());
		}
		return planRepository.getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion, experimental);
	}
	
	/**
	 * Gets the plan identifiers using organization Ids
	 *
	 * @param organizationIds the list of organization Ids
	 * @return the plan identifiers matching the above
	 */
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
	public List<PlanDefinition> getPlansByUsernameAndServerVersion(String username, long serverVersion,
	        boolean experimental) {
		
		List<Long> organizationIds = getOrganizationIdsByUserName(username);
		if (organizationIds != null) {
			return getPlansByOrganizationsAndServerVersion(organizationIds, serverVersion, experimental);
		}
		return null;
	}
	
	/**
	 * Gets the plan identifiers that a user has access to according to the plan location assignment
	 *
	 * @param username the username of user
	 * @return the plans a user has access to
	 */
	public List<String> getPlanIdentifiersByUsername(String username) {
		List<Long> organizationIds = getOrganizationIdsByUserName(username);
		if (organizationIds != null) {
			return getPlanIdentifiersByOrganizations(organizationIds);
		}
		return null;
	}
	
	/**
	 * Gets the organization ids that a user is assigned to according to the plan location assignment
	 *
	 * @param username the username of user
	 * @return the organization ids a user is assigned to
	 */
	public List<Long> getOrganizationIdsByUserName(String username) {
		org.smartregister.domain.Practitioner practitioner = practitionerService.getPractionerByUsername(username);
		if (practitioner != null) {
			List<PractitionerRole> roles = practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier());
			if (roles.isEmpty())
				return null;
			List<Long> organizationIds = new ArrayList<>();
			for (PractitionerRole role : roles)
				organizationIds.add(role.getOrganizationId());
			return organizationIds;
		}
		
		return null;
	}
	
	/**
	 * This method searches for plans ordered by serverVersion ascending
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of plas to fetch
	 * @return list of plan identifiers
	 */
	public List<PlanDefinition> getAllPlans(Long serverVersion, int limit, boolean experimental) {
		return getPlanRepository().getAllPlans(serverVersion, limit, experimental);
	}

	/**
	 * counts all plans
	 * @param serverVersion
	 * @param experimental
	 * @return
	 */
	public Long countAllPlans(Long serverVersion, boolean experimental) {
		return getPlanRepository().countAllPlans(serverVersion, experimental);
	}

	/**
	 * This method searches for all location ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of plans to fetch
	 * @param isDeleted whether to return deleted plan ids
	 * @return a list of location ids and the last server version
	 */
	public Pair<List<String>, Long> findAllIds(Long serverVersion, int limit, boolean isDeleted) {
		return planRepository.findAllIds(serverVersion, limit, isDeleted);
	}
	
	/**
	 * overloads {@link #findAllIds(Long, int, boolean)} by adding date/time filters
	 * 
	 * @param serverVersion
	 * @param limit
	 * @param isDeleted
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public Pair<List<String>, Long> findAllIds(Long serverVersion, int limit, boolean isDeleted, Date fromDate,
	        Date toDate) {
		return planRepository.findAllIds(serverVersion, limit, isDeleted, fromDate, toDate);
	}
	
	/**
	 * Gets the count of plans using organization Ids that have server version >= the server version
	 * param
	 *
	 * @param organizationIds the list of organization Ids
	 * @param serverVersion the server version to filter plans with
	 * @return the count plans matching the above
	 */
	public Long countPlansByOrganizationsAndServerVersion(List<Long> organizationIds, long serverVersion) {
		
		List<AssignedLocations> assignedPlansAndLocations = organizationService
		        .findAssignedLocationsAndPlans(organizationIds);
		/* @formatter:off */
		List<String> planIdentifiers = assignedPlansAndLocations
				.stream()
				.map(a -> a.getPlanId())
		        .collect(Collectors.toList());
		/* @formatter:on */
		return planRepository.countPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion);
	}
	
	/**
	 * Gets the count of plans that a user has access to according to the plan location assignment that
	 * have server version >= the server version param
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
