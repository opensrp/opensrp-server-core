package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.smartregister.domain.Task;
import org.smartregister.domain.Task.TaskPriority;
import org.smartregister.domain.Task.TaskStatus;
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
		assertEquals("2018-11-10T2200", task.getExecutionPeriod().getStart().toString(dateFormat));
		assertNull(task.getExecutionPeriod().getEnd());
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
	public void testSafeRemoveNonExistentTask() {
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

	@Test
	public void testGetTasksByPlanAndGroup() {
		List<Task> tasks = taskRepository.getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 0);
		assertEquals(1, tasks.size());
		assertEquals("tsk11231jh22", tasks.get(0).getIdentifier());

		Task task = new Task();
		task.setIdentifier("tsk-2332-j");
		task.setPlanIdentifier("IRS_2018_S1");
		task.setGroupIdentifier("2018_IRS-3734");
		task.setBusinessStatus("Not Visited");
		task.setStatus(TaskStatus.DRAFT);
		task.setServerVersion(System.currentTimeMillis());
		taskRepository.add(task);

		assertEquals(2, taskRepository.getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 0).size());

		assertTrue(taskRepository.getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS", 0).isEmpty());

		assertTrue(taskRepository.getTasksByPlanAndGroup("IRS_201", "2018_IRS-3734", 0).isEmpty());

		assertTrue(taskRepository.getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", System.currentTimeMillis())
				.isEmpty());

	}

	@Test
	public void testGetTasksByServerVersion() {
		List<Task> tasks = taskRepository.findByEmptyServerVersion();
		assertTrue(tasks.isEmpty());

		Task task = taskRepository.get("iyr-998njoo");
		task.setServerVersion(0l);
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

	@Test
	public void testFindAllIdsShouldFilterBetweenFromAndToDate(){
		String date1 = "2020-09-25T10:00:00+0300";
		String date3 = "2020-09-27T10:00:00+0300";
		Pair<List<String>, Long> idsModel = taskRepository.findAllIds(0l,2,
				new DateTime(date1, DateTimeZone.UTC).toDate(), new DateTime(date3, DateTimeZone.UTC).toDate());
		assertEquals(2, idsModel.getLeft().size());
	}

	@Test
	public void testFindAllIdsShouldFilterFromDateAsMinimumDate(){
		String date2 = "2020-09-26T10:00:00+0300";
		Pair<List<String>, Long> idsModel = taskRepository.findAllIds(0l,2,
				new DateTime(date2, DateTimeZone.UTC).toDate(), null);
		assertEquals(1, idsModel.getLeft().size());
	}

	@Test
	public void testFindAllIdsShouldFilterFromToDateAsMaximumDate(){
		String date1 = "2020-09-27T10:00:00+0300";
		Pair<List<String>, Long> idsModel = taskRepository.findAllIds(0l,2,
				null, new DateTime(date1, DateTimeZone.UTC).toDate());
		assertEquals(2, idsModel.getLeft().size());
	}

	@Test
	public void testGetTasksByOwnerAndPlan() {
		List<Task> tasks = taskRepository.getTasksByPlanAndOwner("IRS_2018_S1", "demouser", 0);
		assertEquals(1, tasks.size());
		assertEquals("tsk11231jh22", tasks.get(0).getIdentifier());

		Task task = new Task();
		task.setIdentifier("tsk-2332-j");
		task.setPlanIdentifier("IRS_2018_S1");
		task.setGroupIdentifier("2018_IRS-3734");
		task.setBusinessStatus("Not Visited");
		task.setStatus(TaskStatus.DRAFT);
		task.setOwner("demouser");
		task.setServerVersion(System.currentTimeMillis());
		taskRepository.add(task);

		assertEquals(2, taskRepository.getTasksByPlanAndOwner("IRS_2018_S1", "demouser", 0).size());

		assertTrue(taskRepository.getTasksByPlanAndOwner("IRS_2018_S", "demouser", 0).isEmpty());

		assertTrue(taskRepository.getTasksByPlanAndOwner("IRS_2018_S1", "demouser1", 0).isEmpty());

		assertTrue(taskRepository.getTasksByPlanAndOwner("IRS_2018_S1", "demouser", System.currentTimeMillis())
				.isEmpty());
	}

	@Test
	public void testCountTasksByPlanAndGroup() {
		Long tasks = taskRepository.countTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 0);
		assertEquals(1l, tasks.longValue());

		Task task = new Task();
		task.setIdentifier("tsk-2332-j");
		task.setPlanIdentifier("IRS_2018_S1");
		task.setGroupIdentifier("2018_IRS-3734");
		task.setBusinessStatus("Not Visited");
		task.setStatus(TaskStatus.DRAFT);
		task.setServerVersion(System.currentTimeMillis());
		taskRepository.add(task);

		assertEquals(2, taskRepository.countTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 0).longValue());

	}

	@Test
	public void countGetTasksByOwnerAndPlan() {
		Long tasks = taskRepository.countTasksByPlanAndOwner("IRS_2018_S1", "demouser", 0);
		assertEquals(1, tasks.longValue());

		Task task = new Task();
		task.setIdentifier("tsk-2332-j");
		task.setPlanIdentifier("IRS_2018_S1");
		task.setGroupIdentifier("2018_IRS-3734");
		task.setBusinessStatus("Not Visited");
		task.setStatus(TaskStatus.DRAFT);
		task.setOwner("demouser");
		task.setServerVersion(System.currentTimeMillis());
		taskRepository.add(task);

		assertEquals(2, taskRepository.countTasksByPlanAndOwner("IRS_2018_S1", "demouser", 0).longValue());

	}

	@Test
	public void testSaveTask() {
		Task task = new Task();
		task.setIdentifier("tsk-2332-k");
		task.setPlanIdentifier("2018-IRS-S4");
		task.setGroupIdentifier("7633hk-dsadsa");
		task.setDescription("Visit Mwangala household");
		task.setBusinessStatus("Not Visited");
		task.setStatus(TaskStatus.READY);
		task.setPriority(TaskPriority.STAT);
		task.setOwner("testUser");
		task.setRequester("testUser");
		taskRepository.add(task);

		assertEquals(3, taskRepository.getAll().size());
		Task addedTask = taskRepository.get("tsk-2332-k");
		assertNotNull(addedTask);
		assertEquals("2018-IRS-S4", addedTask.getPlanIdentifier());
		assertEquals("7633hk-dsadsa", addedTask.getGroupIdentifier());
		assertEquals("Visit Mwangala household", addedTask.getDescription());
		assertEquals("Not Visited", addedTask.getBusinessStatus());
		assertEquals(TaskStatus.READY, addedTask.getStatus());
		assertEquals("testUser", addedTask.getOwner());
		assertEquals("testUser", addedTask.getRequester());
		assertEquals(TaskPriority.STAT, addedTask.getPriority());
	}

	@Test
	public void testCheckIfTaskExists() {
		Task task = new Task();
		task.setIdentifier("tsk-2332-kl");
		task.setPlanIdentifier("test-plan-id-1");
	    task.setForEntity("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fd");
		task.setCode("test-code");
		task.setGroupIdentifier("group-1");
		task.setStatus(TaskStatus.READY);
		taskRepository.add(task);
		boolean taskExists = taskRepository.checkIfTaskExists("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fd","group-1",
				"test-plan-id-1","test-code");
		assertTrue(taskExists);
	}

	@Test
	public void testCheckIfTaskExistsReturnsFalse() {
		Task task = new Task();
		task.setIdentifier("tsk-2332-km");
		task.setPlanIdentifier("test-plan-id-2");
		task.setForEntity("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fe");
		task.setCode("test-code-2");
		task.setGroupIdentifier("group-1");
		task.setStatus(TaskStatus.ARCHIVED);
		taskRepository.add(task);
		boolean taskExists = taskRepository.checkIfTaskExists("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fe","group-1",
				"test-plan-id-2","test-code-2");
		assertFalse(taskExists);
	}

	@Test
	public void testCountAllTasksShouldReturnCorrectValue(){
		Long count = taskRepository.countAllTasks(1000l);
		assertEquals(Long.valueOf(2), count);
	}

}
