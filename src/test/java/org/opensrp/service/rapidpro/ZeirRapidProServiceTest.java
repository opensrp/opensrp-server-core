package org.opensrp.service.rapidpro;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ZeirRapidProServiceTest extends BaseRepositoryTest {

	@Autowired
	private ZeirRapidProService zeirRapidProService;

	private ZeirRapidProService zeirRapidProServiceMock;

	@Mock
	private HttpClient httpClient;

	@Mock
	private HttpGet contactsHttpRequest;

	@Mock
	private HttpGet supervisorHttpRequest;

	@Mock
	private RapidProOnTaskComplete onTaskComplete;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		zeirRapidProService.setHttpClient(httpClient);
		zeirRapidProServiceMock = Mockito.spy(zeirRapidProService);
	}

	@Test
	public void testQueryContactsWithNoLocationId() throws IOException {
		mockContactsHttpResponse();
		mockSupervisorHttpResponse();
		zeirRapidProServiceMock.queryContacts(onTaskComplete);
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
		Mockito.doReturn(supervisorHttpRequest).when(zeirRapidProServiceMock)
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
		Mockito.doReturn(contactsHttpRequest).when(zeirRapidProServiceMock).getContactRequest();
		Mockito.doReturn(contactsHttpResponse).when(httpClient).execute(contactsHttpRequest);
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<>();
		scripts.add("client.sql");
		return scripts;
	}
}
