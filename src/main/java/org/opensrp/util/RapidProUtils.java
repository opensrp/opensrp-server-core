package org.opensrp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.util.constants.RapidProConstants;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.net.URI;
import java.util.List;

public class RapidProUtils {

	private static final String API_URL = "/api/v2";

	public static final int RATE_LIMIT_EXCEEDED = 429;

	// Optimal number of requests to make to avoid 2500 requests per hour limit
	// (8 (number of hits per scheduled task) by 25 (data/requests) by 12 (from 5 minutes interval) = 2400)
	public static final int RAPIDPRO_DATA_LIMIT = 25;

	public static String getBaseUrl(String rapidProUrl) {
		return StringUtils.isBlank(rapidProUrl) || StringUtils.isEmpty(rapidProUrl) ? "" :
				rapidProUrl.endsWith(API_URL) ?
						rapidProUrl :
						rapidProUrl.endsWith("/") ?
								rapidProUrl.substring(0, rapidProUrl.length() - 1) + API_URL :
								rapidProUrl + API_URL;
	}

	public static HttpRequestBase setupRapidproRequest(String url, HttpRequestBase httpRequestBase,
			String rapidProToken) {
		httpRequestBase.setURI(URI.create(url));
		httpRequestBase.setHeader("Authorization", " Token " + rapidProToken);
		httpRequestBase.addHeader("content-type", "application/json");
		httpRequestBase.addHeader("Accept", "application/json");
		return httpRequestBase;
	}

	public static RequestConfig getRequestConfig(int timeoutSeconds) {
		int CONNECTION_TIMEOUT_MS = timeoutSeconds * 1000;
		return RequestConfig.custom()
				.setConnectionRequestTimeout(CONNECTION_TIMEOUT_MS)
				.setConnectTimeout(CONNECTION_TIMEOUT_MS)
				.setSocketTimeout(CONNECTION_TIMEOUT_MS)
				.build();
	}

	public static void logResponseStatusCode(@NonNull CloseableHttpResponse httpResponse, Logger logger) throws IOException {
		StatusLine statusLine = httpResponse.getStatusLine();
		if (statusLine != null) {
			switch (statusLine.getStatusCode()) {
				case HttpStatus.SC_OK:
				case HttpStatus.SC_CREATED:
				case HttpStatus.SC_NO_CONTENT:
					logger.info("[Status {}] RapidPro resource successfully listed/created/updated",
							statusLine.getStatusCode());
					break;
				case HttpStatus.SC_BAD_REQUEST:
					logger.error("[Status {}] RapidPro request failed due to invalid parameters",
							statusLine.getStatusCode());
					logger.error(EntityUtils.toString(httpResponse.getEntity()));
					break;
				case HttpStatus.SC_FORBIDDEN:
					logger.warn("[Status {}] RapidPro request failed due to lack of permissions to access resource",
							statusLine.getStatusCode());
					logger.warn(EntityUtils.toString(httpResponse.getEntity()));
					break;
				case HttpStatus.SC_NOT_FOUND:
					logger.warn("[Status {}] RapidPro Resource was not found", statusLine.getStatusCode());
					break;
				case RATE_LIMIT_EXCEEDED:
					logger.warn("[Status {}] RapidPro rate limit for the endpoint has been exceeded",
							statusLine.getStatusCode());
					logger.warn(EntityUtils.toString(httpResponse.getEntity()));
					break;
				default:
					logger.error("Unsupported status code");
					break;
			}
		}
	}

	public static RapidProContact getRapidProContactByPhone(CloseableHttpClient closeableHttpClient,
			HttpGet httpGet, ObjectMapper objectMapper, Logger logger) {
		try (CloseableHttpResponse httpResponse = closeableHttpClient.execute(httpGet)) {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				JSONArray results = getResults(new JSONObject(EntityUtils.toString(httpResponse.getEntity())));
				if (results != null) {
					if (results.isEmpty())
						return null;
					List<RapidProContact> rapidProContacts = getRapidProContacts(results, objectMapper);
					if (rapidProContacts == null || rapidProContacts.isEmpty()) {
						return null;
					}
					return rapidProContacts.get(0);
				}
			}
		}
		catch (JSONException | IOException exception) {
			logger.error(exception.getMessage(), exception.fillInStackTrace().toString());
		}
		return null;
	}

	public static JSONArray getResults(JSONObject responseJson) {
		try {
			return responseJson.optJSONArray(RapidProConstants.RESULTS);
		}
		catch (JSONException jsonException) {
			return null;
		}

	}

	public static List<RapidProContact> getRapidProContacts(JSONArray results, ObjectMapper objectMapper) throws
			JsonProcessingException {
		return objectMapper.readValue(results.toString(), new TypeReference<>() {

		});
	}

	public static HttpGet contactByPhoneRequest(String phone, String rapidProUrl, String rapidProToken) {
		return (HttpGet) setupRapidproRequest(getBaseUrl(rapidProUrl) + "/contacts.json?urn=tel:" + phone,
				new HttpGet(), rapidProToken);
	}
}
