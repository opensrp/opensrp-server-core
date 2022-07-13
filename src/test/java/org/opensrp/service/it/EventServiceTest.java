package org.opensrp.service.it;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.BaseIntegrationTest;
import org.opensrp.repository.postgres.ClientsRepositoryImpl;
import org.opensrp.repository.postgres.EventsRepositoryImpl;
import org.opensrp.service.EventService;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.opensrp.common.AllConstants.OpenSRPEvent.Client.ZEIR_ID;
import static org.opensrp.util.SampleFullDomainObject.*;
import static org.utils.AssertionUtil.*;
import static org.utils.DbAccessUtils.addObjectToRepository;

public class EventServiceTest extends BaseIntegrationTest {

    @Autowired
    private EventsRepositoryImpl allEvents;

    @Autowired
    private ClientsRepositoryImpl allClients;

    @Autowired
    private EventService eventService;

    private String username = "johndoe";

    @Before
    public void setUp() {
        allClients.removeAll();
        allEvents.removeAll();
    }

    @Before
    public void cleanUp() {
        allClients.removeAll();
        allEvents.removeAll();
    }

    @Test
    public void shouldFindAllByIdentifier() {
        Event expectedEvent = getEvent();
        Event expectedEvent2 = getEvent();
        expectedEvent2.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);
        List<Event> expectedEventList = asList(expectedEvent, expectedEvent2);

        addObjectToRepository(asList(expectedEvent, expectedEvent2, invalidEvent), allEvents);

        List<Event> actualEventList = eventService.findAllByIdentifier(IDENTIFIER_VALUE);

