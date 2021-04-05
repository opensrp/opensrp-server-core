package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.postgres.SmsApiProcessingStatus;
import org.opensrp.repository.SmsApiProcessingStatusRepository;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class SmsApiProcessingStatusServiceTest {

	private SmsApiProcessingStatusService statusService;

	private SmsApiProcessingStatusRepository statusRepository;

	@Before
	public void setUp() {
		statusRepository = mock(SmsApiProcessingStatusRepository.class);
		statusService = new SmsApiProcessingStatusService(statusRepository);
	}

	@Test
	public void getSmsProcessingStatusByIdReturnsStatus() {
		Long testId = 1L;
		SmsApiProcessingStatus expectedStatus = getTestSmsApiProcessingStatus();
		when(statusRepository.getSmsProcessingStatusById(testId)).thenReturn(expectedStatus);
		SmsApiProcessingStatus returnedStatus = statusService.getSmsProcessingStatusById(testId);
		verify(statusRepository).getSmsProcessingStatusById(testId);
		assertEquals(testId, returnedStatus.getId());
	}

	@Test
	public void getStatusListByRequestStatusReturnsList() {
		List<SmsApiProcessingStatus> expectedStatusList = new ArrayList<>();
		String requestStatus = "NEW";
		expectedStatusList.add(getTestSmsApiProcessingStatus());
		expectedStatusList.add(getTestSmsApiProcessingStatus(2L, "test-2-1234"));
		when(statusRepository.getStatusListByRequestStatus(requestStatus)).thenReturn(expectedStatusList);
		List<SmsApiProcessingStatus> returnedList = statusService.getStatusListByRequestStatus(requestStatus);
		verify(statusRepository).getStatusListByRequestStatus(requestStatus);
		assertEquals("test-123", returnedList.get(0).getBaseEntityId());
		assertEquals(requestStatus, returnedList.get(0).getRequestStatus());
		assertEquals("test-2-1234", returnedList.get(1).getBaseEntityId());
		assertEquals(requestStatus, returnedList.get(1).getRequestStatus());
	}


	@Test
	public void getStatusListBySmsDeliveryStatusReturnsList() {
		List<SmsApiProcessingStatus> expectedStatusList = new ArrayList<>();
		String smsDeliveryStatus = "QUEUED";
		expectedStatusList.add(getTestSmsApiProcessingStatus());
		expectedStatusList.add(getTestSmsApiProcessingStatus(2L, "test-2-1234"));
		when(statusRepository.getStatusListBySmsDeliveryStatus(smsDeliveryStatus)).thenReturn(expectedStatusList);
		List<SmsApiProcessingStatus> returnedList = statusService.getStatusListBySmsDeliveryStatus(smsDeliveryStatus);
		verify(statusRepository).getStatusListBySmsDeliveryStatus(smsDeliveryStatus);
		assertEquals("test-123", returnedList.get(0).getBaseEntityId());
		assertEquals(smsDeliveryStatus, returnedList.get(0).getSmsDeliveryStatus());
		assertEquals("test-2-1234", returnedList.get(1).getBaseEntityId());
		assertEquals(smsDeliveryStatus, returnedList.get(1).getSmsDeliveryStatus());
	}

	@Test
	public void getStatusListBySmsDeliveryStatusAndAttemptsReturnsList() {
		List<SmsApiProcessingStatus> expectedStatusList = new ArrayList<>();
		String smsDeliveryStatus = "QUEUED";
		expectedStatusList.add(getTestSmsApiProcessingStatus());
		expectedStatusList.add(getTestSmsApiProcessingStatus(2L, "test-2-1234"));
		when(statusRepository.getStatusListBySmsDeliveryStatusAndAttempts(smsDeliveryStatus, 0)).thenReturn(expectedStatusList);
		List<SmsApiProcessingStatus> returnedList = statusService.getStatusListBySmsDeliveryStatusAndAttempts(smsDeliveryStatus, 0);
		verify(statusRepository).getStatusListBySmsDeliveryStatusAndAttempts(smsDeliveryStatus, 0);
		assertEquals("test-123", returnedList.get(0).getBaseEntityId());
		assertEquals(smsDeliveryStatus, returnedList.get(0).getSmsDeliveryStatus());
		assertEquals("test-2-1234", returnedList.get(1).getBaseEntityId());
		assertEquals(smsDeliveryStatus, returnedList.get(1).getSmsDeliveryStatus());
	}

	@Test
	public void getStatusByEntityIdReturnsStatus() {
		SmsApiProcessingStatus expectedStatus = getTestSmsApiProcessingStatus();
		String entityId = "test-123";
		when(statusRepository.get(entityId)).thenReturn(expectedStatus);
		SmsApiProcessingStatus returnedStatus = statusService.getStatusByEntityId(entityId);
		verify(statusRepository).get(entityId);
		assertEquals((Long)1L, returnedStatus.getId());
		assertEquals(entityId, returnedStatus.getBaseEntityId());
	}

	@Test
	public void getAllStatusEntriessReturnsList() {
		List<SmsApiProcessingStatus> expectedStatusList = new ArrayList<>();
		expectedStatusList.add(getTestSmsApiProcessingStatus());
		expectedStatusList.add(getTestSmsApiProcessingStatus(2L, "test-2-1234"));
		String requestStatus = "NEW";
		when(statusRepository.getAll()).thenReturn(expectedStatusList);
		List<SmsApiProcessingStatus> returnedList = statusService.getAllStatusEntries();
		verify(statusRepository).getAll();
		assertEquals((Long)1L, returnedList.get(0).getId());
		assertEquals(requestStatus, returnedList.get(0).getRequestStatus());
		assertEquals((Long)2L, returnedList.get(1).getId());
		assertEquals(requestStatus, returnedList.get(1).getRequestStatus());
	}

	@Test
	public void addOrUpdateCallsRepositoryAddOrUpdateMethod() {
		SmsApiProcessingStatus testStatus = getTestSmsApiProcessingStatus();
		statusService.addOrUpdateSmsApiProcessingEntry(testStatus);
		verify(statusRepository).add(testStatus);
	}

	@Test
	public void deleteCallsRepositorySafeRemoveMethod() {
		SmsApiProcessingStatus testStatus = getTestSmsApiProcessingStatus();
		statusService.deleteSmsApiProcessingEntry(testStatus);
		verify(statusRepository).safeRemove(testStatus);
	}


	private static SmsApiProcessingStatus getTestSmsApiProcessingStatus() {
		return getTestSmsApiProcessingStatus(null, null);
	}


	private static SmsApiProcessingStatus getTestSmsApiProcessingStatus(Long id, String entityId) {
		SmsApiProcessingStatus status = new SmsApiProcessingStatus();
		status.setId(id != null ? id : 1L);
		status.setBaseEntityId(entityId != null ? entityId : "test-123");
		status.setEventType("CHILD REGISTRATION");
		status.setDateCreated(new Date());
		status.setLastUpdated(new Date());
		status.setRequestStatus("NEW");
		status.setSmsDeliveryStatus("QUEUED");
		status.setAttempts(0);
		status.setSmsDeliveryDate(null);
		return status;
	}
}
