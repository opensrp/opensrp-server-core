package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.opensrp.service.OpenmrsIDService.CHILD_REGISTER_CARD_NUMBER;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Set;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.domain.Address;
import org.smartregister.domain.Client;
import org.opensrp.domain.UniqueId;
import org.opensrp.repository.UniqueIdRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class OpenmrsIDServiceTest extends BaseRepositoryTest {

	@Autowired
	OpenmrsIDService openmrsIDService;

	@Autowired
	UniqueIdRepository uniqueIdRepository;

	private Set<String> scripts = new HashSet<String>();

	@Override
	protected Set<String> getDatabaseScripts() {
		scripts.add("unique_ids.sql");
		return scripts;
	}
	
	public Client createClient(String baseEntityId, String firstName, String lastName, String gender,
	        String childRegisterCardNumber) {
		DateTime dateOfBirth = new DateTime();
		Map<String, String> addressFields = new HashMap<>();
		addressFields.put("address4", "birthFacilityName");
		addressFields.put("address3", "resolvedResidentialAddress");
		addressFields.put("address2", "residentialAddress");
		addressFields.put("address1", "physicalLandmark");
		
		Address address = new Address("usual_residence", new DateTime(), new DateTime(), addressFields, null, null, null,
		        "homeFacility", null);
		ArrayList<Address> addressList = new ArrayList<Address>();
		addressList.add(address);
		
		Client client = new Client(baseEntityId, firstName, "", lastName, dateOfBirth, null, false, false, gender,
		        addressList, null, null);
		client.addAttribute(CHILD_REGISTER_CARD_NUMBER, childRegisterCardNumber);
		return client;
	}
	
	@Test
	public void testAssignOpenmrsIdToClient() throws SQLException {
		Client client = this.createClient("12345", "First", "Last", "Male", "454/16");
		
		openmrsIDService.assignOpenmrsIdToClient("12345-1", client);
		assertNotNull(client.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));
	}
	
	@Test
	public void testExistingClientsDoNotReceiveNewOpenmrsId() throws Exception {
		Client client = this.createClient("45678", "Jane", "Doe", "Female", "102/17");
		Client duplicateClient = this.createClient("45677", "Jane", "Doe", "Female", "102/17");
		
		openmrsIDService.assignOpenmrsIdToClient("12345-1", client);
		assertNotNull(client.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));
		
		openmrsIDService.assignOpenmrsIdToClient("12345-1", duplicateClient);
		assertTrue(openmrsIDService.checkIfClientExists(duplicateClient));
		assertNull(duplicateClient.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));
	}
	
	@Test
	public void testCheckClient() throws SQLException {
		Client client = this.createClient("45678", "Jane", "Doe", "Female", "102/17");
		openmrsIDService.assignOpenmrsIdToClient("12345-1", client);
		assertTrue(openmrsIDService.checkIfClientExists(client));
	}
	
	@Test
	public void testCheckClientWithFalseData() throws SQLException {
		Client client = this.createClient("45678", "Jane", "Doe", "Female", "102/17");
		assertFalse(openmrsIDService.checkIfClientExists(client));
	}
	
	@Test
	public void testCheckClientWithInvalidData() throws SQLException {
		Client client = this.createClient("*", "Jane", "Doe", "Female", "*");
		assertNull(openmrsIDService.checkIfClientExists(null));
	}
	
	@Test
	public void testDownloadAndSaveIds() {
		List<String> downloadedIds = new ArrayList<>();
		downloadedIds.add("1");
		downloadedIds.add("2");
		OpenmrsIDService openmrsIDServiceSpy = Mockito.spy(openmrsIDService);
		Mockito.doReturn(downloadedIds).when(openmrsIDServiceSpy).downloadOpenmrsIds(anyLong());
		
		openmrsIDServiceSpy.downloadAndSaveIds(2, "test");

		List<UniqueId> uniqueIds = uniqueIdRepository.getNotUsedIds(2);
		List<String> actualList = new ArrayList<>();
		for (UniqueId uniqueId : uniqueIds) {
			assertEquals("test", uniqueId.getUsedBy());
			actualList.add(uniqueId.getOpenmrsId());
		}
		
		assertEquals(2, (long) uniqueIdRepository.totalUnUsedIds());
		assertEquals(downloadedIds, actualList);
	}
	
	@Test
	public void testClearRecord() throws SQLException {
		Client client = this.createClient("12345", "First", "Last", "Male", "454/16");
		openmrsIDService.assignOpenmrsIdToClient("12345-1", client);
		
		assertTrue(openmrsIDService.checkIfClientExists(client));
		
		openmrsIDService.clearRecords();
		
		assertFalse(openmrsIDService.checkIfClientExists(client));
	}
	
	@Test
	public void testGetNotUsedId() throws Exception {
		int size = 10;
		List<UniqueId> expectedList = createNotUsedUniqIdEntries(size);
		List<UniqueId> actualList = openmrsIDService.getNotUsedIds(100);
		assertEquals(size, actualList.size());
		
		for (int i = 0; i < size; i++) {
			UniqueId expected = expectedList.get(i);
			UniqueId actual = actualList.get(i);
			assertUniqueId(expected, actual);
		}
		
	}
	
	@Test
	public void testGetNotUsedIdAsString() throws Exception {
		int size = 10;
		List<UniqueId> ids = createNotUsedUniqIdEntries(size);
		List<String> expectedList = new ArrayList<>();
		List<String> actualList = openmrsIDService.getNotUsedIdsAsString(100);
		for (int i = 0; i < size; i++) {
			expectedList.add(ids.get(i).getOpenmrsId());
		}
		
		assertEquals(size, actualList.size());
		assertEquals(expectedList, actualList);
	}
	
	@Test
	public void testMarkIdAsUsed() throws Exception {
		int size = 10;
		List<UniqueId> ids = createNotUsedUniqIdEntries(size);
		List<String> idListAsString = new ArrayList<>();
		
		for (int i = 0; i < size; i++) {
			idListAsString.add(ids.get(i).getOpenmrsId());
		}
		
		Long[] actualIds = openmrsIDService.markIdsAsUsed(idListAsString);
		List<String> actualList = openmrsIDService.getNotUsedIdsAsString(100);
		
		assertEquals(size, actualIds.length);
		assertEquals(0, actualList.size());
		
	}
	
	private List<UniqueId> createNotUsedUniqIdEntries(int size) throws Exception {
		List<UniqueId> notUsedUniqueIds = new ArrayList<>();
		long id = 6;
		for (int i = 0; i < size; i++) {
			UniqueId uniqueId = new UniqueId();
			uniqueId.setId(Long.valueOf(id));
			uniqueId.setOpenmrsId(String.valueOf(i));
			uniqueId.setStatus(UniqueId.STATUS_NOT_USED);
			uniqueId.setUsedBy("test" + i);
			uniqueId.setCreatedAt(new Date());
			uniqueId.setLocation("test");
			notUsedUniqueIds.add(uniqueId);
			uniqueIdRepository.add(uniqueId);
			id++;
		}
		return notUsedUniqueIds;
	}
	
	private void assertUniqueId(UniqueId expected, UniqueId actual) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-mm-hh hh:MM");
		assertEquals(dateFormat.format(expected.getCreatedAt()), dateFormat.format(actual.getCreatedAt()));
		assertEquals(expected.getLocation(), actual.getLocation());
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getOpenmrsId(), actual.getOpenmrsId());
		assertEquals(expected.getStatus(), actual.getStatus());
		assertEquals(expected.getUsedBy(), actual.getUsedBy());
	}
}
