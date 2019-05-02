package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Plan;
import org.opensrp.domain.postgres.PlanExample;
import org.opensrp.repository.postgres.mapper.PlanMapper;

import java.util.List;

/**
 * Created by Vincent Karuri on 02/05/2019
 */
public interface CustomPlanMapper extends PlanMapper {
    List<Plan> selectMany(@Param("example") PlanExample planExample, @Param("operationalAreaId") String operationalAreaId, @Param("offset") int offset, @Param("limit") int limit);
}
