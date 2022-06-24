package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Plan;
import org.opensrp.domain.postgres.PlanExample;
import org.opensrp.repository.postgres.mapper.PlanMapper;
import org.opensrp.search.PlanSearchBean;

import java.util.List;

/**
 * Created by Vincent Karuri on 02/05/2019
 */
public interface CustomPlanMapper extends PlanMapper {
    List<Plan> selectMany(@Param("example") PlanExample planExample, @Param("offset") int offset,
                          @Param("limit") int limit);

    int insertSelectiveAndSetId(Plan plan);

    List<Plan> selectManyReturnOptionalFields(@Param("example") PlanExample planExample, @Param("fields") List<String> fields, @Param("offset") int offset,
                                              @Param("limit") int limit);

    List<String> selectManyIds(@Param("example") PlanExample planExample, @Param("offset") int offset,
                               @Param("limit") int limit);

    Long countMany(@Param("example") PlanExample planExample);

    List<Plan> selectPlansBySearchBean(@Param("searchBean") PlanSearchBean searchBean,
                                       @Param("offset") int offset, @Param("limit") int limit);

    Long selectServerVersionByPrimaryKey(Long id);

    int updateByPrimaryKeyAndGenerateServerVersion(Plan plan);

}
