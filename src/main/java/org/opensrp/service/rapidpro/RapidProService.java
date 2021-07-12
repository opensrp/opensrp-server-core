package org.opensrp.service.rapidpro;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Service
public class RapidProService {

	private static final Logger logger = LogManager.getLogger(RapidProService.class);

	private HttpClient httpClient;

	private ZeirRapidProService zeirRapidProService;

	public RapidProService() {
		this.httpClient = HttpClientBuilder.create().build();
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Autowired
	public void setZeirRapidProService(ZeirRapidProService zeirRapidProService) {
		this.zeirRapidProService = zeirRapidProService;
	}

	/**
	 * This method will query RapidPro contacts filtering by the date/time they were last modified.
	 * The default placeholder string is the hash sign ('#'). Query all contacts modified before today's date/time when the
	 * parameter passed is a '#' otherwise get only the contacts that were updated after the last modified date.
	 *
	 * @param dateModified the last date the contact was updated. Default placeholder '#'
	 * @return A list of RapidPro contacts
	 */
	public void queryContacts(String dateModified, RapidProOnTaskComplete onTaskComplete) {
		String baseUrl = zeirRapidProService.getBaseUrl();
		String url = !dateModified.equalsIgnoreCase("#") ? baseUrl + "/contacts.json?after=" + dateModified :
				baseUrl + "/contacts.json?before=" + Instant.now().toString();

		HttpGet contactsRequest = (HttpGet) zeirRapidProService.setupRapidproRequest(url, new HttpGet());

		try {
			HttpResponse httpResponse = httpClient.execute(contactsRequest);
			if (httpResponse != null && httpResponse.getEntity() != null) {
				zeirRapidProService.handleContactResponse(EntityUtils.toString(httpResponse.getEntity()), onTaskComplete);
			}
		}
		catch (IOException exception) {
			logger.error(exception.getMessage(), exception);
		}
	}
}
