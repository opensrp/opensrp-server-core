package org.opensrp.repository.postgres;

import org.junit.BeforeClass;
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

        PlanProcessingStatus status = planProcessingStatusRepository.getByEventId(1l);
        assertEquals(PlanProcessingStatusConstants.INITIAL, status.getStatus().intValue());
        assertEquals(1l, status.getPlanId().longValue());
        assertEquals(1l, status.getTemplateId().longValue());
        assertEquals(1l, status.getEventId().longValue());
    }

    private PlanProcessingStatus initTestProcessingStatus() {
        PlanProcessingStatus planProcessingStatus = new PlanProcessingStatus();
        planProcessingStatus.setStatus(PlanProcessingStatusConstants.COMPLETE);
        planProcessingStatus.setPlanId(1l);
        planProcessingStatus.setTemplateId(1l);
        planProcessingStatus.setEventId(1l);
        return planProcessingStatus;
    }
}
