package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.postgres.TaskMetadata;
import org.opensrp.domain.postgres.TaskMetadataExample;
import org.opensrp.repository.TaskRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomTaskMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomTaskMetadataMapper;
import org.opensrp.search.TaskSearchBean;
import org.smartregister.converters.TaskConverter;
import org.smartregister.domain.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.fhir.model.resource.QuestionnaireResponse;

import static org.opensrp.util.RepositoryUtil.getPageSizeAndOffset;

@Repository
public class TaskRepositoryImpl extends BaseRepositoryImpl<Task> implements TaskRepository {
	
	private static final String SEQUENCE = "core.task_server_version_seq";
	
	@Autowired
	private CustomTaskMapper taskMapper;
	
	@Autowired
	private CustomTaskMetadataMapper taskMetadataMapper;
	
	@Override
	public Task get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		
		org.opensrp.domain.postgres.Task pgTask = taskMetadataMapper.selectByIdentifier(id);
		if (pgTask == null) {
			return null;
		}
		return convert(pgTask);
	}
	
	@Override
	@Transactional
	public void add(Task entity) {
		if (getUniqueField(entity) == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { // Task already added
			return;
		}
		
		org.opensrp.domain.postgres.Task pgTask = convert(entity, null);
		if (pgTask == null) {
			return;
		}
		
		int rowsAffected = taskMapper.insertSelectiveAndSetId(pgTask);
		if (rowsAffected < 1 || pgTask.getId() == null) {
			throw new IllegalStateException();
		}
		
		TaskMetadata taskMetadata = createMetadata(entity, pgTask.getId());
		
		taskMetadataMapper.insertSelective(taskMetadata);
		
	}
	
	@Override
	@Transactional
	public void update(Task entity) {
		if (getUniqueField(entity) == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Task does not exist
			return;
		}
		
		org.opensrp.domain.postgres.Task pgTask = convert(entity, id);
		if (pgTask == null) {
			return;
		}
		TaskMetadata taskMetadata = createMetadata(entity, pgTask.getId());
		
		int rowsAffected = taskMapper.updateByPrimaryKey(pgTask);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
	
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andTaskIdEqualTo(id);
		TaskMetadata metadata = taskMetadataMapper.selectByExample(taskMetadataExample).get(0);
		taskMetadata.setId(metadata.getId());
		taskMetadata.setDateCreated(metadata.getDateCreated());
		taskMetadataMapper.updateByPrimaryKey(taskMetadata);
		
	}
	
	@Override
	public List<Task> getAll() {
		List<org.opensrp.domain.postgres.Task> tasks = taskMetadataMapper.selectMany(new TaskMetadataExample(), 0,
		    DEFAULT_FETCH_SIZE);
		return convert(tasks);
	}
	
	@Override
	public List<Task> getTasksByPlanAndGroup(String plan, String group, long serverVersion) {
		return getTasksByPlanAndGroup(plan, group, serverVersion, false);
	}
	
	@Override
	public List<Task> getTasksByPlanAndGroup(String plan, String group, long serverVersion, boolean returnPk) {
		List<String> plans = Arrays.asList(org.apache.commons.lang.StringUtils.split(plan, ","));
		List<String> groups = Arrays.asList(org.apache.commons.lang.StringUtils.split(group, ","));
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andPlanIdentifierIn(plans).andGroupIdentifierIn(groups)
		        .andServerVersionGreaterThanOrEqualTo(serverVersion);
		taskMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		List<org.opensrp.domain.postgres.Task> tasks = taskMetadataMapper.selectMany(taskMetadataExample, 0,
		    DEFAULT_FETCH_SIZE);
		return convert(tasks,returnPk);
	}
	
	@Override
	public List<Task> findByEmptyServerVersion() {
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andServerVersionIsNull();
		taskMetadataExample.or(taskMetadataExample.createCriteria().andServerVersionEqualTo(0l));
		List<org.opensrp.domain.postgres.Task> tasks = taskMetadataMapper.selectMany(taskMetadataExample, 0,
		    DEFAULT_FETCH_SIZE);
		return convert(tasks);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair<List<String>, Long> findAllIds(Long serverVersion, int limit) {
		Long lastServerVersion = null;
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion);
		taskMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		return getTaskListLongPair(limit, lastServerVersion, taskMetadataExample);
	}
	
	@Override
	public Pair<List<String>, Long> findAllIds(Long serverVersion, int limit, Date fromDate, Date toDate) {
		if (toDate == null && fromDate == null) {
			return findAllIds(serverVersion, limit);
		} else {
			Long lastServerVersion = null;
			TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
			TaskMetadataExample.Criteria criteria = taskMetadataExample.createCriteria();
			criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
			taskMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
			
			if (toDate != null && fromDate != null) {
				criteria.andDateCreatedBetween(fromDate, toDate);
			} else if (fromDate != null) {
				criteria.andDateCreatedGreaterThanOrEqualTo(fromDate);
			} else {
				criteria.andDateCreatedLessThanOrEqualTo(toDate);
			}
			
			return getTaskListLongPair(limit, lastServerVersion, taskMetadataExample);
		}
	}
	
	private Pair<List<String>, Long> getTaskListLongPair(int limit, Long lastServerVersion,
	        TaskMetadataExample taskMetadataExample) {
		int fetchLimit = limit > 0 ? limit : DEFAULT_FETCH_SIZE;
		Long serverVersion = lastServerVersion;
		TaskMetadataExample metadataExample = taskMetadataExample;
		
		List<String> taskIdentifiers = taskMetadataMapper.selectManyIds(metadataExample, 0, fetchLimit);
		
		if (taskIdentifiers != null && !taskIdentifiers.isEmpty()) {
			metadataExample = new TaskMetadataExample();
			metadataExample.createCriteria().andIdentifierEqualTo(taskIdentifiers.get(taskIdentifiers.size() - 1));
			List<TaskMetadata> taskMetaDataList = taskMetadataMapper.selectByExample(metadataExample);
			
			serverVersion = taskMetaDataList != null && !taskMetaDataList.isEmpty()
			        ? taskMetaDataList.get(0).getServerVersion()
			        : 0;
			
		}
		
		return Pair.of(taskIdentifiers, serverVersion);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Task> getAllTasks(Long serverVersion, int limit) {
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion);
		taskMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		
		List<org.opensrp.domain.postgres.Task> tasks = taskMetadataMapper.selectMany(taskMetadataExample, 0, limit);
		return convert(tasks);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countAllTasks(Long serverVersion) {
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion);
		return taskMetadataMapper.countMany(taskMetadataExample);
	}
	
	@Override
	public List<Task> getTasksByPlanAndOwner(String plan, String owner, long serverVersion) {
		List<String> plans = Arrays.asList(org.apache.commons.lang.StringUtils.split(plan, ","));
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andPlanIdentifierIn(plans).andOwnerEqualTo(owner)
		        .andServerVersionGreaterThanOrEqualTo(serverVersion);
		taskMetadataExample.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		List<org.opensrp.domain.postgres.Task> tasks = taskMetadataMapper.selectMany(taskMetadataExample, 0,
		    DEFAULT_FETCH_SIZE);
		return convert(tasks);
	}
	
	@Override
	public Long countTasksByPlanAndGroup(String plan, String group, long serverVersion) {
		List<String> campaigns = Arrays.asList(org.apache.commons.lang.StringUtils.split(plan, ","));
		List<String> groups = Arrays.asList(org.apache.commons.lang.StringUtils.split(group, ","));
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andPlanIdentifierIn(campaigns).andGroupIdentifierIn(groups)
		        .andServerVersionGreaterThanOrEqualTo(serverVersion);
		return taskMetadataMapper.countByExample(taskMetadataExample);
	}
	
	@Override
	public Long countTasksByPlanAndOwner(String plan, String owner, long serverVersion) {
		List<String> plans = Arrays.asList(org.apache.commons.lang.StringUtils.split(plan, ","));
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andPlanIdentifierIn(plans).andOwnerEqualTo(owner)
		        .andServerVersionGreaterThanOrEqualTo(serverVersion);
		return taskMetadataMapper.countByExample(taskMetadataExample);
	}
	
	@Override
	@Transactional
	public void safeRemove(Task entity) {
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		TaskMetadataExample taskMetadataExample = new TaskMetadataExample();
		taskMetadataExample.createCriteria().andTaskIdEqualTo(id);
		int rowsAffected = taskMetadataMapper.deleteByExample(taskMetadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		taskMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	protected Long retrievePrimaryKey(Task task) {
		Object uniqueId = getUniqueField(task);
		if (uniqueId == null) {
			return null;
		}
		
		String identifier = uniqueId.toString();
		
		org.opensrp.domain.postgres.Task pgTask = taskMetadataMapper.selectByIdentifier(identifier);
		if (pgTask == null) {
			return null;
		}
		return pgTask.getId();
	}
	
	@Override
	protected Object getUniqueField(Task task) {
		if (task == null) {
			return null;
		}
		return task.getIdentifier();
	}
	
	private Task convert(org.opensrp.domain.postgres.Task pgTask) {
		return convert(pgTask,false);
	}
	
	private Task convert(org.opensrp.domain.postgres.Task pgTask, boolean returnPk) {
		if (pgTask == null || pgTask.getJson() == null || !(pgTask.getJson() instanceof Task)) {
			return null;
		}
		Task task=(Task) pgTask.getJson();
		if(returnPk) {
			task.setRowid(pgTask.getId());
		}
		return task;
	}
	
	private org.opensrp.domain.postgres.Task convert(Task task, Long primaryKey) {
		if (task == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Task pgTask = new org.opensrp.domain.postgres.Task();
		pgTask.setId(primaryKey);
		pgTask.setJson(task);
		
		return pgTask;
	}
	
	private List<Task> convert(List<org.opensrp.domain.postgres.Task> tasks){
		return convert(tasks,false);
	}
	
	private List<Task> convert(List<org.opensrp.domain.postgres.Task> tasks,boolean returnPk) {
		if (tasks == null || tasks.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Task> convertedTasks = new ArrayList<>();
		for (org.opensrp.domain.postgres.Task task : tasks) {
			Task convertedTask = convert(task,returnPk);
			if (convertedTask != null) {
				convertedTasks.add(convertedTask);
			}
		}
		
		return convertedTasks;
	}
	
	private TaskMetadata createMetadata(Task entity, Long id) {
		TaskMetadata taskMetadata = new TaskMetadata();
		taskMetadata.setTaskId(id);
		taskMetadata.setIdentifier(entity.getIdentifier());
		taskMetadata.setPlanIdentifier(entity.getPlanIdentifier());
		taskMetadata.setGroupIdentifier(entity.getGroupIdentifier());
		taskMetadata.setForEntity(entity.getForEntity());
		taskMetadata.setServerVersion(entity.getServerVersion());
		taskMetadata.setOwner(entity.getOwner());
		taskMetadata.setCode(entity.getCode());
		
		if (id != null) {
			taskMetadata.setDateEdited(new Date());
		}
		return taskMetadata;
	}
	
	@Override
	public List<com.ibm.fhir.model.resource.Task> findTasksForEntity(String id, String planIdentifier) {
		TaskMetadataExample example = new TaskMetadataExample();
		example.createCriteria().andPlanIdentifierEqualTo(planIdentifier).andForEntityEqualTo(id);
		return convertToFHIRTasks(convert(taskMetadataMapper.selectMany(example, 0, DEFAULT_FETCH_SIZE)));
	}
	
	@Override
	public void saveTask(Task task, QuestionnaireResponse questionnaireResponse) {
		task.setServerVersion(getNextServerVersion());
		add(task);
	}
	
	@Override
	public boolean checkIfTaskExists(String baseEntityId, String jurisdiction, String planIdentifier, String code) {
		List<String> statuses = new ArrayList<>();
		statuses.add("Cancelled");
		statuses.add("Archived");
		
		int taskCount = taskMetadataMapper.countTasksByEntityIdAndPlanIdentifierAndCode(baseEntityId, jurisdiction,
		    planIdentifier, code, statuses);
		return taskCount >= 1;
	}
	
	@Override
	public List<com.ibm.fhir.model.resource.Task> findAllTasksForEntity(String id) {
		TaskMetadataExample example = new TaskMetadataExample();
		example.createCriteria().andForEntityEqualTo(id);
		return convertToFHIRTasks(convert(taskMetadataMapper.selectMany(example, 0, DEFAULT_FETCH_SIZE)));
	}
	
	@Override
	public Task getTaskByIdentifier(String identifier) {
		return get(identifier);
	}
	
	@Override
	public Task updateTask(Task task) {
		task.setServerVersion(getNextServerVersion());
		update(task);
		return get(task.getIdentifier());
	}

	@Override
	public List<com.ibm.fhir.model.resource.Task> findTasksByJurisdiction(String s) {
		throw new NotImplementedException();
	}

	@Override
	public List<Task> getTasksBySearchBean(TaskSearchBean taskSearchBean) {
		Pair<Integer, Integer> pageSizeAndOffset = getPageSizeAndOffset(taskSearchBean);
		return taskMetadataMapper.selectTasksBySearchBean(taskSearchBean, pageSizeAndOffset.getRight(),
				pageSizeAndOffset.getLeft());
	}

	@Override
	public int getTaskCount(TaskSearchBean taskSearchBean) {
		return taskMetadataMapper.selectTaskCount(taskSearchBean);
	}

	private List<com.ibm.fhir.model.resource.Task> convertToFHIRTasks(List<Task> tasks) {
		return tasks.stream().map(task -> TaskConverter.convertTasktoFihrResource(task)).collect(Collectors.toList());
	}
	
	@Override
	protected String getSequenceName() {
		return SEQUENCE;
	}
	
}
