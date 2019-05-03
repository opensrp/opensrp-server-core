package org.opensrp.repository.postgres;

import org.junit.Test;
import org.opensrp.domain.PlanDefinition;
import org.opensrp.domain.postgres.Jurisdiction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Vincent Karuri on 03/05/2019
 */
public class PlanRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PlanRepositoryImpl planRepository;

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

    private boolean testIfAllIdsExists(List<PlanDefinition> plans, Set<String> ids) {
        for (PlanDefinition plan : plans) {
            ids.remove(plan.getIdentifier());
        }
        return ids.size() == 0;
    }
}
