package org.opensrp.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.PlanTaskCount;
import org.opensrp.domain.TaskCount;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.repository.PlanRepository;
import org.opensrp.search.PlanSearchBean;
import org.opensrp.util.constants.PlanConstants;
import org.smartregister.domain.Action;
import org.smartregister.domain.Event;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import static org.opensrp.util.constants.PlanConstants.PLAN_TEMPLATE_1;
import static org.opensrp.util.constants.PlanConstants.PLAN_TEMPLATE_2;

@Service
public class PlanService {

	private PlanRepository planRepository;

	private PractitionerService practitionerService;

	private PractitionerRoleService practitionerRoleService;

	private OrganizationService organizationService;

	private TaskService taskService;

	private PhysicalLocationService locationService;

	private ClientService clientService;

	private TaskGenerator taskGenerator;

	@Autowired
	public PlanService(PlanRepository planRepository, PractitionerService practitionerService,
	    PractitionerRoleService practitionerRoleService, OrganizationService organizationService,
	    TaskGenerator taskGenerator, TaskService taskService, PhysicalLocationService locationService,
					   ClientService clientService) {
		this.planRepository = planRepository;
		this.practitionerService = practitionerService;
		this.practitionerRoleService=practitionerRoleService;
		this.organizationService = organizationService;
		this.taskGenerator = taskGenerator;
		this.taskService = taskService;
		this.locationService = locationService;
		this.clientService = clientService;
	}

	public PlanRepository getPlanRepository() {
		return planRepository;
	}
	
	@PreAuthorize("hasRole('PLAN_VIEW')")
	@PostFilter("hasPermission(filterObject, 'PLAN_VIEW')")
	public List<PlanDefinition> getAllPlans(PlanSearchBean planSearchBean) {
		return getPlanRepository().getAllPlans(planSearchBean);
	}
	
	@PreAuthorize("(hasPermission(#plan,'PlanDefinition', 'PLAN_CREATE') and "
	        + "hasPermission(#plan,'PlanDefinition', 'PLAN_UPDATE'))")
	public void addOrUpdatePlan(PlanDefinition plan,String username) {
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
	
	/* @formatter:off */
	@PreAuthorize("hasPermission(#plan,'PlanDefinition', 'PLAN_CREATE')")
	@CachePut(value = "plans", key = "#plan.identifier")
	/* @formatter:on */
	public PlanDefinition addPlan(PlanDefinition plan, String username) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		getPlanRepository().add(plan);
		taskGenerator.processPlanEvaluation(plan, null, username);
		return plan;
	}

	/* @formatter:off */
	@PreAuthorize("hasPermission(#plan,'PlanDefinition', 'PLAN_UPDATE') ")
    @CachePut(value = "plans", key = "#plan.identifier")
	/* @formatter:on */
	public PlanDefinition updatePlan(PlanDefinition plan, String username) {
		return this.updatePlan(plan, username, true);
	}

