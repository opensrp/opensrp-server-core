package org.opensrp.repository;

import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.RapidProStateSyncStatus;

import java.util.List;

public interface RapidProStateRepository {

	void saveState(RapidproState rapidproState);

	void updateSyncStatus(Long id, String uuid,  RapidProStateSyncStatus stateSyncStatus);

	List<RapidproState> getState(String entity, String property, String propertyKey);

	List<RapidproState> getStateByUuid(String uuid, String entity, String property);

	List<RapidproState> getUnSyncedStates(String entity, String property);

	boolean updateUuids(List<Long> ids, String uuid);

	List<RapidproState> getByStatesPropertyKey(String entity, String property, String propertyKey);

	List<RapidproState> getByStatesPropertyKey(String uuid, String entity, String property, String propertyKey);
}
