package org.opensrp.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RapidProServiceTest extends BaseRepositoryTest {

	@Mock
	private HttpClient httpClient;

	@Autowired
	private RapidProService rapidProService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		rapidProService.setHttpClient(httpClient);
	}

	@Test
	public void testQueryContacts() throws IOException {
		HttpResponse httpResponse = Mockito.spy(HttpResponse.class);
		HttpEntity entity = Mockito.spy(HttpEntity.class);
		String jsonResponse =
				"{\"next\":null,\"previous\":null,\"results\":[{\"uuid\":\"fac6a048-804d-476e-917b-0190b4b272b5\","
						+ "\"name\":\"Moon\",\"language\":null,\"urns\":[\"telegram:1131648682#alwasymoon\"],\"groups\":[],\"fields\""
						+ ":{\"supervisor_phone\":null,\"temp\":null,\"bcg\":null,\"birth\":null,\"children_count\":null,\"date_joined\""
						+ ":null,\"district\":null,\"dob\":null,\"dpt1\":null,\"dpt2\":null,\"dpt3\":null,\"facility\":null,"
						+ "\"location\":null,\"measles\":null,\"measles2\":null,\"monthly_children_count\":null,"
						+ "\"mother_name\":null,\"mother_phone\":null,\"mvacc_id\":null,\"no_vaccine\":null,\"opv0\":null,"
						+ "\"opv1\":null,\"opv2\":null,\"opv3\":null,\"opv4\":null,\"outreach\":null,\"outreach_session\":null,"
						+ "\"pcv1\":null,\"pcv2\":null,\"pcv3\":null,\"position\":null,\"province\":null,\"rota1\":null,\"rota2\":null,"
						+ "\"sex\":null,\"supervisor\":null,\"under5_id\":null,\"yearly_children_count\":null,\"zone\":null},\"blocked\":false,"
						+ "\"stopped\":false,\"created_on\":\"2021-07-01T12:05:03.404724Z\",\"modified_on\":\"2021-07-01T12:05:03.539077Z\","
						+ "\"last_seen_on\":\"2021-07-01T12:05:03Z\"}]}";

		Mockito.doReturn(new ByteArrayInputStream(jsonResponse.getBytes())).when(entity).getContent();
		Mockito.doReturn(entity).when(httpResponse).getEntity();
		Mockito.doReturn(httpResponse).when(httpClient).execute(Mockito.any(HttpUriRequest.class));
		JSONArray contacts = rapidProService.queryContacts("2021-07-01T00:00:00");
		Assert.assertNotNull(contacts);
		Assert.assertEquals(1, contacts.length());
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<>();
		scripts.add("client.sql");
		return scripts;
	}
}