	@CachePut(value = "plans", key = "#result.identifier")
	public PlanDefinition updatePlan(final PlanDefinition plan, final String username,
			final boolean revokeAssignments) {
		if (StringUtils.isBlank(plan.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		PlanDefinition existing = getPlan(plan.getIdentifier());
		getPlanRepository().update(plan);
		taskGenerator.processPlanEvaluation(plan, existing, username);

		if (revokeAssignments && plan.getStatus() != null
				&& (plan.getStatus().equals(PlanDefinition.PlanStatus.COMPLETED)
				|| plan.getStatus().equals(PlanDefinition.PlanStatus.RETIRED))) {
			organizationService.unassignLocationAndPlan(plan.getIdentifier());
		}
		return plan;
	}

	@PreAuthorize("hasRole('PLAN_VIEW')")
	@PostAuthorize("hasPermission(returnObject,'PlanDefinition', 'PLAN_VIEW')")
	@Cacheable(value = "plans", key = "#identifier")
	public PlanDefinition getPlan(String identifier) {
		return StringUtils.isBlank(identifier) ? null : getPlanRepository().get(identifier);
	}
	
	@PreAuthorize("hasPermission(#operationalAreaIds,'Jurisdiction', 'PLAN_VIEW')")
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
	@PreAuthorize("hasRole('PLAN_VIEW')")
	@PostFilter("hasPermission(filterObject, 'PLAN_VIEW')")
	public List<PlanDefinition> getPlansByIdsReturnOptionalFields(List<String> ids, List<String> fields, boolean experimental) {
		return getPlanRepository().getPlansByIdsReturnOptionalFields(ids, fields, experimental);
	}

	/**
	 * Gets the plans using organization Ids that have server version >= the server version param
	 *
	 * @param organizationIds the list of organization Ids
	 * @param serverVersion the server version to filter plans with
	 * @return the plans matching the above
	 */
	@PreAuthorize("hasPermission(#organizationIds,'Organization', 'PLAN_VIEW')")
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
	@PreAuthorize("hasRole('PLAN_ADMIN') or hasPermission(#username,'User', 'PLAN_VIEW')")
	public List<String> getPlanIdentifiersByUsername(String username) {
		List<Long> organizationIds = practitionerService.getOrganizationIdsByUserName(username);
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
	 * @param limit upper limit on number of plans to fetch
	 * @return list of plan identifiers
	 */
	@PreAuthorize("hasRole('PLAN_ADMIN')")
	@PostFilter("hasPermission(filterObject, 'PLAN_VIEW')")
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
	@PreAuthorize("hasRole('PLAN_ADMIN')")
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
	@PreAuthorize("hasPermission(#organizationIds,'Organization', 'PLAN_VIEW')")
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
	@PreAuthorize("hasPermission(#username,'User', 'PLAN_VIEW')")
	public Long countPlansByUsernameAndServerVersion(String username, long serverVersion) {

		List<Long> organizationIds = getOrganizationIdsByUserName(username);
		if (organizationIds != null) {
			return countPlansByOrganizationsAndServerVersion(organizationIds, serverVersion);
		}
		return 0l;
	}

	public List<PlanTaskCount> getPlanTaskCounts(List<String> planIdentifiers, Date fromDate, Date toDate) {
		List<PlanDefinition> plans = getPlansByIdentifiersAndStatusAndDateEdited(planIdentifiers, PlanDefinition.PlanStatus.ACTIVE, fromDate, toDate);
		List<PlanTaskCount> planTaskCounts = new ArrayList<>();
		if (plans == null || plans.isEmpty()) {
			return planTaskCounts;
		}
		for (PlanDefinition plan : plans) {
			PlanTaskCount planTaskCount = populatePlanTaskCount(getPlan(plan.getIdentifier()));
			if ( planTaskCount !=null){
				planTaskCounts.add(planTaskCount);
			}
		}

		return planTaskCounts;
	}

	public PlanTaskCount populatePlanTaskCount(PlanDefinition plan) {
		if (plan.getActions() == null) {
			return null;
		}
		PlanTaskCount planTaskCount = null;
		boolean planHasMissingTasks = false;
		List<TaskCount> taskCountList = new ArrayList<>();
		for (Action action: plan.getActions()) {
			planTaskCount = new PlanTaskCount();
			List<String> planJurisdictionIds = new ArrayList<>();
			for (Jurisdiction jurisdiction : plan.getJurisdiction()) {
				if (StringUtils.isNotBlank(jurisdiction.getCode())) {
					planJurisdictionIds.add(jurisdiction.getCode());
				}
			}
			switch (action.getCode()) {
				case PlanConstants.CASE_CONFIRMATION:
					// get case confirmation task counts
					planHasMissingTasks = populateCaseConfirmationCounts(plan, taskCountList, planHasMissingTasks);
					break;
				case PlanConstants.BCC:
					// get BCC task counts
					planHasMissingTasks =  populateBCCCounts(plan, taskCountList, planHasMissingTasks);
					break;
				case PlanConstants.RACD_REGISTER_FAMILY:
					// get register family task counts
					planHasMissingTasks = populateFamilyRegistrationCounts(plan, taskCountList,planJurisdictionIds, planHasMissingTasks);
					break;
				case PlanConstants.BLOOD_SCREENING:
					// get blood screening task counts
					planHasMissingTasks = populateBloodScreeningCounts(plan, taskCountList, planJurisdictionIds, planHasMissingTasks);
					break;
				case PlanConstants.BEDNET_DISTRIBUTION:
					// get bednet distribution task counts
					planHasMissingTasks = populateBedNetDistributionCounts(plan, taskCountList,planJurisdictionIds, planHasMissingTasks);
					break;
				case PlanConstants.LARVAL_DIPPING:
					// get larval dipping task counts
					planHasMissingTasks = populateLarvalDippingCounts(plan,taskCountList,planJurisdictionIds, planHasMissingTasks);
					break;
				case PlanConstants.MOSQUITO_COLLECTION:
					// get mosquito collection task counts
					planHasMissingTasks = populateMosquitoCollectionCounts(plan,taskCountList,planJurisdictionIds, planHasMissingTasks);
					break;
				default:
					// do nothing
					break;
			}
		}

		if (!planHasMissingTasks) {
			return null;
		}

		planTaskCount.setTaskCounts(taskCountList);
		planTaskCount.setPlanIdentifier(plan.getIdentifier());
		return planTaskCount;

	}

	private boolean populateCaseConfirmationCounts(PlanDefinition plan, List<TaskCount> taskCountList, boolean planHasMissingTasks) {
		long actualTaskCount = taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.CASE_CONFIRMATION, null, false);
		long missingTaskCount = 1l - actualTaskCount;
		boolean hasMissingTasks = missingTaskCount > 0;
		if (hasMissingTasks) {
			TaskCount taskCount = new TaskCount();
			taskCount.setCode(PlanConstants.CASE_CONFIRMATION);
			taskCount.setActualCount(actualTaskCount);
			taskCount.setExpectedCount(1l);
			taskCount.setMissingCount(missingTaskCount);
			taskCountList.add(taskCount);
		}
		return planHasMissingTasks || hasMissingTasks;
	}

	private boolean populateBCCCounts(PlanDefinition plan, List<TaskCount> taskCountList, boolean planHasMissingTasks) {
		long actualTaskCount = taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.BCC, null,false);
		long missingTaskCount = 1l - actualTaskCount;
		boolean hasMissingTasks = missingTaskCount > 0;
		if (hasMissingTasks) {
			TaskCount taskCount = new TaskCount();
			taskCount.setCode(PlanConstants.BCC);
			taskCount.setActualCount(actualTaskCount);
			taskCount.setExpectedCount(1l);
			taskCount.setMissingCount(missingTaskCount);
			taskCountList.add(taskCount);
		}
		return planHasMissingTasks || hasMissingTasks;
	}

	private boolean populateFamilyRegistrationCounts(PlanDefinition plan, List<TaskCount> taskCountList, List<String> planJurisdictionIds, boolean planHasMissingTasks) {
		long actualTaskCount = taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.RACD_REGISTER_FAMILY, null,false);
		Map<String, String> properties = new HashMap<>();
		properties.put(PlanConstants.TYPE, PlanConstants.RESIDENTIAL_STRUCTURE);
		List<String> residentialStructureIds;
		residentialStructureIds = locationService.findStructureIdsByProperties(planJurisdictionIds, properties, Integer.MAX_VALUE);
		long registeredFamilyCount = clientService.countFamiliesByLocation(planJurisdictionIds);
		long expectedTaskCount = residentialStructureIds.size() - registeredFamilyCount;
		long missingTaskCount = expectedTaskCount - actualTaskCount;
		boolean hasMissingTasks = missingTaskCount > 0;
		if (hasMissingTasks) {
			TaskCount taskCount = new TaskCount();
			taskCount.setCode(PlanConstants.RACD_REGISTER_FAMILY);
			taskCount.setActualCount(actualTaskCount);
			taskCount.setExpectedCount(expectedTaskCount);
			taskCount.setMissingCount(missingTaskCount);
			taskCountList.add(taskCount);
		}

		return planHasMissingTasks || hasMissingTasks;
	}

