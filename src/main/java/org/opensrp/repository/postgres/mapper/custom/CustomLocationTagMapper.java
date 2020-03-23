package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.LocationTag;
import org.opensrp.domain.postgres.LocationTagExample;
import org.opensrp.repository.postgres.mapper.LocationTagMapper;

public interface CustomLocationTagMapper extends LocationTagMapper {
	
	List<LocationTag> selectMany(@Param("example") LocationTagExample locationTagExample, @Param("offset") int offset,
	                             @Param("limit") int limit);
	
}
