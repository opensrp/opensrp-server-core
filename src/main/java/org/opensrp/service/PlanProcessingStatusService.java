package org.opensrp.service;

import org.opensrp.domain.postgres.PlanProcessingStatus;
import org.opensrp.repository.PlanProcessingStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlanProcessingStatusService {

    private PlanProcessingStatusRepository planProcessingStatusRepository;

    @Autowired
    public void setPlanProcessingStatusRepository(PlanProcessingStatusRepository planProcessingStatusRepository) {
        this.planProcessingStatusRepository = planProcessingStatusRepository;
    }

    public PlanProcessingStatusRepository getPlanProcessingStatusRepository() {
        return planProcessingStatusRepository;
    }

    public PlanProcessingStatus getTemplateByTemplateId(Long eventId) {
        return getPlanProcessingStatusRepository().getByEventId(eventId);
    }

    public PlanProcessingStatus addOrUpdatePlanProcessingStatus(PlanProcessingStatus planProcessingStatus) {
        if (planProcessingStatus.getEventId() == null) {
            throw new IllegalArgumentException("Event id not specified");
        }

        if (getPlanProcessingStatusRepository().getByEventId(planProcessingStatus.getEventId()) != null) {
            getPlanProcessingStatusRepository().update(planProcessingStatus);
        } else {
            getPlanProcessingStatusRepository().add(planProcessingStatus);
        }
        return planProcessingStatus;
    }

}
