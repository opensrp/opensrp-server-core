package org.opensrp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.opensrp.domain.TaskUpdate;
import org.opensrp.repository.TaskRepository;
import org.opensrp.search.TaskSearchBean;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.domain.Task;
import org.smartregister.domain.Task.TaskPriority;
import org.smartregister.domain.Task.TaskStatus;
import org.smartregister.utils.TaskDateTimeTypeConverter;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*","org.w3c.*"})
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
	public void testTaskExistsChecksRepo(){
		Task task = initializeTask();
		taskService.taskExists(task);
		verify(taskRepository).checkIfTaskExists(eq(task.getForEntity()), eq(task.getGroupIdentifier()), eq(task.getPlanIdentifier()), eq(task.getCode()));
	}

	@Test
	public void testTaskExistsReturnsExpected(){
		Task task = initializeTask();
		when(taskRepository.checkIfTaskExists(eq(task.getForEntity()), eq(task.getGroupIdentifier()), eq(task.getPlanIdentifier()), eq(task.getCode())))
				.thenReturn(true);
		assertTrue(taskService.taskExists(task));
	}

	@Test
	public void testAddTaskChecksIfTaskExists(){
		Task task = initializeTask();
		taskService = spy(taskService);
		taskService.addTask(task);
		verify(taskService).taskExists(eq(task));
	}

	@Test
	public void testAddTask() {
		Task task = initializeTask();
		taskService.addTask(task);
		verify(taskRepository).add(task);
	}

	@Test
	public void testAddTaskDoesNotDuplicate(){
		taskService = spy(taskService);

		doReturn(false, true, true, true, true)
				.when(taskService)
				.taskExists(any(Task.class));

		Task task = initializeTask();
		taskService.addTask(task);
		verify(taskRepository).add(task);
		taskService.addTask(task);
		taskService.addTask(task);
		taskService.addTask(task);
		verify(taskRepository, times(1)).add(task);
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
		assertEquals(TaskPriority.ROUTINE, task.getPriority());
		assertEquals("IRS", task.getCode());
		assertEquals("Spray House", task.getDescription());
		assertEquals("IRS Visit", task.getFocus());
		assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getForEntity());
		assertEquals("2018-11-10T2200", task.getExecutionPeriod().getStart().toString(dateFormat));
		assertNull(task.getExecutionPeriod().getEnd());
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

	private Task initializeTask() {
		Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new TaskDateTimeTypeConverter())
				.serializeNulls().create();
		return gson.fromJson(
				"{\"identifier\":\"tsk11231jh22\",\"campaignIdentifier\":\"IRS_2018_S1\",\"groupIdentifier\":\"2018_IRS-3734{\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":\"routine\",\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc\",\"executionPeriod\":{\"start\":\"2018-11-10T2200\",\"end\":null},\"authoredOn\":\"2018-10-31T0700\",\"lastModified\":\"2018-10-31T0700\",\"owner\":\"demouser\",\"note\":[{\"authorString\":\"demouser\",\"time\":\"2018-01-01T0800\",\"text\":\"This should be assigned to patrick.\"}],\"serverVersion\":0}",
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
		assertEquals(0l,argumentCaptor.getValue().getServerVersion().longValue());
		assertEquals("Not Sprayable", argumentCaptor.getValue().getBusinessStatus());

		assertEquals("tsk11231jh22", task.getIdentifier());
		assertEquals("2018_IRS-3734{", task.getGroupIdentifier());
		assertEquals(TaskPriority.ROUTINE, task.getPriority());
		assertEquals("IRS", task.getCode());
		assertEquals("Spray House", task.getDescription());
		assertEquals("IRS Visit", task.getFocus());
		assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getForEntity());
		assertEquals("2018-11-10T2200", task.getExecutionPeriod().getStart().toString(dateFormat));
		assertNull(task.getExecutionPeriod().getEnd());
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

	@Test
	public void testGetTasksByPlanAndOwner() {
		Task task = initializeTask();
		List<Task> expected = new ArrayList<>();
		expected.add(task);
		when(taskRepository.getTasksByPlanAndOwner("IRS_2018_S1", "demouser", 15421904649873l))
				.thenReturn(expected);
		List<Task> tasks = taskService.getTasksByPlanAndOwner("IRS_2018_S1", "demouser", 15421904649873l);
		verify(taskRepository).getTasksByPlanAndOwner("IRS_2018_S1", "demouser", 15421904649873l);
		assertEquals(task, tasks.get(0));
	}

	@Test
	public void testCountTasksByPlanAndOwner() {
		when(taskRepository.countTasksByPlanAndOwner("IRS_2018_S1", "demouser", 15421904649873l))
				.thenReturn(4l);
		Long tasks = taskService.countTasksByPlanAndOwner("IRS_2018_S1", "demouser", 15421904649873l);
		verify(taskRepository).countTasksByPlanAndOwner("IRS_2018_S1", "demouser", 15421904649873l);
		assertEquals(4, tasks.longValue());
	}

	@Test
	public void testCountTasksByPlanAndGroup() {
		when(taskRepository.countTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 15421904649873l))
				.thenReturn(7l);
		Long tasks = taskService.countTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 15421904649873l);
		verify(taskRepository).countTasksByPlanAndGroup("IRS_2018_S1", "2018_IRS-3734", 15421904649873l);
		assertEquals(7, tasks.longValue());
	}

	@Test
	public void testGetTasksBySearchBean() {
		List<Task> tasks = new ArrayList<>();
		tasks.add(initializeTask());
		TaskSearchBean taskSearchBean = new TaskSearchBean();
		taskSearchBean.setPlanIdentifier("IRS_2018_S1");
		when(taskRepository.getTasksBySearchBean(any(TaskSearchBean.class))).thenReturn(tasks);
		List<Task> result = taskService.getTasksBySearchBean(taskSearchBean);
		verify(taskRepository).getTasksBySearchBean(taskSearchBean);
		assertEquals(1, result.size());
	}

	@Test
	public void testFindTaskCountBySearchBean() {
		TaskSearchBean taskSearchBean = new TaskSearchBean();
		taskSearchBean.setPlanIdentifier("IRS_2018_S1");
		when(taskRepository.getTaskCount(any(TaskSearchBean.class))).thenReturn(1);
		int count = taskService.findTaskCountBySearchBean(taskSearchBean);
		verify(taskRepository).getTaskCount(taskSearchBean);
		assertEquals(1, count);
	}

}
