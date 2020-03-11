package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Test;
import org.opensrp.domain.Task;
import org.opensrp.domain.Task.TaskStatus;
import org.opensrp.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskRepositoryTest extends BaseRepositoryTest {

	private String dateFormat = "yyyy-MM-dd'T'HHmm";

	@Autowired
	private TaskRepository taskRepository;

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("task.sql");
		return scripts;
	}

	@Test
	public void testGet() {
		Task task = taskRepository.get("tsk11231jh22");
		assertEquals("tsk11231jh22", task.getIdentifier());
		assertEquals("2018_IRS-3734", task.getGroupIdentifier());
		assertEquals(TaskStatus.READY, task.getStatus());
		assertEquals("Not Visited", task.getBusinessStatus());
		assertEquals(3, task.getPriority());
		assertEquals("IRS", task.getCode());
		assertEquals("Spray House", task.getDescription());
		assertEquals("IRS Visit", task.getFocus());
		assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getForEntity());
		assertEquals("2018-11-10T2200", task.getExecutionStartDate().toString(dateFormat));
		assertNull(task.getExecutionEndDate());
		assertEquals("2018-10-31T0700", task.getAuthoredOn().toString(dateFormat));
		assertEquals("2018-11-12T1550", task.getLastModified().toString(dateFormat));
		assertEquals("demouser", task.getOwner());
		assertEquals(1, task.getNotes().size());
		assertEquals("demouser", task.getNotes().get(0).getAuthorString());
		assertEquals("2018-01-01T0800", task.getNotes().get(0).getTime().toString(dateFormat));
		assertEquals("This should be assigned to patrick.", task.getNotes().get(0).getText());
	}

	@Test
	public void testGetWithNoIdentifier() {

		Task task = taskRepository.get("");
		assertNull(task);

	}

	@Test
	public void testSafeRemove() {
		assertEquals(2, taskRepository.getAll().size());
		Task task = taskRepository.get("iyr-998njoo");
		taskRepository.safeRemove(task);

		assertEquals(1, taskRepository.getAll().size());
		assertNull(taskRepository.get("iyr-998njoo"));
	}

	@Test
	public void testSafeRemoveNonExistentCampaign() {
		taskRepository.safeRemove(null);
		taskRepository.safeRemove(new Task());
		assertEquals(2, taskRepository.getAll().size());

		taskRepository.safeRemove(taskRepository.get("iyr-998njoo"));
		assertEquals(1, taskRepository.getAll().size());

		taskRepository.safeRemove(taskRepository.get("iyr-998njoo"));
		assertEquals(1, taskRepository.getAll().size());

	}

	@Test
	public void testAdd() {
		Task task = new Task();
		task.setIdentifier("tsk-2332-j");
		task.setPlanIdentifier("2018-IRS-S4");
		task.setGroupIdentifier("7633hk-dsadsa");
		task.setDescription("Visit Mwangala household");
		task.setBusinessStatus("Not Visited");
		task.setStatus(TaskStatus.READY);
		task.setOwner("worker12");
		taskRepository.add(task);

		assertEquals(3, taskRepository.getAll().size());
		Task addedTask = taskRepository.get("tsk-2332-j");
		assertNotNull(addedTask);
		assertEquals("2018-IRS-S4", addedTask.getPlanIdentifier());
		assertEquals("7633hk-dsadsa", addedTask.getGroupIdentifier());
		assertEquals("Not Visited", addedTask.getBusinessStatus());
		assertEquals(TaskStatus.READY, addedTask.getStatus());
		assertEquals("worker12", addedTask.getOwner());

	}

	@Test
	public void testAddInvalidObject() {
		assertEquals(2, taskRepository.getAll().size());
		Task task = new Task();
		taskRepository.add(task);
		assertEquals(2, taskRepository.getAll().size());

		taskRepository.add(null);
		assertEquals(2, taskRepository.getAll().size());

	}

	@Test
	public void testAddExistingObject() {
		assertEquals(2, taskRepository.getAll().size());
		Task task = taskRepository.get("iyr-998njoo");
		taskRepository.add(task);
		assertEquals(2, taskRepository.getAll().size());

	}

	@Test
	public void testEdit() {
		Task task = taskRepository.get("iyr-998njoo");
		task.setStatus(TaskStatus.FAILED);
		task.setBusinessStatus("Non Residential");
		DateTime now = new DateTime();
		task.setLastModified(now);
		taskRepository.update(task);

		Task updatedTask = taskRepository.get("iyr-998njoo");
		assertNotNull(updatedTask);
		assertEquals("Non Residential", updatedTask.getBusinessStatus());
		assertEquals(TaskStatus.FAILED, updatedTask.getStatus());
		assertEquals(now, updatedTask.getLastModified());
	}

	@Test
	public void testEditInvalidObject() {
		assertEquals(2, taskRepository.getAll().size());
		Task task = new Task();
		taskRepository.update(task);
		assertEquals(2, taskRepository.getAll().size());

		taskRepository.update(null);
		assertEquals(2, taskRepository.getAll().size());

	}

	@Test
	public void testEditNonExistingObject() {
		Task task = taskRepository.get("iyr-998njoo");

		taskRepository.safeRemove(task);

		taskRepository.update(task);
		assertNull(taskRepository.get("iyr-998njoo"));

	}

	public void testGetTasksByCampaignAndGroup() {
		List<Task> tasks = taskRepository.getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 0);
		assertEquals(1, tasks.size());
		assertEquals("tsk11231jh22", tasks.get(0).getIdentifier());

		Task task = new Task();
		task.setIdentifier("tsk-2332-j");
		task.setPlanIdentifier("IRS_2018_S");
		task.setGroupIdentifier("2018_IRS-3734");
		task.setBusinessStatus("Not Visited");
		task.setStatus(TaskStatus.DRAFT);
		task.setServerVersion(System.currentTimeMillis());
		taskRepository.add(task);

		assertEquals(2, taskRepository.getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS", 0).size());

		assertTrue(taskRepository.getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS", 0).isEmpty());

		assertTrue(taskRepository.getTasksByPlanAndGroup("IRS_201", "2018_IRS-373", 0).isEmpty());

		assertTrue(taskRepository.getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-373", System.currentTimeMillis())
				.isEmpty());

	}

	@Test
	public void testGetCampaignsByServerVersion() {
		List<Task> tasks = taskRepository.findByEmptyServerVersion();
		assertTrue(tasks.isEmpty());

		Task task = taskRepository.get("iyr-998njoo");
		task.setServerVersion(null);
		taskRepository.update(task);

		tasks = taskRepository.findByEmptyServerVersion();
		assertEquals(1, tasks.size());
		assertEquals("iyr-998njoo", tasks.get(0).getIdentifier());

	}

	@Test
	public void testFindAllIdsShouldOrderByServerVersion() {
		Pair<List<String>, Long> idsModel = taskRepository.findAllIds(0l, 10);
		List<String> taskIdentifiers = idsModel.getLeft();
		assertEquals(2, taskIdentifiers.size());
		assertEquals("tsk11231jh22", taskIdentifiers.get(0));
		assertEquals("iyr-998njoo", taskIdentifiers.get(1));
		assertEquals(1542031602680l, idsModel.getRight().longValue());
	}

	@Test
	public void testFindAllIdsShouldLimitByGivenParam() {
		Pair<List<String>, Long> idsModel = taskRepository.findAllIds(0l, 1);
		List<String> taskIdentifiers = idsModel.getLeft();
		assertEquals(1, taskIdentifiers.size());
		assertEquals("tsk11231jh22", taskIdentifiers.get(0));
		assertEquals(1542027762554l, idsModel.getRight().longValue());
	}

	public void testGetTasksByOwnerAndPlan() {
		List<Task> tasks = taskRepository.getTasksByPlanAndOwner("IRS_2018_S1", "demouser", 0);
		assertEquals(1, tasks.size());
		assertEquals("tsk11231jh22", tasks.get(0).getIdentifier());

		Task task = new Task();
		task.setIdentifier("tsk-2332-j");
		task.setPlanIdentifier("IRS_2018_S");
		task.setGroupIdentifier("2018_IRS-3734");
		task.setBusinessStatus("Not Visited");
		task.setStatus(TaskStatus.DRAFT);
		task.setServerVersion(System.currentTimeMillis());
		taskRepository.add(task);

		assertEquals(2, taskRepository.getTasksByPlanAndOwner("IRS_2018_S1", "demouser", 0).size());

		assertTrue(taskRepository.getTasksByPlanAndOwner("IRS_2018_S1", "demouser", 0).isEmpty());

		assertTrue(taskRepository.getTasksByPlanAndOwner("IRS_201", "demouser", 0).isEmpty());

		assertTrue(taskRepository.getTasksByPlanAndOwner("IRS_2018_S1", "demouser", System.currentTimeMillis())
				.isEmpty());
	}

}
