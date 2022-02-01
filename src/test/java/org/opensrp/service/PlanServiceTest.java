package org.opensrp.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.PlanTaskCount;
import org.opensrp.domain.TaskCount;
import org.opensrp.search.PlanSearchBean;
import org.opensrp.util.constants.PlanConstants;
import org.smartregister.domain.Action;
import org.smartregister.domain.Event;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.repository.PlanRepository;

/**
 * Created by Vincent Karuri on 06/05/2019
 */
public class PlanServiceTest {
	
	@Rule
	public MockitoRule rule = MockitoJUnit.rule();
	
	private PlanService planService;
	
	@Mock
	private PlanRepository planRepository;
	
	@Mock
	private PractitionerService practitionerService;
	
	@Mock
	private PractitionerRoleService practitionerRoleService;
	
	@Mock
	private OrganizationService organizationService;
	
	@Mock
	private TaskGenerator taskGenerator;

	@Mock
	private TaskService taskService;

	@Mock
	private PhysicalLocationService locationService;

	@Mock
	private ClientService clientService;
	
	private String user="johndoe";
	
	@Before
	public void setUp() {
		planService = new PlanService(planRepository, practitionerService, practitionerRoleService, organizationService,
				taskGenerator, taskService, locationService, clientService);
	}
	
	@Test
	public void testGetAllPlansShouldCallRepositoryGetAllMethod() {
		PlanSearchBean planSearchBean = new PlanSearchBean();
		planService.getAllPlans(planSearchBean);
		verify(planRepository).getAllPlans(planSearchBean);
	}
	
	@Test
	public void testAddOrUpdatePlanShouldCallRepositoryAddMethod() {
		when(planRepository.get(anyString())).thenReturn(null);
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("identifier");
		planService.addOrUpdatePlan(plan,user);
		verify(planRepository).add(eq(plan));
	}
	
	@Test
	public void testAddOrUpdatePlanShouldCallRepositoryUpdateMethod() {
		when(planRepository.get(anyString())).thenReturn(new PlanDefinition());
		doNothing().when(organizationService).unassignLocationAndPlan(anyString());
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("identifier");
		planService.addOrUpdatePlan(plan,user);
		verify(planRepository).update(eq(plan));
	}
	
	@Test
	public void testAddPlanShouldCallRepositoryAddMethod() {
		when(planRepository.get(anyString())).thenReturn(null);
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("identifier");
		planService.addPlan(plan,user);
		verify(planRepository).add(eq(plan));
	}
	
	@Test
	public void updatePlanShouldCallRepositoryUpdateMethod() {
		when(planRepository.get(anyString())).thenReturn(new PlanDefinition());
		doNothing().when(organizationService).unassignLocationAndPlan(anyString());
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("identifier");
		planService.updatePlan(plan,user);
		verify(planRepository).update(eq(plan));
	}
	
	@Test
	public void testGetPlanShouldCallRepositoryGetMethod() {
		when(planRepository.get(anyString())).thenReturn(new PlanDefinition());
		planService.getPlan("identifier");
		verify(planRepository).get(eq("identifier"));
	}
	
	@Test
	public void testGetPlansByServerVersionAndOperationalAreaShouldCallRepositoryGetPlansByServerVersionAndOperationalAreaMethod() {
		when(planRepository.getPlansByServerVersionAndOperationalAreas(anyLong(), any(List.class), anyBoolean()))
		        .thenReturn(new ArrayList<PlanDefinition>());
		List<String> operationalAreaIds = new ArrayList<>();
		operationalAreaIds.add("operation_area_1");
		planService.getPlansByServerVersionAndOperationalArea(0l, operationalAreaIds,false);
		verify(planRepository).getPlansByServerVersionAndOperationalAreas(eq(0l), eq(operationalAreaIds),eq(false));
	}
	
	@Test
	public void testGetPlansByIdsReturnOptionalFields() {
		List<String> ids = Arrays.asList("plan1", "plan2");
		List<String> fields = Arrays.asList("name", "action");
		List<PlanDefinition> expected = Collections.singletonList(new PlanDefinition());
		when(planRepository.getPlansByIdsReturnOptionalFields(ids, fields,false)).thenReturn(expected);
		List<PlanDefinition> plans = planService.getPlansByIdsReturnOptionalFields(ids, fields,false);
		verify(planRepository).getPlansByIdsReturnOptionalFields(ids, fields,false);
		assertEquals(expected, plans);
	}
	
