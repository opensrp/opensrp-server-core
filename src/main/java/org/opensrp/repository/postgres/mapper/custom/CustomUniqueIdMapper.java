package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.UniqueId;
import org.opensrp.domain.postgres.UniqueIdExample;
import org.opensrp.repository.postgres.mapper.UniqueIdMapper;

import java.util.List;

public interface CustomUniqueIdMapper extends UniqueIdMapper {

    List<UniqueId> selectMany(@Param("example") UniqueIdExample uniqueIdExample,
                              @Param("offset") int offset, @Param("limit") int limit);

    List<String> selectManyIds(@Param("example") UniqueIdExample uniqueIdExample,
                               @Param("offset") int offset, @Param("limit") int limit);

    int insertSelectiveAndSetId(UniqueId record);
}
