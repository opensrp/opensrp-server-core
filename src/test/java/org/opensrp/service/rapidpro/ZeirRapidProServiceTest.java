package org.opensrp.service.rapidpro;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.opensrp.domain.postgres.Organization;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.service.OrganizationService;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ZeirRapidProServiceTest extends BaseRepositoryTest {

	@Autowired
	private ZeirRapidProService zeirRapidProService;

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

	@Spy
	private Organization organization;

	private ZeirRapidProService zeirRapidProServiceSpy;

	@BeforeClass
	public static void bootStrap() {
		tableNames = Arrays.asList("core.event", "core.event_metadata", "core.client", "core.client_metadata");
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		zeirRapidProService.setHttpClient(httpClient);
		zeirRapidProService.setOrganizationService(organizationService);

		zeirRapidProServiceSpy = Mockito.spy(zeirRapidProService);
		Mockito.doReturn("102e1ee92s9-12a90192-1s999b1").when(zeirRapidProServiceSpy)
				.getProviderLocationId(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

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
	public void testQueryContactsWithLocationId() throws IOException {
		mockContactsHttpResponse();
		mockSupervisorHttpResponse();
		zeirRapidProServiceSpy.queryContacts(onTaskComplete);
		Mockito.verify(onTaskComplete, Mockito.atLeastOnce()).completeTask();
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

	@After
	public void tearDown() {
		truncateTables();
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<>();
		scripts.add("client.sql");
		return scripts;
	}
}
