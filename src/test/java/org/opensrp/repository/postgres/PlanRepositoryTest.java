package org.opensrp.repository.postgres;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensrp.domain.PlanDefinition;
import org.opensrp.domain.postgres.Jurisdiction;
import org.opensrp.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.*;

/**
 * Created by Vincent Karuri on 03/05/2019
 */
public class PlanRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PlanRepository planRepository;

    @BeforeClass
    public static void bootStrap() {
        tableNames.add("core.plan");
        tableNames.add("core.plan_metadata");
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<>();
        return scripts;
    }

    @Test
    public void testAddShouldAddNewPlans() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_1");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_2");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        List<PlanDefinition> plans = planRepository.getAll();
        assertEquals(plans.size(), 2);

        Set<String> ids = new HashSet<>();
        ids.add("identifier_1");
        ids.add("identifier_2");
        assertTrue(testIfAllIdsExists(plans, ids));
    }

    @Test
    public void testGetShouldGetByPlanId() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_3");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_4");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        PlanDefinition result = planRepository.get("identifier_4");
        assertNotNull(result);
        assertEquals(result.getIdentifier(), "identifier_4");
        assertEquals(result.getJurisdiction().get(0).getCode(), "operation_area_2");
    }

    @Test
    public void testGetShouldNotGetDeletedPlans() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_3");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);
        planRepository.safeRemove(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_4");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        List<PlanDefinition> plans = planRepository.getAll();
        assertEquals(plans.size(), 1);

        Set<String> ids = new HashSet<>();
        ids.add("identifier_4");
        assertTrue(testIfAllIdsExists(plans, ids));

        assertNull(planRepository.get("identifier_3"));
    }

    @Test
    public void testUpdateShouldUpdateExistingPlan() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_5");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        PlanDefinition result = planRepository.get("identifier_5");
        assertEquals(result.getIdentifier(), "identifier_5");
        assertEquals(result.getJurisdiction().get(0).getCode(), "operation_area_1");

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_5");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.update(plan);

        result = planRepository.get("identifier_5");
        assertEquals(result.getIdentifier(), "identifier_5");
        assertEquals(result.getJurisdiction().get(0).getCode(), "operation_area_2");
    }

    @Test
    public void testGetAllShouldGetAllPlans() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_6");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        List<PlanDefinition> plans = planRepository.getAll();
        assertEquals(plans.size(), 2);

        Set<String> ids = new HashSet<>();
        ids.add("identifier_6");
        ids.add("identifier_7");
        assertTrue(testIfAllIdsExists(plans, ids));
    }

    @Test
    public void testSafeRemoveShouldMarkPlansAsDelete() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_8");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        planRepository.add(plan);

        List<PlanDefinition> plans = planRepository.getAll();
        assertEquals(plans.size(), 2);

        planRepository.safeRemove(plan);
        plans = planRepository.getAll();
        assertEquals(plans.size(), 1);
        assertEquals(planRepository.getAll().get(0).getIdentifier(), "identifier_7");
    }

    @Test
    public void testSyncPlansByServerVersionAndOperationalAreaShouldReturnAllPlansAtOrBeforeAServerVersion() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1l);
        planRepository.add(plan);

        Set<String> ids = new HashSet<>();

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_8");
        ids.add("identifier_8");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(2l);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_9");
        ids.add("identifier_9");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(3l);
        planRepository.add(plan);

        List<PlanDefinition> plans = planRepository.getPlansByServerVersionAndOperationalArea(2l, null);
        assertEquals(plans.size(), 2);
        testIfAllIdsExists(plans, ids);
    }

    @Test
    public void testGetPlansByServerVersionAndOperationalAreaShouldReturnAllPlansAtOrBeforeAServerVersionAndCorrectOperationalArea() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1l);
        planRepository.add(plan);

        Set<String> ids = new HashSet<>();

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_8");
        ids.add("identifier_8");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(2l);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_9");
        ids.add("identifier_9");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(3l);
        planRepository.add(plan);

        List<PlanDefinition> plans = planRepository.getPlansByServerVersionAndOperationalArea(2l, "operation_area_2");
        assertEquals(plans.size(), 2);
        testIfAllIdsExists(plans, ids);
    }


    @Test
    public void testSyncShouldNotGetDeletedOperationalAreas() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1l);
        planRepository.add(plan);

        Set<String> ids = new HashSet<>();

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_8");
        ids.add("identifier_8");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(2l);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1l);
        planRepository.update(plan);

        List<PlanDefinition> plans = planRepository.getPlansByServerVersionAndOperationalArea(0l, null);
        assertEquals(plans.size(), 1);
        testIfAllIdsExists(plans, ids);
    }

    private boolean testIfAllIdsExists(List<PlanDefinition> plans, Set<String> ids) {
        for (PlanDefinition plan : plans) {
            ids.remove(plan.getIdentifier());
        }
        return ids.size() == 0;
    }
}
