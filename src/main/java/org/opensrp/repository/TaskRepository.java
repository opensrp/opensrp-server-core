package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.Task;

public interface TaskRepository extends BaseRepository<Task> {

	List<Task> getTasksByPlanAndGroup(String campaign, String group, long serverVersion);

	List<Task> findByEmptyServerVersion();

}
