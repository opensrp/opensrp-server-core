package org.opensrp.service.rapidpro;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.RapidProStateSyncStatus;
import org.opensrp.repository.RapidProStateRepository;
import org.opensrp.util.RapidProUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

public abstract class BaseRapidProStateService {

	protected final Logger logger = LogManager.getLogger(getClass());

	protected CloseableHttpClient closeableHttpClient;

	private RapidProStateRepository rapidProStateRepository;

	@Value("#{opensrp['rapidpro.url']}")
	private String rapidProUrl;

	@Value("#{opensrp['rapidpro.token']}")
	private String rapidProToken;

	public BaseRapidProStateService() {
		this.closeableHttpClient = HttpClients.createDefault();
	}

	@Autowired
	public void setRapidProStateRepository(RapidProStateRepository rapidProStateRepository) {
		this.rapidProStateRepository = rapidProStateRepository;
	}

	public void saveRapidProState(RapidproState rapidproState) {
		rapidProStateRepository.saveState(rapidproState);
	}

	public void updateRapidProState(Long id, RapidProStateSyncStatus stateSyncStatus) {
		rapidProStateRepository.updateSyncStatus(id, stateSyncStatus);
	}

	public List<RapidproState> getUnSyncedRapidProStates(String entity, String property) {
		return rapidProStateRepository.getUnSyncedStates(entity, property);
	}

	public List<RapidproState> getRapidProState(String entity, String property, String propertyKey) {
		return rapidProStateRepository.getState(entity, property, propertyKey);
	}

	public void updateRapidProContact(RapidproState rapidproState, String payload) {
		String updateContactUrl = RapidProUtils.getBaseUrl(rapidProUrl) +  "/contacts.json?uuid=" + rapidproState.getUuid();
		try {
			HttpPost httpPost = (HttpPost) RapidProUtils
					.setupRapidproRequest(updateContactUrl, new HttpPost(), rapidProToken);
			StringEntity stringEntity = new StringEntity(payload);
			httpPost.setEntity(stringEntity);
			CloseableHttpResponse httpResponse = closeableHttpClient.execute(httpPost);
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine != null && (statusLine.getStatusCode() == HttpStatus.SC_ACCEPTED
					|| statusLine.getStatusCode() == HttpStatus.SC_CREATED
					|| statusLine.getStatusCode() == HttpStatus.SC_OK)) {
				updateRapidProState(rapidproState.getId(), RapidProStateSyncStatus.SYNCED);
			}
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
}