	private boolean populateBloodScreeningCounts(PlanDefinition plan, List<TaskCount> taskCountList, List<String> planJurisdictionIds, boolean planHasMissingTasks) {
		long actualTaskCount = taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.BLOOD_SCREENING, null,false);
		long expectedTaskCount = clientService.countFamilyMembersByLocation(planJurisdictionIds, 5);
		long missingTaskCount = expectedTaskCount - actualTaskCount;
		boolean hasMissingTasks = missingTaskCount > 0;
		if (hasMissingTasks) {
			TaskCount taskCount = new TaskCount();
			taskCount.setCode(PlanConstants.BLOOD_SCREENING);
			taskCount.setActualCount(actualTaskCount);
			taskCount.setExpectedCount(expectedTaskCount);
			taskCount.setMissingCount(missingTaskCount);
			taskCountList.add(taskCount);
		}
		return planHasMissingTasks || hasMissingTasks;
	}

	private boolean populateBedNetDistributionCounts(PlanDefinition plan, List<TaskCount> taskCountList, List<String> planJurisdictionIds, boolean planHasMissingTasks) {
		long actualTaskCount = taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.BEDNET_DISTRIBUTION, null,false);
		long expectedTaskCount = clientService.countFamiliesByLocation(planJurisdictionIds);
		long missingTaskCount = expectedTaskCount - actualTaskCount;
		boolean hasMissingTasks = missingTaskCount > 0;
		if (hasMissingTasks) {
			TaskCount taskCount = new TaskCount();
			taskCount.setCode(PlanConstants.BEDNET_DISTRIBUTION);
			taskCount.setActualCount(actualTaskCount);
			taskCount.setExpectedCount(expectedTaskCount);
			taskCount.setMissingCount(missingTaskCount);
			taskCountList.add(taskCount);
		}

		return  planHasMissingTasks || hasMissingTasks;
	}

	private boolean populateLarvalDippingCounts(PlanDefinition plan, List<TaskCount> taskCountList, List<String> planJurisdictionIds, boolean planHasMissingTasks) {
		long actualTaskCount = taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.LARVAL_DIPPING, null,false);
		Map<String, String> properties = new HashMap<>();
		properties.put(PlanConstants.TYPE, PlanConstants.LARVAL_DIPPING_SITE);
		long expectedTaskCount = locationService.countStructuresByProperties(planJurisdictionIds,properties);
		long missingTaskCount = expectedTaskCount - actualTaskCount;
		boolean hasMissingTasks = missingTaskCount > 0;
		if (hasMissingTasks) {
			TaskCount taskCount = new TaskCount();
			taskCount.setCode(PlanConstants.LARVAL_DIPPING);
			taskCount.setActualCount(actualTaskCount);
			taskCount.setExpectedCount(expectedTaskCount);
			taskCount.setMissingCount(missingTaskCount);
			taskCountList.add(taskCount);
		}

		return planHasMissingTasks || hasMissingTasks;
	}

	private boolean populateMosquitoCollectionCounts(PlanDefinition plan, List<TaskCount> taskCountList, List<String> planJurisdictionIds, boolean planHasMissingTasks) {
		long actualTaskCount = taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.MOSQUITO_COLLECTION, null,false);
		Map<String, String> properties = new HashMap<>();
		properties.put(PlanConstants.TYPE, PlanConstants.MOSQUITO_COLLECTION_POINT);
		long expectedTaskCount = locationService.countStructuresByProperties(planJurisdictionIds,properties);
		long missingTaskCount = expectedTaskCount - actualTaskCount;
		boolean hasMissingTasks = missingTaskCount > 0;
		if (hasMissingTasks) {
			TaskCount taskCount = new TaskCount();
			taskCount.setCode(PlanConstants.MOSQUITO_COLLECTION);
			taskCount.setActualCount(actualTaskCount);
			taskCount.setExpectedCount(expectedTaskCount);
			taskCount.setMissingCount(missingTaskCount);
			taskCountList.add(taskCount);
		}
		return planHasMissingTasks || hasMissingTasks;
	}


	/** Gets the plans using the plan identifiers filtered by date edited and status
	 * @param planIdentifiers the plan identifiers
	 * @param status status of the plan
	 * @param fromDate lower bound of when the plan was edited
	 * @param toDate upper bound of when the plan was edited
	 * @return plans with the identifiers filtered by date edited and status
	 */
	List<PlanDefinition> getPlansByIdentifiersAndStatusAndDateEdited(List<String> planIdentifiers, PlanDefinition.PlanStatus status,
																	 Date fromDate, Date toDate){
		return planRepository.getPlansByIdentifiersAndStatusAndDateEdited(planIdentifiers, status, fromDate, toDate);
	};

	public Integer getPlanTemplate(Event event) {
		String historicalIntervention = getHistoricalIntervention(Collections.singletonList(event.getLocationId()));
		Integer planTemplateId = null;
		if (event == null || event.getDetails() == null) {
			return null;
		}
		if (PlanConstants.A1.equalsIgnoreCase(event.getDetails().get(PlanConstants.FOCUS_STATUS))
				|| PlanConstants.A2.equalsIgnoreCase(event.getDetails().get(PlanConstants.FOCUS_STATUS))) {
			planTemplateId = PLAN_TEMPLATE_1;
		} else if ((PlanConstants.B1.equalsIgnoreCase(event.getDetails().get(PlanConstants.FOCUS_STATUS))
				|| PlanConstants.B2.equalsIgnoreCase(event.getDetails().get(PlanConstants.FOCUS_STATUS)))
				&& PlanConstants.LOCAL_CASE_CLASSIFICATIONS.contains(event.getDetails().get(PlanConstants.CASE_CLASSIFICATION))
		) {
			if (PlanConstants.BEDNET_DISTRIBUTION.equalsIgnoreCase(historicalIntervention)) {
				planTemplateId = PLAN_TEMPLATE_1;
			} else if (PlanConstants.IRS.equalsIgnoreCase(historicalIntervention)) {
				planTemplateId = PLAN_TEMPLATE_1;
			}
		} else if (PlanConstants.B1.equalsIgnoreCase(event.getDetails().get(PlanConstants.FOCUS_STATUS))) {
			planTemplateId = PLAN_TEMPLATE_2;
		}
		return planTemplateId;
	}

	public String getHistoricalIntervention(List<String> operationalAreaIds) {
		String historicalIntervention = null;
		List<PlanDefinition> planList = getPlansByServerVersionAndOperationalArea(0, operationalAreaIds, false);
		for (PlanDefinition plan: planList ) {
			if(plan.getActions() == null || plan.getActions().isEmpty()){
				continue;
			}
			for (Action action: plan.getActions() ) {
				if (action.getCode() != null) {
					if (PlanConstants.BEDNET_DISTRIBUTION.equalsIgnoreCase(action.getCode())) {
						historicalIntervention = PlanConstants.BEDNET_DISTRIBUTION;
						break;
					} else if (PlanConstants.IRS.equalsIgnoreCase(action.getCode())) {
						historicalIntervention = PlanConstants.IRS;
						break;
					}
				}
			}
		}
		return historicalIntervention != null ? historicalIntervention : PlanConstants.BEDNET_DISTRIBUTION;
	}

	public boolean validateCaseDetailsEvent (Event caseDetailsEvent) {
		return caseDetailsEvent != null
				&& StringUtils.isNotEmpty(caseDetailsEvent.getId())
				&& caseDetailsEvent.getDetails() !=null
				&& StringUtils.isNotEmpty(caseDetailsEvent.getDetails().get(PlanConstants.CASE_NUMBER))
				&& StringUtils.isNotEmpty(caseDetailsEvent.getDetails().get(PlanConstants.FOCUS_ID))
				&& StringUtils.isNotEmpty(caseDetailsEvent.getDetails().get(PlanConstants.FOCUS_STATUS))
				&& StringUtils.isNotEmpty(caseDetailsEvent.getDetails().get(PlanConstants.FLAG));
	}

}
