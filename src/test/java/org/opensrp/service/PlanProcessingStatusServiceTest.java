package org.opensrp.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.opensrp.domain.postgres.PlanProcessingStatus;
import org.opensrp.repository.PlanProcessingStatusRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlanProcessingStatusServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private PlanProcessingStatusService planProcessingStatusService;

    @Mock
    PlanProcessingStatusRepository planProcessingStatusRepository;

    @Before
    public void setUp() {
        planProcessingStatusService = new PlanProcessingStatusService();
        planProcessingStatusService.setPlanProcessingStatusRepository(planProcessingStatusRepository);
    }

    @Test
    public void testGetPlanProcessingStatusRepositoryReturnsCorrectRepository() {
        assertEquals(planProcessingStatusRepository, planProcessingStatusService.getPlanProcessingStatusRepository());
    }

    @Test
    public void testGetProcessingStatusByEventIdCallsRepositoryMethod() {
        planProcessingStatusService.getProcessingStatusByEventId(1l);
        verify(planProcessingStatusRepository).getByEventId(1l);
    }

    @Test
    public void testAddOrUpdatePlanProcessingStatusCallsUpdateMethod() {
        PlanProcessingStatus planProcessingStatus = initTestPlanProcessingStatus();
        when(planProcessingStatusRepository.getByEventId(4l)).thenReturn(planProcessingStatus);
        planProcessingStatusService.addOrUpdatePlanProcessingStatus(planProcessingStatus);

        verify(planProcessingStatusRepository).getByEventId(4l);
        verify(planProcessingStatusRepository).update(planProcessingStatus);
    }

    @Test
    public void testAddOrUpdatePlanProcessingStatusCallsAddMethod() {
        PlanProcessingStatus planProcessingStatus = initTestPlanProcessingStatus();
        when(planProcessingStatusRepository.getByEventId(4l)).thenReturn(null);
        planProcessingStatusService.addOrUpdatePlanProcessingStatus(planProcessingStatus);

        verify(planProcessingStatusRepository).getByEventId(4l);
        verify(planProcessingStatusRepository).add(planProcessingStatus);
    }

    @Test
    public void testGetProcessingStatusByStatusCallsRepositoryMethod() {
        planProcessingStatusService.getProcessingStatusByStatus(3);
        verify(planProcessingStatusRepository).getByStatus(3);
    }

    @Test
    public void testAddPlanProcessingStatusCallsRepositoryMethod() {
        planProcessingStatusService.addPlanProcessingStatus("event-identifier",0);
        verify(planProcessingStatusRepository).addPlanProcessingStatus("event-identifier",0);
    }

    @Test
    public void testUpdatePlanProcessingStatusCallsRepositoryMethod() {
        PlanProcessingStatus status = initTestPlanProcessingStatus();
        planProcessingStatusService.updatePlanProcessingStatus(
                status,"event-identifier","plan-identifier",1);
        verify(planProcessingStatusRepository).updatePlanProcessingStatus(
                status,"event-identifier","plan-identifier",1);
    }

    private PlanProcessingStatus initTestPlanProcessingStatus() {
        PlanProcessingStatus planProcessingStatus = new PlanProcessingStatus();
        planProcessingStatus.setPlanId(1l);
        planProcessingStatus.setStatus(0);
        planProcessingStatus.setTemplateId(2l);
        planProcessingStatus.setEventId(4l);
        return planProcessingStatus;
    }
}
