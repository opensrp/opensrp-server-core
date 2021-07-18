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
import org.opensrp.util.constants.RapidProConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

public abstract class BaseRapidProStateService {

	protected final Logger logger = LogManager.getLogger(getClass());

	protected CloseableHttpClient closeableHttpClient;

	private RapidProStateRepository rapidProStateRepository;

	@Value("#{opensrp['rapidpro.url']}")
	protected String rapidProUrl;

	@Value("#{opensrp['rapidpro.token']}")
	protected String rapidProToken;

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

	public boolean updateUuids(List<Long> ids, String uuid) {
		return rapidProStateRepository.updateUuids(ids, uuid);
	}

	public List<RapidproState> getStatesByPropertyKey(String entity, String property, String propertyKey) {
		return rapidProStateRepository.getByStatesPropertyKey(entity, property, propertyKey);
	}

	public void updateRapidProContact(RapidproState rapidproState, String payload, boolean existing) {
		String uuid = rapidproState.getUuid();
		if (uuid == null || RapidProConstants.UNPROCESSED_UUID.equalsIgnoreCase(uuid)) {
			return;
		}
		try {
			CloseableHttpResponse httpResponse = postToRapidPro(payload, getContactUrl(existing, uuid));
			StatusLine statusLine = httpResponse.getStatusLine();
			if (statusLine != null && statusLine.getStatusCode() == HttpStatus.SC_OK && existing) {
				updateRapidProState(rapidproState.getId(), RapidProStateSyncStatus.SYNCED);
			}
		}
		catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	protected String getContactUrl(boolean existing, String uuid) {
		return RapidProUtils.getBaseUrl(rapidProUrl) + (existing ? "/contacts.json?uuid=" + uuid : "/contacts.json");
	}

	public CloseableHttpResponse postToRapidPro(String payload, String url) throws IOException {
		HttpPost httpPost = (HttpPost) RapidProUtils
				.setupRapidproRequest(url, new HttpPost(), rapidProToken);
		StringEntity stringEntity = new StringEntity(payload);
		httpPost.setEntity(stringEntity);
		return closeableHttpClient.execute(httpPost);
	}
}
