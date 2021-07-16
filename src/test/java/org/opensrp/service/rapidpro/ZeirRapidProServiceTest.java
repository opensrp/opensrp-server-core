package org.opensrp.service.rapidpro;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.domain.postgres.Organization;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.service.IdentifierSourceService;
import org.opensrp.service.OrganizationService;
import org.opensrp.service.UniqueIdentifierService;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.smartregister.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ZeirRapidProServiceTest extends BaseRepositoryTest {

	@Autowired
	private ZeirRapidProService zeirRapidProService;

	@Spy
	private RestTemplate restTemplate;

	@Mock
	private HttpClient httpClient;

	@Mock
	private HttpGet contactsHttpRequest;

	@Mock
	private HttpGet supervisorHttpRequest;

	@Mock
	private RapidProOnTaskComplete onTaskComplete;

	@Mock
	private OrganizationService organizationService;

	@Mock
	private IdentifierSourceService identifierSourceService;

	@Mock
	private UniqueIdentifierService uniqueIdentifierService;

	@Spy
	private Organization organization;

	private ZeirRapidProService zeirRapidProServiceSpy;

	@BeforeClass
	public static void bootStrap() {
		tableNames = Arrays
				.asList("core.rapidpro_state", "core.event", "core.event_metadata", "core.client", "core.client_metadata");
	}

	@Before
	public void setUp() throws Exception {
		truncateTables();
		MockitoAnnotations.initMocks(this);
		zeirRapidProService.setHttpClient(httpClient);
		zeirRapidProService.setOrganizationService(organizationService);
		zeirRapidProService.setIdentifierSourceService(identifierSourceService);
		zeirRapidProService.setUniqueIdentifierService(uniqueIdentifierService);
		zeirRapidProServiceSpy = Mockito.spy(zeirRapidProService);

		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setIdentifier("2");
		identifierSource.setId(1L);

		Mockito.doReturn(Arrays.asList(identifierSource)).when(identifierSourceService).findAllIdentifierSources();

		Mockito.doReturn(Arrays.asList("1201200-1")).when(uniqueIdentifierService)
				.generateIdentifiers(Mockito.any(IdentifierSource.class), Mockito.anyInt(), Mockito.anyString());

		Mockito.doReturn("102e1ee92s9-12a90192-1s999b1").when(zeirRapidProServiceSpy)
				.getProviderLocationId(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

		organization.setName("Team");
		organization.setIdentifier("5039573a-6f39-4385-9e38-4809811faf6b");
		Mockito.doReturn(organization).when(organizationService).getOrganizationByLocationId(Mockito.anyString());
	}

	@Test
	public void testQueryContactsWithNoLocationId() throws IOException {
		mockContactsHttpResponse();
		mockSupervisorHttpResponse();
		zeirRapidProServiceSpy.queryContacts(onTaskComplete);
		Mockito.verify(onTaskComplete, Mockito.atLeastOnce()).completeTask();
	}

	@Test
	public void testQueryContactsWithLocationIdSavesEventsAndClients() throws IOException {
		mockContactsHttpResponse();
		mockSupervisorHttpResponse();
		zeirRapidProServiceSpy.queryContacts(onTaskComplete);
		Mockito.verify(onTaskComplete, Mockito.atLeastOnce()).completeTask();
		List<Event> events = zeirRapidProServiceSpy.getEventService().getAll();

		Assert.assertTrue(events.size() >= 7);
		Assert.assertTrue(eventIsCreated(events, "Birth Registration"));
		Assert.assertTrue(eventIsCreated(events, "New Woman Registration"));
		Assert.assertTrue(eventIsCreated(events, "Vaccination"));

		//At least 3 Vaccination events were created
		Assert.assertTrue(
				events.stream().filter(event -> event.getEventType().equalsIgnoreCase("Vaccination")).count() >= 3);

		//At least 2 Growth Monitoring events were created
		Assert.assertTrue(
				events.stream().filter(event -> event.getEventType().equalsIgnoreCase("Growth Monitoring")).count() >= 2);

		//At least 2 clients created
		Assert.assertTrue(zeirRapidProServiceSpy.getClientService().countAll(0) >= 2);
	}

	private boolean eventIsCreated(List<Event> events, String evenType) {
		return events.stream().anyMatch(it -> it.getEventType().equalsIgnoreCase(evenType));
	}

	private void mockSupervisorHttpResponse() throws IOException {
		//Mock querying supervisors
		HttpResponse supervisorHttpResponse = Mockito.spy(HttpResponse.class);
		HttpEntity supervisorHttpEntity = Mockito.spy(HttpEntity.class);
		String supervisorJsonResponse = getFileContentAsString("rapidpro_supervisor_contact.json");
		Mockito.doReturn(new ByteArrayInputStream(supervisorJsonResponse.getBytes())).when(supervisorHttpEntity)
				.getContent();
		Mockito.doReturn(supervisorHttpEntity).when(supervisorHttpResponse).getEntity();
		Mockito.doReturn(supervisorHttpRequest).when(zeirRapidProServiceSpy)
				.getSupervisorContactRequest(Mockito.anyString());
		Mockito.doReturn(supervisorHttpResponse).when(httpClient).execute(supervisorHttpRequest);
	}

	private void mockContactsHttpResponse() throws IOException {
		//Mock querying contacts
		HttpResponse contactsHttpResponse = Mockito.spy(HttpResponse.class);
		HttpEntity contactsHttpEntity = Mockito.spy(HttpEntity.class);
		String contactJsonResponse = getFileContentAsString("rapidpro_contacts.json");
		Mockito.doReturn(new ByteArrayInputStream(contactJsonResponse.getBytes())).when(contactsHttpEntity).getContent();
		Mockito.doReturn(contactsHttpEntity).when(contactsHttpResponse).getEntity();
		Mockito.doReturn(contactsHttpRequest).when(zeirRapidProServiceSpy).getContactRequest();
		Mockito.doReturn(contactsHttpResponse).when(httpClient).execute(contactsHttpRequest);
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<>();
		scripts.add("client.sql");
		return scripts;
	}
}
