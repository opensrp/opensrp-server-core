package org.opensrp.repository.postgres;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opensrp.domain.postgres.PlanProcessingStatus;
import org.opensrp.repository.PlanProcessingStatusRepository;
import org.opensrp.util.constants.PlanProcessingStatusConstants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class PlanProcessingStatusRepositoryTest extends BaseRepositoryTest{

    @Autowired
    private PlanProcessingStatusRepository planProcessingStatusRepository;

    @BeforeClass
    public static void bootStrap() {
        tableNames= Arrays.asList("core.plan_processing_status","core.plan","core.template","core.event");
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<>();
        scripts.add("plan_processing_status.sql");
        return scripts;
    }

    @Test
    public void testGetAllFetchesAllRecords() {
        List<PlanProcessingStatus> statusList = planProcessingStatusRepository.getAll();
        assertNotNull(statusList);
        assertEquals(1, statusList.size());
        PlanProcessingStatus actualStatus = statusList.get(0);
        assertEquals(PlanProcessingStatusConstants.INITIAL, actualStatus.getStatus().intValue());
        assertEquals(1l, actualStatus.getPlanId().longValue());
        assertEquals(1l, actualStatus.getTemplateId().longValue());
        assertEquals(1l, actualStatus.getEventId().longValue());
    }

    @Test
    public void testUpdateShouldUpdateExistingPlanProcessingStatus() {

        PlanProcessingStatus addedStatus = planProcessingStatusRepository.getByPrimaryKey(1l);
        assertEquals(PlanProcessingStatusConstants.INITIAL, addedStatus.getStatus().intValue());
        assertEquals(1l, addedStatus.getPlanId().longValue());
        assertEquals(1l, addedStatus.getTemplateId().longValue());
        assertEquals(1l, addedStatus.getEventId().longValue());

        addedStatus.setStatus(PlanProcessingStatusConstants.PROCESSING);
        planProcessingStatusRepository.update(addedStatus);

        PlanProcessingStatus updatedStatus = planProcessingStatusRepository.getByPrimaryKey(1l);
        assertEquals(PlanProcessingStatusConstants.PROCESSING, updatedStatus.getStatus().intValue());
    }

    @Test
    public void testSafeRemoveDeletesPlanProcessingStatus() {
        PlanProcessingStatus status = planProcessingStatusRepository.getByPrimaryKey(1l);
        assertNotNull(status);

        planProcessingStatusRepository.safeRemove(status);
        PlanProcessingStatus existingStatus = planProcessingStatusRepository.getByPrimaryKey(1l);
        assertNull(existingStatus);
    }

    @Test
    public void testGetByEventIdReturnsCorrectValue() {

        PlanProcessingStatus status = planProcessingStatusRepository.getByEventId(1l);
        assertEquals(PlanProcessingStatusConstants.INITIAL, status.getStatus().intValue());
        assertEquals(1l, status.getPlanId().longValue());
        assertEquals(1l, status.getTemplateId().longValue());
        assertEquals(1l, status.getEventId().longValue());
    }

    @Test
    public void testGetByStatusReturnsCorrectValue() {

        List<PlanProcessingStatus> statusList = planProcessingStatusRepository.getByStatus(PlanProcessingStatusConstants.INITIAL);
        assertEquals(PlanProcessingStatusConstants.INITIAL, statusList.get(0).getStatus().intValue());
        assertEquals(1l, statusList.get(0).getPlanId().longValue());
        assertEquals(1l, statusList.get(0).getTemplateId().longValue());
        assertEquals(1l, statusList.get(0).getEventId().longValue());
    }

    @Test
    @Ignore
    public void testAddPlanProcessingStatus() {
        List<PlanProcessingStatus> statusList = planProcessingStatusRepository.getByStatus(PlanProcessingStatusConstants.COMPLETE);
        assertTrue(statusList.isEmpty());
        planProcessingStatusRepository.addPlanProcessingStatus("1c3f6eac-a765-4423-bbda-c01ed6430ae8",PlanProcessingStatusConstants.COMPLETE);

        List<PlanProcessingStatus> addedStatusList = planProcessingStatusRepository.getByStatus(PlanProcessingStatusConstants.COMPLETE);
        assertEquals(1, addedStatusList.size());
        assertEquals(PlanProcessingStatusConstants.COMPLETE, addedStatusList.get(0).getStatus().intValue());
        assertEquals(2l, addedStatusList.get(0).getEventId().longValue());
    }

    @Test
    public void testUpdatePlanProcessingStatus() {

        PlanProcessingStatus status = planProcessingStatusRepository.getByPrimaryKey(1l);
        assertEquals(PlanProcessingStatusConstants.INITIAL, status.getStatus().intValue());
        assertEquals(1l, status.getPlanId().longValue());
        assertEquals(1l, status.getTemplateId().longValue());
        assertEquals(1l, status.getEventId().longValue());

        planProcessingStatusRepository.updatePlanProcessingStatus(status, "1c3f6eac-a765-4423-bbda-c01ed6430ae8",
                "3450034c-1ba5-556d-8b16-71266397b8b9", PlanProcessingStatusConstants.PROCESSING);

        PlanProcessingStatus updatedStatus = planProcessingStatusRepository.getByPrimaryKey(1l);
        assertEquals(PlanProcessingStatusConstants.PROCESSING, updatedStatus.getStatus().intValue());
        assertEquals(2l, updatedStatus.getPlanId().longValue());
        assertEquals(1l, updatedStatus.getTemplateId().longValue());
        assertEquals(2l, updatedStatus.getEventId().longValue());
    }

    @Test
    public void testAddShouldCreateNewPlanProcessingStatus() {

        List<PlanProcessingStatus> statusList = planProcessingStatusRepository.getByStatus(PlanProcessingStatusConstants.COMPLETE);
        assertTrue(statusList.isEmpty());
        PlanProcessingStatus status = initTestProcessingStatus();
        planProcessingStatusRepository.add(status);

        List<PlanProcessingStatus> addedStatusList = planProcessingStatusRepository.getByStatus(PlanProcessingStatusConstants.COMPLETE);
        assertEquals(1, addedStatusList.size());
        assertEquals(PlanProcessingStatusConstants.COMPLETE, addedStatusList.get(0).getStatus().intValue());
        assertEquals(2l, addedStatusList.get(0).getEventId().longValue());
        assertEquals(2l, addedStatusList.get(0).getPlanId().longValue());
        assertEquals(1l, addedStatusList.get(0).getTemplateId().longValue());

    }

    private PlanProcessingStatus initTestProcessingStatus() {
        PlanProcessingStatus planProcessingStatus = new PlanProcessingStatus();
        planProcessingStatus.setStatus(PlanProcessingStatusConstants.COMPLETE);
        planProcessingStatus.setPlanId(2l);
        planProcessingStatus.setTemplateId(1l);
        planProcessingStatus.setEventId(2l);
        return planProcessingStatus;
    }
}
