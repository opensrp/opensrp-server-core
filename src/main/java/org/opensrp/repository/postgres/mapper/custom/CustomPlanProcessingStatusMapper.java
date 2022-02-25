package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.PlanProcessingStatus;
import org.opensrp.domain.postgres.PlanProcessingStatusExample;
import org.opensrp.repository.postgres.mapper.PlanProcessingStatusMapper;

import java.util.List;

public interface CustomPlanProcessingStatusMapper extends PlanProcessingStatusMapper {

    List<PlanProcessingStatus> selectMany(@Param("example") PlanProcessingStatusExample templateExample,
                              @Param("offset") int offset, @Param("limit") int limit);
}
