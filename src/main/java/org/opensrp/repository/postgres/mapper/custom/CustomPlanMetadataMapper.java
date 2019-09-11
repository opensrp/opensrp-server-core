package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Plan;
import org.opensrp.domain.postgres.PlanExample;
import org.opensrp.repository.postgres.mapper.PlanMetadataMapper;

import java.util.List;

public interface CustomPlanMetadataMapper extends PlanMetadataMapper {
	List<Plan> selectMany(@Param("example") PlanExample planExample, @Param("operationalAreaIds") List<String> operationalAreaIds, @Param("offset") int offset,
			@Param("limit") int limit);

}
