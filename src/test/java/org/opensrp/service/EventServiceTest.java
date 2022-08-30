package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.common.AllConstants.Event.OPENMRS_UUID_IDENTIFIER_TYPE;
import static org.opensrp.repository.postgres.EventsRepositoryTest.createFlagProblemEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opensrp.common.AllConstants.Client;
import org.opensrp.dto.ExportEventDataSummary;
import org.opensrp.dto.ExportFlagProblemEventImageMetadata;
import org.opensrp.dto.ExportImagesSummary;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.PlanRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.repository.postgres.EventsRepositoryImpl;
import org.opensrp.repository.postgres.handler.BaseTypeHandler;
import org.opensrp.util.DateTimeDeserializer;
import org.opensrp.util.constants.EventConstants;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.smartregister.domain.Period;
import org.smartregister.domain.PlanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class EventServiceTest extends BaseRepositoryTest {

    @InjectMocks
    private EventService eventService;

    @Autowired
    @Qualifier("eventsRepositoryPostgres")
    private EventsRepository eventsRepository;

    @Autowired
    @Qualifier("clientsRepositoryPostgres")
    private ClientsRepository clientsRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private ExportEventDataMapper exportEventDataMapper;

    @Mock
    private TaskGenerator taskGenerator;

    private Set<String> scripts = new HashSet<String>();
    ;

    private String username = "johndoe";

    @Captor
    private ArgumentCaptor<PlanDefinition> planDefinitionArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);

    @Captor
    private ArgumentCaptor<Event> eventArgumentCaptor;


    @Before
    public void setUpPostgresRepository() {
        initMocks(this);
        eventService = new EventService(eventsRepository, new ClientService(clientsRepository), taskGenerator, planRepository, exportEventDataMapper);
        ReflectionTestUtils.setField(eventService, "isPlanEvaluationEnabled", true);
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        scripts.add("event.sql");
        scripts.add("client.sql");
        return scripts;
    }

    @Test
    public void testFindByUniqueId() {
        assertEquals("05934ae338431f28bf6793b241be69a5", eventService.find("4aecc0c1-e008-4227-938d-66db17236a3d").getId());

        assertEquals("05934ae338431f28bf6793b241bdb88c", eventService.find("06c8644b-b560-45fd-9af5-b6b1484e3504").getId());

        assertNull(eventService.find("50102f0a-a9c9-1d-a6c476433b3c"));

        //deleted event
        eventsRepository.safeRemove(eventService.find("06c8644b-b560-45fd-9af5-b6b1484e3504"));
        assertNull(eventService.find("06c8644b-b560-45fd-9af5-b6b1484e3504"));
    }

    @Test
    public void testFindByEvent() {
        Event event = new Event();
        assertNull(eventService.find(event));

        event.withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "94ec8561-14ab-48d1-a6d4-4ae05191f6e6");

        assertEquals("05934ae338431f28bf6793b241bdbc55", eventService.find(event).getId());

        //deleted event
        eventsRepository.safeRemove(eventService.find(event));
        assertNull(eventService.find(event));
    }

    @Test
    public void testFindByEventId() {
        Event event = eventService.findById("05934ae338431f28bf6793b2419c319a");
        assertEquals("ea1f9439-a663-4073-93b9-6ef2b8bca3c1", event.getBaseEntityId());
        assertEquals("d960046a-e2a0-4bbf-b687-d41c2a52d8c8", event.getFormSubmissionId());
        assertEquals("Vaccination", event.getEventType());

        assertNull(eventService.findById("50102f0a-a9c9-1d-a6c476433b3c"));

        //deleted event
        eventsRepository.safeRemove(event);
        assertNull(eventService.findById(event.getId()));
    }

    @Test
    public void testAddEvent() {
        Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
        Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
                .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier");

        when(planRepository.get(anyString())).thenReturn(plan);
        Mockito.doNothing().when(taskGenerator).processPlanEvaluation(any(PlanDefinition.class), anyString(), any(Event.class));
        eventService.addEvent(event, username);

        event = eventService.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
        assertEquals("435534534543", event.getBaseEntityId());
        assertEquals("Growth Monitoring", event.getEventType());
        assertEquals(1, event.getObs().size());
        assertEquals("3.5", event.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());

        //test if an event with voided date add event as deleted
        event = new Event().withBaseEntityId("2423nj-sdfsd-sf2dfsd-2399d").withEventType("Vaccination")
                .withFormSubmissionId("hshj2342_jsjs-jhjsdfds-23").withEventDate(new DateTime()).withObs(obs);
        event.setDateVoided(new DateTime());
        eventService.addEvent(event, username);
        assertNull(eventService.findByFormSubmissionId(event.getFormSubmissionId()));
    }

    @Test
    public void testAddEventOutOfCatchment() {
        Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
        Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
                .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier");

        when(planRepository.get(anyString())).thenReturn(plan);
        Mockito.doNothing().when(taskGenerator).processPlanEvaluation(any(PlanDefinition.class), anyString(), any(Event.class));
        eventService.addEvent(event, username);

        event = eventService.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
        assertEquals("435534534543", event.getBaseEntityId());
        assertEquals("Growth Monitoring", event.getEventType());
        assertEquals(1, event.getObs().size());
        assertEquals("3.5", event.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());

        //test if an event with voided date add event as deleted
        event = new Event().withBaseEntityId("2423nj-sdfsd-sf2dfsd-2399d").withEventType("Vaccination")
                .withFormSubmissionId("hshj2342_jsjs-jhjsdfds-23").withEventDate(new DateTime()).withObs(obs);
        event.setDateVoided(new DateTime());
        eventService.addEventOutOfCatchment(event, username);
        assertNull(eventService.findByFormSubmissionId(event.getFormSubmissionId()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEventForExistingEvent() {
        Event event = eventService.findById("05934ae338431f28bf6793b241bdc44a");
        eventService.addEvent(event, username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEventDuplicateIdentifiers() {
        Event event = new Event().withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "4aecc0c1-e008-4227-938d-66db17236a3d");
        eventService.addEvent(event, username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddEventDuplicateBaseEntityFormSubmission() {
        Event event = new Event().withBaseEntityId("58b33379-dab2-4f5c-8f09-6d2bd63023d8").withFormSubmissionId(
                "5f1b201d-2132-4eb9-8fa1-3169a61cc50a");
        eventService.addEvent(event, username);
    }

    @Test(expected = DuplicateKeyException.class)
    public void testAddEventDuplicateFormSubmission() {
        Event event = new Event().withBaseEntityId("58b33379-dab2-4f").withFormSubmissionId(
                "5f1b201d-2132-4eb9-8fa1-3169a61cc50a");
        eventService.addEvent(event, username);

    }

    @Test
    public void testAddEventShouldEvaluatePlan() {
        Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
        Map<String, String> details = new HashMap<>();
        details.put("planIdentifier", "plan-id-1");
        Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
                .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);
        event.setDetails(details);
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("plan-id-1");
        plan.setStatus(PlanDefinition.PlanStatus.ACTIVE);
        Period executionPeriod = new Period();
        executionPeriod.setEnd(new LocalDate().plusYears(2).toDateTimeAtStartOfDay());
        plan.setEffectivePeriod(executionPeriod);

        when(planRepository.get("plan-id-1")).thenReturn(plan);
        Mockito.doNothing().when(taskGenerator).processPlanEvaluation(any(PlanDefinition.class), any(PlanDefinition.class), anyString());
        eventService.addEvent(event, username);
        verify(planRepository, times(1)).get(stringArgumentCaptor.capture());
        verify(taskGenerator, times(1)).processPlanEvaluation(planDefinitionArgumentCaptor.capture(), stringArgumentCaptor.capture(), eventArgumentCaptor.capture());
    }

    @Test
    public void testProcessOutOfAreaDoesNotAddOutOfCatchmentEventWhenClientIdentifierIsInvalid() throws SQLException {
        populateDatabase();

        Event event = new Event().withEventType("Out of Area Service - Vaccination")
                .withProviderId("tester112")
                .withLocationId("2242342-23dsfsdfds")
                .withIdentifier(Client.ZEIR_ID, "c_2182291985");

        int prevCount = eventService.getAll().size();
        Event outOfAreaEvent = eventService.processOutOfArea(event);
        assertNotNull(outOfAreaEvent);
        assertEquals(event, outOfAreaEvent);
        assertEquals(prevCount, eventService.getAll().size());
    }

    @Test
    public void testProcessOutOfAreaAddsOutOfCatchmentVaccinationEventWhenEventHasValidClientIdentifier() throws SQLException {
        populateDatabase();

        Event event = new Event()
                .withEventType("Out of Area Service - Vaccination")
                .withProviderId("tester111")
                .withLocationId("2242342-23dsfsdfds")
                .withIdentifier(Client.ZEIR_ID, "218229-3");

        int prevCount = eventService.getAll().size();
        Event outOfAreaEvent = eventService.processOutOfArea(event);
        assertEquals(event, outOfAreaEvent);
        assertEquals(prevCount + 1, eventService.getAll().size());
    }

    @Test
    public void testProcessOutOfAreaAddsOutOfCatchmentGrowthMonitoringEventWhenEventHasValidClientIdentifier() throws SQLException {
        populateDatabase();

        Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
        Event event = new Event()
                .withEventType("Out of Area Service - Growth Monitoring")
                .withFormSubmissionId("gjhg34534 nvbnv3345345__4")
                .withEventDate(new DateTime()).withObs(obs)
                .withIdentifier(Client.ZEIR_ID, "218229-3");

        int prevCount = eventService.getAll().size();
        Event outOfAreaEvent = eventService.processOutOfArea(event);
        assertEquals(event, outOfAreaEvent);
        assertEquals(prevCount + 1, eventService.getAll().size());
    }

    @Test
    public void testProcessOutOfAreaRecurringService() throws SQLException {
        scripts.add("client.sql");
        populateDatabase();
        Event event = new Event().withEventType("Out of Area Service - Recurring Service")
                .withIdentifier(Client.ZEIR_ID, "218229-3");
        Map<String, String> details = new HashMap<>() {
            {
                put(EventConstants.RECURRING_SERVICE_TYPES, "[deworming, vit_a]");
            }
        };
        event.setDetails(details);
        Event outOfAreaEvent = eventService.processOutOfArea(event);
        assertEquals(event, outOfAreaEvent);
        assertEquals(23, eventService.getAll().size());

    }

    @Test
    public void testAddorUpdateEvent() {
        Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
        Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
                .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);

        eventService.addorUpdateEvent(event, username);

        Event updatedEvent = eventService.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
        assertEquals("435534534543", updatedEvent.getBaseEntityId());
        assertEquals("Growth Monitoring", updatedEvent.getEventType());
        assertEquals(1, updatedEvent.getObs().size());
        assertEquals("3.5", updatedEvent.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
        assertNull(updatedEvent.getDateEdited());
        event.setTeam("ATeam");
        event.setProviderId("tester11");
        event.setLocationId("321312-fsff-2328");
        eventService.addorUpdateEvent(event, username);

        updatedEvent = eventService.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
        assertEquals("ATeam", updatedEvent.getTeam());
        assertEquals("tester11", updatedEvent.getProviderId());
        assertEquals("321312-fsff-2328", updatedEvent.getLocationId());
        assertEquals(EventsRepositoryImpl.REVISION_PREFIX + 2, updatedEvent.getRevision());
        assertNotNull(updatedEvent.getDateEdited());

        //test if an event with voided date add event as deleted
        event = new Event().withBaseEntityId("2423nj-sdfsd-sf2dfsd-2399d").withEventType("Vaccination")
                .withFormSubmissionId("hshj2342_jsjs-jhjsdfds-23").withEventDate(new DateTime()).withObs(obs);
        event.setDateVoided(new DateTime());
        eventService.addorUpdateEvent(event, username);
        assertNull(eventService.findByFormSubmissionId(event.getFormSubmissionId()));
    }

    @Test
    public void testAddorUpdateEventWithMissingEventIdUpdatesEvent() throws Exception {
        String baseEntityId = UUID.randomUUID().toString();
        Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
        Event event = new Event().withBaseEntityId(baseEntityId).withEventType("Growth Monitoring")
                .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);
        ObjectMapper mapper = BaseTypeHandler.mapper;
        SimpleModule dateTimeModule = new SimpleModule("DateTimeModule");
        dateTimeModule.addDeserializer(DateTime.class, new DateTimeDeserializer());
        mapper.registerModule(dateTimeModule);
        String jsonString = mapper.writeValueAsString(event);
        eventService.addorUpdateEvent(event, username);

        Event updatedEvent = eventService.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
        String eventId = updatedEvent.getId();
        assertEquals(baseEntityId, updatedEvent.getBaseEntityId());
        assertEquals("Growth Monitoring", updatedEvent.getEventType());
        assertEquals(1, updatedEvent.getObs().size());
        assertEquals("3.5", updatedEvent.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
        assertNull(updatedEvent.getDateEdited());

        Event originalEventWithoutId = mapper.readValue(jsonString, Event.class);
        originalEventWithoutId.setTeam("ATeam");
        originalEventWithoutId.setProviderId("tester11");
        originalEventWithoutId.setLocationId("321312-fsff-2328");
        eventService.addorUpdateEvent(originalEventWithoutId, username);

        updatedEvent = eventService.findByFormSubmissionId("gjhg34534 nvbnv3345345__4");
        assertEquals(eventId, updatedEvent.getId());
        assertEquals("ATeam", updatedEvent.getTeam());
        assertEquals("tester11", updatedEvent.getProviderId());
        assertEquals("321312-fsff-2328", updatedEvent.getLocationId());
        assertEquals(EventsRepositoryImpl.REVISION_PREFIX + 2, updatedEvent.getRevision());
        assertNotNull(updatedEvent.getDateEdited());

        List<Event> events = eventService.findByBaseEntityId(baseEntityId);
        assertEquals(1, events.size());
        assertEquals(eventId, events.get(0).getId());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateEventNonExistingEvent() {
        Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
        Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
                .withFormSubmissionId("gjhg34534 nvbnv3345345__4").withEventDate(new DateTime()).withObs(obs);

        eventService.updateEvent(event, username);
    }

    @Test
    public void testUpdateEvent() {
        DateTime timebeforeUpdate = DateTime.now();
        Event event = eventService.findById("05934ae338431f28bf6793b24177a1dc");
        Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
        event.withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "62242n-223423-2332").addObs(obs);
        eventService.updateEvent(event, username);

        Event updatedEvent = eventService.findById(event.getId());
        assertEquals(0, Minutes.minutesBetween(timebeforeUpdate, updatedEvent.getDateEdited()).getMinutes());
        assertEquals("3.5", updatedEvent.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
        assertEquals("62242n-223423-2332", updatedEvent.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeEventMissingIdentifiers() {
        Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring");
        eventService.mergeEvent(event);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMergeEventNonExistingIdentifiers() {
        Event event = new Event().withBaseEntityId("435534534543").withEventType("Growth Monitoring")
                .withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "242332-hgfhfh-dfg8d");
        eventService.mergeEvent(event);
    }

    @Test
    public void testMergeEvent() {
        Obs obs = new Obs("concept", "decimal", "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, "3.5", null, "weight");
        Event event = new Event().withEventType("Growth Monitoring")
                .withIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "4aecc0c1-e008-4227-938d-66db17236a3d")
                .withEventDate(new DateTime()).withObs(obs);

        eventService.mergeEvent(event);

        Event updatedEvent = eventService.find("4aecc0c1-e008-4227-938d-66db17236a3d");

        assertEquals("05934ae338431f28bf6793b241be69a5", updatedEvent.getId());
        assertEquals("Growth Monitoring", updatedEvent.getEventType());
        assertEquals(1, updatedEvent.getObs().size());
        assertEquals("3.5", updatedEvent.getObs(null, "1730AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue());
        assertEquals(0, Minutes.minutesBetween(DateTime.now(), updatedEvent.getDateEdited()).getMinutes());
    }

    @Test
    public void testFindAllEventIds() {
        Pair<List<String>, Long> eventIdsModel = eventService.findAllIdsByEventType(null, false, 0l, 100);
        List<String> actualEventIds = eventIdsModel.getLeft();

        assertNotNull(actualEventIds);
        assertEquals(21, actualEventIds.size());
    }

    @Test
    public void testFindAllIdsByEventType() {

        String growthMonitoringEventype = "Growth Monitoring";
        Pair<List<String>, Long> eventIdsModel = eventService.findAllIdsByEventType(growthMonitoringEventype, false, 0l, 100);
        List<String> actualEventIds = eventIdsModel.getLeft();

        assertNotNull(actualEventIds);
        assertEquals(4, actualEventIds.size());

        Map<String, Boolean> expectedIdMap = new HashMap<>();
        expectedIdMap.put("05934ae338431f28bf6793b24177a1dc", true);
        expectedIdMap.put("05934ae338431f28bf6793b241780bac", true);
        expectedIdMap.put("05934ae338431f28bf6793b241781149", true);
        expectedIdMap.put("05934ae338431f28bf6793b241781a1e", true);

        assertTrue(expectedIdMap.containsKey(actualEventIds.get(0)));
        assertTrue(expectedIdMap.containsKey(actualEventIds.get(1)));
        assertTrue(expectedIdMap.containsKey(actualEventIds.get(2)));
        assertTrue(expectedIdMap.containsKey(actualEventIds.get(3)));

    }

    @Test
    public void testFindAllDeletedIdsByEventType() {

        String growthMonitoringEventype = "Growth Monitoring";

        Pair<List<String>, Long> eventIdsModel = eventService.findAllIdsByEventType(growthMonitoringEventype, true, 0l, 100);
        List<String> actualEventIds = eventIdsModel.getLeft();

        assertNotNull(actualEventIds);
        assertEquals(1, actualEventIds.size());

        assertEquals("cfcc0e7e3cef11eab77f2e728ce88125", actualEventIds.get(0));
    }

    @Test
    public void testFindAllDeletedIds() {

        Pair<List<String>, Long> eventIdsModel = eventService.findAllIdsByEventType(null, true, 0l, 100);
        List<String> actualEventIds = eventIdsModel.getLeft();

        assertNotNull(actualEventIds);
        assertEquals(1, actualEventIds.size());
        assertEquals("cfcc0e7e3cef11eab77f2e728ce88125", actualEventIds.get(0));
    }

    @Test
    public void testFindAllIdsByEventTypeOrderedByServerVersion() {

        String growthMonitoringEventype = "Growth Monitoring";
        Pair<List<String>, Long> eventIdsModel = eventService.findAllIdsByEventType(growthMonitoringEventype, false, 0l, 100);
        List<String> actualEventIds = eventIdsModel.getLeft();

        assertNotNull(actualEventIds);
        assertEquals(4, actualEventIds.size());
        assertEquals("05934ae338431f28bf6793b24177a1dc", actualEventIds.get(0));
        assertEquals("05934ae338431f28bf6793b241780bac", actualEventIds.get(1));
        assertEquals("05934ae338431f28bf6793b241781149", actualEventIds.get(2));
        assertEquals("05934ae338431f28bf6793b241781a1e", actualEventIds.get(3));
        assertEquals(1521469045590l, eventIdsModel.getRight().longValue());

    }

    @Test
    public void testFindAllIdsByEventTypeLimitsByGivenParam() {

        String growthMonitoringEventype = "Growth Monitoring";
        Pair<List<String>, Long> eventIdsModel = eventService.findAllIdsByEventType(growthMonitoringEventype, false, 0l, 2);
        List<String> actualEventIds = eventIdsModel.getLeft();

        assertNotNull(actualEventIds);
        assertEquals(2, actualEventIds.size());
        assertEquals("05934ae338431f28bf6793b24177a1dc", actualEventIds.get(0));
        assertEquals("05934ae338431f28bf6793b241780bac", actualEventIds.get(1));
        assertEquals(1521469045588l, eventIdsModel.getRight().longValue());

    }

    @Test
    public void testFindAllIdsOrdersByServerVersionAnd() {

        Pair<List<String>, Long> eventIdsModel = eventService.findAllIdsByEventType(null, false, 0l, 100);
        List<String> actualEventIds = eventIdsModel.getLeft();

        assertNotNull(actualEventIds);
        assertEquals(21, actualEventIds.size());
        assertEquals("05934ae338431f28bf6793b2417696bf", actualEventIds.get(0));
        assertEquals("65de6fd9-c061-4026-b2e7-e10eb22169af", actualEventIds.get(19));
        assertEquals(1573736256054l, eventIdsModel.getRight().longValue());

    }

    @Test
    public void testExportEventDataWithoutSettingsConfigured() throws JsonProcessingException {
        List<Object> rowData = new ArrayList<>();
        rowData.add("location_name");
        rowData.add("location_id");
        eventsRepository.add(createFlagProblemEvent());
        when(exportEventDataMapper
                .getExportEventDataAfterMapping(any(Object.class), anyString(), anyBoolean(), anyBoolean()))
                .thenReturn(rowData);
        ExportEventDataSummary exportEventDataSummary = eventService
                .exportEventData("335ef7a3-7f35-58aa-8263-4419464946d8", "flag_problem", null, null);
        assertNotNull(exportEventDataSummary);
        assertEquals(2, exportEventDataSummary.getRowsData().size());
    }

    @Test
    public void testExportEventDataWithSettingsConfigured() throws JsonProcessingException {
        eventsRepository.add(createFlagProblemEvent());
        List<Object> rowData = new ArrayList<>();
        rowData.add("location_name");
        rowData.add("location_id");
        Map<String, String> settingsConfigsMap = new HashMap<>();
        settingsConfigsMap.put("Location id", "$.locationId");
        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("identifier");

        when(planRepository.get(anyString())).thenReturn(plan);
        when(exportEventDataMapper.getColumnNamesAndLabelsByEventType(anyString())).thenReturn(settingsConfigsMap);
        when(exportEventDataMapper
                .getExportEventDataAfterMapping(any(Object.class), anyString(), anyBoolean(), anyBoolean()))
                .thenReturn(rowData);
        ExportEventDataSummary exportEventDataSummary = eventService
                .exportEventData("335ef7a3-7f35-58aa-8263-4419464946d8", "flag_problem", null, null);
        assertNotNull(exportEventDataSummary);
        assertEquals(2, exportEventDataSummary.getRowsData().size());
    }

    @Test
    public void testGetImagesMetadataForFlagProblemEvent() throws JsonProcessingException {
        eventsRepository.add(createFlagProblemEvent());
        when(exportEventDataMapper.getFlagProblemEventImagesMetadata(anyObject(), anyString(), anyString(), anyString())).thenReturn(createExportFlagProblemEventImageMetadata());
        ExportImagesSummary exportImagesSummary = eventService.getImagesMetadataForFlagProblemEvent("335ef7a3-7f35-58aa-8263-4419464946d8", "flag_problem", null, null);
        assertNotNull(exportImagesSummary);
        assertEquals("ddcaf383-882e-448b-b701-8b72cb0d4d7a", exportImagesSummary.getExportFlagProblemEventImageMetadataList().get(0).getStockId());
        assertEquals("EPP Ambodisatrana 2", exportImagesSummary.getExportFlagProblemEventImageMetadataList().get(0).getServicePointName());
        assertEquals("Midwifery Kit", exportImagesSummary.getExportFlagProblemEventImageMetadataList().get(0).getProductName());

        assertEquals(1, exportImagesSummary.getServicePoints().size());
        assertTrue(exportImagesSummary.getServicePoints().contains("EPP Ambodisatrana 2"));
    }

    private ExportFlagProblemEventImageMetadata createExportFlagProblemEventImageMetadata() {
        ExportFlagProblemEventImageMetadata exportFlagProblemEventImageMetadata = new ExportFlagProblemEventImageMetadata();
        exportFlagProblemEventImageMetadata.setProductName("Midwifery Kit");
        exportFlagProblemEventImageMetadata.setStockId("ddcaf383-882e-448b-b701-8b72cb0d4d7a");
        exportFlagProblemEventImageMetadata.setServicePointName("EPP Ambodisatrana 2");
        return exportFlagProblemEventImageMetadata;
    }

    @Test
    public void testFindByServerVersion() {
        List<Event> events = eventService.findByServerVersion(0L);

        assertEquals(22, events.size());
    }
}
