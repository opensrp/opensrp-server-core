package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.Task;
import org.opensrp.repository.postgres.mapper.TaskMapper;

public interface CustomTaskMapper extends TaskMapper {
	
	int insertSelectiveAndSetId(Task task);
	
	/**
	 * Updates a task record while generating new server version
	 * 
	 * @param task the task to update
	 * @return the number of records updated
	 */
	int updateByPrimaryKeyAndGenerateServerVersion(Task task);
	
}
