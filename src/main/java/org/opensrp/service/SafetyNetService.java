package org.opensrp.service;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.util.Base64;
import com.google.api.client.util.Key;
import org.opensrp.domain.AttestationStatement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class SafetyNetService {

	@Value("#{opensrp['safetynet.apikey']}")
	private String safetyNetAPIKey;

	private static final String SAFETYNET_BASEURL = "https://www.googleapis.com/androidcheck/v1/attestations/verify";

	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	public static class VerificationRequest {
		@Key
		public String signedAttestation;

		public VerificationRequest(String signedAttestation) {
			this.signedAttestation = signedAttestation;
		}
	}

	public static class VerificationResponse {
		@Key
		public boolean isValidSignature;

		@Key
		public String error;
	}

	private VerificationResponse onlineVerify(VerificationRequest request) {
		HttpRequestFactory requestFactory =
				HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
					@Override
					public void initialize(HttpRequest request) {
						request.setParser(new JsonObjectParser(JSON_FACTORY));
					}
				});

		String URL = SAFETYNET_BASEURL + "?key=" + safetyNetAPIKey;
		GenericUrl url = new GenericUrl(URL);
		HttpRequest httpRequest;
		try {
			httpRequest = requestFactory.buildPostRequest(url, new JsonHttpContent(JSON_FACTORY,
					request));
			return httpRequest.execute().parseAs(VerificationResponse.class);
		} catch (IOException e) {
			System.err.println(
					"Failure: Network error while connecting to the Google Service " + URL + ".");
			System.err.println("Ensure that you added your API key and enabled the Android device "
					+ "verification API.");
			return null;
		}
	}

	/**
	 * Extracts the data part from a JWS signature.
	 */
	private static byte[] extractJwsData(String jws) {
		String[] parts = jws.split("[.]");
		if (parts.length != 3) {
			System.err.println("Failure: Illegal JWS signature format. The JWS consists of "
					+ parts.length + " parts instead of 3.");
			return null;
		}
		return Base64.decodeBase64(parts[1]);
	}

	public AttestationStatement parseAndVerify(String signedAttestationStatment) {
		VerificationRequest request = new VerificationRequest(signedAttestationStatment);
		VerificationResponse response = onlineVerify(request);
		if (response == null) {
			return null;
		}

		if (response.error != null) {
			System.err.println(
					"Failure: The API encountered an error processing this request: "
							+ response.error);
			return null;
		}

		if (!response.isValidSignature) {
			System.err.println(
					"Failure: The cryptographic signature of the attestation statement couldn't be "
							+ "verified.");
			return null;
		}

		System.out.println("Sucessfully verified the signature of the attestation statement.");

		// The signature is valid, extract the data JSON from the JWS signature.
		byte[] data = extractJwsData(signedAttestationStatment);

		// Parse and use the data JSON.
		try {
			return JSON_FACTORY.fromInputStream(new ByteArrayInputStream(data),
					AttestationStatement.class);
		} catch (IOException e) {
			System.err.println("Failure: Failed to parse the data portion of the JWS as valid " +
					"JSON.");
			return null;
		}
	}
}
