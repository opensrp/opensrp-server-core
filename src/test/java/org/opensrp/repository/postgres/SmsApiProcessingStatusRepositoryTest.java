package org.opensrp.repository.postgres;

import org.junit.Test;
import org.opensrp.domain.postgres.SmsApiProcessingStatus;
import org.opensrp.repository.SmsApiProcessingStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class SmsApiProcessingStatusRepositoryTest extends BaseRepositoryTest{

	@Autowired
	private SmsApiProcessingStatusRepository statusRepository;

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<>();
		scripts.add("sms_api_processing_status.sql");
		return  scripts;
	}

	@Test
	public void getReturnsCorrectStatus() {
		SmsApiProcessingStatus status = statusRepository.get("123cbcd4-0851-404a-a8b2-000b02f7b85e");
		assertEquals((Long)3L, status.getId());
		assertEquals("CHILD REMOVED", status.getEventType());
		assertEquals("QUEUED", status.getRequestStatus());
		assertEquals("SENT", status.getSmsDeliveryStatus());
	}

	@Test
	public void getAllReturnsAllEntries() {
		assertEquals(3, statusRepository.getAll().size());
	}

	@Test
	public void addShouldAddNewStatusEntry() {
		SmsApiProcessingStatus newStatus = new SmsApiProcessingStatus();
		newStatus.setBaseEntityId("0851-304cbcd4-000b02f7b85e");
		newStatus.setEventType("CHILD REGISTRATION");
		newStatus.setServiceType("REGISTRATION");
		newStatus.setRequestStatus("NEW");
		newStatus.setDateCreated(new Date());
		newStatus.setLastUpdated(new Date());
		newStatus.setSmsDeliveryStatus("QUEUED");
		newStatus.setAttempts(0);
		newStatus.setSmsDeliveryDate(null);

		statusRepository.add(newStatus);
		assertEquals(4, statusRepository.getAll().size());
	}

	@Test
	public void updateShouldUpdateStatus() {
		SmsApiProcessingStatus status = statusRepository.get("304cbcd4-0850-404a-a8b1-486b02f7b84d");
		assertEquals("NEW", status.getRequestStatus());
		assertEquals("QUEUED", status.getSmsDeliveryStatus());
		status.setRequestStatus("QUEUED");
		status.setSmsDeliveryStatus("SENT");
		statusRepository.update(status);

		SmsApiProcessingStatus updatedStatus = statusRepository.get("304cbcd4-0850-404a-a8b1-486b02f7b84d");
		assertEquals("QUEUED", updatedStatus.getRequestStatus());
		assertEquals("SENT", updatedStatus.getSmsDeliveryStatus());

	}

	@Test
	public void canGetSmsApiProcessingStatusById() {
		SmsApiProcessingStatus status = statusRepository.getSmsApiProcessingStatusById(1L);
		assertEquals((Long)1L, status.getId());
		assertEquals("CHILD REGISTRATION", status.getEventType());

		SmsApiProcessingStatus status2 = statusRepository.getSmsApiProcessingStatusById(2L);
		assertEquals("CHILD HOME VISIT", status2.getEventType());
	}

	@Test
	public void canGetStatusListByRequestStatus() {
		List<SmsApiProcessingStatus> statusList = statusRepository.getStatusListByRequestStatus("NEW");
		assertEquals(2, statusList.size());
		assertEquals("304cbcd4-0850-404a-a8b1-486b02f7b84d", statusList.get(0).getBaseEntityId());
		assertEquals("CHILD REGISTRATION", statusList.get(0).getEventType());
		assertEquals("000b02f7b85e-404a-404a-a8b1-123cbcd4", statusList.get(1).getBaseEntityId());
		assertEquals("CHILD HOME VISIT", statusList.get(1).getEventType());
	}

	@Test
	public void canGetStatusListBySmsDeliveryStatus() {
		List<SmsApiProcessingStatus> statusList = statusRepository.getStatusListBySmsDeliveryStatus("QUEUED");
		assertEquals(2, statusList.size());
		assertEquals("304cbcd4-0850-404a-a8b1-486b02f7b84d", statusList.get(0).getBaseEntityId());
		assertEquals("CHILD REGISTRATION", statusList.get(0).getEventType());
		assertEquals("000b02f7b85e-404a-404a-a8b1-123cbcd4", statusList.get(1).getBaseEntityId());
		assertEquals("CHILD HOME VISIT", statusList.get(1).getEventType());

		List<SmsApiProcessingStatus> statusListSent = statusRepository.getStatusListBySmsDeliveryStatus("SENT");
		assertEquals(1, statusListSent.size());
		assertEquals("123cbcd4-0851-404a-a8b2-000b02f7b85e", statusListSent.get(0).getBaseEntityId());
	}

	@Test
	public void canGetStatusListBySmsDeliveryStatusAndAttempts() {
		List<SmsApiProcessingStatus> notAttemptedList = statusRepository.getStatusListBySmsDeliveryStatusAndAttempts("QUEUED", 0);
		assertEquals(2, notAttemptedList.size());
		List<SmsApiProcessingStatus> attemptedList = statusRepository.getStatusListBySmsDeliveryStatusAndAttempts("SENT", 1);
		assertEquals(1, attemptedList.size());
		assertEquals((Long)3L, attemptedList.get(0).getId());
	}

	@Test
	public void canSafeRemoveStatus() {
		SmsApiProcessingStatus statusToRemove = statusRepository.get("304cbcd4-0850-404a-a8b1-486b02f7b84d");
		assertNotNull(statusToRemove);
		statusRepository.safeRemove(statusToRemove);
		assertNull(statusRepository.get("304cbcd4-0850-404a-a8b1-486b02f7b84d"));
	}
}