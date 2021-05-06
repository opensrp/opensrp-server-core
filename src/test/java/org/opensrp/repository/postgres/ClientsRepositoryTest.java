package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.AllConstants.BaseEntity.BASE_ENTITY_ID;
import static org.opensrp.common.AllConstants.Client.OPENMRS_UUID_IDENTIFIER_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensrp.domain.postgres.HouseholdClient;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;
import org.smartregister.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.ibm.fhir.model.resource.Patient;

public class ClientsRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("clientsRepositoryPostgres")
	private ClientsRepository clientsRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("client.sql");
		scripts.add("event.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		Client client = clientsRepository.get("05934ae338431f28bf6793b2416946b7");
		assertEquals("469597f0-eefe-4171-afef-f7234cbb2859", client.getBaseEntityId());
		assertEquals("eb4b258c-7558-436c-a1fe-e91d9e12f849", client.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		assertEquals("January", client.getFirstName().trim());
		assertEquals("Babysix", client.getLastName());
		assertEquals("05934ae338431f28bf6793b2416946b7", client.getId());
		//missing client
		assertNull(clientsRepository.get("05934ae338bf6793b2416946b7"));
		
		//test deleted client
		clientsRepository.safeRemove(clientsRepository.get("05934ae338431f28bf6793b2416946b7"));
		assertNull(clientsRepository.get("05934ae338431f28bf6793b2416946b7"));
	}
	
	@Test
	public void testAdd() {
		Client client = new Client("f67823b0-378e-4a35-93fc-bb00def74e2f").withBirthdate(new DateTime("2017-03-31"), true)
		        .withGender("Male").withFirstName("xobili").withLastName("mbangwa");
		
		client.withIdentifier("ZEIR_ID", "233864-8").withAttribute("Home_Facility", "Linda");
		clientsRepository.add(client);
		assertEquals(24, clientsRepository.getAll().size());
		
		Client savedClient = clientsRepository.findByBaseEntityId("f67823b0-378e-4a35-93fc-bb00def74e2f");
		assertNotNull(savedClient.getId());
		assertEquals(new DateTime("2017-03-31"), client.getBirthdate());
		assertEquals("xobili", client.getFirstName());
		assertEquals("mbangwa", client.getLastName());
		assertEquals("233864-8", client.getIdentifier("ZEIR_ID"));
		
		//test if a client with voided date add client as deleted
		client = new Client("f67823b0-4a35-93fc-bb00def74e2f").withBirthdate(new DateTime("2017-03-31"), true)
		        .withGender("Male").withFirstName("Test").withLastName("Deleted");
		client.withDateVoided(new DateTime());
		clientsRepository.add(client);
		assertNull(clientsRepository.findByBaseEntityId(client.getBaseEntityId()));
		MatcherAssert.assertThat(savedClient.getServerVersion(), Matchers.greaterThan(5l));
		
	}
	
	@Test
	public void testUpdate() {
		Client client = clientsRepository.get("05934ae338431f28bf6793b2416946b7");
		client.setFirstName("Hummel");
		client.setLastName("Basialis");
		client.withIdentifier("ZEIR_ID", "09876-98");
		long serverVersion=client.getServerVersion();
		clientsRepository.update(client);
		
		Client updatedClient = clientsRepository.get(client.getId());
		assertEquals("Hummel", updatedClient.getFirstName());
		assertEquals("Basialis", updatedClient.getLastName());
		assertEquals("09876-98", client.getIdentifier("ZEIR_ID"));
		MatcherAssert.assertThat(updatedClient.getServerVersion(), Matchers.greaterThan(serverVersion));
		
		//test update with voided date deletes client
		updatedClient.setDateVoided(new DateTime());
		clientsRepository.update(updatedClient);
		assertNull(clientsRepository.get(updatedClient.getId()));
	}
	
	@Test
	public void testGetAll() {
		assertEquals(23, clientsRepository.getAll().size());
		clientsRepository.safeRemove(clientsRepository.get("05934ae338431f28bf6793b24164cbd9"));
		
		List<Client> clients = clientsRepository.getAll();
		
		//test deleted clients
		assertEquals(22, clients.size());
		for (Client client : clients)
			assertNotEquals("05934ae338431f28bf6793b24164cbd9", client.getId());
		
		assertNull(clientsRepository.get("05934ae338431f28bf6793b24164cbd9"));
		
	}
	
	@Test
	public void testSafeRemove() {
		Client client = clientsRepository.get("05934ae338431f28bf6793b2416946b7");
		clientsRepository.safeRemove(client);
		List<Client> clients = clientsRepository.getAll();
		assertEquals(22, clients.size());
		
		for (Client cl : clients)
			assertNotEquals("05934ae338431f28bf6793b2416946b7", cl.getId());
		
		assertNull(clientsRepository.get("05934ae338431f28bf6793b2416946b7"));
	}
	
	@Test
	public void testFindByBaseEntityId() {
		Client client = clientsRepository.findByBaseEntityId("86c039a2-0b68-4166-849e-f49897e3a510");
		assertEquals("05934ae338431f28bf6793b24164cbd9", client.getId());
		assertEquals("ab91df5d-e433-40f3-b44f-427b73c9ae2a", client.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		assertEquals("Sally", client.getFirstName());
		assertEquals("Mtini", client.getLastName().trim());
		
		assertNull(clientsRepository.findByBaseEntityId("f67823b0-378e-4a35-93fc-bb00def74e2f"));
		
		//test deleted client
		clientsRepository.safeRemove(client);
		assertNull(clientsRepository.findByBaseEntityId("86c039a2-0b68-4166-849e-f49897e3a510"));
	}
	
	@Test
	public void testFindAllClients() {
		assertEquals(23, clientsRepository.findAllClients().size());
		
		clientsRepository.safeRemove(clientsRepository.get("05934ae338431f28bf6793b24164cbd9"));
		
		List<Client> clients = clientsRepository.findAllClients();
		
		assertEquals(22, clients.size());
		for (Client client : clients)
			assertNotEquals("05934ae338431f28bf6793b24164cbd9", client.getId());
		
		//test deleted client	
		assertNull(clientsRepository.get("05934ae338431f28bf6793b24164cbd9"));
	}
	
	@Test
	public void testFindAllByIdentifier() {
		assertTrue(clientsRepository.findAllByIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE, "ab91df5d-e433-40f3-b44f-427b73ca")
		        .isEmpty());
		
		assertTrue(clientsRepository.findAllByIdentifier("identifier_type", "ab91df5d-e433-40f3-b44f-427b73c9ae2a")
		        .isEmpty());
		
		List<Client> clients = clientsRepository.findAllByIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE,
		    "ab91df5d-e433-40f3-b44f-427b73c9ae2a");
		
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		assertEquals("Sally", clients.get(0).getFirstName());
		assertEquals("Mtini", clients.get(0).getLastName().trim());
		
		//test deleted clients
		clientsRepository.safeRemove(clients.get(0));
		assertTrue(clientsRepository.findAllByIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE,
		    "ab91df5d-e433-40f3-b44f-427b73c9ae2a").isEmpty());
		
	}
	
	@Test
	public void testAllByAttribute() {
		List<Client> clients = clientsRepository.findAllByAttribute("Home_Facility", "Happy Kids Clinic");
		
		assertEquals(9, clients.size());
		
		clients = clientsRepository.findAllByAttribute("CHW_Phone_Number", "0964357951");
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		assertEquals("Sally", clients.get(0).getFirstName());
		assertEquals("Mtini", clients.get(0).getLastName().trim());
		
		assertTrue(clientsRepository.findAllByAttribute("CHW_Phone_Number", "+0964357951").isEmpty());
		
		assertTrue(clientsRepository.findAllByAttribute("Phone_Number", "0964357951").isEmpty());
		
		//test deleted clients
		clientsRepository.safeRemove(clients.get(0));
		assertTrue(clientsRepository.findAllByAttribute("CHW_Phone_Number", "0964357951").isEmpty());
	}

	@Test
	public void testAllByAttributes() {
		List<Client> clients = clientsRepository.findAllByAttribute("Home_Facility", "Happy Kids Clinic");

		assertEquals(9, clients.size());

		clients = clientsRepository.findAllByAttribute("CHW_Phone_Number", "0964357951");
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		assertEquals("Sally", clients.get(0).getFirstName());
		assertEquals("Mtini", clients.get(0).getLastName().trim());

		assertEquals(clientsRepository.findAllByAttributes("CHW_Phone_Number", new ArrayList<>(Arrays.asList("+0964357951", "0964357951"))).size(), 1);

		//test deleted clients
		clientsRepository.safeRemove(clients.get(0));
		assertTrue(clientsRepository.findAllByAttributes("Phone_Number", new ArrayList<>(Arrays.asList("+0964357951", "0964357951"))).isEmpty());
	}
	
	@Test
	public void testFindAllByMatchingName() {
		assertEquals(9, clientsRepository.findAllByMatchingName("b").size());
		
		List<Client> clients = clientsRepository.findAllByMatchingName("babytwo");
		
		assertEquals(1, clients.size());
		
		assertEquals("05934ae338431f28bf6793b24167b6d1", clients.get(0).getId());
		assertEquals("fe7b6350-16d2-41d0-8574-c194088705df", clients.get(0).getBaseEntityId());
		assertEquals("218227-7", clients.get(0).getIdentifier("ZEIR_ID"));
		assertEquals("ba5d3927-414f-4796-ae1e-9b73b50a5573", clients.get(0).getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		
		assertEquals(12, clientsRepository.findAllByMatchingName("a").size());
		
		assertEquals(6, clientsRepository.findAllByMatchingName("Ja").size());
		
		assertEquals(4, clientsRepository.findAllByMatchingName("Janu").size());
		
		assertEquals(2, clientsRepository.findAllByMatchingName("January").size());
		
		assertTrue(clientsRepository.findAllByMatchingName("Kimbley").isEmpty());
		
		//test deleted clients
		clientsRepository.safeRemove(clientsRepository.findAllByMatchingName("babytwo").get(0));
		assertTrue(clientsRepository.findAllByMatchingName("babytwo").isEmpty());
	}
	
	@Test
	public void testFindByRelationshipIdAndDateCreated() {
		assertEquals(
		    2,
		    clientsRepository.findByRelationshipIdAndDateCreated("0154839f-8766-4eda-b729-89067c7a8c5d",
		        new DateTime("2018-03-13").toString(), new DateTime().toString()).size());
		
		assertTrue(clientsRepository.findByRelationshipIdAndDateCreated("0154839f-8766-4eda-89067c7a8c5d",
		    new DateTime("2018-03-14").toString(), new DateTime().toString()).isEmpty());
		
		assertTrue(clientsRepository.findByRelationshipIdAndDateCreated("0154839f-8766-4eda-b729-89067c7a8c5d",
		    new DateTime("2018-03-14").toString(), new DateTime().toString()).isEmpty());
		
		Client client = clientsRepository.findByRelationshipIdAndDateCreated("3abdb25a-f151-4a95-9311-bd30bf935085",
		    new DateTime("2018-03-13").toString(), new DateTime().toString()).get(0);
		assertEquals("05934ae338431f28bf6793b2415a0374", client.getId());
		assertEquals("94f3e8fb-2f05-4fca-8119-2b593d1962eb", client.getBaseEntityId());
		assertEquals("2018-03-01", client.getBirthdate().toLocalDate().toString());
		
		//test deleted clients
		clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.findByRelationshipIdAndDateCreated("3abdb25a-f151-4a95-9311-bd30bf935085",
		    new DateTime("2018-03-13").toString(), new DateTime().toString()).isEmpty());
	}
	
	@Test
	public void testFindByRelationshipId() {
		assertEquals(2, clientsRepository.findByRelationshipId("mother", "0154839f-8766-4eda-b729-89067c7a8c5d").size());
		
		Client client = clientsRepository.findByRelationshipId("mother", "3abdb25a-f151-4a95-9311-bd30bf935085").get(0);
		assertEquals("05934ae338431f28bf6793b2415a0374", client.getId());
		assertEquals("94f3e8fb-2f05-4fca-8119-2b593d1962eb", client.getBaseEntityId());
		assertEquals("cf58894b-71c6-41e0-a977-7283f2411cd5", client.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		assertEquals("2018-03-01", client.getBirthdate().toLocalDate().toString());
		
		assertTrue(clientsRepository.findByRelationshipId("father", "0154839f-8766-4eda-b729-89067c7a8c5d").isEmpty());
		
		assertTrue(clientsRepository.findByRelationshipId("mother", "0154839f-4eda-b729-89067c7a8c5d").isEmpty());
		
		//test deleted clients
		clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.findByRelationshipId("mother", "3abdb25a-f151-4a95-9311-bd30bf935085").isEmpty());
	}
	
	@Test
	public void testFindByCriteria() {
		ClientSearchBean searchBean = new ClientSearchBean();
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		assertEquals(23, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setNameLike("Janu");
		assertEquals(4, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setNameLike("Baby");
		searchBean.setGender("Male");
		assertEquals(2, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setBirthdateFrom(new DateTime("2016-04-13"));
		searchBean.setBirthdateTo(new DateTime());
		assertEquals(2, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setDeathdateFrom(new DateTime("2018-01-01"));
		searchBean.setDeathdateTo(new DateTime());
		assertEquals(1, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean = new ClientSearchBean();
		searchBean.setAttributeType("Home_Facility");
		searchBean.setAttributeValue("Happy Kids Clinic");
		assertEquals(9, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		searchBean.setAttributeType("CHW_Name");
		searchBean.setAttributeValue("Hellen");
		List<Client> clients = clientsRepository.findByCriteria(searchBean, addressSearchBean);
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		
		searchBean = new ClientSearchBean();
		searchBean.setLastEditFrom(new DateTime("2018-03-13T12:57:05.652"));
		searchBean.setLastEditTo(new DateTime());

		assertEquals(6, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		addressSearchBean.setAddressType("usual_residence");
		assertEquals(6, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		
		addressSearchBean.setAddressType("usual_residence");
		assertEquals(6, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());
		Client testClient = new Client("f67823b0-378e-4a35-93fc-bb00def74e2f").withBirthdate(new DateTime("2017-03-31"), true)
				.withGender("Male").withFirstName("xobili").withLastName("mbangwa");
		testClient.setLocationId("123");
		clientsRepository.add(testClient);
		List<String> locationIds = new ArrayList<>();
		locationIds.add("123");
		searchBean.setLocations(locationIds);
		searchBean = new ClientSearchBean();
		searchBean.setLocations(locationIds);
		addressSearchBean = new AddressSearchBean();
		assertEquals(1, clientsRepository.findByCriteria(searchBean, addressSearchBean).size());

		//test deleted clients
		for (Client client : clientsRepository.findByCriteria(searchBean, addressSearchBean))
			clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.findByCriteria(searchBean, addressSearchBean).isEmpty());
		
	}
	
	@Test
	public void testFindByCriteriaWithoutAddressBean() {
		ClientSearchBean searchBean = new ClientSearchBean();
		assertEquals(23, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setNameLike("Janu");
		assertEquals(4, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setNameLike("Baby");
		searchBean.setGender("Male");
		assertEquals(2, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setBirthdateFrom(new DateTime("2016-04-13"));
		searchBean.setBirthdateTo(new DateTime());
		assertEquals(2, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setDeathdateFrom(new DateTime("2018-01-01"));
		searchBean.setDeathdateTo(new DateTime());
		assertEquals(1, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean = new ClientSearchBean();
		searchBean.setAttributeType("Home_Facility");
		searchBean.setAttributeValue("Happy Kids Clinic");
		assertEquals(9, clientsRepository.findByCriteria(searchBean).size());
		
		searchBean.setAttributeType("CHW_Name");
		searchBean.setAttributeValue("Hellen");
		List<Client> clients = clientsRepository.findByCriteria(searchBean);
		assertEquals(1, clients.size());
		assertEquals("05934ae338431f28bf6793b24164cbd9", clients.get(0).getId());
		
		searchBean = new ClientSearchBean();
		searchBean.setLastEditFrom(new DateTime("2018-03-13T12:57:05.652"));
		searchBean.setLastEditTo(new DateTime());
		assertEquals(6, clientsRepository.findByCriteria(searchBean).size());
		
		//test deleted clients
		for (Client client : clientsRepository.findByCriteria(searchBean))
			clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.findByCriteria(searchBean).isEmpty());
		
	}
	
	@Test
	public void testFindByCriteriaWithEditDateParams() {
		
		DateTime from = new DateTime("2018-03-13T12:57:05.652");
		DateTime to = new DateTime();
		List<Client> clients = clientsRepository.findByCriteria(new AddressSearchBean(), from, to);
		assertEquals(6, clients.size());
		
		for (Client client : clients)
			assertTrue(client.getDateEdited().isEqual(from) || client.getDateEdited().isAfter(from));
		
		to = from;
		from = new DateTime("2018-01-01");
		clients = clientsRepository.findByCriteria(new AddressSearchBean(), from, to);
		assertEquals(11, clients.size());
		
		for (Client client : clients) {
			assertTrue(client.getDateEdited().isEqual(from) || client.getDateEdited().isAfter(from));
			assertTrue(client.getDateEdited().isEqual(to) || client.getDateEdited().isBefore(to));
		}
		
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		addressSearchBean.setCityVillage("hui");
		assertTrue(clientsRepository.findByCriteria(addressSearchBean, new DateTime("2018-01-01"), new DateTime()).isEmpty());
		
		//test deleted clients
		for (Client client : clientsRepository.findByCriteria(new AddressSearchBean(), from, to))
			clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.findByCriteria(new AddressSearchBean(), from, to).isEmpty());
	}
	
	@Test
	public void testFindByRelationShip() {
		assertEquals(2, clientsRepository.findByRelationShip("0154839f-8766-4eda-b729-89067c7a8c5d").size());
		
		Client client = clientsRepository.findByRelationShip("3abdb25a-f151-4a95-9311-bd30bf935085").get(0);
		assertEquals("05934ae338431f28bf6793b2415a0374", client.getId());
		assertEquals("94f3e8fb-2f05-4fca-8119-2b593d1962eb", client.getBaseEntityId());
		assertEquals("Fith", client.getFirstName());
		assertEquals("2018-03-01", client.getBirthdate().toLocalDate().toString());
		
		//test deleted clients
		clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.findByRelationShip("3abdb25a-f151-4a95-9311-bd30bf935085").isEmpty());
		
		assertTrue(clientsRepository.findByRelationShip("0154839f-4eda-b729-89067c7a8c5d").isEmpty());
	}
	
	@Test
	public void testFindByEmptyServerVersion() {
		assertTrue(clientsRepository.findByEmptyServerVersion().isEmpty());
		
		Client client = clientsRepository.get("05934ae338431f28bf6793b2415a0374");
		client.setServerVersion(0l);
		clientsRepository.update(client);
		
		assertTrue(clientsRepository.findByEmptyServerVersion().isEmpty());
		
		//test deleted clients
		clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.findByEmptyServerVersion().isEmpty());
		
	}

	@Test
	public void testFindByServerVersion() {
		assertEquals(13, clientsRepository.findByServerVersion(1520935878136l, null).size());
		List<Client> clients = clientsRepository.findByServerVersion(1521003136406l, null);
		List<String> expectedIds = Arrays.asList("05934ae338431f28bf6793b241839005", "05934ae338431f28bf6793b2418380ce",
		    "ade884f8-2685-45fd-93f8-122045b2635e", "2e14b66f-206c-4314-a0f7-c5d2c4d9860f",
		    "b0cb057b-c396-4ec9-bfab-388117a9a5f6", "28ea8f0a-fa53-447d-b8f9-ad07263b382c",
		    "5bd3e1eb-5cd4-4e8d-9180-4293b7ea3b78", "f5934ae338431f28bf6793b24159ce5a", "c778e5a4-5384-4886-b783-65f1abb4c7b0", "5e287780-770e-460b-bc86-8559c73e85d9");
		assertEquals(10, clients.size());
		for (Client client : clients) {
			assertTrue(client.getServerVersion() >= 1521003136406l);
			assertTrue(expectedIds.contains(client.getId()));
		}

		//test deleted clients
		for (Client client : clients)
			clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.findByServerVersion(1521003136406l, null).isEmpty());
	}
	
	@Test
	public void testFindByFieldValue() {
		assertEquals(
		    3,
		    clientsRepository.findByFieldValue(
		        BASE_ENTITY_ID,
		        Arrays.asList(new String[] { "86c039a2-0b68-4166-849e-f49897e3a510", "f33c71c7-a9a4-495d-8028-b6d59e4034b3",
		                "fe7b6350-16d2-41d0-8574-c194088705df" })).size());
		
		assertTrue(clientsRepository.findByFieldValue("Firstname", Arrays.asList(new String[] { "Baby", "Jan" })).isEmpty());
		
		assertTrue(clientsRepository.findByFieldValue(BASE_ENTITY_ID, Arrays.asList(new String[] { "Baby", "Jan" }))
		        .isEmpty());
		
		Client client = clientsRepository.findByFieldValue(BASE_ENTITY_ID,
		    Arrays.asList(new String[] { "f33c71c7-a9a4-495d-8028-b6d59e4034b3" })).get(0);
		
		assertEquals("05934ae338431f28bf6793b241679500", client.getId());
		assertEquals("Jan", client.getFirstName());
		assertEquals("2018-01-01", client.getBirthdate().toLocalDate().toString());
		assertEquals("218226-9", client.getIdentifier("ZEIR_ID"));
		
		//test deleted clients
		clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.findByFieldValue(BASE_ENTITY_ID,
		    Arrays.asList(new String[] { "f33c71c7-a9a4-495d-8028-b6d59e4034b3" })).isEmpty());
	}


	@Test
	public void testNotInOpenMRSByServerVersion() {
		assertTrue(clientsRepository.notInOpenMRSByServerVersion(0l, Calendar.getInstance()).isEmpty());

		Client client = clientsRepository.get("05934ae338431f28bf6793b2415a0374");
		client.removeIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE);
		client.setServerVersion(1l);
		clientsRepository.update(client);

		client = clientsRepository.notInOpenMRSByServerVersion(0l, Calendar.getInstance()).get(0);
		assertEquals("94f3e8fb-2f05-4fca-8119-2b593d1962eb", client.getBaseEntityId());
		assertEquals("Fith", client.getFirstName());
		assertEquals("2018-03-01", client.getBirthdate().toLocalDate().toString());
		assertEquals("218224-4", client.getIdentifier("ZEIR_ID"));

		//test deleted clients
		clientsRepository.safeRemove(client);
		assertTrue(clientsRepository.notInOpenMRSByServerVersion(0l, Calendar.getInstance()).isEmpty());
	}
	
	@Test
	public void shouldFindMembersByRelationshipId() {
		List<Client> expectedClient = clientsRepository.findMembersByRelationshipId("43930c23-c787-4ddb-ab76-770f77e7b17d");
		assertNotNull(expectedClient);
		assertEquals(1, expectedClient.size());
		
	}
	
	@Test
	public void shouldGetMemberCountHouseholdHeadProviderByClients() {
		List<String> id = new ArrayList<String>();
		id.add("43930c23-c787-4ddb-ab76-770f77e7b17d");
		List<HouseholdClient> householdClient = clientsRepository.selectMemberCountHouseholdHeadProviderByClients("", id,
		    "mother");
		
		assertEquals("biddemo", householdClient.get(0).getProviderId());
		assertEquals(1, householdClient.get(0).getMemebrCount());
	}
	
	@Test
	public void shouldFindAllClientsByCriteria() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setClientType("ec_family");
		List<String> locationUuids = new ArrayList<String>();
		locationUuids.add("42abc582-6658-488b-922e-7be500c070f3");
		searchBean.setLocations(locationUuids);
		List<Client> clients = clientsRepository.findAllClientsByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(2, clients.size());
	}
	
	@Test
	public void shouldFindCountAllClientsByCriteria() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setClientType("ec_family");
		List<String> locationUuids = new ArrayList<String>();
		locationUuids.add("42abc582-6658-488b-922e-7be500c070f3");
		searchBean.setLocations(locationUuids);
		HouseholdClient householdClient = clientsRepository.findCountAllClientsByCriteria(searchBean, addressSearchBean);
		assertNotNull(householdClient.getTotalCount());
		assertEquals(2, householdClient.getTotalCount());
	}
	
	@Test
	public void shouldFindHouseholdByCriteria() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setNameLike("Haran Mia");
		List<Client> clients = clientsRepository.findHouseholdByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.size());
	}
	
	@Test
	public void shouldFindHouseholdByLocation() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		List<String> locationUuids = new ArrayList<String>();
		locationUuids.add("e3170b23-bb22-42a4-875d-35c7f32c28ae");
		searchBean.setLocations(locationUuids);
		List<Client> clients = clientsRepository.findHouseholdByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.size());
	}
	
	@Test
	public void shouldCountHouseholdByLocation() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		List<String> locationUuids = new ArrayList<String>();
		locationUuids.add("e3170b23-bb22-42a4-875d-35c7f32c28ae");
		searchBean.setLocations(locationUuids);
		HouseholdClient clients = clientsRepository.findTotalCountHouseholdByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.getTotalCount());
	}
	
	@Test
	public void shouldCountHouseholdByCriteria() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setNameLike("Haran Mia");
		HouseholdClient clients = clientsRepository.findTotalCountHouseholdByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.getTotalCount());
	}
	
	@Test
	public void shouldFindANCByCriteria() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setNameLike("Nobonita");
		searchBean.setClientType(null);
		List<Client> clients = clientsRepository.findANCByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.size());
	}
	
	@Test
	public void shouldFindAllANCByCriteria() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setClientType(null);
		List<Client> clients = clientsRepository.findANCByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.size());
	}
	
	@Test
	public void shouldFindChildByCriteria() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setNameLike("mala");
		searchBean.setClientType(null);
		List<Client> clients = clientsRepository.findChildByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.size());
	}
	
	@Test
	public void shouldFindANCByLocation() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setClientType(null);
		List<String> locations = new ArrayList<>();
		locations.add("ce39f858-a2f6-4676-8db1-81fbb1891b01");
		searchBean.setLocations(locations);
		List<Client> clients = clientsRepository.findChildByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.size());
	}
	
	@Test
	public void shouldFindAllChildByCriteria() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setClientType(null);
		List<Client> clients = clientsRepository.findChildByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.size());
	}
	
	@Test
	public void shouldFindChildByLocation() {
		AddressSearchBean addressSearchBean = new AddressSearchBean();
		ClientSearchBean searchBean = new ClientSearchBean();
		searchBean.setClientType(null);
		List<String> locations = new ArrayList<>();
		locations.add("ce39f858-a2f6-4676-8db1-81fbb1891b01");
		searchBean.setLocations(locations);
		List<Client> clients = clientsRepository.findChildByCriteria(searchBean, addressSearchBean);
		assertNotNull(clients);
		assertEquals(1, clients.size());
	}

	@Ignore
	@Test
	public void testGetAllIdsShouldGetAllClientIds() {
		Pair<List<String>, Long> idsModel = clientsRepository.findAllIds(0, 1000, false);
		List<String> clientIds = idsModel.getLeft();
		assertEquals(21, clientIds.size());
		assertEquals(1573733953502l, idsModel.getRight().longValue());
	}

	@Ignore
	@Test
	public void testGetAllIdsShouldLimitByGiventAmount() {
		Pair<List<String>, Long> idsModel = clientsRepository.findAllIds(0, 1, false);
		List<String> clientIds = idsModel.getLeft();
		assertEquals(1, clientIds.size());
		assertEquals("05934ae338431f28bf6793b24159ce5d", clientIds.get(0));
		assertEquals(1520891339766l, idsModel.getRight().longValue());
	}

	@Ignore
	@Test
	public void testGetAllIdsShouldOrderByServerVersionAsc() {
		Pair<List<String>, Long> idsModel = clientsRepository.findAllIds(0, 3, false);
		List<String> clientIds = idsModel.getLeft();
		assertEquals(3, clientIds.size());
		assertEquals("05934ae338431f28bf6793b24159ce5d", clientIds.get(0));
		assertEquals("05934ae338431f28bf6793b24159dea7", clientIds.get(1));
		assertEquals("05934ae338431f28bf6793b24159ebc2", clientIds.get(2));
		assertEquals(1520891682846l, idsModel.getRight().longValue());
	}

	@Test
	public void testGetAllIdsShouldGetAllArchivedClientIds() {
		Pair<List<String>, Long> idsModel = clientsRepository.findAllIds(0, 1000, true);
		List<String> clientIds = idsModel.getLeft();
		assertEquals(1, clientIds.size());
		assertEquals("5bd3e1eb-5cd4-4e8d-9180", clientIds.get(0));
		assertEquals(1573733955111l, idsModel.getRight().longValue());
	}

	@Test
	public void testGetAllIdsShouldFilterBetweenFromDateAndToDate() {
		String date1 = "2019-09-24T10:00:00+0300";
		String date2 = "2019-10-01T10:00:00+0300";
		Pair<List<String>, Long> idsModel = clientsRepository.findAllIds(0, 10,
				false, new DateTime(date1, DateTimeZone.UTC).toDate(), new DateTime(date2, DateTimeZone.UTC).toDate());
		List<String> clientIds = idsModel.getLeft();
		assertEquals(6, clientIds.size());
	}

	@Test
	public void testGetAllIdsShouldFilterFromDateAsMinimumDate() {
		String date1 = "2019-09-25T10:00:00+0300";

		Pair<List<String>, Long> idsModel = clientsRepository.findAllIds(0, 10,
				false, new DateTime(date1, DateTimeZone.UTC).toDate(), null);
		List<String> clientIds = idsModel.getLeft();
		assertEquals(10, clientIds.size());
	}

	@Test
	public void testGetAllIdsShouldFilterToDateAsMaximumDate() {
		String date1 = "2019-09-24T10:00:00+0300";

		Pair<List<String>, Long> idsModel = clientsRepository.findAllIds(0, 10,
				false, null, new DateTime(date1, DateTimeZone.UTC).toDate());
		List<String> clientIds = idsModel.getLeft();
		assertEquals(0, clientIds.size());
	}


	@Test
	public void testFindByClientTypeAndLocationId() {
		Client client = new Client("f67823b0-378e-4a35-93fc-bb00def74e24").withLocationId("location-1");
		client.setClientType("Client-type-1");
		clientsRepository.add(client);
		List<Client> clients = clientsRepository.findByClientTypeAndLocationId("Client-type-1","location-1");
		assertEquals(1,clients.size());
		assertEquals("f67823b0-378e-4a35-93fc-bb00def74e24", clients.get(0).getBaseEntityId());
	}

	@Test
	public void testFindById() {
		Client client = clientsRepository.findById("05934ae338431f28bf6793b24164cbd9");
		assertEquals("86c039a2-0b68-4166-849e-f49897e3a510", client.getBaseEntityId());
		assertEquals("ab91df5d-e433-40f3-b44f-427b73c9ae2a", client.getIdentifier(OPENMRS_UUID_IDENTIFIER_TYPE));
		assertEquals("Sally", client.getFirstName());
		assertEquals("Mtini", client.getLastName().trim());

	}

	@Test
	public void testFindByServerVersionWithLimit() {
		assertEquals(23, clientsRepository.findByServerVersion(0l, null).size());
		List<Client> clients = clientsRepository.findByServerVersion(1521003136406l, 2);
		List<String> expectedIds = Arrays.asList("05934ae338431f28bf6793b241839005", "05934ae338431f28bf6793b2418380ce");
		assertEquals(2, clients.size());
		for (Client client : clients) {
			assertTrue(client.getServerVersion() >= 1521003136406l);
			assertTrue(expectedIds.contains(client.getId()));
		}
	}

	@Test
	public void testCountAllClientsShouldReturnCorrectValue() {
		Long count = clientsRepository.countAll(0l);
		assertEquals(Long.valueOf(23), count);
	}

	@Test
	public void testFindFamilyMemberBYJurisdictionReturnsNotEmpty(){
		String jurisdictionId = "f9ce9265-88ef-4bdd-ac73-b24ca3653871";
		List<Patient> patients = clientsRepository.findFamilyMemberyByJurisdiction(jurisdictionId);
		assertEquals(1, patients.size());
	}

	@Test
	public void testFindByLocationIdExclusiveOfTypeFiltersByLocationIdAndExcludesTypeFamily(){
		String jurisdictionId = "f9ce9265-88ef-4bdd-ac73-b24ca3653871";
		List<Client> clients = clientsRepository.findByLocationIdExclusiveOfType(jurisdictionId, "Family");
		boolean allHaveLocationIdExcludingFamilyType = clients.stream()
				.allMatch(client -> (client.getClientType() == null || !client.getClientType().equalsIgnoreCase("Family")));
		assertTrue(allHaveLocationIdExcludingFamilyType);
	}

}
