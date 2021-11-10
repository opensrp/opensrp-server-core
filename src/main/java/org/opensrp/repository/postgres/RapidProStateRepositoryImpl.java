package org.opensrp.repository.postgres;

import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.postgres.RapidproStateExample;
import org.opensrp.domain.rapidpro.RapidProStateSyncStatus;
import org.opensrp.repository.RapidProStateRepository;
import org.opensrp.repository.postgres.mapper.RapidproStateMapper;
import org.opensrp.util.constants.RapidProConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RapidProStateRepositoryImpl implements RapidProStateRepository {

	private RapidproStateMapper rapidproStateMapper;

	@Autowired
	public void setRapidproStateMapper(RapidproStateMapper rapidproStateMapper) {
		this.rapidproStateMapper = rapidproStateMapper;
	}

	@Override
	public void saveState(RapidproState rapidproState) {
		rapidproStateMapper.insertSelective(rapidproState);
	}

	@Override
	public void updateSyncStatus(Long id, String uuid, RapidProStateSyncStatus stateSyncStatus) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.createCriteria().andIdEqualTo(id);
		RapidproState rapidproState = new RapidproState();
		rapidproState.setUuid(uuid);
		rapidproState.setSyncStatus(stateSyncStatus.name());
		rapidproStateMapper.updateByExampleSelective(rapidproState, rapidproStateExample);
	}

	@Override
	public List<RapidproState> getState(String entity, String property, String propertyKey) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.createCriteria()
				.andEntityEqualTo(entity)
				.andPropertyEqualTo(property)
				.andPropertyKeyEqualTo(propertyKey);
		rapidproStateExample.setOrderByClause(RapidProConstants.ORDER_BY_ID_CLAUSE);
		return rapidproStateMapper.selectByExample(rapidproStateExample);
	}

	@Override
	public List<RapidproState> getStateByUuid(String uuid, String entity, String property) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.createCriteria()
				.andUuidEqualTo(uuid)
				.andEntityEqualTo(entity)
				.andPropertyEqualTo(property);
		rapidproStateExample.setOrderByClause(RapidProConstants.ORDER_BY_ID_CLAUSE);
		return rapidproStateMapper.selectByExample(rapidproStateExample);
	}

	@Override
	public List<RapidproState> getUnSyncedStates(String entity, String property) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.createCriteria()
				.andEntityEqualTo(entity)
				.andPropertyEqualTo(property)
				.andSyncStatusEqualTo(RapidProStateSyncStatus.UN_SYNCED.name());
		rapidproStateExample.setOrderByClause(RapidProConstants.ORDER_BY_ID_CLAUSE);
		return rapidproStateMapper.selectByExample(rapidproStateExample);
	}

	@Override
	public List<RapidproState> getAllStates(String entity, String property) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.createCriteria()
				.andEntityEqualTo(entity)
				.andPropertyEqualTo(property)
				.andSyncStatusEqualTo(RapidProStateSyncStatus.SYNCED.name());
		rapidproStateExample.setOrderByClause(RapidProConstants.ORDER_BY_ID_CLAUSE);
		return rapidproStateMapper.selectByExample(rapidproStateExample);
	}

	@Override
	public boolean updateUuids(List<Long> ids, String uuid) {
		int counter = 0;
		for (Long id : ids) {
			RapidproState rapidproState = new RapidproState();
			rapidproState.setId(id);
			rapidproState.setUuid(uuid);
			rapidproState.setSyncStatus(RapidProStateSyncStatus.SYNCED.name());
			counter = counter + rapidproStateMapper.updateByPrimaryKeySelective(rapidproState);
		}
		return counter == ids.size();
	}

	@Override
	public List<RapidproState> getStatesByPropertyKey(String entity, String property, String propertyKey) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.createCriteria()
				.andEntityEqualTo(entity)
				.andPropertyEqualTo(property)
				.andPropertyKeyEqualTo(propertyKey);
		return rapidproStateMapper.selectByExample(rapidproStateExample);
	}

	@Override
	public List<RapidproState> getStatesByPropertyKey(String uuid, String entity, String property, String propertyKey) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.createCriteria()
				.andUuidEqualTo(uuid)
				.andEntityEqualTo(entity)
				.andPropertyEqualTo(property)
				.andPropertyKeyEqualTo(propertyKey);
		return rapidproStateMapper.selectByExample(rapidproStateExample);
	}

	@Override
	public List<RapidproState> getDistinctStatesByUuidAndSyncStatus(String uuid, String syncStatus) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.setDistinct(true);
		rapidproStateExample.createCriteria()
				.andUuidEqualTo(uuid)
				.andSyncStatusEqualTo(syncStatus);
		return rapidproStateMapper.selectByExample(rapidproStateExample);
	}
}
