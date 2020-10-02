package org.opensrp.repository.postgres;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.BeforeClass;
import org.junit.Test;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Jurisdiction;
import org.opensrp.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
        scripts.add("plan.sql");
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
        assertEquals(6, plans.size());

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
        assertEquals(5, plans.size());

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
        assertEquals(6,plans.size());

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
        assertEquals(6, plans.size());

        planRepository.safeRemove(plan);
        plans = planRepository.getAll();
        assertEquals(5, plans.size());
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

        List<PlanDefinition> plans = planRepository.getPlansByServerVersionAndOperationalAreas(2l, null,false);
        assertEquals(2, plans.size());
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

        List<String> operationalAreaIds = new ArrayList<>();
        operationalAreaIds.add("operation_area_2");
        List<PlanDefinition> plans = planRepository.getPlansByServerVersionAndOperationalAreas(2l, operationalAreaIds,false);
        assertEquals(plans.size(), 2);
        testIfAllIdsExists(plans, ids);
    }


    @Test
    public void testSyncShouldNotGetDeletedOperationalAreas() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
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
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(0l);
        planRepository.update(plan);

        List<String> operationalAreaIds = new ArrayList<>();
        operationalAreaIds.add("operation_area_1");
        List<PlanDefinition> plans = planRepository.getPlansByServerVersionAndOperationalAreas(0l, operationalAreaIds,false);

        assertEquals(plans.size(), 1);
        testIfAllIdsExists(plans, ids);
    }

    @Test
    public void testGetPlansByIdsReturnOptionalFields() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");
        plan.setVersion("v1");
        plan.setName("Focus Investigation");
        plan.setTitle("Plan title");
        plan.setStatus(PlanDefinition.PlanStatus.ACTIVE);

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);

        planRepository.add(plan);

        List<PlanDefinition> plans = planRepository.getAll();
        assertEquals(5, plans.size());

        List<String> fields = new ArrayList<>();
        fields.add("identifier");
        fields.add("name");

        plans = planRepository.getPlansByIdsReturnOptionalFields(Collections.singletonList("identifier_7"), fields,false);
        assertEquals(1, plans.size());
        assertEquals("identifier_7", plans.get(0).getIdentifier());
        assertEquals("Focus Investigation", plans.get(0).getName());
        assertEquals(null, plans.get(0).getVersion());
        assertEquals(null, plans.get(0).getTitle());
        assertEquals(null, plans.get(0).getStatus());

    }
    
    @Test
    public void getGetPlansByIdentifiersAndServerVersion() {
    	PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
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

        List<String> operationalAreaIds = new ArrayList<>();
        operationalAreaIds.add("operation_area_1");
        List<PlanDefinition> plans = planRepository.getPlansByIdentifiersAndServerVersion(Arrays.asList("identifier_7","identifier_8"), 0l,false);

        assertEquals(2,plans.size());
        testIfAllIdsExists(plans, ids);
        
        
        
        plans = planRepository.getPlansByIdentifiersAndServerVersion(Arrays.asList("identifier_7","identifier_8"), 2l,false);
        assertEquals(1,plans.size());
        assertEquals("identifier_8",plans.get(0).getIdentifier());
        
        
        plans = planRepository.getPlansByIdentifiersAndServerVersion(Arrays.asList("identifier_7","identifier_8"), 3l,false);
        assertEquals(0,plans.size());
        
        
        plans = planRepository.getPlansByIdentifiersAndServerVersion(Arrays.asList("identifier_70"), 0l,false);
        assertEquals(0,plans.size());

    }

    @Test
    public void testGetAllIdsShouldGetAllPlanIds() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_6");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1234l);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1235l);
        planRepository.add(plan);

        Pair<List<String>, Long> planIdsObject = planRepository.findAllIds(2l, 1, false);

        List<String> planids = planIdsObject.getLeft();
        assertEquals(1, planids.size());

        assertEquals("identifier_6", planids.get(0));
        assertEquals(1234l, planIdsObject.getRight().longValue());
    }

    @Test
    public void testGetAllIdsShouldGetAllPlanIdsOrderedByServerVersion() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_6");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1234l);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1235l);
        planRepository.add(plan);

        Pair<List<String>, Long> planIdsObject = planRepository.findAllIds(2l, 10, false);

        List<String> planids = planIdsObject.getLeft();
        assertEquals(2, planids.size());

        assertEquals("identifier_6", planids.get(0));
        assertEquals("identifier_7", planids.get(1));
        assertEquals(1235l, planIdsObject.getRight().longValue());
    }

    @Test
    public void testGetAllIdsShouldFilterBetweenFromDateAndToDate() {
        String date1 = "2019-09-25T10:00:00+0300";
        String date2 = "2019-09-29T10:00:00+0300";
        Pair<List<String>, Long> planIdsObject = planRepository.findAllIds(0L, 3, false,
                new DateTime(date1, DateTimeZone.UTC).toDate(), new DateTime(date2,DateTimeZone.UTC).toDate());
        List<String> planids = planIdsObject.getLeft();
        assertEquals(3, planids.size());
    }

    @Test
    public void testGetAllIdsShouldFilterFromDateAsMinimumDate() {
        String date1 = "2019-09-24T10:00:00+0300";
        Pair<List<String>, Long> planIdsObject = planRepository.findAllIds(0L, 3, false,
                new DateTime(date1, DateTimeZone.UTC).toDate(), null);
        List<String> planids = planIdsObject.getLeft();
        assertEquals(3, planids.size());
    }

    @Test
    public void testGetAllIdsShouldFilterToDateAsMaximumDate() {
        String date1 = "2019-09-27T09:00:00+0300";
        Pair<List<String>, Long> planIdsObject = planRepository.findAllIds(0L, 3, false,
                null, new DateTime(date1, DateTimeZone.UTC).toDate());
        List<String> planids = planIdsObject.getLeft();
        assertEquals(1, planids.size());
    }

    @Test
    public void testGetAllIdsShouldGetAllDeletedPlanIds() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_6");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_1");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1234l);
        planRepository.add(plan);

        plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");
        jurisdictions = new ArrayList<>();
        jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
        jurisdictions.add(jurisdiction);
        plan.setJurisdiction(jurisdictions);
        plan.setServerVersion(1235l);
        planRepository.add(plan);

        planRepository.safeRemove(plan);

        Pair<List<String>, Long> planIdsObject = planRepository.findAllIds(0l, 1, true);

        List<String> planids = planIdsObject.getLeft();
        assertEquals(1, planids.size());

        assertEquals("identifier_7", planids.get(0));
        assertEquals(1235l, planIdsObject.getRight().longValue());
    }


    private boolean testIfAllIdsExists(List<PlanDefinition> plans, Set<String> ids) {
        for (PlanDefinition plan : plans) {
            ids.remove(plan.getIdentifier());
        }
        return ids.size() == 0;
    }

    @Test
    public void testCountPlansByIdentifiersAndServerVersion() {
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier_7");

        List<Jurisdiction> jurisdictions = new ArrayList<>();
        Jurisdiction jurisdiction = new Jurisdiction();
        jurisdiction.setCode("operation_area_2");
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

        List<String> operationalAreaIds = new ArrayList<>();
        operationalAreaIds.add("operation_area_1");
        Long plans = planRepository.countPlansByIdentifiersAndServerVersion(Arrays.asList("identifier_7","identifier_8"), 0l);
        assertEquals(2,plans.longValue());

        plans = planRepository.countPlansByIdentifiersAndServerVersion(Arrays.asList("identifier_7","identifier_8"), 2l);
        assertEquals(1,plans.longValue());

        plans = planRepository.countPlansByIdentifiersAndServerVersion(Arrays.asList("identifier_7","identifier_8"), 3l);
        assertEquals(0,plans.longValue());

        plans = planRepository.countPlansByIdentifiersAndServerVersion(Arrays.asList("identifier_70"), 0l);
        assertEquals(0,plans.longValue());

    }

}
