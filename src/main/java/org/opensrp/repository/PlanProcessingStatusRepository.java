package org.opensrp.repository;

import org.opensrp.domain.postgres.PlanProcessingStatus;

public interface PlanProcessingStatusRepository extends BaseRepository<PlanProcessingStatus> {

    PlanProcessingStatus getByPrimaryKey(Long id);

    PlanProcessingStatus getByEventId(Long eventId);
}
