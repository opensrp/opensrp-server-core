package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.postgres.EventExample;
import org.opensrp.domain.postgres.EventMetadata;
import org.opensrp.domain.postgres.EventMetadataExample;
import org.opensrp.domain.postgres.EventMetadataExample.Criteria;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomEventMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomEventMetadataMapper;
import org.opensrp.search.EventSearchBean;
import org.smartregister.converters.EventConverter;
import org.smartregister.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.fhir.model.resource.QuestionnaireResponse;

@Repository("eventsRepositoryPostgres")
public class EventsRepositoryImpl extends BaseRepositoryImpl<Event> implements EventsRepository {
	
	@Autowired
	private CustomEventMapper eventMapper;
	
	@Autowired
	private CustomEventMetadataMapper eventMetadataMapper;
	
	@Override
	public Event get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		org.opensrp.domain.postgres.Event pgEvent = eventMetadataMapper.selectByDocumentId(id);
		
		return convert(pgEvent);
	}
	
	@Transactional
	@Override
	public void add(Event entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { // Event already added
			throw new IllegalArgumentException("Event exists");
		}
		
		if (entity.getId() == null || entity.getId().isEmpty())
			entity.setId(UUID.randomUUID().toString());
		setRevision(entity);
		
		org.opensrp.domain.postgres.Event pgEvent = convert(entity, null);
		if (pgEvent == null) {
			throw new IllegalStateException();
		}
		
		int rowsAffected = eventMapper.insertSelectiveAndSetId(pgEvent);
		if (rowsAffected < 1 || pgEvent.getId() == null) {
			throw new IllegalStateException();
		}
		
		updateServerVersion(pgEvent, entity);
		
		EventMetadata eventMetadata = createMetadata(entity, pgEvent.getId());
		if (eventMetadata != null) {
			eventMetadataMapper.insertSelective(eventMetadata);
		}
		
	}
	
	private void updateServerVersion(org.opensrp.domain.postgres.Event pgEvent, Event entity) {
		long serverVersion = eventMapper.selectServerVersionByPrimaryKey(pgEvent.getId());
		entity.setServerVersion(serverVersion);
		pgEvent.setJson(entity);
		int rowsAffected = eventMapper.updateByPrimaryKeySelective(pgEvent);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
	}
	
	@Override
	public void update(Event entity) {
		update(entity, false);
	}
	
	@Transactional
	@Override
	public void update(Event entity, boolean allowArchived) {
		if (entity == null || entity.getBaseEntityId() == null) {
			throw new IllegalStateException();
		}
		
		Long id = retrievePrimaryKey(entity, allowArchived);
		if (id == null) { // Event not added
			throw new IllegalStateException();
		}
		
		setRevision(entity);
		
		org.opensrp.domain.postgres.Event pgEvent = convert(entity, id);
		if (pgEvent == null) {
			throw new IllegalStateException();
		}
		
		int rowsAffected = eventMapper.updateByPrimaryKeyAndGenerateServerVersion(pgEvent);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
		
		updateServerVersion(pgEvent, entity);
		
		EventMetadata eventMetadata = createMetadata(entity, id);
		if (eventMetadata == null) {
			throw new IllegalStateException();
		}
		
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		Criteria criteria = eventMetadataExample.createCriteria();
		criteria.andEventIdEqualTo(id);
		if (!allowArchived) {
			criteria.andDateDeletedIsNull();
		}
		eventMetadata.setId(eventMetadataMapper.selectByExample(eventMetadataExample).get(0).getId());
		eventMetadataMapper.updateByPrimaryKey(eventMetadata);
		
	}
	
	@Override
	public List<Event> getAll() {
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		eventMetadataExample.createCriteria().andDateDeletedIsNull();
		List<org.opensrp.domain.postgres.Event> events = eventMetadataMapper.selectManyWithRowBounds(eventMetadataExample, 0,
		    DEFAULT_FETCH_SIZE);
		return convert(events);
	}
	
	@Override
	public void safeRemove(Event entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		Date dateDeleted = entity.getDateVoided() == null ? new Date() : entity.getDateVoided().toDate();
		EventMetadata eventMetadata = new EventMetadata();
		eventMetadata.setDateDeleted(dateDeleted);
		
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		eventMetadataExample.createCriteria().andEventIdEqualTo(id).andDateDeletedIsNull();
		int rowsAffected = eventMetadataMapper.updateByExampleSelective(eventMetadata, eventMetadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		org.opensrp.domain.postgres.Event pgEvent = new org.opensrp.domain.postgres.Event();
		pgEvent.setId(id);
		pgEvent.setDateDeleted(dateDeleted);
		eventMapper.updateByPrimaryKeySelective(pgEvent);
		
	}
	
	@Override
	public List<Event> findAllByIdentifier(String identifier) {
		List<org.opensrp.domain.postgres.Event> events = eventMapper.selectByIdentifier(identifier);
		return convert(events);
	}
	
	@Override
	public List<Event> findAllByIdentifier(String identifierType, String identifier) {
		List<org.opensrp.domain.postgres.Event> events = eventMapper.selectByIdentifierOfType(identifierType, identifier);
		return convert(events);
	}
	
	@Override
	public Event findById(String id) {
		return get(id);
	}
	
	@Override
	public Event findByFormSubmissionId(String formSubmissionId, boolean includeArchived) {
		if (StringUtils.isBlank(formSubmissionId)) {
			return null;
		}
		EventMetadataExample example = new EventMetadataExample();
		Criteria criteria = example.createCriteria().andFormSubmissionIdEqualTo(formSubmissionId);
		if (!includeArchived)
			criteria.andDateDeletedIsNull();
		List<org.opensrp.domain.postgres.Event> events = eventMetadataMapper.selectMany(example);
		if (events.size() > 1) {
			throw new IllegalStateException("Multiple events for formSubmissionId " + formSubmissionId);
		} else if (!events.isEmpty())
			return convert(events.get(0));
		else
			return null;
	}
	
	@Override
	public List<Event> findByBaseEntityId(String baseEntityId) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andBaseEntityIdEqualTo(baseEntityId).andDateDeletedIsNull();
		return convert(eventMetadataMapper.selectMany(example));
	}
	
	@Override
	public Event findByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andBaseEntityIdEqualTo(baseEntityId).andFormSubmissionIdEqualTo(formSubmissionId)
		        .andDateDeletedIsNull();
		List<org.opensrp.domain.postgres.Event> events = eventMetadataMapper.selectMany(example);
		if (events.size() > 1) {
			throw new IllegalStateException("Multiple events for baseEntityId and formSubmissionId combination ("
			        + baseEntityId + "," + formSubmissionId + ")");
		} else if (!events.isEmpty())
			return convert(events.get(0));
		else
			return null;
	}
	
	@Override
	public List<Event> findByBaseEntityAndType(String baseEntityId, String eventType) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andBaseEntityIdEqualTo(baseEntityId).andEventTypeEqualTo(eventType).andDateDeletedIsNull();
		return convert(eventMetadataMapper.selectMany(example));
	}
	
	@Override
	public List<Event> findEvents(EventSearchBean eventSearchBean) {
		EventMetadataExample example = new EventMetadataExample();
		Criteria criteria = example.createCriteria();
		if (StringUtils.isNotEmpty(eventSearchBean.getBaseEntityId()))
			criteria.andBaseEntityIdEqualTo(eventSearchBean.getBaseEntityId());
		if (eventSearchBean.getEventDateFrom() != null && eventSearchBean.getEventDateTo() != null)
			criteria.andEventDateBetween(eventSearchBean.getEventDateFrom().toDate(),
			    eventSearchBean.getEventDateTo().toDate());
		if (StringUtils.isNotEmpty(eventSearchBean.getEventType()))
			criteria.andEventTypeEqualTo(eventSearchBean.getEventType());
		if (StringUtils.isNotEmpty(eventSearchBean.getEntityType()))
			criteria.andEntityTypeEqualTo(eventSearchBean.getEntityType());
		if (StringUtils.isNotEmpty(eventSearchBean.getProviderId()))
			criteria.andProviderIdEqualTo(eventSearchBean.getProviderId());
		if (StringUtils.isNotEmpty(eventSearchBean.getLocationId()))
			criteria.andLocationIdEqualTo(eventSearchBean.getLocationId());
		if (eventSearchBean.getLastEditFrom() != null && eventSearchBean.getLastEditTo() != null)
			criteria.andDateEditedBetween(eventSearchBean.getLastEditFrom().toDate(),
			    eventSearchBean.getLastEditTo().toDate());
		if (StringUtils.isNotEmpty(eventSearchBean.getTeam()))
			criteria.andTeamEqualTo(eventSearchBean.getTeam());
		if (StringUtils.isNotEmpty(eventSearchBean.getTeamId()))
			criteria.andTeamIdEqualTo(eventSearchBean.getTeamId());
		if (!criteria.isValid())
			throw new IllegalArgumentException("Atleast one search filter must be specified");
		criteria.andDateDeletedIsNull();
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> findEventsByDynamicQuery(String query) {
		throw new IllegalArgumentException("Dynamic query feature not supported");
	}
	
	@Override
	public List<Event> findByServerVersion(long serverVersion) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion + 1);
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar) {
		return convert(eventMetadataMapper.selectNotInOpenMRSByServerVersion(serverVersion, calendar.getTimeInMillis(),
		    DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> notInOpenMRSByServerVersionAndType(String type, long serverVersion, Calendar calendar) {
		return convert(eventMetadataMapper.selectNotInOpenMRSByServerVersionAndType(type, serverVersion,
		    calendar.getTimeInMillis(), DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> findByClientAndConceptAndDate(String baseEntityId, String concept, String conceptValue,
	        String dateFrom, String dateTo) {
		if (StringUtils.isBlank(baseEntityId) && StringUtils.isBlank(concept) && StringUtils.isBlank(conceptValue))
			return new ArrayList<Event>();
		Date from = null;
		Date to = null;
		if (StringUtils.isNotEmpty(dateFrom))
			from = new DateTime(dateFrom).toDate();
		if (StringUtils.isNotEmpty(dateTo))
			to = new DateTime(dateTo).toDate();
		return convert(eventMapper.selectByBaseEntityIdConceptAndDate(baseEntityId, concept, conceptValue, from, to));
	}
	
	@Override
	public List<Event> findByBaseEntityIdAndConceptParentCode(String baseEntityId, String concept, String parentCode) {
		if (StringUtils.isBlank(baseEntityId) && StringUtils.isBlank(concept) && StringUtils.isBlank(parentCode))
			return new ArrayList<Event>();
		return convert(eventMapper.selectByBaseEntityIdAndConceptParentCode(baseEntityId, concept, parentCode));
	}
	
	@Override
	public List<Event> findByConceptAndValue(String concept, String conceptValue) {
		if (StringUtils.isBlank(concept) && StringUtils.isBlank(conceptValue))
			return new ArrayList<Event>();
		return convert(eventMapper.selectByConceptAndValue(concept, conceptValue));
	}
	
	@Override
	public List<Event> findByEmptyServerVersion() {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andServerVersionIsNull();
		example.or(example.createCriteria().andServerVersionEqualTo(0l));
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> findEvents(EventSearchBean eventSearchBean, String sortBy, String sortOrder, int limit) {
		EventMetadataExample example = new EventMetadataExample();
		Criteria criteria = populateEventSearchCriteria(eventSearchBean, example);
		
		criteria.andDateDeletedIsNull();
		example.setOrderByClause(getOrderByClause(sortBy, sortOrder));
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, limit));
	}
	
	private Criteria populateEventSearchCriteria(EventSearchBean eventSearchBean, EventMetadataExample example) {
		Criteria criteria = example.createCriteria();
		
		addTeamCriteria(criteria, eventSearchBean);
		
		addTeamIdCriteria(criteria, eventSearchBean);
		
		addProviderIdCriteria(criteria, eventSearchBean);
		
		addLocationIdCriteria(criteria, eventSearchBean);
		
		addBaseEntityCriteria(criteria, eventSearchBean);
		
		if (eventSearchBean.getServerVersion() != null)
			criteria.andServerVersionGreaterThanOrEqualTo(eventSearchBean.getServerVersion());
		
		if (StringUtils.isNotEmpty(eventSearchBean.getEventType()))
			criteria.andEventTypeEqualTo(eventSearchBean.getEventType());
		
		if (!criteria.isValid())
			throw new IllegalArgumentException("Atleast one search filter must be specified");
		return criteria;
	}
	
	private void addTeamCriteria(Criteria criteria, EventSearchBean eventSearchBean) {
		if (StringUtils.isNotEmpty(eventSearchBean.getTeam())) {
			if (eventSearchBean.getTeam().contains(",")) {
				String[] teamsArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getTeam(), ",");
				criteria.andTeamIn(Arrays.asList(teamsArray));
			} else {
				criteria.andTeamEqualTo(eventSearchBean.getTeam());
			}
		}
	}
	
	private void addTeamIdCriteria(Criteria criteria, EventSearchBean eventSearchBean) {
		if (StringUtils.isNotEmpty(eventSearchBean.getTeamId())) {
			if (eventSearchBean.getTeamId().contains(",")) {
				String[] teamsArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getTeamId(), ",");
				criteria.andTeamIdIn(Arrays.asList(teamsArray));
			} else {
				criteria.andTeamIdEqualTo(eventSearchBean.getTeamId());
			}
		}
	}
	
	private void addProviderIdCriteria(Criteria criteria, EventSearchBean eventSearchBean) {
		if (StringUtils.isNotEmpty(eventSearchBean.getProviderId())) {
			if (eventSearchBean.getProviderId().contains(",")) {
				String[] providersArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getProviderId(), ",");
				criteria.andProviderIdIn(Arrays.asList(providersArray));
			} else {
				criteria.andProviderIdEqualTo(eventSearchBean.getProviderId());
			}
		}
	}
	
	private void addLocationIdCriteria(Criteria criteria, EventSearchBean eventSearchBean) {
		if (StringUtils.isNotEmpty(eventSearchBean.getLocationId())) {
			if (eventSearchBean.getLocationId().contains(",")) {
				String[] locationArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getLocationId(), ",");
				criteria.andLocationIdIn(Arrays.asList(locationArray));
			} else {
				criteria.andLocationIdEqualTo(eventSearchBean.getLocationId());
			}
		}
	}
	
	private void addBaseEntityCriteria(Criteria criteria, EventSearchBean eventSearchBean) {
		if (StringUtils.isNotEmpty(eventSearchBean.getBaseEntityId())) {
			if (eventSearchBean.getBaseEntityId().contains(",")) {
				String[] idsArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getBaseEntityId(), ",");
				criteria.andBaseEntityIdIn(Arrays.asList(idsArray));
			} else {
				criteria.andBaseEntityIdEqualTo(eventSearchBean.getBaseEntityId());
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countEvents(EventSearchBean eventSearchBean) {
		EventMetadataExample example = new EventMetadataExample();
		Criteria criteria = populateEventSearchCriteria(eventSearchBean, example);
		criteria.andDateDeletedIsNull();
		return eventMetadataMapper.countByExample(example);
	}
	
	/**
	 * Compatibility method inherited from couch to fetch events of a given type within the current
	 * month
	 *
	 * @param eventType the type of event to query
	 * @return list of events of given type within the current month
	 */
	@Override
	public List<Event> findEventByEventTypeBetweenTwoDates(String eventType) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andEventTypeEqualTo(eventType)
		        .andDateCreatedBetween(calendar.getTime(),new Date()).andDateDeletedIsNull();
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<Event> findByProvider(String provider) {
		EventMetadataExample example = new EventMetadataExample();
		example.createCriteria().andProviderIdEqualTo(provider);
		return convert(eventMetadataMapper.selectManyWithRowBounds(example, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public Pair<List<String>, Long> findIdsByEventType(String eventType, boolean isDeleted, Long serverVersion, int limit) {
		Long lastServerVersion = null;
		EventMetadataExample example = new EventMetadataExample();
		Criteria criteria = example.createCriteria();
		criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
		
		if (!StringUtils.isBlank(eventType)) {
			criteria.andEventTypeEqualTo(eventType);
		}
		
		if (isDeleted) {
			criteria.andDateDeletedIsNotNull();
		} else {
			criteria.andDateDeletedIsNull();
		}
		
		example.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
		
		return getEventListLongPair(limit, lastServerVersion, example);
	}
	
	@Override
	public Pair<List<String>, Long> findIdsByEventType(String eventType, boolean isDeleted, Long serverVersion, int limit,
	        Date fromDate, Date toDate) {
		if (fromDate == null && toDate == null) {
			return findIdsByEventType(eventType, isDeleted, serverVersion, limit);
		} else {
			Long lastServerVersion = null;
			EventMetadataExample example = new EventMetadataExample();
			Criteria criteria = example.createCriteria();
			criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
			example.setOrderByClause(getOrderByClause(SERVER_VERSION, ASCENDING));
			
			if (!StringUtils.isBlank(eventType)) {
				criteria.andEventTypeEqualTo(eventType);
			}
			
			if (isDeleted) {
				criteria.andDateDeletedIsNotNull();
			} else {
				criteria.andDateDeletedIsNull();
			}
			
			if (fromDate != null && toDate != null) {
				criteria.andDateCreatedBetween(fromDate, toDate);
			} else if (fromDate != null) {
				criteria.andDateCreatedGreaterThanOrEqualTo(fromDate);
			} else {
				criteria.andDateCreatedLessThanOrEqualTo(toDate);
			}
			
			return getEventListLongPair(limit, lastServerVersion, example);
		}
	}
	
	private Pair<List<String>, Long> getEventListLongPair(int limit, Long lastServerVersion, EventMetadataExample example) {
		int fetchLimit = limit > 0 ? limit : DEFAULT_FETCH_SIZE;
		
		Long serverVersion = lastServerVersion;
		EventMetadataExample eventMetadataExample = example;
		List<String> eventIdentifiers = eventMetadataMapper.selectManyIds(eventMetadataExample, 0, fetchLimit);
		
		if (eventIdentifiers != null && !eventIdentifiers.isEmpty()) {
			eventMetadataExample = new EventMetadataExample();
			eventMetadataExample.createCriteria().andDocumentIdEqualTo(eventIdentifiers.get(eventIdentifiers.size() - 1));
			List<EventMetadata> eventMetaDataList = eventMetadataMapper.selectByExample(eventMetadataExample);
			
			serverVersion = eventMetaDataList != null && !eventMetaDataList.isEmpty()
			        ? eventMetaDataList.get(0).getServerVersion()
			        : 0;
		}
		return Pair.of(eventIdentifiers, serverVersion);
	}
	
	@Override
	public List<String> findBaseEntityIdsByLocation(String locationId) {
		EventMetadataExample example = new EventMetadataExample();
		Criteria criteria = example.createCriteria();
		criteria.andLocationIdEqualTo(locationId);
		example.setDistinct(true);
		return eventMetadataMapper.selectManyBaseEntityIds(example);
	}

	/**
	 *
	 * @param planIdentifier
	 * @param eventType
	 * @param fromDate
	 * @param toDate
	 * @return
	 */

	@Override
	public List<org.opensrp.domain.postgres.Event> getEventData(String planIdentifier, String eventType, Date fromDate,
			Date toDate) {
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		EventMetadataExample.Criteria criteria = eventMetadataExample.createCriteria();
		criteria.andPlanIdentifierEqualTo(planIdentifier).andEventTypeEqualTo(eventType).andDateDeletedIsNull();
		if (fromDate != null) {
			criteria.andDateCreatedGreaterThanOrEqualTo(fromDate);
		}
		if (toDate != null) {
			criteria.andDateCreatedLessThanOrEqualTo(toDate);
		}
		return eventMetadataMapper.selectManyWithRowBounds(eventMetadataExample, 0, DEFAULT_FETCH_SIZE);
	}

	/**
	 * Gets events for a entity with details values
	 * 
	 * @param baseEntityId entity id
	 * @param planIdentifier plan id
	 * @return events for an entity in a plan
	 */
	private List<Event> findByBaseEntityIdAndPlanIdentifier(String baseEntityId, String planIdentifier) {
		return convert(eventMetadataMapper.selectByBaseEntityIdAndPlanIdentifier(baseEntityId, planIdentifier));
	}
	
	/**
	 * Get the primary key of an event
	 * 
	 * @param event
	 * @param allowArchived
	 * @return the promary key
	 */
	private Long retrievePrimaryKey(Event event, boolean allowArchived) {
		Object uniqueId = getUniqueField(event);
		if (uniqueId == null) {
			return null;
		}
		
		String documentId = uniqueId.toString();
		
		EventMetadataExample eventMetadataExample = new EventMetadataExample();
		Criteria criteria = eventMetadataExample.createCriteria();
		criteria.andDocumentIdEqualTo(documentId);
		if (!allowArchived) {
			criteria.andDateDeletedIsNull();
		}
		
		return eventMetadataMapper.selectPrimaryKey(eventMetadataExample);
		
	}
	
	@Override
	protected Long retrievePrimaryKey(Event event) {
		return retrievePrimaryKey(event, false);
	}
	
	@Override
	protected Object getUniqueField(Event t) {
		if (t == null) {
			return null;
		}
		return t.getId();
	}
	
	// Private Methods
	private Event convert(org.opensrp.domain.postgres.Event event) {
		if (event == null || event.getJson() == null || !(event.getJson() instanceof Event)) {
			return null;
		}
		return (Event) event.getJson();
	}
	
	private org.opensrp.domain.postgres.Event convert(Event event, Long primaryKey) {
		if (event == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Event pgEvent = new org.opensrp.domain.postgres.Event();
		pgEvent.setId(primaryKey);
		pgEvent.setJson(event);
		
		return pgEvent;
	}
	
	private List<Event> convert(List<org.opensrp.domain.postgres.Event> events) {
		if (events == null || events.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Event> convertedEvents = new ArrayList<>();
		for (org.opensrp.domain.postgres.Event event : events) {
			Event convertedEvent = convert(event);
			if (convertedEvent != null) {
				convertedEvents.add(convertedEvent);
			}
		}
		
		return convertedEvents;
	}
	
	private EventMetadata createMetadata(Event event, Long eventId) {
		try {
			EventMetadata eventMetadata = new EventMetadata();
			eventMetadata.setBaseEntityId(event.getBaseEntityId());
			eventMetadata.setEventId(eventId);
			eventMetadata.setDocumentId(event.getId());
			eventMetadata.setBaseEntityId(event.getBaseEntityId());
			eventMetadata.setFormSubmissionId(event.getFormSubmissionId());
			eventMetadata.setOpenmrsUuid(event.getIdentifier(AllConstants.Client.OPENMRS_UUID_IDENTIFIER_TYPE));
			eventMetadata.setEventType(event.getEventType());
			if (event.getEventDate() != null)
				eventMetadata.setEventDate(event.getEventDate().toDate());
			eventMetadata.setEntityType(event.getEntityType());
			eventMetadata.setProviderId(event.getProviderId());
			eventMetadata.setLocationId(event.getLocationId());
			eventMetadata.setTeam(event.getTeam());
			eventMetadata.setTeamId(event.getTeamId());
			eventMetadata.setServerVersion(event.getServerVersion());
			if (event.getDateCreated() != null)
				eventMetadata.setDateCreated(event.getDateCreated().toDate());
			if (event.getDateEdited() != null)
				eventMetadata.setDateEdited(event.getDateEdited().toDate());
			if (event.getDateVoided() != null)
				eventMetadata.setDateDeleted(event.getDateVoided().toDate());
			String planIdentifier = event.getDetails() != null ? event.getDetails().get("planIdentifier") : null;
			eventMetadata.setPlanIdentifier(planIdentifier);
			return eventMetadata;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	/**
	 * Method should be used only during Unit testing Deletes all existing records
	 */
	public void removeAll() {
		eventMetadataMapper.deleteByExample(new EventMetadataExample());
		eventMapper.deleteByExample(new EventExample());
		
	}
	
	@Override
	public List<QuestionnaireResponse> findEventsByEntityIdAndPlan(String resourceId, String planIdentifier) {
		return findByBaseEntityIdAndPlanIdentifier(resourceId, planIdentifier).stream()
		        .map(event -> EventConverter.convertEventToEncounterResource(event)).collect(Collectors.toList());
	}

	@Override
	public List<QuestionnaireResponse> findEventsByJurisdictionIdAndPlan(String jurisdictionId, String planIdentifier) {
		EventMetadataExample example=new EventMetadataExample();
		example.createCriteria().andLocationIdEqualTo(jurisdictionId).andPlanIdentifierEqualTo(planIdentifier).andDateDeletedIsNull();
		
		/**@formatter:off*/
		return eventMetadataMapper.selectMany(example)
				.stream()
				.map(event -> convert(event))
		        .map(event -> EventConverter.convertEventToEncounterResource(event))
		        .collect(Collectors.toList());
		/**@formatter:on*/
	}
	
	
}
