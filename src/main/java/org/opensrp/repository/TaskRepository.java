package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.AllIdsModel;
import org.opensrp.domain.Task;

public interface TaskRepository extends BaseRepository<Task> {

	List<Task> getTasksByPlanAndGroup(String campaign, String group, long serverVersion);

	List<Task> findByEmptyServerVersion();

	/**
	 * This method fetches all task Ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of tasks ids to fetch
	 * @return a list of task ids and last server version
	 */
	AllIdsModel findAllIds(Long serverVersion, int limit);

	/**
	 *  This method searches for tasks ordered by serverVersion ascending
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of tasks to fetch
	 * @return list of plan identifiers
	 */
	List<Task> getAllTasks(Long serverVersion, int limit);

}
