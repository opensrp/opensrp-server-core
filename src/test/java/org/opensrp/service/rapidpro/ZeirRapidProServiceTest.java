package org.opensrp.service.rapidpro;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
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

	@Mock
	private HttpClient httpClient;

	@Mock
	private RapidProOnTaskComplete onTaskComplete;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		zeirRapidProService.setHttpClient(httpClient);
	}

	@Test
	public void testQueryContacts() throws IOException {
		HttpResponse httpResponse = Mockito.spy(HttpResponse.class);
		HttpEntity entity = Mockito.spy(HttpEntity.class);
		String jsonResponse = getFileContentAsString("rapidpro_contacts.json");
		Mockito.doReturn(new ByteArrayInputStream(jsonResponse.getBytes())).when(entity).getContent();
		Mockito.doReturn(entity).when(httpResponse).getEntity();
		Mockito.doReturn(httpResponse).when(httpClient).execute(Mockito.any(HttpUriRequest.class));
		zeirRapidProService.queryContacts("2021-07-01T00:00:00", onTaskComplete);
		Mockito.verify(onTaskComplete, Mockito.atLeastOnce()).completeTask();
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<>();
		scripts.add("client.sql");
		return scripts;
	}
}
