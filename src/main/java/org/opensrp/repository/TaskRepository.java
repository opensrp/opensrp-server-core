package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.Task;

public interface TaskRepository extends BaseRepository<Task> {

	List<Task> getTasksByPlanAndGroup(String campaign, String group, long serverVersion);

	List<Task> findByEmptyServerVersion();

	/**
	 * This method fetches all task Ids
	 * @return a list of task ids
	 */
	List<String> findAllIds();

	/**
	 *  This method searches for tasks ordered by serverVersion ascending
	 *
	 * @param serverVersion
	 * @param sortBy field to sort by
	 * @param sortOrder Order by which to sort the plans
	 * @param limit upper limit on number of tasks to fetch
	 * @return list of plan identifiers
	 */
	List<Task> getAllTasksPaginated(Long serverVersion, String sortBy, String sortOrder, int limit);

}
