package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.*;
import org.opensrp.repository.postgres.mapper.IdentifierSourceMapper;

import java.util.List;

public interface CustomIdentifierSourceMapper extends IdentifierSourceMapper {

	List<IdentifierSource> selectMany(@Param("example") IdentifierSourceExample example, @Param("offset") int offset,
			@Param("limit") int limit);

	IdentifierSource selectByIdentifier(String identifier);

	IdentifierSource selectOne(@Param("example") IdentifierSourceExample example);

	int insertSelectiveAndSetId(IdentifierSource record);
}
