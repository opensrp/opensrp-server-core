package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Task;
import org.opensrp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepository;

	public List<Task> getAllTasks() {
		return taskRepository.getAll();
	}

	public void addOrUpdateTask(Task task) {
		if (StringUtils.isBlank(task.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		task.setServerVersion(System.currentTimeMillis());
		task.setLastModified(new DateTime());
		if (taskRepository.get(task.getIdentifier()) != null) {
			taskRepository.update(task);
		} else {
			task.setAuthoredOn(new DateTime());
			taskRepository.add(task);
		}
	}

	public Task addTask(Task task) {
		if (StringUtils.isBlank(task.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		task.setServerVersion(System.currentTimeMillis());
		task.setAuthoredOn(new DateTime());
		task.setLastModified(new DateTime());
		taskRepository.add(task);
		return task;

	}

	public Task updateTask(Task task) {
		if (StringUtils.isBlank(task.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		task.setServerVersion(System.currentTimeMillis());
		task.setLastModified(new DateTime());
		taskRepository.update(task);
		return task;
	}

	public Task getTask(String identifier) {
		if (StringUtils.isBlank(identifier))
			return null;
		return taskRepository.get(identifier);
	}

	public List<Task> getTasks(String campaign, long serverVersion) {
		return taskRepository.getTasksByCampaignAndServerVersion(campaign, serverVersion);
	}

}