	@Test
	public void testGetPlansByOrganizationsAndServerVersion() {
		
		List<String> planIdentifiers = Arrays.asList("plan1", "plan2");
		List<AssignedLocations> assignedLocations = new ArrayList<AssignedLocations>();
		for (String id : planIdentifiers) {
			AssignedLocations assignedLocation = new AssignedLocations();
			assignedLocation.setPlanId(id);
			assignedLocations.add(assignedLocation);
		}
		List<Long> organizationIds = Arrays.asList(1l, 40l);
		long serverVersion = 1234l;
		List<PlanDefinition> expected = Collections.singletonList(new PlanDefinition());
		when(organizationService.findAssignedLocationsAndPlans(organizationIds))
		        .thenReturn(assignedLocations);
		when(planRepository.getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion, false))
		        .thenReturn(expected);
		List<PlanDefinition> plans = planService.getPlansByOrganizationsAndServerVersion(organizationIds, serverVersion, false);
		verify(planRepository).getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion, false);
		verify(organizationService).findAssignedLocationsAndPlans(organizationIds);
		assertEquals(expected, plans);
	}
	
	
	
	@Test
	public void testGetPlansByUsernameAndServerVersion() {
		
		List<String> planIdentifiers = Arrays.asList("plan1", "plan2");
		List<AssignedLocations> assignedLocations = new ArrayList<AssignedLocations>();
		for (String id : planIdentifiers) {
			AssignedLocations assignedLocation = new AssignedLocations();
			assignedLocation.setPlanId(id);
			assignedLocations.add(assignedLocation);
		}
		List<Long> organizationIds = Arrays.asList(11l, 40l,7667l);
		long serverVersion = 1234l;
		
		org.smartregister.domain.Practitioner practitioner = new org.smartregister.domain.Practitioner();
		practitioner.setIdentifier("practioner-1");
		
		List<PractitionerRole> roles = new ArrayList<>();
		for(long id:organizationIds) {
			PractitionerRole role = new PractitionerRole();
			role.setOrganizationId(id);
			roles.add(role);
		}
		
		List<PlanDefinition> expected = Collections.singletonList(new PlanDefinition());
		when(organizationService.findAssignedLocationsAndPlans(organizationIds))
		        .thenReturn(assignedLocations);
		when(planRepository.getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion, false))
		        .thenReturn(expected);
		when(practitionerService.getPractionerByUsername("janedoe")).thenReturn(practitioner);
		when(practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier())).thenReturn(roles);
		List<PlanDefinition> plans = planService.getPlansByUsernameAndServerVersion("janedoe", serverVersion, false);
		verify(planRepository).getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion, false);
		verify(organizationService).findAssignedLocationsAndPlans(organizationIds);
		assertEquals(expected, plans);
	}

	@Test
	public void testFindAllPlanIds() {
		List<String> expectedPlanIds = new ArrayList<>();
		expectedPlanIds.add("Location-1");
		expectedPlanIds.add("Location-2");
		Pair<List<String>, Long> idsModel = Pair.of(expectedPlanIds, 0l);

		when(planRepository.findAllIds(anyLong(), anyInt(), anyBoolean())).thenReturn(idsModel);
		Pair<List<String>, Long> planIdsObject = planService.findAllIds(0l, 10, false);
		List<String> actualPlanIds = planIdsObject.getLeft();

		verify(planRepository).findAllIds(0l, 10, false);
		assertEquals(2, actualPlanIds.size());
		assertEquals(expectedPlanIds.get(0), actualPlanIds.get(0));
		assertEquals(expectedPlanIds.get(1), actualPlanIds.get(1));
		assertEquals(0l, planIdsObject.getRight().longValue());

	}

	@Test
	public void testGetPlanIdentifiersByOrganizations() {

		List<String> planIdentifiers = Arrays.asList("plan1", "plan2");
		List<AssignedLocations> assignedLocations = new ArrayList<AssignedLocations>();
		for (String id : planIdentifiers) {
			AssignedLocations assignedLocation = new AssignedLocations();
			assignedLocation.setPlanId(id);
			assignedLocations.add(assignedLocation);
		}
		List<Long> organizationIds = Arrays.asList(1l, 40l);
		when(organizationService.findAssignedLocationsAndPlans(organizationIds))
				.thenReturn(assignedLocations);
		List<String> actualPlanIdentifiers = planService.getPlanIdentifiersByOrganizations(organizationIds);

		verify(organizationService).findAssignedLocationsAndPlans(organizationIds);
		assertEquals(planIdentifiers, actualPlanIdentifiers);
	}

	@Test
	public void testGetPlanIdentifiersByUserName() {

		List<String> planIdentifiers = Arrays.asList("plan1", "plan2");
		List<AssignedLocations> assignedLocations = new ArrayList<AssignedLocations>();
		for (String id : planIdentifiers) {
			AssignedLocations assignedLocation = new AssignedLocations();
			assignedLocation.setPlanId(id);
			assignedLocations.add(assignedLocation);
		}
		org.smartregister.domain.Practitioner practitioner = new org.smartregister.domain.Practitioner();
		practitioner.setIdentifier("practioner-1");
		List<Long> organizationIds = Arrays.asList(1l, 40l);
		List<PractitionerRole> roles = new ArrayList<>();
		for(long id:organizationIds) {
			PractitionerRole role = new PractitionerRole();
			role.setOrganizationId(id);
			roles.add(role);
		}
		when(practitionerService.getPractionerByUsername("janedoe")).thenReturn(practitioner);
		when(practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier())).thenReturn(roles);

		when(organizationService.findAssignedLocationsAndPlans(organizationIds))
				.thenReturn(assignedLocations);
		List<String> actualPlanIdentifiers = planService.getPlanIdentifiersByOrganizations(organizationIds);

		verify(organizationService).findAssignedLocationsAndPlans(organizationIds);
		assertEquals(planIdentifiers, actualPlanIdentifiers);
	}

	@Test
	public void testCountPlansByOrganizationsAndServerVersion() {

		List<String> planIdentifiers = Arrays.asList("plan1", "plan2");
		List<AssignedLocations> assignedLocations = new ArrayList<AssignedLocations>();
		for (String id : planIdentifiers) {
			AssignedLocations assignedLocation = new AssignedLocations();
			assignedLocation.setPlanId(id);
			assignedLocations.add(assignedLocation);
		}
		List<Long> organizationIds = Arrays.asList(1l, 40l);
		long serverVersion = 1234l;
		when(organizationService.findAssignedLocationsAndPlans(organizationIds))
				.thenReturn(assignedLocations);
		when(planRepository.countPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion))
				.thenReturn(1l);
		Long plans = planService.countPlansByOrganizationsAndServerVersion(organizationIds, serverVersion);
		verify(planRepository).countPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion);
		verify(organizationService).findAssignedLocationsAndPlans(organizationIds);
		assertEquals(1, plans.longValue());
	}

	@Test
	public void testCountPlansByUsernameAndServerVersion() {

		List<String> planIdentifiers = Arrays.asList("plan1", "plan2");
		List<AssignedLocations> assignedLocations = new ArrayList<AssignedLocations>();
		for (String id : planIdentifiers) {
			AssignedLocations assignedLocation = new AssignedLocations();
			assignedLocation.setPlanId(id);
			assignedLocations.add(assignedLocation);
		}
		List<Long> organizationIds = Arrays.asList(11l, 40l,7667l);
		long serverVersion = 1234l;

		org.smartregister.domain.Practitioner practitioner = new org.smartregister.domain.Practitioner();
		practitioner.setIdentifier("practioner-1");

		List<PractitionerRole> roles = new ArrayList<>();
		for(long id:organizationIds) {
			PractitionerRole role = new PractitionerRole();
			role.setOrganizationId(id);
			roles.add(role);
		}

		when(organizationService.findAssignedLocationsAndPlans(organizationIds))
				.thenReturn(assignedLocations);
		when(planRepository.countPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion))
				.thenReturn(2l);
		when(practitionerService.getPractionerByUsername("janedoe")).thenReturn(practitioner);
		when(practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier())).thenReturn(roles);
		Long plans = planService.countPlansByUsernameAndServerVersion("janedoe", serverVersion);
		verify(planRepository).countPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion);
		verify(organizationService).findAssignedLocationsAndPlans(organizationIds);
		assertEquals(2, plans.longValue());
	}

	@Test
	public void testCountAllShouldInvokeCorrectMethod() {
		doReturn(1l).when(planRepository).countAllPlans(anyLong(), eq(true));
		planService.countAllPlans(0l, true);
		verify(planRepository, times(1)).countAllPlans(eq(0l), eq(true));
	}

	@Test
	public void testGetPlansByIdentifiersAndStatusAndDateEditedWithPlanIdentifersOnly() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("a8b3010c-1ba5-556d-8b16-71266397b8b9");
		List<String> planIdentifiers = Collections.singletonList("a8b3010c-1ba5-556d-8b16-71266397b8b9");
		when(planRepository.getPlansByIdentifiersAndStatusAndDateEdited(planIdentifiers, PlanDefinition.PlanStatus.ACTIVE,null, null)).thenReturn(Collections.singletonList(plan));
		List<PlanDefinition> actualPlans = planService.getPlansByIdentifiersAndStatusAndDateEdited(planIdentifiers, PlanDefinition.PlanStatus.ACTIVE,null, null);
		assertNotNull(actualPlans);
		assertEquals(1, actualPlans.size());
		assertEquals("a8b3010c-1ba5-556d-8b16-71266397b8b9", actualPlans.get(0).getIdentifier());
	}

	@Test
	public void testGetPlanTaskCounts() {
		planService = spy(planService);
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("d2ac9f2b-91a5-4273-bf97-7c78ae154bce");
		List<PlanDefinition> plans = Collections.singletonList(plan);
		PlanTaskCount expectedPlanTaskCount = new PlanTaskCount();
		TaskCount taskCount = new TaskCount();
		taskCount.setActualCount(1l);
		taskCount.setExpectedCount(2l);
		taskCount.setMissingCount(1l);
		expectedPlanTaskCount.setTaskCounts(Collections.singletonList(taskCount));
		when(planRepository.getPlansByIdentifiersAndStatusAndDateEdited(any(), any(), any(), any()))
				.thenReturn(plans);
		when(planService.getPlan(any())).thenReturn(plan);
		when(planService.populatePlanTaskCount(plan)).thenReturn(expectedPlanTaskCount);

		List<PlanTaskCount> actualPlanTaskCounts = planService.getPlanTaskCounts(null, null, null);
		verify(planRepository).getPlansByIdentifiersAndStatusAndDateEdited(any(), any(), any(),any());
		verify(planService).getPlan(any());
		verify(planService).populatePlanTaskCount(any());
		TaskCount actualTaskCount = actualPlanTaskCounts.get(0).getTaskCounts().get(0);
		assertEquals(1l, actualTaskCount.getActualCount());
		assertEquals(1l, actualTaskCount.getMissingCount());
		assertEquals(2l, actualTaskCount.getExpectedCount());
	}

	@Test
	public void testPopulatePlanTaskCountForCaseConfirmation() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("case-confirmation-plan");
		Action action = new Action();
		action.setIdentifier("case_confirmation-action");
		action.setCode(PlanConstants.CASE_CONFIRMATION);
		plan.setActions(Collections.singletonList(action));
		plan.setJurisdiction(Collections.singletonList(new Jurisdiction("location-id1")));
		when(taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.CASE_CONFIRMATION,
				null, false)).thenReturn(0l);

		PlanTaskCount planTaskCount = planService.populatePlanTaskCount(plan);
		assertNotNull(planTaskCount);
		TaskCount actualTaskCount = planTaskCount.getTaskCounts().get(0);
		assertEquals("case-confirmation-plan", planTaskCount.getPlanIdentifier());
		assertEquals(1l, actualTaskCount.getExpectedCount());
		assertEquals(0l,  actualTaskCount.getActualCount());
		assertEquals(1l, actualTaskCount.getMissingCount());

	}

	@Test
	public void testPopulatePlanTaskCountForBCC() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("bcc-plan");
		Action action = new Action();
		action.setIdentifier("bcc-action");
		action.setCode(PlanConstants.BCC);
		plan.setActions(Collections.singletonList(action));
		plan.setJurisdiction(Collections.singletonList(new Jurisdiction("location-id1")));
		when(taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.BCC,
				null, false)).thenReturn(0l);

		PlanTaskCount planTaskCount = planService.populatePlanTaskCount(plan);
		assertNotNull(planTaskCount);
		TaskCount actualTaskCount = planTaskCount.getTaskCounts().get(0);
		assertEquals("bcc-plan", planTaskCount.getPlanIdentifier());
		assertEquals(1l, actualTaskCount.getExpectedCount());
		assertEquals(0l, actualTaskCount.getActualCount());
		assertEquals(1l, actualTaskCount.getMissingCount());

	}

	@Test
	public void testPopulateTaskCountForFamilyRegistration() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("family-reg-plan");
		Action action = new Action();
		action.setIdentifier("family-reg-action");
		action.setCode(PlanConstants.RACD_REGISTER_FAMILY);
		plan.setActions(Collections.singletonList(action));
		plan.setJurisdiction(Collections.singletonList(new Jurisdiction("location-id1")));
		when(taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.RACD_REGISTER_FAMILY,
				null, false)).thenReturn(1l);
		Map<String, String> properties = new HashMap<>();
		properties.put(PlanConstants.TYPE, PlanConstants.RESIDENTIAL_STRUCTURE);
		List<String> structureIds = new ArrayList<>();
		structureIds.add("structure-id-1");
		structureIds.add("structure-id-2");
		structureIds.add("structure-id-3");
		when(locationService.findStructureIdsByProperties(Collections.singletonList("location-id1"),
						properties, Integer.MAX_VALUE)).thenReturn(structureIds);

		when(clientService.countFamiliesByLocation(Collections.singletonList("location-id1"))).thenReturn(1l);

		PlanTaskCount planTaskCount = planService.populatePlanTaskCount(plan);
		assertNotNull(planTaskCount);
		TaskCount actualTaskCount = planTaskCount.getTaskCounts().get(0);
		assertEquals("family-reg-plan", planTaskCount.getPlanIdentifier());
		assertEquals(2l, actualTaskCount.getExpectedCount());
		assertEquals(1l,  actualTaskCount.getActualCount());
		assertEquals(1l, actualTaskCount.getMissingCount());

	}

	@Test
	public void testPopulateTaskCountForBedNetDistribution() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("bednet-distribution-plan");
		Action action = new Action();
		action.setIdentifier("bednet-distribution-action");
		action.setCode(PlanConstants.BEDNET_DISTRIBUTION);
		plan.setActions(Collections.singletonList(action));
		plan.setJurisdiction(Collections.singletonList(new Jurisdiction("location-id1")));
		when(taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.BEDNET_DISTRIBUTION,
				null, false)).thenReturn(1l);
		Map<String, String> properties = new HashMap<>();
		properties.put(PlanConstants.TYPE, PlanConstants.RESIDENTIAL_STRUCTURE);
		List<String> structureIds = new ArrayList<>();
		structureIds.add("structure-id-1");
		structureIds.add("structure-id-2");
		structureIds.add("structure-id-3");
		when(locationService.findStructureIdsByProperties(Collections.singletonList("location-id1"),
				properties, Integer.MAX_VALUE)).thenReturn(structureIds);
		when(clientService.countFamiliesByLocation(Collections.singletonList("location-id1"))).thenReturn(2l);
		PlanTaskCount planTaskCount = planService.populatePlanTaskCount(plan);
		assertNotNull(planTaskCount);
		TaskCount actualTaskCount = planTaskCount.getTaskCounts().get(0);
		assertEquals("bednet-distribution-plan", planTaskCount.getPlanIdentifier());
		assertEquals(2l, actualTaskCount.getExpectedCount());
		assertEquals(1l,  actualTaskCount.getActualCount());
		assertEquals(1l, actualTaskCount.getMissingCount());

	}

	@Test
	public void testPopulateTaskCountForLarvalDipping() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("larval-dipping-plan");
		Action action = new Action();
		action.setIdentifier("larval-dipping-action");
		action.setCode(PlanConstants.LARVAL_DIPPING);
		plan.setActions(Collections.singletonList(action));
		plan.setJurisdiction(Collections.singletonList(new Jurisdiction("location-id1")));
		when(taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.LARVAL_DIPPING,
				null, false)).thenReturn(1l);
		Map<String, String> properties = new HashMap<>();
		properties.put(PlanConstants.TYPE, PlanConstants.LARVAL_DIPPING_SITE);
		List<String> structureIds = new ArrayList<>();
		structureIds.add("structure-id-1");
		structureIds.add("structure-id-2");
		structureIds.add("structure-id-3");
		when(locationService.countStructuresByProperties(Collections.singletonList("location-id1"),
				properties)).thenReturn(3l);

		PlanTaskCount planTaskCount = planService.populatePlanTaskCount(plan);
		assertNotNull(planTaskCount);
		TaskCount actualTaskCount = planTaskCount.getTaskCounts().get(0);
		assertEquals("larval-dipping-plan", planTaskCount.getPlanIdentifier());
		assertEquals(3l, actualTaskCount.getExpectedCount());
		assertEquals(1l,  actualTaskCount.getActualCount());
		assertEquals(2l, actualTaskCount.getMissingCount());

	}

	@Test
	public void testPopulateTaskCountForMosquitoCollection() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("mosquito-collection-plan");
		Action action = new Action();
		action.setIdentifier("mosquito-collection-action");
		action.setCode(PlanConstants.MOSQUITO_COLLECTION);
		plan.setActions(Collections.singletonList(action));
		plan.setJurisdiction(Collections.singletonList(new Jurisdiction("location-id1")));
		when(taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.MOSQUITO_COLLECTION,
				null, false)).thenReturn(1l);
		Map<String, String> properties = new HashMap<>();
		properties.put(PlanConstants.TYPE, PlanConstants.MOSQUITO_COLLECTION_POINT);
		List<String> structureIds = new ArrayList<>();
		structureIds.add("structure-id-1");
		structureIds.add("structure-id-2");
		structureIds.add("structure-id-3");
		when(locationService.countStructuresByProperties(Collections.singletonList("location-id1"),
				properties)).thenReturn(3l);

		PlanTaskCount planTaskCount = planService.populatePlanTaskCount(plan);
		assertNotNull(planTaskCount);
		TaskCount actualTaskCount = planTaskCount.getTaskCounts().get(0);
		assertEquals("mosquito-collection-plan", planTaskCount.getPlanIdentifier());
		assertEquals(3l, actualTaskCount.getExpectedCount());
		assertEquals(1l,  actualTaskCount.getActualCount());
		assertEquals(2l, actualTaskCount.getMissingCount());

	}

	@Test
	public void testPopulateTaskCountForBloodScreening() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("blood-screening-plan");
		Action action = new Action();
		action.setIdentifier("blood-screening-action");
		action.setCode(PlanConstants.BLOOD_SCREENING);
		plan.setActions(Collections.singletonList(action));
		plan.setJurisdiction(Collections.singletonList(new Jurisdiction("location-id1")));
		when(taskService.countTasksByPlanAndCode(plan.getIdentifier(), PlanConstants.BLOOD_SCREENING,
				null, false)).thenReturn(1l);

		when(clientService.countFamilyMembersByLocation(Collections.singletonList("location-id1"),
				5)).thenReturn(3l);

		PlanTaskCount planTaskCount = planService.populatePlanTaskCount(plan);
		assertNotNull(planTaskCount);
		TaskCount actualTaskCount = planTaskCount.getTaskCounts().get(0);
		assertEquals("blood-screening-plan", planTaskCount.getPlanIdentifier());
		assertEquals(3l, actualTaskCount.getExpectedCount());
		assertEquals(1l,  actualTaskCount.getActualCount());
		assertEquals(2l, actualTaskCount.getMissingCount());

	}

	@Test
	public void testValidateCaseDetailsEvent() {
		Event event = initTestCaseDetailsEvent();
		assertTrue(planService.validateCaseDetailsEvent(event));

		event.setDetails(null);
		assertFalse(planService.validateCaseDetailsEvent(event));

		event = initTestCaseDetailsEvent();
		event.getDetails().remove(PlanConstants.CASE_NUMBER);
		assertFalse(planService.validateCaseDetailsEvent(event));

		event = initTestCaseDetailsEvent();
		event.getDetails().remove(PlanConstants.FOCUS_ID);
		assertFalse(planService.validateCaseDetailsEvent(event));

		event = initTestCaseDetailsEvent();
		event.getDetails().remove(PlanConstants.FOCUS_STATUS);
		assertFalse(planService.validateCaseDetailsEvent(event));

		event = initTestCaseDetailsEvent();
		event.getDetails().remove(PlanConstants.FLAG);
		assertFalse(planService.validateCaseDetailsEvent(event));
	}

	@Test
	public void testGetPlanTemplateWhenFocusStateIsA1() {
		Event event = initTestCaseDetailsEvent();
		event.getDetails().put(PlanConstants.FOCUS_STATUS, PlanConstants.A1);
		int planTemplate = planService.getPlanTemplate(event);
		assertEquals(1, planTemplate);
	}

	@Test
	public void testGetPlanTemplateWhenFocusStateIsB1AndCaseClassificationIsLocal() {
		Event event = initTestCaseDetailsEvent();
		event.getDetails().put(PlanConstants.FOCUS_STATUS, PlanConstants.B1);
		event.getDetails().put(PlanConstants.CASE_CLASSIFICATION, PlanConstants.LOCAL);
		planService = spy(planService);
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("bednet-distribution-plan");
		Action action = new Action();
		action.setIdentifier("bednet-distribution-action");
		action.setCode(PlanConstants.BEDNET_DISTRIBUTION);
		plan.setActions(Collections.singletonList(action));
		List<String> operationalAreaIds = Collections.singletonList("location-id-1");
		planService = spy(planService);
		when(planService.getPlansByServerVersionAndOperationalArea(0,
				operationalAreaIds,false))
				.thenReturn(Collections.singletonList(plan));
		int planTemplate = planService.getPlanTemplate(event);
		assertEquals(1, planTemplate);
	}

	@Test
	public void testGetPlanTemplateWhenFocusStateIsB1AndCaseClassificationIsLocalAndIRSHistoricalEvent() {
		Event event = initTestCaseDetailsEvent();
		event.getDetails().put(PlanConstants.FOCUS_STATUS, PlanConstants.B1);
		event.getDetails().put(PlanConstants.CASE_CLASSIFICATION, PlanConstants.LOCAL);
		planService = spy(planService);
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("irs-plan");
		Action action = new Action();
		action.setIdentifier("irs-action");
		action.setCode(PlanConstants.IRS);
		plan.setActions(Collections.singletonList(action));
		List<String> operationalAreaIds = Collections.singletonList("location-id-1");
		planService = spy(planService);
		when(planService.getPlansByServerVersionAndOperationalArea(0,
				operationalAreaIds,false))
				.thenReturn(Collections.singletonList(plan));
		int planTemplate = planService.getPlanTemplate(event);
		assertEquals(1, planTemplate);
	}

	@Test
	public void testGetPlanTemplateWhenFocusStateIsB1AndCaseClassificationIsBF() {
		Event event = initTestCaseDetailsEvent();
		event.getDetails().put(PlanConstants.FOCUS_STATUS, PlanConstants.B1);
		event.getDetails().put(PlanConstants.CASE_CLASSIFICATION, "BF");
		int planTemplate = planService.getPlanTemplate(event);
		assertEquals(2, planTemplate);
	}

	@Test
	public void testGetHistoricalInterventionForBedNetDistribution() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("bednet-distribution-plan");
		Action action = new Action();
		action.setIdentifier("bednet-distribution-action");
		action.setCode(PlanConstants.BEDNET_DISTRIBUTION);
		plan.setActions(Collections.singletonList(action));
		List<String> operationalAreaIds = Collections.singletonList("location-id-1");
		planService = spy(planService);
		when(planService.getPlansByServerVersionAndOperationalArea(0,
				operationalAreaIds,false))
				.thenReturn(Collections.singletonList(plan));

		String historicalIntervention = planService.getHistoricalIntervention(operationalAreaIds);
		assertEquals(PlanConstants.BEDNET_DISTRIBUTION, historicalIntervention);

	}

	@Test
	public void testGetHistoricalInterventionForIRS() {
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("irs-plan");
		Action action = new Action();
		action.setIdentifier("irs-action");
		action.setCode(PlanConstants.IRS);
		plan.setActions(Collections.singletonList(action));
		List<String> operationalAreaIds = Collections.singletonList("location-id-1");
		planService = spy(planService);
		when(planService.getPlansByServerVersionAndOperationalArea(0,
				operationalAreaIds,false))
				.thenReturn(Collections.singletonList(plan));

		String historicalIntervention = planService.getHistoricalIntervention(operationalAreaIds);
		assertEquals(PlanConstants.IRS, historicalIntervention);

	}

	private Event initTestCaseDetailsEvent() {
		Event event = new Event();
		event.setId("case-details-event-id");
		event.setLocationId("location-id");
		Map<String,String> details = new HashMap<>();
		details.put(PlanConstants.CASE_NUMBER, "case-number");
		details.put(PlanConstants.FOCUS_ID, "focus-id");
		details.put(PlanConstants.FOCUS_STATUS, "focus-status");
		details.put(PlanConstants.FLAG, "flag");
		event.setDetails(details);
		return event;
	}

}
