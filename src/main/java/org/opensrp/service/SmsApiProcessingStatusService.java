package org.opensrp.service;

import org.opensrp.domain.postgres.SmsApiProcessingStatus;
import org.opensrp.repository.SmsApiProcessingStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsApiProcessingStatusService {

	private SmsApiProcessingStatusRepository statusRepository;


	@Autowired
	public SmsApiProcessingStatusService(SmsApiProcessingStatusRepository repository) {
		this.statusRepository = repository;
	}

	public SmsApiProcessingStatus getSmsProcessingStatusById(long id) {
		return statusRepository.getSmsApiProcessingStatusById(id);
	}

	public List<SmsApiProcessingStatus> getStatusListByRequestStatus(String requestStatus) {
		return statusRepository.getStatusListByRequestStatus(requestStatus);
	}

	public List<SmsApiProcessingStatus> getStatusListBySmsDeliveryStatus(String deliveryStatus) {
		return statusRepository.getStatusListBySmsDeliveryStatus(deliveryStatus);
	}

	public List<SmsApiProcessingStatus> getStatusListBySmsDeliveryStatusAndAttempts(String deliveryStatus, int attempts) {
		return statusRepository.getStatusListBySmsDeliveryStatusAndAttempts(deliveryStatus, attempts);
	}

	public SmsApiProcessingStatus getStatusByEntityId(String entityId) {
		return statusRepository.get(entityId);
	}

	public List<SmsApiProcessingStatus> getAllStatusEntries() {
		return statusRepository.getAll();
	}

	public void addOrUpdateSmsApiProcessingEntry(SmsApiProcessingStatus status) {
		if(statusRepository.getSmsApiProcessingStatusById(status.getId()) != null) {
			statusRepository.update(status);
		}
		else {
			statusRepository.add(status);
		}
	}

	public void deleteSmsApiProcessingEntry(SmsApiProcessingStatus statusEntry) {
		statusRepository.safeRemove(statusEntry);
	}

}
