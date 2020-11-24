/**
 * 
 */
package org.opensrp.service.it;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;
import org.opensrp.BaseIntegrationTest;
import org.opensrp.service.TaskService;
import org.smartregister.domain.Task;
import org.smartregister.domain.Task.TaskPriority;
import org.smartregister.domain.Task.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Samuel Githengi created on 11/23/20
 */
public class TaskServiceIntegrationTest extends BaseIntegrationTest {
	
	@Autowired
	private TaskService taskService;
	
	@Test
	public void testCreateBatchShouldGenerateServerVersionWithCorrectOrder() throws Exception {
		List<Task> taskList = getTasks(500);
		taskList.parallelStream().forEach(task -> taskService.addOrUpdateTask(task));
		Thread.sleep(5000);
		List<Task> savedTasks = taskService.getTasksByTaskAndGroup("plan1", "oa1", 0l);
		savedTasks.sort(new Comparator<Task>() {
			
			public int compare(Task arg0, Task arg1) {
				return arg0.getIdentifier().compareTo(arg1.getIdentifier());
			};
		});
		long maxServerVersion = 0;
		for (Task task : savedTasks) {
			if (maxServerVersion > task.getServerVersion()) {
				maxServerVersion = task.getServerVersion();
			} else if (maxServerVersion < task.getServerVersion()) {
				fail("Server version generated in the wrong Order");
			}
			
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
