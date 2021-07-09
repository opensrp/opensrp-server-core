package org.opensrp.service.rapidpro;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

@Service
public class RapidProService {

	private static final Logger logger = LogManager.getLogger(RapidProService.class);

	private static final String API_URL = "/api/v2";

	@Value("#{opensrp['rapidpro.url']}")
	private String rapidProUrl;

	@Value("#{opensrp['rapidpro.token']}")
	private String rapidProToken;

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
	public void queryContacts(String dateModified) {

		String url = !dateModified.equalsIgnoreCase("#") ? getBaseUrl() + "/contacts.json?after=" + dateModified :
				getBaseUrl() + "/contacts.json?before=" + Instant.now().toString();

		HttpGet contactsRequest = (HttpGet) setupRapidproRequest(url, new HttpGet());

		try {
			HttpResponse httpResponse = httpClient.execute(contactsRequest);
			if (httpResponse != null && httpResponse.getEntity() != null) {
				zeirRapidProService.handleContactResponse(EntityUtils.toString(httpResponse.getEntity()));
			}
		}
		catch (IOException exception) {
			logger.error(exception.getMessage(), exception);
		}
	}

	private String getBaseUrl() {
		return StringUtils.isBlank(rapidProUrl) || StringUtils.isEmpty(rapidProUrl) ? "" :
				rapidProUrl.endsWith(API_URL) ? rapidProUrl : rapidProUrl + API_URL;
	}

	private HttpRequestBase setupRapidproRequest(String url, HttpRequestBase httpRequestBase) {
		httpRequestBase.setURI(URI.create(url));
		httpRequestBase.setHeader("Authorization", " Token " + rapidProToken);
		httpRequestBase.addHeader("content-type", "application/json");
		httpRequestBase.addHeader("Accept", "application/json");
		return httpRequestBase;
	}
}
