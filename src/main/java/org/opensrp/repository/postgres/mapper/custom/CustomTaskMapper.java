package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.Task;
import org.opensrp.repository.postgres.mapper.TaskMapper;

public interface CustomTaskMapper extends TaskMapper {
	
	int insertSelectiveAndSetId(Task task);
	
	Long selectServerVersionByPrimaryKey(Long id);
	
	int updateByPrimaryKeyAndGenerateServerVersion(Task record);
	
}
