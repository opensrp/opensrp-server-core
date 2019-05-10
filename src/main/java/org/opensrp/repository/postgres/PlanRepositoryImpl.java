package org.opensrp.repository.postgres;

import org.apache.commons.lang.StringUtils;
import org.opensrp.domain.PlanDefinition;
import org.opensrp.domain.postgres.*;
import org.opensrp.repository.PlanRepository;
import org.opensrp.repository.postgres.mapper.PlanMetadataMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

import static org.opensrp.util.Utils.isEmptyList;

/**
 * Created by Vincent Karuri on 02/05/2019
 */

@Repository
public class PlanRepositoryImpl extends BaseRepositoryImpl<PlanDefinition> implements PlanRepository {

    @Autowired
    private CustomPlanMapper planMapper;

    @Autowired
    private PlanMetadataMapper planMetadataMapper;
    
    @Override
    public PlanDefinition get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PlanExample planExample = new PlanExample();
        planExample.createCriteria().andIdEqualTo(id).andDateDeletedIsNull();

        List<Plan> pgPlan = planMapper.selectByExample(planExample);
        List<PlanDefinition> plan = convert(pgPlan);

        return isEmptyList(plan) ? null : plan.get(0);
    }

    @Override
    public void add(PlanDefinition plan) {
        if (getUniqueField(plan) == null) {
            return;
        }

        if (retrievePrimaryKey(plan) != null) { 
            return; // plan already added
        }

        Plan pgPlan = convert(plan);
        if (pgPlan == null) {
            return;
        }

        int rowsAffected = planMapper.insert(pgPlan);
        if (rowsAffected < 1) {
            return;
        }
        insertPlanMetadata(plan);
    }

    @Override
    public void update(PlanDefinition plan) {
        // todo: should we update deleted plans?
        if (getUniqueField(plan) == null) {
            return;
        }

        String id = retrievePrimaryKey(plan);
        if (id == null) {
            return; // plan does not exist
        }

        Plan pgPlan = convert(plan);
        if (pgPlan == null) {
            return;
        }

        int rowsAffected = planMapper.updateByPrimaryKey(pgPlan);
        if (rowsAffected < 1) {
            return;
        }

        updatePlanMetadata(plan);
    }

    @Override
    public List<PlanDefinition> getAll() {
        PlanExample planExample = new PlanExample();
        planExample.createCriteria().andDateDeletedIsNull();
        List<Plan> plans = planMapper.selectMany(planExample, null,0, DEFAULT_FETCH_SIZE);
        return convert(plans);
    }

    @Override
    public void safeRemove(PlanDefinition plan) {
        if (plan == null) {
            return;
        }

        String id = retrievePrimaryKey(plan);
        if (id == null) {
            return;
        }

        Plan pgPlan = convert(plan);
        pgPlan.setDateDeleted(new Date());
        planMapper.updateByPrimaryKey(pgPlan);
    }

    public List<PlanDefinition> getPlansByServerVersionAndOperationalArea(Long serverVersion, String operationalAreaId) {
        PlanExample planExample = new PlanExample();
        planExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion).andDateDeletedIsNull();
        List<Plan> plans = planMapper.selectMany(planExample, operationalAreaId, 0, DEFAULT_FETCH_SIZE);

        return convert(plans);
    }

    @Override
    protected String retrievePrimaryKey(PlanDefinition plan) {
        Object uniqueId = getUniqueField(plan);
        if (uniqueId == null) {
            return null;
        }

        String identifier = uniqueId.toString();
        PlanDefinition pgPlan = get(identifier);

        return pgPlan == null ? null : pgPlan.getIdentifier();
    }

    @Override
    protected Object getUniqueField(PlanDefinition plan) {
        return plan == null ? null : plan.getIdentifier();
    }

    private PlanDefinition convert(Plan pgPlan) {
        if (pgPlan == null || pgPlan.getJson() == null || !(pgPlan.getJson() instanceof PlanDefinition)) {
            return null;
        }
        return (PlanDefinition) pgPlan.getJson();
    }

    private Plan convert(PlanDefinition plan) {
        if (plan == null) {
            return null;
        }
        Plan pgPlan = new Plan();
        pgPlan.setId(plan.getIdentifier());
        pgPlan.setJson(plan);
        pgPlan.setServerVersion(plan.getServerVersion());

        return pgPlan;
    }

    private List<PlanDefinition> convert(List<Plan> pgPlans) {
        List<PlanDefinition> plans = new ArrayList<>();
        if (isEmptyList(pgPlans)) {
            return plans;
        }
        for(Plan pgPlan : pgPlans) {
            plans.add(convert(pgPlan));
        }
        return plans;
    }

    private void insertPlanMetadata(PlanDefinition plan) {
        if (isEmptyList(plan.getJurisdiction())) {
            return;
        }
        for (Jurisdiction jurisdiction : plan.getJurisdiction()) {
           insert(jurisdiction, plan);
        }
    }

    private void insert(Jurisdiction jurisdiction, PlanDefinition plan) {
        PlanMetadata planMetadata = new PlanMetadata();
        planMetadata.setOperationalAreaId(jurisdiction.getCode());
        planMetadata.setPlanId(plan.getIdentifier());
        planMetadataMapper.insert(planMetadata);
    }

    private void updatePlanMetadata(PlanDefinition plan) {
        Set<String> operationalAreas = new HashSet<>();
        for (Jurisdiction jurisdiction : plan.getJurisdiction()) {
            if (retrievePrimaryKey(plan) == null) {
                insert(jurisdiction, plan);
            }
            operationalAreas.add(jurisdiction.getCode());
        }

        // soft delete operational areas that no longer exist in the plan
        PlanMetadataExample planMetadataExample = new PlanMetadataExample();
        planMetadataExample.createCriteria().andPlanIdEqualTo(plan.getIdentifier());
        List <PlanMetadataKey> pgPlans = planMetadataMapper.selectByExample(planMetadataExample);
        Set<PlanMetadataKey> planMetadata = new HashSet<>(pgPlans);
        for (PlanMetadataKey metadata : planMetadata) {
            if (!operationalAreas.contains(metadata.getOperationalAreaId())) {
                PlanMetadataExample metadataExample = new PlanMetadataExample();
                metadataExample.createCriteria().andPlanIdEqualTo(metadata.getPlanId()).andOperationalAreaIdEqualTo(metadata.getOperationalAreaId());
                planMetadataMapper.deleteByExample(metadataExample);
            }
        }
    }
}
