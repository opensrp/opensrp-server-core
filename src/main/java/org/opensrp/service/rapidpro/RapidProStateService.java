package org.opensrp.service.rapidpro;

import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.RapidProStateSyncStatus;
import org.opensrp.repository.RapidProStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RapidProStateService {

	private RapidProStateRepository rapidProStateRepository;

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

	public List<RapidproState> getUnSyncedRapidProState(String entity, String property) {
		return rapidProStateRepository.getUnSyncedState(entity, property);
	}

	public List<RapidproState> getSyncedRapidProStates(String entity, String property, String propertyKey) {
		return rapidProStateRepository.getState(entity, property, propertyKey, false);
	}
}
