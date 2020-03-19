package org.opensrp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.opensrp.domain.Task;
import org.opensrp.domain.Task.TaskStatus;
import org.opensrp.domain.TaskUpdate;
import org.opensrp.repository.TaskRepository;
import org.opensrp.util.TaskDateTimeTypeConverter;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class TaskServiceTest {

	private static String dateFormat = "yyyy-MM-dd'T'HHmm";

	private TaskService taskService;

	private TaskRepository taskRepository;

	@Before
	public void setUp() {
		taskRepository = mock(TaskRepository.class);
		taskService = new TaskService();
		taskService.setTaskRepository(taskRepository);
	}

	@Test
	public void testGetAllTasks() {
		List<Task> expected = new ArrayList<>();
		expected.add(initializeTask());
		when(taskRepository.getAll()).thenReturn(expected);
		List<Task> tasks = taskService.getAllTasks();
		verify(taskRepository).getAll();
		assertEquals(1, tasks.size());
		assertEquals("tsk11231jh22", tasks.get(0).getIdentifier());
	}

	@Test
	public void testAddOrUpdateTaskShouldUpdate() {
		Task task = initializeTask();
		when(taskRepository.get("tsk11231jh22")).thenReturn(task);
		taskService.addOrUpdateTask(task);
		verify(taskRepository).update(task);
	}

	@Test
	public void testAddOrUpdateTaskShouldAdd() {
		Task task = initializeTask();
		when(taskRepository.get("tsk11231jh22")).thenReturn(null);
		taskService.addOrUpdateTask(task);
		verify(taskRepository).add(task);

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddOrUpdateTaskWithoutIdentifier() {
		Task task = new Task();
		taskService.addOrUpdateTask(task);

	}

	@Test
	public void testAddTask() {
		Task task = initializeTask();
		taskService.addTask(task);
		verify(taskRepository).add(task);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddTaskWithoutIdentifier() {
		Task task = new Task();
		taskService.addTask(task);
	}

	@Test
	public void testUpdateTask() {
		Task task = initializeTask();
		taskService.updateTask(task);
		verify(taskRepository).update(task);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateTaskWithoutIdentifier() {
		Task task = new Task();
		taskService.updateTask(task);
	}

	@Test
	public void testGetTask() {
		Task expected = initializeTask();
		when(taskRepository.get("tsk11231jh22")).thenReturn(expected);
		Task task = taskService.getTask("tsk11231jh22");

		assertEquals("tsk11231jh22", task.getIdentifier());
		assertEquals("2018_IRS-3734{", task.getGroupIdentifier());
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
		assertEquals("2018-10-31T0700", task.getLastModified().toString(dateFormat));
		assertEquals("demouser", task.getOwner());
		assertEquals(1, task.getNotes().size());
		assertEquals("demouser", task.getNotes().get(0).getAuthorString());
		assertEquals("2018-01-01T0800", task.getNotes().get(0).getTime().toString(dateFormat));
		assertEquals("This should be assigned to patrick.", task.getNotes().get(0).getText());
	}

	@Test
	public void testGetTaskWithoutIdentifier() {
		Task task = taskService.getTask("");
		verify(taskRepository, never()).get(anyString());
		assertNull(task);
	}

	@Test
	public void testGetTasksByCampaignAndGroup() {
		Task task = initializeTask();
		List<Task> expected = new ArrayList<>();
		expected.add(task);
		when(taskRepository.getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 15421904649873l))
				.thenReturn(expected);
		List<Task> tasks = taskService.getTasksByTaskAndGroup("IRS_2018_S1", "2018_IRS-3734", 15421904649873l);
		verify(taskRepository).getTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 15421904649873l);
		assertEquals(task, tasks.get(0));
	}

	@Test
	public void addServerVersion() {

		List<Task> expected = new ArrayList<>();
		Task task = initializeTask();
		task.setServerVersion(null);
		expected.add(task);
		when(taskRepository.findByEmptyServerVersion()).thenReturn(expected);

		long now = System.currentTimeMillis();
		taskService.addServerVersion();
		verify(taskRepository).findByEmptyServerVersion();
		ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
		verify(taskRepository).update(argumentCaptor.capture());
		assertNotNull(argumentCaptor.getValue().getServerVersion());
		assertTrue(argumentCaptor.getValue().getServerVersion() >= now);

	}

	private Task initializeTask() {
		Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new TaskDateTimeTypeConverter())
				.serializeNulls().create();
		return gson.fromJson(
				"{\"identifier\":\"tsk11231jh22\",\"campaignIdentifier\":\"IRS_2018_S1\",\"groupIdentifier\":\"2018_IRS-3734{\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":3,\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc\",\"executionStartDate\":\"2018-11-10T2200\",\"executionEndDate\":null,\"authoredOn\":\"2018-10-31T0700\",\"lastModified\":\"2018-10-31T0700\",\"owner\":\"demouser\",\"note\":[{\"authorString\":\"demouser\",\"time\":\"2018-01-01T0800\",\"text\":\"This should be assigned to patrick.\"}],\"serverVersion\":0}",
				Task.class);

	}

	@Test
	public void updateTaskStatus() {

		long now = System.currentTimeMillis();
		Task task = initializeTask();
		List<TaskUpdate> updates = new ArrayList<>();
		TaskUpdate taskUpdate = new TaskUpdate();
		taskUpdate.setIdentifier("tsk11231jh22");
		taskUpdate.setBusinessStatus("Not Sprayable");
		taskUpdate.setServerVersion(now);
		taskUpdate.setStatus(TaskStatus.COMPLETED.name());
		updates.add(taskUpdate);
		when(taskRepository.get("tsk11231jh22")).thenReturn(task);
		taskService.updateTaskStatus(updates);
		assertEquals(now, taskUpdate.getServerVersion().longValue());

		ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
		verify(taskRepository).update(argumentCaptor.capture());
		assertNull(argumentCaptor.getValue().getServerVersion());
		assertEquals("Not Sprayable", argumentCaptor.getValue().getBusinessStatus());

		assertEquals("tsk11231jh22", task.getIdentifier());
		assertEquals("2018_IRS-3734{", task.getGroupIdentifier());
		assertEquals(3, task.getPriority());
		assertEquals("IRS", task.getCode());
		assertEquals("Spray House", task.getDescription());
		assertEquals("IRS Visit", task.getFocus());
		assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getForEntity());
		assertEquals("2018-11-10T2200", task.getExecutionStartDate().toString(dateFormat));
		assertNull(task.getExecutionEndDate());
		assertEquals("2018-10-31T0700", task.getAuthoredOn().toString(dateFormat));
		assertEquals("demouser", task.getOwner());
		assertEquals(1, task.getNotes().size());
		assertEquals("demouser", task.getNotes().get(0).getAuthorString());
		assertEquals("2018-01-01T0800", task.getNotes().get(0).getTime().toString(dateFormat));
		assertEquals("This should be assigned to patrick.", task.getNotes().get(0).getText());
	}

	@Test
	public void testFindAllTaskIds() {
		List<String> expectedTaskIds = new ArrayList<>();
		expectedTaskIds.add("task1");
		expectedTaskIds.add("task2");
		Pair<List<String>, Long> idsModel = Pair.of(expectedTaskIds, 1234l);

		when(taskRepository.findAllIds(anyLong(), anyInt())).thenReturn(idsModel);
		Pair<List<String>, Long> actualIdsModels = taskService.findAllTaskIds(0l, 10);
		List<String> actualTaskIds = actualIdsModels.getLeft();

		verify(taskRepository).findAllIds(0l, 10);
		assertEquals(2, actualTaskIds.size());
		assertEquals(expectedTaskIds.get(0), actualTaskIds.get(0));
		assertEquals(expectedTaskIds.get(1), actualTaskIds.get(1));
	}

}
