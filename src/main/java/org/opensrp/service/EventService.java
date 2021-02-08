package org.opensrp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.opensrp.common.AllConstants.Client;
import org.opensrp.dto.ExportEventDataSummary;
import org.opensrp.dto.ExportFlagProblemEventImageMetadata;
import org.opensrp.dto.ExportImagesSummary;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.PlanRepository;
import org.opensrp.search.EventSearchBean;
import org.opensrp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.smartregister.domain.PlanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class EventService {

	public static final String OUT_OF_CATCHMENT_PROVIDER_ID = "out_of_catchment_provider_id";

	public static final String BIRTH_REGISTRATION_EVENT = "Birth Registration";

	public static final String GROWTH_MONITORING_EVENT = "Growth Monitoring";

	public static final String GROWTH_MONITORING_EVENT_UNDERSCORED = "growth_monitoring";

	public static final String VACCINATION_EVENT = "Vaccination";

	public static final String OUT_OF_AREA_SERVICE = "Out of Area Service";

	public static final String NEW_OUT_OF_AREA_SERVICE = "out_of_area_service";

	public static final String RECURRING_SERVICE_UNDERSCORED = "recurring_service";

	public static final String RECURRING_SERVICE = "Recurring Service";

	public static final String NFC_CARD_IDENTIFIER = "NFC_Card_Identifier";

	public static final String CARD_ID_PREFIX = "c_";

	private static final String OPENSRP_ID = "opensrp_id";

	public static final String RECURRING_SERVICE_TYPES = "recurring_service_types";

	public static final String NUMERIC = "numeric";

	public static final String DOSE = "dose";

	public static final String DATE = "date";

	private static final String CODED = "coded";

	private final EventsRepository allEvents;

	private final ClientService clientService;

	private final TaskGenerator taskGenerator;

	private final PlanRepository planRepository;

	private final ExportEventDataMapper exportEventDataMapper;

	@Value("#{opensrp['plan.evaluation.enabled'] ?: false}")
	private boolean isPlanEvaluationEnabled;

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	public EventService(EventsRepository allEvents, ClientService clientService, TaskGenerator taskGenerator,
			PlanRepository planRepository, ExportEventDataMapper exportEventDataMapper) {
		this.allEvents = allEvents;
		this.clientService = clientService;
		this.taskGenerator = taskGenerator;
		this.planRepository = planRepository;
		this.exportEventDataMapper = exportEventDataMapper;
	}

	public List<Event> findAllByIdentifier(String identifier) {
		return allEvents.findAllByIdentifier(identifier);
	}

	public List<Event> findAllByIdentifier(String identifierType, String identifier) {
		return allEvents.findAllByIdentifier(identifierType, identifier);
	}

	public Event getById(String id) {
		return allEvents.findById(id);
	}

	public Event getByBaseEntityAndFormSubmissionId(String baseEntityId, String formSubmissionId) {
		return allEvents.findByBaseEntityAndFormSubmissionId(baseEntityId, formSubmissionId);
	}

	public List<Event> findByBaseEntityId(String baseEntityId) {
		return allEvents.findByBaseEntityId(baseEntityId);
	}

	public Event findByFormSubmissionId(String formSubmissionId) {
		return allEvents.findByFormSubmissionId(formSubmissionId, false);
	}

	public List<Event> findEventsBy(EventSearchBean eventSearchBean) {
		return allEvents.findEvents(eventSearchBean);
	}

	public List<Event> findEventsByDynamicQuery(String query) {
		return allEvents.findEventsByDynamicQuery(query);
	}

	private static final Logger logger = LoggerFactory.getLogger(EventService.class.toString());

	public Event find(String uniqueId) {
		try {
			List<Event> el = allEvents.findAllByIdentifier(uniqueId);
			return getUniqueEventFromEventList(el);
		}
		catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Multiple events with identifier " + uniqueId + " exist.");
		}
	}

	public Event find(Event event) {
		for (String idt : event.getIdentifiers().keySet()) {
			try {
				List<Event> el = allEvents.findAllByIdentifier(event.getIdentifier(idt));
				return getUniqueEventFromEventList(el);
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException(
						"Multiple events with identifier type " + idt + " and ID " + event.getIdentifier(idt) + " exist.");
			}
		}
		return null;
	}

	/**
	 * Find an event using the event Id
	 *
	 * @param eventId the if for the event
	 * @return an event matching the eventId
	 */
	public Event findById(String eventId) {
		try {
			if (StringUtils.isEmpty(eventId)) {
				return null;
			}
			return allEvents.findById(eventId);
		}
		catch (Exception e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * Find an event using an event Id or form Submission Id
	 *
	 * @param eventId          the if for the event
	 * @param formSubmissionId form submission id for the events
	 * @return an event matching the eventId or formsubmission id
	 */
	public Event findByIdOrFormSubmissionId(String eventId, String formSubmissionId) {
		Event event = null;
		try {
			if (StringUtils.isNotEmpty(eventId)) {
				event = findById(eventId);
			}
			if (event == null && StringUtils.isNotEmpty(formSubmissionId)) {
				return allEvents.findByFormSubmissionId(formSubmissionId, true);
			}
		}
		catch (Exception e) {
			logger.error("", e);
		}
		return event;
	}

	public synchronized Event addEvent(Event event, String username) {
		Event e = find(event);
		if (e != null) {
			throw new IllegalArgumentException(
					"An event already exists with given list of identifiers. Consider updating data.[" + e + "]");
		}

		if (event.getFormSubmissionId() != null
				&& getByBaseEntityAndFormSubmissionId(event.getBaseEntityId(), event.getFormSubmissionId()) != null) {
			throw new IllegalArgumentException(
					"An event already exists with given baseEntity and formSubmission combination. Consider updating");
		}

		event.setDateCreated(DateTime.now());
		allEvents.add(event);
		triggerPlanEvaluation(event, username);
		return event;
	}

	/**
	 * An out of area event is used to record services offered outside a client's catchment area. The
	 * event usually will have a client unique identifier(ZEIR_ID) as the only way to identify the
	 * client.This method finds the client based on the identifier and assigns a basentityid to the
	 * event
	 *
	 * @param event event to be processed
	 * @return event
	 */
	public synchronized Event processOutOfArea(Event event) {
		try {
			String identifier = StringUtils.isBlank(event.getIdentifier(Client.ZEIR_ID)) ?
					event.getIdentifier(OPENSRP_ID) : event.getIdentifier(Client.ZEIR_ID);

			if (StringUtils.isNotBlank(event.getBaseEntityId()) || StringUtils.isBlank(identifier)) {
				return event;
			}

			List<org.smartregister.domain.Client> clients =
					identifier.startsWith(CARD_ID_PREFIX) ? clientService
							.findAllByAttribute(NFC_CARD_IDENTIFIER, identifier.substring(CARD_ID_PREFIX.length()))
							: getClientByIdentifier(identifier);

			if (clients == null || clients.isEmpty()) {
				return event;
			}

			for (org.smartregister.domain.Client client : clients) {

				List<Event> existingEvents = findByBaseEntityAndType(client.getBaseEntityId(), BIRTH_REGISTRATION_EVENT);

				if (existingEvents == null || existingEvents.isEmpty()) {
					return event;
				}
				Event birthRegEvent = existingEvents.get(0);
				createOutOfCatchmentService(event, client, birthRegEvent);
			}
		}
		catch (Exception e) {
			logger.error("Error processing out of catchment service", e);
		}

		return event;
	}

	private void createOutOfCatchmentService(Event event, org.smartregister.domain.Client client, Event birthRegEvent) {
		event.setBaseEntityId(client.getBaseEntityId());

		//Remove identifier since entity id is present, also create new service with the right location and provider
		String eventTypeLowercase = event.getEventType().toLowerCase();
		if ((event.getEventType().startsWith(OUT_OF_AREA_SERVICE) || event.getEventType()
				.startsWith(NEW_OUT_OF_AREA_SERVICE))) {

			boolean hasGrowthMonitoring = eventTypeLowercase.contains(GROWTH_MONITORING_EVENT.toLowerCase()) ||
					eventTypeLowercase.contains(GROWTH_MONITORING_EVENT_UNDERSCORED);

			if (hasGrowthMonitoring || eventTypeLowercase.contains(VACCINATION_EVENT.toLowerCase())) {

				String actualEventType = hasGrowthMonitoring ? GROWTH_MONITORING_EVENT :
						eventTypeLowercase.contains(VACCINATION_EVENT.toLowerCase()) ? VACCINATION_EVENT : null;

				if (actualEventType != null) {
					removeIdentifier(event);
					Event newEvent = getNewOutOfAreaServiceEvent(event, birthRegEvent, actualEventType);
					addEvent(newEvent, birthRegEvent.getProviderId());
				}
			} else if (eventTypeLowercase.contains(RECURRING_SERVICE.toLowerCase()) ||
					eventTypeLowercase.contains(RECURRING_SERVICE_UNDERSCORED)) {
				processOutOfAreaRecurringService(event, birthRegEvent);
			}
		}
	}

	private void removeIdentifier(Event event) {
		//Remove case sensitive identifiers
		TreeMap<String, String> newIdentifiers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		newIdentifiers.putAll(event.getIdentifiers());
		newIdentifiers.remove(Client.ZEIR_ID);
		newIdentifiers.remove(OPENSRP_ID);
		event.setIdentifiers(newIdentifiers);
	}

	private void processOutOfAreaRecurringService(Event event, Event birthRegEvent) {
		//Get and sort previous recurring services related to the current
		String recurringServiceTypes = event.getDetails().get(RECURRING_SERVICE_TYPES);
		List<String> outOfCatchmentServices = Arrays
				.asList(recurringServiceTypes.substring(1, recurringServiceTypes.length() - 1).split(","));
		List<Event> previousServices = findByBaseEntityAndType(birthRegEvent.getBaseEntityId(), RECURRING_SERVICE);
		Map<String, List<Event>> matchedRecurringServices = getMarchedRecurringServices(previousServices,
				outOfCatchmentServices);

		//Create new recurring service event with correct sequence, incrementing on the old recurring service's ; involves updating the obs

		for (String service : outOfCatchmentServices) {
			List<Event> events = matchedRecurringServices.get(service);
			if (!events.isEmpty()) {
				Event lastRecurringService = events.get(events.size() - 1);
				removeIdentifier(event);
				Event newEvent = getNewOutOfAreaServiceEvent(event, birthRegEvent, RECURRING_SERVICE);
				List<Obs> newObsList = new ArrayList<>();

				Obs obsWithValue = getObsWithValue(lastRecurringService);
				if (obsWithValue != null) {
					String newSequence = String.valueOf(Integer.parseInt((String) obsWithValue.getValues().get(0)) + 1);
					String newFormSubmissionField = String.format("%s_%s", service, newSequence);

					for (Obs oldObs : lastRecurringService.getObs()) {
						updateObs(oldObs, event, newSequence, newFormSubmissionField);
						newObsList.add(oldObs);
					}
					newEvent.setObs(newObsList);
					addEvent(newEvent, birthRegEvent.getProviderId());
				}
			}
		}

	}

	private void updateObs(Obs oldObs, Event incomingEvent, String newSequence, String newFormSubmissionField) {
		if (oldObs.getFieldDataType().equalsIgnoreCase(NUMERIC)) {
			oldObs.setFormSubmissionField(newFormSubmissionField + "_dose");
			oldObs.getValues().clear();
			oldObs.getValues().add(newSequence);
		}
		if (oldObs.getFieldDataType().equalsIgnoreCase(DATE)) {
			oldObs.setFormSubmissionField(newFormSubmissionField + "_date");
			oldObs.getValues().clear();
			oldObs.getValues().add(simpleDateFormat.format(incomingEvent.getEventDate()));
		}
		if (oldObs.getFieldDataType().equalsIgnoreCase(CODED)) {
			oldObs.setFormSubmissionField(newFormSubmissionField);
		}
	}

	public Obs getObsWithValue(Event event) {
		for (Obs obs : event.getObs()) {
			if (obs.getFieldDataType().equalsIgnoreCase(NUMERIC) && obs.getFormSubmissionField().contains(DOSE)
					&& !obs.getValues().isEmpty()) {
				return obs;
			}
		}
		return null;
	}

	private Map<String, List<Event>> getMarchedRecurringServices(List<Event> previousServices,
			List<String> outOfCatchmentServices) {
		Map<String, List<Event>> marchedServices = new TreeMap<>();
		for (String service : outOfCatchmentServices) {
			List<Event> serviceEvents = previousServices.stream().filter(new Predicate<Event>() {

				@Override
				public boolean test(Event event) {
					Obs obs = getObsWithValue(event);
					if (obs == null) {
						return false;
					}
					return obs.getFormSubmissionField().startsWith(service.trim());
				}
			}).sorted(new Comparator<Event>() {

				@Override
				public int compare(Event event1, Event event2) {
					int value1 = Integer.parseInt((String) getObsWithValue(event1).getValues().get(0));
					int value2 = Integer.parseInt((String) getObsWithValue(event2).getValues().get(0));
					return Integer.compare(value1, value2);
				}
			}).collect(Collectors.toList());
			marchedServices.put(service, serviceEvents);
		}

		return marchedServices;
	}

	private Event getNewOutOfAreaServiceEvent(Event event, Event birthRegEvent, String eventType) {
		Event newEvent = new Event();
		newEvent.withBaseEntityId(event.getBaseEntityId())
				.withEventType(eventType)
				.withEventDate(event.getEventDate())
				.withEntityType(event.getEntityType())
				.withProviderId(birthRegEvent.getProviderId())
				.withLocationId(birthRegEvent.getLocationId())
				.withChildLocationId(birthRegEvent.getChildLocationId())
				.withFormSubmissionId(UUID.randomUUID().toString())
				.withDateCreated(event.getDateCreated());
		Map<String, String> details = new HashMap<>();
		details.put(OUT_OF_CATCHMENT_PROVIDER_ID, event.getProviderId());
		newEvent.setDetails(details);
		newEvent.setObs(event.getObs());
		newEvent.setTeam(birthRegEvent.getTeam());
		newEvent.setTeamId(birthRegEvent.getTeamId());
		newEvent.setIdentifiers(event.getIdentifiers());
		logger.info(String.format("New %s event with created with id %s", newEvent.getEventType(),
				newEvent.getFormSubmissionId()));
		return newEvent;
	}

	private List<org.smartregister.domain.Client> getClientByIdentifier(String identifier) {
		List<org.smartregister.domain.Client> clients = clientService.findAllByIdentifier(Client.ZEIR_ID, identifier);
		if (clients != null && clients.isEmpty()) {
			clients = clientService.findAllByIdentifier(Client.ZEIR_ID.toUpperCase(), identifier);
		}
		return clients;
	}

	public synchronized Event addorUpdateEvent(Event event, String username) {
		Event existingEvent = findByIdOrFormSubmissionId(event.getId(), event.getFormSubmissionId());
		if (existingEvent != null) {
			event.setId(existingEvent.getId());
			event.setRevision(existingEvent.getRevision());
			event.setDateEdited(DateTime.now());
			event.setRevision(existingEvent.getRevision());
			allEvents.update(event);

		} else {
			event.setDateCreated(DateTime.now());
			allEvents.add(event);

		}

		triggerPlanEvaluation(event, username);

		return event;
	}

	public void updateEvent(Event updatedEvent, String username) {
		// If update is on original entity
		if (updatedEvent.isNew()) {
			throw new IllegalArgumentException(
					"Event to be updated is not an existing and persisting domain object. Update database object instead of new pojo");
		}

		updatedEvent.setDateEdited(DateTime.now());
		allEvents.update(updatedEvent);
		triggerPlanEvaluation(updatedEvent, username);
	}

	//TODO Review and add test cases as well
	public Event mergeEvent(Event updatedEvent) {
		try {
			Event original = find(updatedEvent);
			if (original == null) {
				throw new IllegalArgumentException("No event found with given list of identifiers. Consider adding new!");
			}

			original = (Event) Utils.getMergedJSON(original, updatedEvent, Arrays.asList(Event.class.getDeclaredFields()),
					Event.class);
			for (Obs o : updatedEvent.getObs()) {
				// TODO handle parent
				if (original.getObs(null, o.getFieldCode()) == null) {
					original.addObs(o);
				} else {
					original.getObs(null, o.getFieldCode()).setComments(o.getComments());
					original.getObs(null, o.getFieldCode()).setEffectiveDatetime(o.getEffectiveDatetime());
					original.getObs(null, o.getFieldCode())
							.setValue(o.getValues().size() < 2 ? o.getValue() : o.getValues());
				}
			}
			for (String k : updatedEvent.getIdentifiers().keySet()) {
				original.addIdentifier(k, updatedEvent.getIdentifier(k));
			}

			original.setDateEdited(DateTime.now());
			allEvents.update(original);
			return original;
		}
		catch (JSONException | JsonProcessingException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Event> findByServerVersion(long serverVersion) {
		return allEvents.findByServerVersion(serverVersion);
	}

	public List<Event> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar) {
		return allEvents.notInOpenMRSByServerVersion(serverVersion, calendar);
	}

	public List<Event> notInOpenMRSByServerVersionAndType(String type, long serverVersion, Calendar calendar) {
		return allEvents.notInOpenMRSByServerVersionAndType(type, serverVersion, calendar);
	}

	public List<Event> getAll() {
		return allEvents.getAll();
	}

	public List<Event> findEvents(EventSearchBean eventSearchBean, String sortBy, String sortOrder, int limit) {
		return allEvents.findEvents(eventSearchBean, sortBy, sortOrder, limit);
	}

	public List<Event> findEvents(EventSearchBean eventSearchBean) {
		return allEvents.findEvents(eventSearchBean);
	}

	public List<Event> findEventsByConceptAndValue(String concept, String conceptValue) {
		return allEvents.findByConceptAndValue(concept, conceptValue);

	}

	public List<Event> findByBaseEntityAndType(String baseEntityId, String eventType) {
		return allEvents.findByBaseEntityAndType(baseEntityId, eventType);

	}

	private Event getUniqueEventFromEventList(List<Event> events) throws IllegalArgumentException {
		if (events.size() > 1) {
			throw new IllegalArgumentException();
		}
		if (events.size() == 0) {
			return null;
		}
		return events.get(0);
	}

	public List<Event> findByProviderAndEntityType(String provider) {
		return allEvents.findByProvider(provider);
	}

	/**
	 * This method searches for event ids filtered by eventType and the date they were deleted
	 *
	 * @param eventType     used to filter the event ids
	 * @param isDeleted     whether to return deleted event ids
	 * @param serverVersion incremental server version
	 * @param limit         upper limit on number of tasks ids to fetch
	 * @return a list of event ids
	 */
	public Pair<List<String>, Long> findAllIdsByEventType(String eventType, boolean isDeleted, Long serverVersion,
			int limit) {
		return allEvents.findIdsByEventType(eventType, isDeleted, serverVersion, limit);
	}

	/**
	 * overrides {@link #findAllIdsByEventType(String, boolean, Long, int)} by adding date filters
	 *
	 * @param eventType
	 * @param isDeleted
	 * @param serverVersion
	 * @param limit
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public Pair<List<String>, Long> findAllIdsByEventType(String eventType, boolean isDeleted, Long serverVersion, int limit,
			Date fromDate, Date toDate) {
		return allEvents.findIdsByEventType(eventType, isDeleted, serverVersion, limit, fromDate, toDate);
	}

	/**
	 * This method is used to return a count of locations based on the provided parameters
	 *
	 * @param eventSearchBean object containing params to search by
	 * @return returns a count of events matching the passed parameters
	 */
	public Long countEvents(EventSearchBean eventSearchBean) {
		return allEvents.countEvents(eventSearchBean);
	}

	private void triggerPlanEvaluation(Event event, String username) {
		String planIdentifier = event.getDetails() != null ? event.getDetails().get("planIdentifier") : null;
		if (isPlanEvaluationEnabled && planIdentifier != null) {
			PlanDefinition plan = planRepository.get(planIdentifier);
			if (plan != null && plan.getStatus().equals(PlanDefinition.PlanStatus.ACTIVE) && (
					plan.getEffectivePeriod().getEnd() == null
							|| plan.getEffectivePeriod().getEnd().isAfter(LocalDate.now().toDateTimeAtStartOfDay())))
				taskGenerator.processPlanEvaluation(plan, username, event);
		}
	}

	public ExportEventDataSummary exportEventData(String planIdentifier, String eventType, Date fromDate, Date toDate)
			throws JsonProcessingException {
		List<org.opensrp.domain.postgres.Event> pgEvents = allEvents
				.getEventData(planIdentifier, eventType, fromDate, toDate);
		ExportEventDataSummary exportEventDataSummary = new ExportEventDataSummary();
		List<List<Object>> allRows = new ArrayList<>();
		boolean returnHeader = true;
		Map<String, String> columnNamesAndLabels = exportEventDataMapper.getColumnNamesAndLabelsByEventType(eventType);
		boolean settingsExist = columnNamesAndLabels != null && columnNamesAndLabels.size() > 0;

		if (settingsExist) {
			allRows.add(exportEventDataMapper
					.getExportEventDataAfterMapping(null, eventType, returnHeader, settingsExist)); //for header row
		}

		//Assumption : All pgEvents would have similar obs fields to include as a header
		else {
			if (exportEventDataMapper.getExportEventDataAfterMapping(
					pgEvents.size() > 0 ? pgEvents.get(0).getJson() : "", eventType, returnHeader, settingsExist) != null)
				allRows.add(exportEventDataMapper.getExportEventDataAfterMapping(
						pgEvents.size() > 0 ? pgEvents.get(0).getJson() : "", eventType, returnHeader,
						settingsExist)); //for header row
		}

		for (org.opensrp.domain.postgres.Event pgEvent : pgEvents) {
			allRows.add(exportEventDataMapper
					.getExportEventDataAfterMapping(pgEvent.getJson(), eventType, false, settingsExist));
		}

		exportEventDataSummary.setRowsData(allRows);

		PlanDefinition plan = planIdentifier != null ? planRepository.get(planIdentifier) : null;
		if (plan != null)
			exportEventDataSummary.setMissionName(plan.getName());
		return exportEventDataSummary;
	}

	public ExportImagesSummary getImagesMetadataForFlagProblemEvent(String planIdentifier, String eventType, Date fromDate,
			Date toDate) throws JsonProcessingException {
		List<org.opensrp.domain.postgres.Event> pgEvents = allEvents
				.getEventData(planIdentifier, eventType, fromDate, toDate);

		Set<String> servicePoints = new HashSet<>();
		String servicePointName;
		ExportImagesSummary exportImagesSummary = new ExportImagesSummary();
		ExportFlagProblemEventImageMetadata exportFlagProblemEventImageMetadata;
		List<ExportFlagProblemEventImageMetadata> exportFlagProblemEventImageMetadataList = new ArrayList<>();
		for (org.opensrp.domain.postgres.Event pgEvent : pgEvents) {
			exportFlagProblemEventImageMetadata = exportEventDataMapper
					.getFlagProblemEventImagesMetadata(pgEvent.getJson(), "$.baseEntityId",
							"$.details.locationName", "$.details.productName");
			if (exportFlagProblemEventImageMetadata != null) {
				exportFlagProblemEventImageMetadataList.add(exportFlagProblemEventImageMetadata);
				servicePointName = exportFlagProblemEventImageMetadata.getServicePointName();
				if (servicePointName != null && !servicePoints.contains(servicePointName)) {
					servicePoints.add(servicePointName);
				}
			}
		}

		exportImagesSummary.setExportFlagProblemEventImageMetadataList(exportFlagProblemEventImageMetadataList);
		exportImagesSummary.setServicePoints(servicePoints);
		return exportImagesSummary;

	}

}
