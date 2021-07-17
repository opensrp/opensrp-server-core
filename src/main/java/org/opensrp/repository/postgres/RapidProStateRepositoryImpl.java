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
	public void updateSyncStatus(Long id, RapidProStateSyncStatus stateSyncStatus) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.createCriteria().andIdEqualTo(id);
		RapidproState rapidproState = new RapidproState();
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
	public List<RapidproState> getUnSyncedStates(String entity, String property) {
		RapidproStateExample rapidproStateExample = new RapidproStateExample();
		rapidproStateExample.createCriteria()
				.andEntityEqualTo(entity)
				.andPropertyEqualTo(property)
				.andSyncStatusEqualTo(RapidProStateSyncStatus.UN_SYNCED.name());
		rapidproStateExample.setOrderByClause(RapidProConstants.ORDER_BY_ID_CLAUSE);
		return rapidproStateMapper.selectByExample(rapidproStateExample);
	}
}
