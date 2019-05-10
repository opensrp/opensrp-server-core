package org.opensrp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Task;
import org.opensrp.domain.TaskUpdate;
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

	public List<Task> getTasksByTaskAndGroup(String task, String group, long serverVersion) {
		return taskRepository.getTasksByPlanAndGroup(task, group, serverVersion);
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

	public static Task.TaskStatus fromString(String statusParam) {
		for (Task.TaskStatus status : Task.TaskStatus.values()) {
			if (status.name().equalsIgnoreCase(statusParam)) {
				return status;
			}
		}
		return null;
	}

	public List<String> updateTaskStatus(List<TaskUpdate> taskUpdates) {
		List<String> updatedTaskIds = new ArrayList<>();
		for (TaskUpdate taskUpdate : taskUpdates) {
			Task task = taskRepository.get(taskUpdate.getIdentifier());
			try {
				Task.TaskStatus status = fromString(taskUpdate.getStatus());
				if (task != null && status != null) {
					if (taskUpdate.getServerVersion() >= task.getServerVersion()) {
						task.setBusinessStatus(taskUpdate.getBusinessStatus());
						task.setStatus(status);
						task.setLastModified(new DateTime());
						task.setServerVersion(null);
						taskRepository.update(task);
						updatedTaskIds.add(task.getIdentifier());
					} else {
						logger.info("Ignoring update of task status for " + task.getIdentifier()
								+ " task on server is more recent");
						// mark task as updated so that client does not try to sync it again
						updatedTaskIds.add(task.getIdentifier());
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return updatedTaskIds;
	}
}
