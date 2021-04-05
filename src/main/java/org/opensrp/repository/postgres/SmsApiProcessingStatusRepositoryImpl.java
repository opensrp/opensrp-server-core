package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.postgres.SmsApiProcessingStatus;
import org.opensrp.domain.postgres.SmsApiProcessingStatusExample;
import org.opensrp.repository.SmsApiProcessingStatusRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomSmsApiProcessingStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("smsApiProcessingStatusRepositoryPostgres")
public class SmsApiProcessingStatusRepositoryImpl extends BaseRepositoryImpl<SmsApiProcessingStatus> implements SmsApiProcessingStatusRepository {

	@Autowired
	private CustomSmsApiProcessingStatusMapper statusMapper;

	@Override
	public SmsApiProcessingStatus getSmsProcessingStatusById(long id) {
		return statusMapper.selectByPrimaryKey(id);
	}

	@Override
	public List<SmsApiProcessingStatus> getStatusListByRequestStatus(String requestStatus) {
		if (requestStatus == null)
			return null;
		SmsApiProcessingStatusExample example = new SmsApiProcessingStatusExample();
		example.createCriteria().andRequestStatusEqualTo(requestStatus);
		return statusMapper.selectByExample(example);
	}

	@Override
	public List<SmsApiProcessingStatus> getStatusListBySmsDeliveryStatus(String deliveryStatus) {
		if (deliveryStatus == null)
			return null;
		SmsApiProcessingStatusExample example = new SmsApiProcessingStatusExample();
		example.createCriteria().andRequestStatusEqualTo(deliveryStatus);
		return statusMapper.selectByExample(example);
	}

	@Override
	public List<SmsApiProcessingStatus> getStatusListBySmsDeliveryStatusAndAttempts(String deliveryStatus, int attempts) {
		if (deliveryStatus == null)
			return null;
		SmsApiProcessingStatusExample example = new SmsApiProcessingStatusExample();
		example.createCriteria().andRequestStatusEqualTo(deliveryStatus).andAttemptsEqualTo(attempts);
		return statusMapper.selectByExample(example);
	}

	@Override
	public SmsApiProcessingStatus get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		return statusMapper.selectByEntityId(id);
	}

	@Override
	@Transactional
	public void add(SmsApiProcessingStatus entity) {
		if (entity == null)  {
			return;
		}

		if (retrievePrimaryKey(entity) != null) { // Already exists
			return;
		}

		statusMapper.insertSelective(entity);
	}

	@Override
	public void update(SmsApiProcessingStatus entity) {
		if (entity == null || entity.getId() == null || entity.getId() == 0) {
			return;
		}
		statusMapper.updateByPrimaryKeySelective(entity);
	}

	@Override
	public List<SmsApiProcessingStatus> getAll() {
		return statusMapper.selectMany(new SmsApiProcessingStatusExample(), 0, DEFAULT_FETCH_SIZE);
	}

	@Override
	public void safeRemove(SmsApiProcessingStatus entity) {
		if (entity == null || entity.getId() == null || entity.getId() == 0L) {
			return;
		}
		statusMapper.deleteByPrimaryKey(entity.getId());
	}

	@Override
	protected Long retrievePrimaryKey(SmsApiProcessingStatus smsApiProcessingStatus) {
		return smsApiProcessingStatus == null ? null : smsApiProcessingStatus.getId();
	}

	@Override
	protected Object getUniqueField(SmsApiProcessingStatus smsApiProcessingStatus) {
		return smsApiProcessingStatus == null ? null : smsApiProcessingStatus.getId();
	}
}
