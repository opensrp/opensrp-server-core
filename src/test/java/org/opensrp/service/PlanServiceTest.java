package org.opensrp.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.opensrp.domain.PlanDefinition;
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
        planService =  new PlanService(planRepository, practitionerService, practitionerRoleService, organizationService);
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

    public void testGetPlansByServerVersionAndOperationalAreaShouldCallRepositoryGetPlansByServerVersionAndOperationalAreaMethod() {
        when(planRepository.getPlansByServerVersionAndOperationalAreas(anyLong(), any(List.class))).thenReturn(new ArrayList<PlanDefinition>());
        List<String> operationalAreaIds = new ArrayList<>();
        operationalAreaIds.add("operation_area_1");
        planService.getPlansByServerVersionAndOperationalArea(0l, operationalAreaIds);
        verify(planRepository).getPlansByServerVersionAndOperationalAreas(eq(0l), eq(operationalAreaIds));
    }
}