        assertTwoListAreSameIgnoringOrder(expectedEventList, actualEventList);
    }

    @Test
    public void shouldFindAllByIdentifierTypeAndValue() {
        Event expectedEvent = getEvent();
        Event expectedEvent2 = getEvent();
        expectedEvent2.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);
        List<Event> expectedEventList = asList(expectedEvent, expectedEvent2);

        addObjectToRepository(asList(expectedEvent, expectedEvent2, invalidEvent), allEvents);

        List<Event> actualEventList = eventService.findAllByIdentifier(IDENTIFIER_TYPE, IDENTIFIER_VALUE);

        assertTwoListAreSameIgnoringOrder(expectedEventList, actualEventList);
    }

    @Test
    public void shouldFindByDocumentId() {
        addObjectToRepository(asList(getEvent()), allEvents);
        Event expectedEvent = allEvents.getAll().get(0);

        Event actualEvent = eventService.getById(expectedEvent.getId());

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void shouldFindByBaseEntityAndFormSubmissionId() {
        Event expectedEvent = getEvent();
        Event invalidEvent = getEvent();
        invalidEvent.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);

        addObjectToRepository(asList(expectedEvent, invalidEvent), allEvents);

        Event actualEvent = eventService.getByBaseEntityAndFormSubmissionId(BASE_ENTITY_ID, FORM_SUBMISSION_ID);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfMultipleFound() {
        addObjectToRepository(asList(getEvent(), getEvent()), allEvents);

        eventService.getByBaseEntityAndFormSubmissionId(BASE_ENTITY_ID, FORM_SUBMISSION_ID);
    }

    @Test
    public void shouldReturnNullIfNoEventFound() {
        Event actualEvent = eventService.getByBaseEntityAndFormSubmissionId(BASE_ENTITY_ID, FORM_SUBMISSION_ID);

        assertNull(actualEvent);
    }

    @Test
    public void shouldFindByBaseEntityIdAndFormSubmissionIdUsingCouchDbConnector() throws IOException {
        Event expectedEvent = getEvent();
        Event invalidEvent = getEvent();
        invalidEvent.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);

        addObjectToRepository(asList(expectedEvent, invalidEvent), allEvents);
        Event actualEvent = allEvents.findByBaseEntityAndFormSubmissionId(BASE_ENTITY_ID,
                FORM_SUBMISSION_ID);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void shouldFindByBaseEntityId() {
        Event expectedEvent = getEvent();
        Event expectedEvent2 = getEvent();
        Event invalidEvent = getEvent();
        invalidEvent.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        List<Event> expectedEvents = asList(expectedEvent, expectedEvent2);

        addObjectToRepository(asList(expectedEvent, expectedEvent2, invalidEvent), allEvents);

        List<Event> actualEvents = eventService.findByBaseEntityId(BASE_ENTITY_ID);

        assertTwoListAreSameIgnoringOrder(expectedEvents, actualEvents);
    }

    @Test
    public void shouldFindByFormSubmissionId() {
        Event expectedEvent = getEvent();

        addObjectToRepository(asList(expectedEvent), allEvents);

        Event actualEvent = eventService.findByFormSubmissionId(FORM_SUBMISSION_ID);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void shouldFindByUniqueIdIdentifier() {
        Event expectedEvent = getEvent();
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);

        addObjectToRepository(asList(expectedEvent, invalidEvent), allEvents);

        Event actualEvent = eventService.find(IDENTIFIER_VALUE);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMultipleFoundWithSameIdentifierValue() {
        Event expectedEvent = getEvent();
        Event expectedEvent2 = getEvent();
        expectedEvent2.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);

        addObjectToRepository(asList(expectedEvent, expectedEvent2, invalidEvent), allEvents);

        eventService.find(IDENTIFIER_VALUE);
    }

    @Test
    public void shouldReturnNullIfNothingFoundWithIdentifier() {
        assertNull(eventService.find(IDENTIFIER_VALUE));
    }

    @Test
    public void shouldFindByEventObject() {
        Event expectedEvent = getEvent();

        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);

        addObjectToRepository(asList(expectedEvent, invalidEvent), allEvents);

        Event actualEvent = eventService.find(expectedEvent);

        assertEquals(expectedEvent, actualEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMultipleFoundWithSameEvent() {
        Event expectedEvent = getEvent();
        Event expectedEvent2 = getEvent();
        expectedEvent2.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);

        addObjectToRepository(asList(expectedEvent, expectedEvent2, invalidEvent), allEvents);

        eventService.find(expectedEvent);
    }

    @Test
    public void shouldReturnNullIfNothingFoundWithSameEvent() {
        Event expectedEvent = getEvent();
        assertNull(eventService.find(expectedEvent));
    }

    @Test
    public void shouldFindByEventOrDocumentId() {
        addObjectToRepository(asList(getEvent()), allEvents);
        Event expectedEvent = allEvents.getAll().get(0);

        Event actualEvent = eventService.findById(expectedEvent.getId());

        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void shouldReturnNullForNullOrEmptyIdInFindById() {
        Event actualEvent = eventService.findById("");
        assertNull(actualEvent);
        actualEvent = eventService.findById(null);
        assertNull(actualEvent);
    }

    @Test
    public void shouldReturnNullIfEventNotFound() {
        addObjectToRepository(asList(getEvent()), allEvents);

        Event actualEvent = eventService.findById(DIFFERENT_BASE_ENTITY_ID);

        assertNull(actualEvent);
    }

    @Test
    public void shouldAddEvent() {
        Event expectedEvent = getEvent();

        Event actualEvent = eventService.addEvent(expectedEvent, username);

        List<Event> dbEvents = allEvents.getAll();
        assertEquals(1, dbEvents.size());
        assertEquals(expectedEvent, actualEvent);
        assertNewObjectCreation(expectedEvent, dbEvents.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfAnEventAlreadyExistWithSameIdentifier() {
        Event expectedEvent = getEvent();
        Event expectedEvent2 = getEvent();
        expectedEvent2.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);

        addObjectToRepository(asList(expectedEvent, expectedEvent2, invalidEvent), allEvents);

        eventService.addEvent(expectedEvent, username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfAnEventAlreadyExistWithSameBaseEntityIdAndFormSumbissionId() {
        Event expectedEvent = getEvent();
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);

        addObjectToRepository(asList(expectedEvent, invalidEvent), allEvents);

        eventService.addEvent(expectedEvent, username);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNullFormSubmissionId() {
        Event expectedEvent = getEvent();
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);

        addObjectToRepository(asList(expectedEvent, invalidEvent), allEvents);

        expectedEvent.setFormSubmissionId(null);
        eventService.addEvent(expectedEvent, username);
    }

    @Test
    public void shouldAddEventWithCouchDbConnector() throws IOException {
        Event expectedEvent = getEvent();

        allEvents.add(expectedEvent);

        List<Event> dbEvents = allEvents.getAll();
        assertEquals(1, dbEvents.size());
        assertEquals(expectedEvent, dbEvents.get(0));
        assertNewObjectCreation(expectedEvent, dbEvents.get(0));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfAnEventAlreadyExistWithSameBaseEntityIdAndFormSumbissionIdWithCouchDbConector()
            throws IOException {
        Event expectedEvent = getEvent();
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);

        addObjectToRepository(asList(expectedEvent, invalidEvent), allEvents);

        allEvents.add(expectedEvent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNullFormSubmissionIdWithCouchDbConnector() throws IOException {
        Event expectedEvent = getEvent();
        Event invalidEvent = getEvent();
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        invalidEvent.setIdentifiers(identifiers);

        addObjectToRepository(asList(expectedEvent, invalidEvent), allEvents);

        expectedEvent.setFormSubmissionId(null);
        allEvents.add(expectedEvent);
    }

    @Test
    public void shouldAddIfNewEntityInAddOrUpdate() {
        Event expectedEvent = getEvent();

        Event actualEvent = eventService.addorUpdateEvent(expectedEvent, username);

        List<Event> dbEvents = eventService.getAll();
        assertEquals(1, dbEvents.size());
        assertEquals(expectedEvent, actualEvent);

        assertNewObjectCreation(expectedEvent, dbEvents.get(0));
    }

    @Test
    public void shouldUpdateIfExistingEntityInAddOrUpdate() {
        addObjectToRepository(Collections.singletonList(getEvent()), allEvents);
        Event expectedEvent = allEvents.getAll().get(0);

        Event actualEvent = eventService.addorUpdateEvent(expectedEvent, username);

        List<Event> dbEvents = eventService.getAll();
        assertEquals(1, dbEvents.size());
        assertEquals(expectedEvent, actualEvent);

        dbEvents.get(0).setServerVersion(0l);
        assertObjectUpdate(expectedEvent, dbEvents.get(0));
    }

    @Test
    public void shouldUpdateEvent() {
        addObjectToRepository(Collections.singletonList(getEvent()), allEvents);
        Event expectedEvent = allEvents.getAll().get(0);

        eventService.updateEvent(expectedEvent, username);

        List<Event> dbEvents = eventService.getAll();
        assertEquals(1, dbEvents.size());
        assertObjectUpdate(expectedEvent, dbEvents.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNewEventInUpdate() {
        Event event = getEvent();

        eventService.updateEvent(event, username);
    }

    @Test
    public void shouldFindBySeverVersion() {
        addObjectToRepository(Collections.singletonList(getEvent()), allEvents);

        Event expectedEvent = allEvents.getAll().get(0);

        List<Event> actualEvents = allEvents.findByServerVersion(expectedEvent.getServerVersion() - 1);

        assertEquals(1, actualEvents.size());
        assertEquals(expectedEvent, actualEvents.get(0));
    }

    @Test
    public void shouldGeAllEvents() {
        Event expectedEvent = getEvent();
        Event expectedEvent2 = getEvent();
        expectedEvent2.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        Map<String, String> identifiers = new HashMap<>(identifier);
        identifiers.put(IDENTIFIER_TYPE, "invalidValue");
        expectedEvent2.setIdentifiers(identifiers);
        List<Event> expectedEvents = asList(expectedEvent, expectedEvent2);

        addObjectToRepository(expectedEvents, allEvents);

        List<Event> actualEvents = eventService.getAll();

        assertTwoListAreSameIgnoringOrder(expectedEvents, actualEvents);
    }

    @Test
    public void shouldFindByObsFieldCodeKeyAndValue() {
        Event expectedEvent = getEvent();

        addObjectToRepository(Collections.singletonList(expectedEvent), allEvents);

        List<Event> actualEvents = eventService.findEventsByConceptAndValue(FIELD_CODE, VALUE);

        assertEquals(1, actualEvents.size());
        assertEquals(expectedEvent, actualEvents.get(0));
    }

    @Test
    public void shouldFindByBaseEntityAndEventType() {
        Event expectedEvent = getEvent();
        Event invalidEvent = getEvent();
        invalidEvent.setEventType("diff");

        addObjectToRepository(asList(expectedEvent, invalidEvent), allEvents);

        List<Event> actualEvents = eventService.findByBaseEntityAndType(BASE_ENTITY_ID, EVENT_TYPE);

        assertEquals(1, actualEvents.size());
        assertEquals(expectedEvent, actualEvents.get(0));
    }

    @Test
    public void shouldProcessOutOfArea() {
        Client client = getClient();
        client.addIdentifier(ZEIR_ID.toUpperCase(), "zeirId");
        addObjectToRepository(Collections.singletonList(client), allClients);
        Event existingEvent = getEvent();
        existingEvent.setBaseEntityId(client.getBaseEntityId());
        existingEvent.setEventType("Birth Registration");
        addObjectToRepository(Collections.singletonList(existingEvent), allEvents);

        //For null baseEntityId
        Event expectedEvent = getEvent();
        expectedEvent.setBaseEntityId(null);
        expectedEvent.addIdentifier(ZEIR_ID.toUpperCase(), "zeirId");
        Event actualEvent = eventService.processOutOfArea(expectedEvent);

        assertNull(actualEvent.getIdentifier(ZEIR_ID.toUpperCase()));
        assertEquals(BASE_ENTITY_ID, actualEvent.getBaseEntityId());
        assertEquals(PROVIDER_ID, actualEvent.getDetails().get("out_of_catchment_provider_id"));
        assertEquals(expectedEvent, actualEvent);

        //For empty baseEntityId
        expectedEvent = getEvent();
        expectedEvent.setBaseEntityId("");
        expectedEvent.addIdentifier(ZEIR_ID.toUpperCase(), "zeirId");
        actualEvent = eventService.processOutOfArea(expectedEvent);

        assertNull(actualEvent.getIdentifier(ZEIR_ID.toUpperCase()));
        assertEquals(BASE_ENTITY_ID, actualEvent.getBaseEntityId());
        assertEquals(PROVIDER_ID, actualEvent.getDetails().get("out_of_catchment_provider_id"));
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void shouldNotProcessOutOfAreaIfEventHasBaseEntityId() {
        Client client = getClient();
        client.addIdentifier(ZEIR_ID.toUpperCase(), "zeirId");
        addObjectToRepository(Collections.singletonList(client), allClients);
        Event existingEvent = getEvent();
        existingEvent.setBaseEntityId(client.getBaseEntityId());
        existingEvent.setEventType("Birth Registration");
        addObjectToRepository(Collections.singletonList(existingEvent), allEvents);

        Event expectedEvent = getEvent();
        expectedEvent.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        expectedEvent.addIdentifier(ZEIR_ID.toUpperCase(), "zeirId");

        Event actualEvent = eventService.processOutOfArea(expectedEvent);

        assertEquals("zeirId", actualEvent.getIdentifier(ZEIR_ID.toUpperCase()));
        assertEquals(DIFFERENT_BASE_ENTITY_ID, actualEvent.getBaseEntityId());
        assertNull(actualEvent.getDetails().get("out_of_catchment_provider_id"));
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void shouldNotProcessOutAreaIfNoClientFound() {
        Event existingEvent = getEvent();
        existingEvent.setBaseEntityId(BASE_ENTITY_ID);
        existingEvent.setEventType("Birth Registration");
        addObjectToRepository(Collections.singletonList(existingEvent), allEvents);

        Event expectedEvent = getEvent();
        expectedEvent.setBaseEntityId(null);
        expectedEvent.addIdentifier(ZEIR_ID.toUpperCase(), "zeirId");

        Event actualEvent = eventService.processOutOfArea(expectedEvent);

        assertEquals("zeirId", actualEvent.getIdentifier(ZEIR_ID.toUpperCase()));
        assertNull(actualEvent.getBaseEntityId());
        assertNull(actualEvent.getDetails().get("out_of_catchment_provider_id"));
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void shouldNotProcessOutOfAreaIfNoExistingBirthRegistrationEventFound() {
        Client client = getClient();
        client.addIdentifier(ZEIR_ID.toUpperCase(), "zeirId");
        addObjectToRepository(Collections.singletonList(client), allClients);
        Event existingEvent = getEvent();
        existingEvent.setBaseEntityId(client.getBaseEntityId());
        addObjectToRepository(Collections.singletonList(existingEvent), allEvents);

        //For null baseEntityId
        Event expectedEvent = getEvent();
        expectedEvent.setBaseEntityId(null);
        expectedEvent.addIdentifier(ZEIR_ID.toUpperCase(), "zeirId");
        Event actualEvent = eventService.processOutOfArea(expectedEvent);

        assertEquals("zeirId", actualEvent.getIdentifier(ZEIR_ID.toUpperCase()));
        assertNull(actualEvent.getBaseEntityId());
        assertNull(actualEvent.getDetails().get("out_of_catchment_provider_id"));
        assertEquals(expectedEvent, actualEvent);
    }

    @Test
    public void shouldMergeAndUpdateExistingEvent() {
        addObjectToRepository(Collections.singletonList(getEvent()), allEvents);
        Event updatedEvent = allEvents.getAll().get(0);

        updatedEvent.addIdentifier("Second_Identifier", DIFFERENT_BASE_ENTITY_ID);
        updatedEvent.addObs(getObs().withComments("comments").withFieldCode(DIFFERENT_BASE_ENTITY_ID));

        Event actualEvent = eventService.mergeEvent(updatedEvent);
        List<Event> dbEvents = allEvents.getAll();

        DateTime updatedEventDate = updatedEvent.getEventDate();
        updatedEvent.setEventDate(null);
        actualEvent.setEventDate(null);
        actualEvent.setDateEdited(null);
        assertEquals(1, dbEvents.size());
        assertEquals(updatedEvent, actualEvent);
        updatedEvent.setEventDate(updatedEventDate);
        assertObjectUpdate(updatedEvent, dbEvents.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfExistingClientNotFound() {
        Event updatedEvent = getEvent();

        eventService.mergeEvent(updatedEvent);
    }
}
