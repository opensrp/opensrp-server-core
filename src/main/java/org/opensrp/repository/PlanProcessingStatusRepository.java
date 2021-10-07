package org.opensrp.repository;

import org.opensrp.domain.postgres.PlanProcessingStatus;

import java.util.List;

public interface PlanProcessingStatusRepository extends BaseRepository<PlanProcessingStatus> {

    PlanProcessingStatus getByPrimaryKey(Long id);

    PlanProcessingStatus getByEventId(Long eventId);

    List<PlanProcessingStatus> getByStatus(Integer status);

    void updatePlanProcessingStatus(String eventIdentifier, String planIdentifier, int status);
}
