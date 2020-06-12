package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.opensrp.domain.AssignedLocations;
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
	
	@Before
	public void setUp() {
		planService = new PlanService(planRepository, practitionerService, practitionerRoleService, organizationService);
	}
	
	@Test
	public void testGetAllPlansShouldCallRepositoryGetAllMethod() {
		planService.getAllPlans();
		verify(planRepository).getAll();
	}
	
	@Test
	public void testAddOrUpdatePlanShouldCallRepositoryAddMethod() {
		when(planRepository.get(anyString())).thenReturn(null);
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("identifier");
		planService.addOrUpdatePlan(plan);
		verify(planRepository).add(eq(plan));
	}
	
	@Test
	public void testAddOrUpdatePlanShouldCallRepositoryUpdateMethod() {
		when(planRepository.get(anyString())).thenReturn(new PlanDefinition());
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("identifier");
		planService.addOrUpdatePlan(plan);
		verify(planRepository).update(eq(plan));
	}
	
	@Test
	public void testAddPlanShouldCallRepositoryAddMethod() {
		when(planRepository.get(anyString())).thenReturn(null);
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("identifier");
		planService.addPlan(plan);
		verify(planRepository).add(eq(plan));
	}
	
	@Test
	public void updatePlanShouldCallRepositoryUpdateMethod() {
		when(planRepository.get(anyString())).thenReturn(new PlanDefinition());
		PlanDefinition plan = new PlanDefinition();
		plan.setIdentifier("identifier");
		planService.updatePlan(plan);
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
		when(planRepository.getPlansByServerVersionAndOperationalAreas(anyLong(), any(List.class)))
		        .thenReturn(new ArrayList<PlanDefinition>());
		List<String> operationalAreaIds = new ArrayList<>();
		operationalAreaIds.add("operation_area_1");
		planService.getPlansByServerVersionAndOperationalArea(0l, operationalAreaIds);
		verify(planRepository).getPlansByServerVersionAndOperationalAreas(eq(0l), eq(operationalAreaIds));
	}
	
	@Test
	public void testGetPlansByIdsReturnOptionalFields() {
		List<String> ids = Arrays.asList("plan1", "plan2");
		List<String> fields = Arrays.asList("name", "action");
		List<PlanDefinition> expected = Collections.singletonList(new PlanDefinition());
		when(planRepository.getPlansByIdsReturnOptionalFields(ids, fields)).thenReturn(expected);
		List<PlanDefinition> plans = planService.getPlansByIdsReturnOptionalFields(ids, fields);
		verify(planRepository).getPlansByIdsReturnOptionalFields(ids, fields);
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
		when(planRepository.getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion))
		        .thenReturn(expected);
		List<PlanDefinition> plans = planService.getPlansByOrganizationsAndServerVersion(organizationIds, serverVersion);
		verify(planRepository).getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion);
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
		
		org.opensrp.domain.Practitioner practitioner = new org.opensrp.domain.Practitioner();
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
		when(planRepository.getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion))
		        .thenReturn(expected);
		when(practitionerService.getPractionerByUsername("janedoe")).thenReturn(practitioner);
		when(practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier())).thenReturn(roles);
		List<PlanDefinition> plans = planService.getPlansByUsernameAndServerVersion("janedoe", serverVersion);
		verify(planRepository).getPlansByIdentifiersAndServerVersion(planIdentifiers, serverVersion);
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
		org.opensrp.domain.Practitioner practitioner = new org.opensrp.domain.Practitioner();
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

		org.opensrp.domain.Practitioner practitioner = new org.opensrp.domain.Practitioner();
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

}
