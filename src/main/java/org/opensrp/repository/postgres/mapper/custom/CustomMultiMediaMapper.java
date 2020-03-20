package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.MultiMedia;
import org.opensrp.domain.postgres.MultiMediaExample;
import org.opensrp.repository.postgres.mapper.MultiMediaMapper;

import java.util.List;

public interface CustomMultiMediaMapper extends MultiMediaMapper {
	
	List<MultiMedia> selectMany(@Param("example") MultiMediaExample example, @Param("offset") int offset,
	        @Param("limit") int limit);
}
