package org.opensrp.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.opensrp.domain.TaskUpdate;
import org.opensrp.repository.TaskRepository;
import org.opensrp.search.TaskSearchBean;
import org.smartregister.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {

    private static Logger logger = LogManager.getLogger(TaskService.class.toString());

    private TaskRepository taskRepository;

    public static Task.TaskStatus fromString(String statusParam) {
        for (Task.TaskStatus status : Task.TaskStatus.values()) {
            if (status.name().equalsIgnoreCase(statusParam)) {
                return status;
            }
        }
        return null;
    }

    @Autowired
    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }


    @PreAuthorize("hasRole('TASK_VIEW')")
    @PostFilter("hasPermission(filterObject, 'TASK_VIEW')")
    public List<Task> getAllTasks() {
        return taskRepository.getAll();
    }

    @PreAuthorize("hasPermission(#task,'Task', 'TASK_CREATE') and hasPermission(#task,'Task', 'TASK_UPDATE')")
    public void addOrUpdateTask(Task task) {
        if (StringUtils.isBlank(task.getIdentifier()))
            throw new IllegalArgumentException("Identifier not specified");
        task.setLastModified(new DateTime());
        if (getTask(task.getIdentifier()) != null) {
            updateTask(task);
        } else {
            addTask(task);
        }
    }

    @PreAuthorize("hasPermission(#task,'Task', 'TASK_VIEW')")
    public boolean taskExists(Task task) {
        String taskIdentifier = task.getIdentifier();
        String entityId = task.getForEntity();
        String jurisdiction = task.getGroupIdentifier();
        String planIdentifier = task.getPlanIdentifier();
        String taskCode = task.getCode();

        return (StringUtils.isNotBlank(taskIdentifier) && taskRepository.checkIfTaskExists(entityId, jurisdiction, planIdentifier, taskCode));
    }

    @PreAuthorize("hasPermission(#task,'Task', 'TASK_CREATE')")
    public Task addTask(Task task) {
        if (StringUtils.isBlank(task.getIdentifier()))
            throw new IllegalArgumentException("Identifier not specified");
        if (!taskExists(task)) {
            task.setAuthoredOn(new DateTime());
            task.setLastModified(new DateTime());
            taskRepository.add(task);
        }

        return task;
    }


    @PreAuthorize("hasPermission(#task,'Task', 'TASK_UPDATE')")
    public Task updateTask(Task task) {
        if (StringUtils.isBlank(task.getIdentifier()))
            throw new IllegalArgumentException("Identifier not specified");
        task.setLastModified(new DateTime());
        taskRepository.update(task);
        return task;
    }

    @PreAuthorize("hasRole('TASK_VIEW')")
    @PostAuthorize("hasPermission(returnObject, 'TASK_VIEW')")
    public Task getTask(String identifier) {
        if (StringUtils.isBlank(identifier))
            return null;
        return taskRepository.get(identifier);
    }

    @PreAuthorize("hasRole('TASK_VIEW')")
    @PostFilter("hasPermission(filterObject, 'TASK_VIEW')")
    public List<Task> getTasksByTaskAndGroup(String task, String group, long serverVersion) {
        return taskRepository.getTasksByPlanAndGroup(task, group, serverVersion);
    }

    @PreAuthorize("hasPermission(#tasks,'Task','TASK_CREATE') and hasPermission(#tasks,'Task','TASK_UPDATE')")
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

    @PreAuthorize("hasPermission(#taskUpdates,'TaskUpdate','TASK_UPDATE')")
    public List<String> updateTaskStatus(List<TaskUpdate> taskUpdates) {
        List<String> updatedTaskIds = new ArrayList<>();
        for (TaskUpdate taskUpdate : taskUpdates) {
            Task task = taskRepository.get(taskUpdate.getIdentifier());
            try {
                Task.TaskStatus status = fromString(taskUpdate.getStatus());
                if (task != null && status != null) {
                    task.setBusinessStatus(taskUpdate.getBusinessStatus());
                    task.setStatus(status);
                    task.setLastModified(new DateTime());
                    taskRepository.update(task);
                    updatedTaskIds.add(task.getIdentifier());
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return updatedTaskIds;
    }

    /**
     * This method searches for all task Ids
     *
     * @param serverVersion
     * @param limit         upper limit on number of tasks ids to fetch
     * @return a list of all task ids and last server version
     */
    public Pair<List<String>, Long> findAllTaskIds(Long serverVersion, int limit) {
        return taskRepository.findAllIds(serverVersion, limit);
    }

    /**
     * overloads {@link #findAllTaskIds(Long, int)} by adding date/time filters
     *
     * @param serverVersion
     * @param limit
     * @param fromDate
     * @param toDate
     * @return
     */
    public Pair<List<String>, Long> findAllTaskIds(Long serverVersion, int limit, Date fromDate, Date toDate) {
        return taskRepository.findAllIds(serverVersion, limit, fromDate, toDate);
    }

    /**
     * This method searches for tasks ordered by serverVersion ascending
     *
     * @param serverVersion
     * @param limit         upper limit on number of tasks to fetch
     * @return list of plan identifiers
     */
    @PreAuthorize("hasRole('TASK_VIEW')")
    @PostFilter("hasPermission(filterObject, 'TASK_VIEW')")
    public List<Task> getAllTasks(Long serverVersion, int limit) {
        return taskRepository.getAllTasks(serverVersion, limit);
    }

    /**
     * This method returns a list of tasks belonging to a particular owner
     *
     * @param plan          plan identifier for the task
     * @param owner         the username of the person who initiated the task
     * @param serverVersion Version of the server
     * @return list of tasks created by the provider username (owner)
     */
    @PreAuthorize("hasRole('TASK_VIEW')")
    @PostFilter("hasPermission(filterObject, 'TASK_VIEW')")
    public List<Task> getTasksByPlanAndOwner(String plan, String owner, long serverVersion) {
        return taskRepository.getTasksByPlanAndOwner(plan, owner, serverVersion);
    }

    /**
     * This method returns a count of tasks belonging to a particular owner
     *
     * @param plan          plan identifier for the task
     * @param group         the team who initiated the task
     * @param serverVersion Version of the server
     * @return count of tasks created by the provider username (owner)
     */
    public Long countTasksByPlanAndGroup(String plan, String group, long serverVersion) {
        return taskRepository.countTasksByPlanAndGroup(plan, group, serverVersion);
    }

    /**
     * This method returns a count of tasks belonging to a particular owner
     *
     * @param plan          plan identifier for the task
     * @param owner         the username of the person who initiated the task
     * @param serverVersion Version of the server
     * @return count of tasks created by the provider username (owner)
     */
    public Long countTasksByPlanAndOwner(String plan, String owner, long serverVersion) {
        return taskRepository.countTasksByPlanAndOwner(plan, owner, serverVersion);
    }

    /**
     * Count all tasks
     *
     * @param serverVersion
     * @return
     */
    public Long countAllTasks(long serverVersion) {
        return taskRepository.countAllTasks(serverVersion);
    }

    public List<Task> getTasksBySearchBean(TaskSearchBean taskSearchBean) {
        return taskRepository.getTasksBySearchBean(taskSearchBean);
    }

    public int findTaskCountBySearchBean(TaskSearchBean taskSearchBean) {
        return taskRepository.getTaskCount(taskSearchBean);
    }
}
