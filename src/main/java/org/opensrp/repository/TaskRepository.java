package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.Task;

public interface TaskRepository extends BaseRepository<Task>{

	List<Task> getTasksByCampaignAndServerVersion(String campaign, long serverVersion);

}
