package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensrp.domain.PlanDefinition;
import org.opensrp.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Vincent Karuri on 06/05/2019
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class PlanServiceTest {

    @Autowired
    private PlanService planService;

    @Mock
    private PlanRepository planRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        planService.setPlanRepository(planRepository);
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
        when(planRepository.getPlansByServerVersionAndOperationalArea(anyLong(), anyString())).thenReturn(new ArrayList<PlanDefinition>());
        planService.getPlansByServerVersionAndOperationalArea(0l, "operational_area_id");
        verify(planRepository).getPlansByServerVersionAndOperationalArea(eq(0l), eq("operational_area_id"));
    }
}
