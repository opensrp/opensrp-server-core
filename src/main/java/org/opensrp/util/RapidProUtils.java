package org.opensrp.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.net.URI;

public class RapidProUtils {

	private static final String API_URL = "/api/v2";

	public static final int RATE_LIMIT_EXCEEDED = 429;

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

	public static void logStatusCodeResponse(@NonNull CloseableHttpResponse httpResponse, Logger logger) throws IOException {
		StatusLine statusLine = httpResponse.getStatusLine();
		String message = EntityUtils.toString(httpResponse.getEntity());
		switch (statusLine.getStatusCode()) {
			case HttpStatus.SC_OK:
			case HttpStatus.SC_CREATED:
			case HttpStatus.SC_NO_CONTENT:
				logger.info("RapidPro resource successfully listed/created/updated");
				break;
			case HttpStatus.SC_BAD_REQUEST:
				logger.error("RapidPro request failed due to invalid parameters");
				logger.error(message);
				break;
			case HttpStatus.SC_FORBIDDEN:
				logger.warn("RapidPro request failed due to lack of permissions to access resource");
				break;
			case HttpStatus.SC_NOT_FOUND:
				logger.warn("RapidPro Resource was not found");
				break;
			case RATE_LIMIT_EXCEEDED:
				logger.warn("RapidPro rate limit for the endpoint has been exceeded");
				logger.error(message);
				break;
			default:
				logger.error("Unsupported status code");
				break;
		}
	}
}
