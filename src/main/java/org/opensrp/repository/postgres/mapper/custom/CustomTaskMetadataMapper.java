package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Task;
import org.opensrp.domain.postgres.TaskMetadataExample;
import org.opensrp.repository.postgres.mapper.TaskMetadataMapper;

import java.util.List;

public interface CustomTaskMetadataMapper extends TaskMetadataMapper {
	
	Task selectByIdentifier(String identifier);

	List<Task> selectMany(@Param("example") TaskMetadataExample taskMetadataExample,
			@Param("offset") int offset, @Param("limit") int limit);

	List<String> selectManyIds(@Param("example") TaskMetadataExample taskMetadataExample,
			@Param("offset") int offset, @Param("limit") int limit );
}
