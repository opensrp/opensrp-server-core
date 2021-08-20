package org.opensrp.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.URI;

public class RapidProUtils {

	private static final String API_URL = "/api/v2";

	public static String getBaseUrl(String rapidProUrl) {
		return StringUtils.isBlank(rapidProUrl) || StringUtils.isEmpty(rapidProUrl) ? "" :
				rapidProUrl.endsWith(API_URL) ? rapidProUrl : rapidProUrl + API_URL;
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
}
