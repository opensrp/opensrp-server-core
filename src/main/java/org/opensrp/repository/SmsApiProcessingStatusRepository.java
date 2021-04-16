package org.opensrp.repository;

import org.opensrp.domain.postgres.SmsApiProcessingStatus;
import org.springframework.lang.Nullable;

import java.util.List;

public interface SmsApiProcessingStatusRepository extends BaseRepository<SmsApiProcessingStatus> {

	SmsApiProcessingStatus getSmsApiProcessingStatusById(long id);

	@Nullable
	List<SmsApiProcessingStatus> getStatusListByRequestStatus(String requestStatus);

	@Nullable
	List<SmsApiProcessingStatus> getStatusListBySmsDeliveryStatus(String deliveryStatus);

	@Nullable
	List<SmsApiProcessingStatus> getStatusListBySmsDeliveryStatusAndAttempts(String deliveryStatus, int attempts);

}
