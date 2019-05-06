package org.opensrp.service;

/**
 * Created by Vincent Karuri on 06/05/2019
 */

import org.apache.commons.lang.StringUtils;
import org.opensrp.domain.PlanDefinition;
import org.opensrp.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private PlanRepository planRepository;

    @Autowired
    public void setPlanRepository(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    public List<PlanDefinition> getAllPlans() {
        return planRepository.getAll();
    }

    public void addOrUpdatePlan(PlanDefinition plan) {
        if (StringUtils.isBlank(plan.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }
        plan.setServerVersion(System.currentTimeMillis());
        if (planRepository.get(plan.getIdentifier()) != null) {
            planRepository.update(plan);
        } else {
            planRepository.add(plan);
        }
    }

    public PlanDefinition addPlan(PlanDefinition plan) {
        if (StringUtils.isBlank(plan.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }
        plan.setServerVersion(System.currentTimeMillis());
        planRepository.add(plan);

        return plan;
    }

    public PlanDefinition updatePlan(PlanDefinition plan) {
        if (StringUtils.isBlank(plan.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }
        plan.setServerVersion(System.currentTimeMillis());
        planRepository.update(plan);

        return plan;
    }

    public PlanDefinition getPlan(String identifier) {
        return StringUtils.isBlank(identifier) ? null : planRepository.get(identifier);
    }

    public List<PlanDefinition> getPlansByServerVersionAndOperationalArea(long serverVersion, String operationalAreaId) {
        return planRepository.getPlansByServerVersionAndOperationalArea(serverVersion, operationalAreaId);
    }
}

