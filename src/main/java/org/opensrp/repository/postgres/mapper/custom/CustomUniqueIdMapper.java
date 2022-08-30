package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.UniqueId;
import org.opensrp.domain.postgres.UniqueIdExample;
import org.opensrp.repository.postgres.mapper.UniqueIdMapper;

public interface CustomUniqueIdMapper extends UniqueIdMapper {

    List<UniqueId> selectMany(@Param("example") UniqueIdExample uniqueIdExample,
                              @Param("offset") int offset, @Param("limit") int limit);

    List<String> selectManyIds(@Param("example") UniqueIdExample uniqueIdExample,
                               @Param("offset") int offset, @Param("limit") int limit);

    int insertSelectiveAndSetId(UniqueId record);
}
