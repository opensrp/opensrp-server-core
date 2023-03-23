package org.opensrp.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.UniqueId;
import org.opensrp.repository.UniqueIdRepository;
import org.smartregister.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OpenmrsIDService {
	
	@Value("#{opensrp['openmrs.url']}")
	private String openmrsUrl;
	
	@Value("#{opensrp['openmrs.username']}")
	private String openmrsUserName;
	
	@Value("#{opensrp['openmrs.password']}")
	private String openmrsPassword;
	
	@Value("#{opensrp['openmrs.idgen.idsource']}")
	private int openmrsSourceId;
	
	// Client identifiers constant
	public static final String ZEIR_IDENTIFIER = "ZEIR_ID";
	
	public static final String CHILD_REGISTER_CARD_NUMBER = "Child_Register_Card_Number";
	
	public static final String OPENMRS_IDGEN_URL = "module/idgen/exportIdentifiers.form";
	
	private static Logger logger = LogManager.getLogger(OpenmrsIDService.class.toString());
	
	private CloseableHttpClient client;

	@Autowired
	private UniqueIdRepository uniqueIdPostgresRepository;
	
	public static OpenmrsIDService createInstanceWithOpenMrsUrl(String openmrsUrl) {
		OpenmrsIDService openmrsIDService = new OpenmrsIDService();
		openmrsIDService.openmrsUrl = openmrsUrl;
		return openmrsIDService;
	}
	
	public OpenmrsIDService() {
		this.client = HttpClients.createDefault();
	}
	
	public List<String> downloadOpenmrsIds(long size) {
		List<String> ids = new ArrayList<String>();
		String openmrsQueryUrl = this.openmrsUrl + OPENMRS_IDGEN_URL;
		// Add query parameters
		openmrsQueryUrl += "?source=" + this.openmrsSourceId + "&numberToGenerate=" + size;
		openmrsQueryUrl += "&username=" + this.openmrsUserName + "&password=" + this.openmrsPassword;
		
		try {
			String jsonResponse = getHttpResponse(openmrsQueryUrl);
			
			JSONObject responseJson = new JSONObject(jsonResponse);
			JSONArray jsonArray = responseJson.getJSONArray("identifiers");
			
			if (jsonArray != null && jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					ids.add(jsonArray.getString(i));
				}
			}
		}
		catch (IOException | JSONException e) {
			logger.error("", e);
			return null;
		}
		// import IDs and client data to database together with assignments
		return ids;
	}
	
	/**
	 * download ids only if the total unused is less than the size specified
	 *
	 * @param size
	 */
	public void downloadAndSaveIds(int size, String userName) {
		try {
			Long totalUnUsed = uniqueIdPostgresRepository.totalUnUsedIds();
			if (totalUnUsed < size) {
				long numberToGenerate = size - totalUnUsed;
				List<String> ids = downloadOpenmrsIds(numberToGenerate);
				for (String id : ids) {
					UniqueId uniqueId = new UniqueId();
					uniqueId.setCreatedAt(new Date());
					uniqueId.setOpenmrsId(id);
					uniqueId.setUsedBy(userName);
					uniqueId.setStatus(UniqueId.STATUS_NOT_USED);
					uniqueIdPostgresRepository.add(uniqueId);
				}
			}
		}
		catch (Exception e) {
			logger.error("", e);
		}
		
	}
	
	public void clearRecords() {
		try {
			uniqueIdPostgresRepository.clearTable();
		}
		catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public Boolean checkIfClientExists(Client client) throws SQLException {
		try {
			String location = client.getAddress("usual_residence").getAddressField("address2");

			String usedBy = (String) client.getAttribute(CHILD_REGISTER_CARD_NUMBER);

			boolean clientExists = uniqueIdPostgresRepository.checkIfClientExists(usedBy, location);
			
			logger.info(
			    "[checkIfClientExists] - Card Number:" + usedBy + " - [Exists] " + clientExists);
			
			return clientExists;
		}
		catch (Exception e) {
			logger.error("", e);
			return null;
		}
	}
	
	public void assignOpenmrsIdToClient(String zeirID, Client client) throws SQLException {
		// create jdbc template to persist the ids
		try {
			String location = client.getAddress("usual_residence").getAddressField("address2");
			
			if (!this.checkIfClientExists(client)) {
				String childRegisterCardNumber = (String) client.getAttribute(CHILD_REGISTER_CARD_NUMBER);
				client.addIdentifier(ZEIR_IDENTIFIER, zeirID);
				UniqueId uniqueId = new UniqueId();
				uniqueId.setOpenmrsId(zeirID);
				uniqueId.setStatus(UniqueId.STATUS_USED);
				uniqueId.setUsedBy(childRegisterCardNumber);
				uniqueId.setLocation(location);
				uniqueId.setCreatedAt(new Date());
				uniqueIdPostgresRepository.add(uniqueId);
				logger.info("Assigned " + ZEIR_IDENTIFIER + " to " + client.fullName());
			}
		}
		catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public List<UniqueId> getNotUsedIds(int limit) {
		return uniqueIdPostgresRepository.getNotUsedIds(limit);
	}
	
	public List<String> getNotUsedIdsAsString(int limit) {
		return uniqueIdPostgresRepository.getNotUsedIdsAsString(limit);
	}
	
	public Long[] markIdsAsUsed(List<String> ids) {
		return uniqueIdPostgresRepository.markAsUsed(ids);
	}
	
	public List<String> getOpenMRSIdentifiers(String source, String numberToGenerate)
	        throws JSONException {
		List<String> ids = new ArrayList<>();
		String openMRSUrl = this.openmrsUrl + OPENMRS_IDGEN_URL;
		openMRSUrl += "?source=" + source + "&numberToGenerate=" + numberToGenerate;
		openMRSUrl += "&username=" + openmrsUserName + "&password=" + openmrsPassword;
		
		try {
			String jsonResponse = getHttpResponse(openMRSUrl);
			JSONObject responseJson = new JSONObject(jsonResponse);
			JSONArray jsonArray = responseJson.getJSONArray("identifiers");
			
			if (jsonArray != null && jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					ids.add(jsonArray.getString(i));
				}
			}
			
			return ids;
			
		}
		catch (IOException | JSONException e) {
			logger.error("", e);
			return null;
		}
		
	}

	protected String getHttpResponse(String url) throws IOException {
		HttpGet get = new HttpGet(url);
		try (CloseableHttpResponse response = client.execute(get)) {
			return EntityUtils.toString(response.getEntity());
		}
	}
	
}
