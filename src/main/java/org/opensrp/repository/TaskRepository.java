package org.opensrp.repository;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.search.TaskSearchBean;
import org.smartregister.domain.Task;
import org.smartregister.pathevaluator.dao.TaskDao;

public interface TaskRepository extends BaseRepository<Task>, TaskDao {

	List<Task> getTasksByPlanAndGroup(String plan, String group, long serverVersion);

	List<Task> findByEmptyServerVersion();

	/**
	 * This method fetches all task Ids
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of tasks ids to fetch
	 * @return a list of task ids and last server version
	 */
	Pair<List<String>, Long> findAllIds(Long serverVersion, int limit);

	/**
	 * overloads {@link #findAllIds(Long, int)} by adding date/time filters
	 * @param serverVersion
	 * @param limit
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	Pair<List<String>, Long> findAllIds(Long serverVersion, int limit, Date fromDate, Date toDate);

	/**
	 *  This method searches for tasks ordered by serverVersion ascending
	 *
	 * @param serverVersion
	 * @param limit upper limit on number of tasks to fetch
	 * @return list of plan identifiers
	 */
	List<Task> getAllTasks(Long serverVersion, int limit);

	/**
	 * Count all tasks
	 * @param serverVersion
	 * @return
	 */
	Long countAllTasks(Long serverVersion);

	/**
	 * This method is used to return a list of tasks based on the provided parameters
	 * @param plan plan identifier for the task
	 * @param owner the username of the person who initiated the task
	 * @return returns a list of tasks matching the passed parameters
	 */
	List<Task> getTasksByPlanAndOwner(String plan, String owner, long serverVersion);

	/**
	 * This method returns a count of tasks belonging to a particular owner
	 * @param plan plan identifier for the task
	 * @param group the team who initiated the task
	 * @param serverVersion Version of the server
	 * @return count of tasks created by the provider username (owner)
	 */
	Long countTasksByPlanAndGroup(String plan, String group, long serverVersion);

	/**
	 * This method returns a count of tasks belonging to a particular owner
	 * @param plan plan identifier for the task
	 * @param owner the username of the person who initiated the task
	 * @param serverVersion Version of the server
	 * @return count of tasks created by the provider username (owner)
	 */
	Long countTasksByPlanAndOwner(String plan, String owner, long serverVersion);

	List<Task> getTasksBySearchBean(TaskSearchBean taskSearchBean);

	int getTaskCount(TaskSearchBean searchBean);

}
