package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.LocationTag;
import org.opensrp.domain.postgres.LocationTagExample;
import org.opensrp.repository.postgres.mapper.LocationTagMapper;

import java.util.List;

public interface CustomLocationTagMapper extends LocationTagMapper {

    List<LocationTag> selectMany(@Param("example") LocationTagExample locationTagExample, @Param("offset") int offset,
                                 @Param("limit") int limit);

}
