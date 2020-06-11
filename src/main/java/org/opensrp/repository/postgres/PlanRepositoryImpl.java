package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.PlanDefinition;
import org.opensrp.domain.postgres.Jurisdiction;
import org.opensrp.domain.postgres.Plan;
import org.opensrp.domain.postgres.PlanExample;
import org.opensrp.domain.postgres.PlanMetadata;
import org.opensrp.domain.postgres.PlanMetadataExample;
import org.opensrp.repository.PlanRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomPlanMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomPlanMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.opensrp.util.Utils.isEmptyList;

/**
 * Created by Vincent Karuri on 02/05/2019
 */

@Repository
public class PlanRepositoryImpl extends BaseRepositoryImpl<PlanDefinition> implements PlanRepository {

    @Autowired
    private CustomPlanMapper planMapper;

    @Autowired
    private CustomPlanMetadataMapper planMetadataMapper;
    
    @Override
    public PlanDefinition get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        PlanExample planExample = new PlanExample();
        planExample.createCriteria().andIdentifierEqualTo(id).andDateDeletedIsNull();

        List<Plan> pgPlan = planMapper.selectByExample(planExample);
        List<PlanDefinition> plan = convert(pgPlan);

        return isEmptyList(plan) ? null : plan.get(0);
    }

    @Override
    public void add(PlanDefinition plan) {
        if (getUniqueField(plan) == null) {
            return;
        }

        Long id=retrievePrimaryKey(plan);
        if (id != null) { 
            return; // plan already added
        }

        Plan pgPlan = convert(plan,id);
        if (pgPlan == null) {
            return;
        }

        int rowsAffected = planMapper.insertSelectiveAndSetId(pgPlan);
        if (rowsAffected < 1) {
            return;
        }
        insertPlanMetadata(plan,pgPlan.getId());
    }

    @Override
    public void update(PlanDefinition plan) {
        // todo: should we update deleted plans?
        if (getUniqueField(plan) == null) {
            return;
        }

        Long id = retrievePrimaryKey(plan);
        if (id == null) {
            return; // plan does not exist
        }

        Plan pgPlan = convert(plan,id);
        if (pgPlan == null) {
            return;
        }

        int rowsAffected = planMapper.updateByPrimaryKey(pgPlan);
        if (rowsAffected < 1) {
            return;
        }

        updatePlanMetadata(plan,pgPlan.getId());
    }

    @Override
    public List<PlanDefinition> getAll() {
        PlanExample planExample = new PlanExample();
        planExample.createCriteria().andDateDeletedIsNull();
        List<Plan> plans = planMapper.selectMany(planExample,0, DEFAULT_FETCH_SIZE);
        return convert(plans);
    }

    @Override
    public void safeRemove(PlanDefinition plan) {
        if (plan == null) {
            return;
        }

        Long id = retrievePrimaryKey(plan);
        if (id == null) {
            return;
        }

        Plan pgPlan = convert(plan,id);
        pgPlan.setDateDeleted(new Date());
        planMapper.updateByPrimaryKey(pgPlan);
    }

    public List<PlanDefinition> getPlansByServerVersionAndOperationalAreas(Long serverVersion, List<String> operationalAreaIds) {
        PlanExample planExample = new PlanExample();
        planExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion).andDateDeletedIsNull();
        List<Plan> plans = planMetadataMapper.selectMany(planExample, operationalAreaIds, 0, DEFAULT_FETCH_SIZE);

        return convert(plans);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanDefinition> getPlansByIdentifiersAndServerVersion(List<String> planIdentifiers,Long serverVersion ) {
        PlanExample planExample = new PlanExample();
        planExample.createCriteria().andIdentifierIn(planIdentifiers).andServerVersionGreaterThanOrEqualTo(serverVersion);
        List<Plan> plans = planMapper.selectMany(planExample, 0, DEFAULT_FETCH_SIZE);

        return convert(plans);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanDefinition> getAllPlans(Long serverVersion, int limit) {
        PlanExample planExample = new PlanExample();
        planExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion).andDateDeletedIsNull();
        planExample.setOrderByClause(getOrderByClause(SERVER_VERSION,  ASCENDING));

        List<Plan> plans = planMapper.selectMany(planExample, 0, limit);

        return convert(plans);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<List<String>, Long> findAllIds(Long serverVersion, int limit, boolean isDeleted) {
        Long lastServerVersion = null;
        PlanExample planExample = new PlanExample();
        PlanExample.Criteria criteria = planExample.createCriteria();
        criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);

        if (isDeleted) {
            criteria.andDateDeletedIsNotNull();
        } else {
            criteria.andDateDeletedIsNull();
        }

        planExample.setOrderByClause(getOrderByClause(SERVER_VERSION,  ASCENDING));
        List<String> planIdentifiers = planMapper.selectManyIds(planExample, 0, limit);

        if (planIdentifiers != null && !planIdentifiers.isEmpty()) {
            planExample = new PlanExample();
            planExample.createCriteria().andIdentifierEqualTo(planIdentifiers.get(planIdentifiers.size() - 1));
            List<Plan> plans = planMapper.selectByExample(planExample);

            lastServerVersion = plans != null && !plans.isEmpty() ? plans.get(0).getServerVersion() : 0;
        }

        return Pair.of(planIdentifiers, lastServerVersion);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanDefinition> getPlansByIdsReturnOptionalFields(List<String> ids, List<String> fields) {
        PlanExample planExample = new PlanExample();
        if (ids != null && !ids.isEmpty()) {
            planExample.createCriteria().andIdentifierIn(ids);
        }
        List<String>  optionalFields = fields != null && fields.size() > 0 ? fields : null;
        List<Plan> plans = planMapper.selectManyReturnOptionalFields(planExample, optionalFields, 0, DEFAULT_FETCH_SIZE);

        return convert(plans);
    }

	@Override
	public Long retrievePrimaryKey(String identifier) {
		if (StringUtils.isBlank(identifier)) {
			return null;
		}
		PlanExample example = new PlanExample();
		example.createCriteria().andIdentifierEqualTo(identifier);
		List<Plan> pgEntity = planMapper.selectByExample(example);

		return pgEntity.isEmpty() ? null : pgEntity.get(0).getId();
	}

    @Override
    protected Long retrievePrimaryKey(PlanDefinition plan) {
        Object uniqueId = getUniqueField(plan);
        if (uniqueId == null) {
            return null;
        }

        return retrievePrimaryKey(uniqueId.toString());
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

    private Plan convert(PlanDefinition plan, Long id) {
        if (plan == null) {
            return null;
        }
        Plan pgPlan = new Plan();
        pgPlan.setIdentifier(plan.getIdentifier());
        pgPlan.setJson(plan);
        pgPlan.setServerVersion(plan.getServerVersion());
        pgPlan.setId(id);
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

    private void insertPlanMetadata(PlanDefinition plan, Long id) {
        if (isEmptyList(plan.getJurisdiction())) {
            return;
        }
        for (Jurisdiction jurisdiction : plan.getJurisdiction()) {
           insert(jurisdiction, plan,id);
        }
    }

    private void insert(Jurisdiction jurisdiction, PlanDefinition plan, Long id) {
        PlanMetadata planMetadata = new PlanMetadata();
        planMetadata.setOperationalAreaId(jurisdiction.getCode());
        planMetadata.setIdentifier(plan.getIdentifier());
        planMetadata.setPlanId(id);
        planMetadataMapper.insert(planMetadata);
    }

    private void updatePlanMetadata(PlanDefinition plan, Long id) {
        Set<String> operationalAreas = new HashSet<>();
        if (retrievePrimaryKey(plan) != null) {
            for (Jurisdiction jurisdiction : plan.getJurisdiction()) {
                PlanMetadataExample planMetadataExample = new PlanMetadataExample();
                planMetadataExample.createCriteria().andOperationalAreaIdEqualTo(jurisdiction.getCode()).andPlanIdEqualTo(id);
                if (planMetadataMapper.selectByExample(planMetadataExample).size() == 0) {
                    insert(jurisdiction, plan,id);
                }
                operationalAreas.add(jurisdiction.getCode());
            }
        }

        // soft delete operational areas that no longer exist in the plan
        PlanMetadataExample planMetadataExample = new PlanMetadataExample();
        planMetadataExample.createCriteria().andPlanIdEqualTo(id);
        List<PlanMetadata> pgPlans = planMetadataMapper.selectByExample(planMetadataExample);
        for (PlanMetadata metadata : pgPlans) {
            if (!operationalAreas.contains(metadata.getOperationalAreaId())) {
                PlanMetadataExample metadataExample = new PlanMetadataExample();
                metadataExample.createCriteria().andPlanIdEqualTo(metadata.getPlanId()).andOperationalAreaIdEqualTo(metadata.getOperationalAreaId());
                planMetadataMapper.deleteByExample(metadataExample);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long countPlansByIdentifiersAndServerVersion(List<String> planIdentifiers, Long serverVersion) {
        PlanExample planExample = new PlanExample();
        planExample.createCriteria().andIdentifierIn(planIdentifiers).andServerVersionGreaterThanOrEqualTo(serverVersion);
        return planMapper.countByExample(planExample);
    }
}
