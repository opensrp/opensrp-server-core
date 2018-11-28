package org.opensrp.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Task;
import org.opensrp.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

	private static Logger logger = LoggerFactory.getLogger(TaskService.class.toString());

	private TaskRepository taskRepository;

	@Autowired
	public void setTaskRepository(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	public List<Task> getAllTasks() {
		return taskRepository.getAll();
	}

	public void addOrUpdateTask(Task task) {
		if (StringUtils.isBlank(task.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		task.setServerVersion(null);
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
		task.setServerVersion(null);
		task.setAuthoredOn(new DateTime());
		task.setLastModified(new DateTime());
		taskRepository.add(task);
		return task;

	}

	public Task updateTask(Task task) {
		if (StringUtils.isBlank(task.getIdentifier()))
			throw new IllegalArgumentException("Identifier not specified");
		task.setServerVersion(null);
		task.setLastModified(new DateTime());
		taskRepository.update(task);
		return task;
	}

	public Task getTask(String identifier) {
		if (StringUtils.isBlank(identifier))
			return null;
		return taskRepository.get(identifier);
	}

	public List<Task> getTasksByCampaignAndGroup(String campaign, String group, long serverVersion) {
		return taskRepository.getTasksByCampaignAndGroup(campaign, group, serverVersion);
	}

	public Set<String> saveTasks(List<Task> tasks) {
		Set<String> tasksWithErrors = new HashSet<>();
		for (Task task : tasks) {
			try {
				addOrUpdateTask(task);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				tasksWithErrors.add(task.getIdentifier());
			}
		}
		return tasksWithErrors;
	}

	public void addServerVersion() {
		try {
			List<Task> tasks = taskRepository.findByEmptyServerVersion();
			logger.info("RUNNING addServerVersion tasks size: " + tasks.size());
			long currentTimeMillis = System.currentTimeMillis();
			for (Task task : tasks) {
				try {
					Thread.sleep(1);
					task.setServerVersion(currentTimeMillis);
					taskRepository.update(task);
					currentTimeMillis += 1;
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

}
