package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.common.util.DateUtil;
import org.opensrp.util.constants.RapidProConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;

@Service
public class RapidProService {

	private static final Logger logger = LogManager.getLogger(RapidProService.class.toString());

	private static final String API_URL = "/api/v2";

	@Value("#{opensrp['rapidpro.url']}")
	private String rapidProUrl;

	@Value("#{opensrp['rapidpro.token']}")
	private String rapidProToken;

	private HttpClient httpClient;

	private EventService eventService;

	private ClientService clientService;

	public RapidProService() {
		this.httpClient = HttpClientBuilder.create().build();
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	public JSONArray queryContacts(String dateModified) {

		JSONArray results = null;
		String url = StringUtils.isNotBlank(dateModified) ? getBaseUrl() + "/contacts.json?after=" + dateModified :
				getBaseUrl() + "/contacts.json?before=" + DateUtil.getTodayAsString();
		HttpGet contactsRequest = (HttpGet) setupRapidproRequest(url, new HttpGet());

		try {
			HttpResponse httpResponse = httpClient.execute(contactsRequest);
			if(httpResponse != null && httpResponse.getEntity() != null) {
				JSONObject responseJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));

				results = responseJson.optJSONArray(RapidProConstants.RESULTS);

				if (results != null) {
					processResults(results);
				}
			}
		} catch (IOException exception) {
			logger.error(exception.getMessage(), exception);
			return null;
		}
		return results;
	}

	private void processResults(JSONArray results) {
		logger.info("Found " + (results.isEmpty() ? 0 : results.length()) + " recently modified contacts");
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
