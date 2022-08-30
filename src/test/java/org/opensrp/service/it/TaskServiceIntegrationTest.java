/**
 *
 */
package org.opensrp.service.it;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Test;
import org.opensrp.BaseIntegrationTest;
import org.opensrp.repository.TaskRepository;
import org.opensrp.service.TaskService;
import org.smartregister.domain.Task;
import org.smartregister.domain.Task.TaskPriority;
import org.smartregister.domain.Task.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.utils.DbAccessUtils;

/**
 * @author Samuel Githengi created on 11/23/20
 */
public class TaskServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;


    @Autowired
    private DataSource openSRPDataSource;

    @Test
    public void testCreateBatchShouldGenerateServerVersionWithCorrectOrder() throws Exception {
        DbAccessUtils.truncateTable("core.task", openSRPDataSource);
        List<Task> taskList = getTasks(500);
        taskList.parallelStream().forEach(task -> taskService.addOrUpdateTask(task));
        Thread.sleep(500);
        List<Task> savedTasks = taskRepository.getTasksByPlanAndGroup("plan1", "oa1", 0l, true);
        savedTasks.sort(new Comparator<Task>() {

            public int compare(Task arg0, Task arg1) {
                return arg0.getRowid().compareTo(arg1.getRowid());
            }

            ;
        });
        long previousServerVersion = savedTasks.get(0).getServerVersion();
        for (Task task : savedTasks) {
            if (previousServerVersion > task.getServerVersion()) {
                fail(String.format("Server version generated in the wrong Order. id:%d, Sv: %d, Previous Sv: %d ",
                        task.getRowid(), task.getServerVersion(), previousServerVersion));
            }

            previousServerVersion = task.getServerVersion();

        }
    }

    private List<Task> getTasks(int count) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Task task = new Task();
            task.setGroupIdentifier("oa1");
            task.setPlanIdentifier("plan1");
            task.setStatus(TaskStatus.READY);
            task.setPriority(TaskPriority.ROUTINE);
            task.setIdentifier("id" + i);
            tasks.add(task);
        }
        return tasks;
    }

}
