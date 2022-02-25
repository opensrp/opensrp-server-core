package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;
import org.opensrp.service.ClientService;
import org.smartregister.common.Gender;
import org.smartregister.domain.Address;
import org.smartregister.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class AllClientsIntegrationTest extends BaseRepositoryTest{
	//TODO detailed testign
	
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private ClientsRepository ac;

	@Before
	public void setUp() throws Exception {
		tableNames=Collections.singletonList("core.client");
		System.out.println("Removing all data");
		truncateTables();
		System.out.println("Removed");
		initMocks(this);
	}
	
	private void addClients() {
		for (int i = 0; i < 10; i++) {
			Client c = new Client("eid" + i).withName("fn" + i, "mn" + i, "ln" + i).withGender("MALE")
			        .withBirthdate(new DateTime(), false);
			c.withAddress(new Address().withAddressType("usual_residence").withCityVillage("city" + i).withTown("town" + i));
			c.withAttribute("at1", "atval" + i);
			
			clientService.addClient(c);
		}
	}
	
	@Test
	public void shouldMergeSuccessfullyIfClientFound() throws JSONException {//TODO
		Client c = new Client("eid0").withName("fn", "mn", "ln").withGender("MALE").withBirthdate(new DateTime(), false);
		c.withAddress(new Address().withAddressType("usual_residence").withCityVillage("city").withTown("town"));
		c.withAttribute("at1", "atval1");
		
		c = clientService.addClient(c);
		
		Client cu = new Client("eid0").withGender("FEMALE").withBirthdate(new DateTime(), false);
		cu.withAddress(new Address().withAddressType("deathplace").withCityVillage("city1").withTown("town1"));
		cu.withAttribute("at2", "atval2");
		
		clientService.mergeClient(cu);
		
		Client updated=clientService.findClient(cu);
		assertEquals("FEMALE", updated.getGender());
		assertNotNull( updated.getAddress("deathplace"));
		assertEquals("town1", updated.getAddress("deathplace").getTown());
		assertEquals("city1", updated.getAddress("deathplace").getCityVillage());
		assertEquals("atval2", updated.getAttribute("at2"));
	}
	
	@Test
	public void shouldSearchByLastUpdatedDate() throws JSONException {//TODO
		addClients();
		DateTime start = DateTime.now();
		
		addClients();
		
		DateTime end = DateTime.now();
		
		ClientSearchBean clientSearchBean = new ClientSearchBean();
		clientSearchBean.setLastEditFrom(start);
		clientSearchBean.setLastEditTo(end);
		
		List<Client> cll = clientService.findByCriteria(clientSearchBean, null);
		
		assertEquals(10, cll.size());
	}
	
	public static void main(String[] args) {
		System.out.println(new DateTime("2016-01-23").toString("MMMM (yyyy)"));
	}
	
	@Test
	public void shouldSearchFullDataClientsIn10Sec() throws MalformedURLException {
		
		final long start = System.currentTimeMillis();
		
		for (int i = 0; i < 100; i++) {
			addClient(i, false);
		}
		clientService.findAllByIdentifier("1234556" + "786");
		ClientSearchBean clientSearchBean = new ClientSearchBean();
		clientSearchBean.setNameLike("first");
		clientSearchBean.setGender("MALE");
		clientSearchBean.setBirthdateFrom(new DateTime());
		clientSearchBean.setAttributeType("ethnicity");
		clientSearchBean.setAttributeValue("eth3");
		List<Client> clientList = clientService.findByCriteria(clientSearchBean, null);
		assertEquals(7,clientList.size());
		for(Client client : clientList) {
			String filter=clientSearchBean.getNameLike().toLowerCase();
			assertTrue(client.getFirstName().contains(filter)
				|| client.getMiddleName().toLowerCase().contains(filter)
				|| client.getLastName().toLowerCase().contains(filter));
			assertEquals(clientSearchBean.getGender(), client.getGender());
			assertTrue( client.getBirthdate().equals(clientSearchBean.getDeathdateFrom())
				|| client.getBirthdate().isBefore(clientSearchBean.getBirthdateFrom()));
			assertEquals("eth3", client.getAttribute("ethnicity"));
		}
	}
	
	private void addClient(int i, boolean direct) {
		int ageInWeeks = new Random().nextInt(2860);// assuming average age of people is 55 years
		DateTime birthdate = new DateTime().minusWeeks(ageInWeeks);
		DateTime deathdate = i % 7 == 0 ? new DateTime() : null;// every 7th person died today
		Client c = new Client("entityId" + i, "firstName" + i, "middleName" + i, "lastName" + i, birthdate, deathdate, false,
		        false, i % 2 == 0 ? "FEMALE" : "MALE");
		
		Map<String, String> am = new HashMap<>();
		Address ab = new Address("birthplace", null, null, am, null, null, null, "Sindh", "Pakistan");
		ab.setCityVillage("Karachi");
		ab.setTown("Korangi");
		ab.setSubTown("UC" + i % 11);
		c.addAddress(ab);
		
		Address ur = new Address("usual_residence", null, null, am, null, null, "752" + new Random().nextInt(5), "Sindh",
		        "Pakistan");
		ur.setCityVillage("Karachi");
		ur.setTown(i % 3 == 0 ? "Korangi" : "Baldia");
		ur.setSubTown("UC" + i % 11);
		c.addAddress(ur);
		
		c.addAttribute("ethnicity", "eth" + i % 7);
		c.addAttribute("health area", "healtha" + i % 7);
		
		c.addIdentifier("CNIC", "1234556" + i);
		c.addIdentifier("NTN", "564300" + i);
		if (direct) {
			ac.add(c);
		} else {
			clientService.addClient(c);
		}
	}
	
	@Test
	public void shouldGetByDynamicView() {
		addClients();
		
		ClientSearchBean clientSearchBean = new ClientSearchBean();
		clientSearchBean.setGender("MALE");
		List<Client> l2 = clientService.findByCriteria(clientSearchBean, new AddressSearchBean(), null, null);
		assertTrue(l2.size() == 10);
		
		clientSearchBean = new ClientSearchBean();
		clientSearchBean.setGender("FEMALE");
		l2 = clientService.findByCriteria(clientSearchBean, new AddressSearchBean(), null, null);
		assertTrue(l2.size() == 0);
	}
	
	@Test
	public void shouldFetchClientByIdentifier() {
		String baseEntityId = "testclient2";
		Client c = new Client(baseEntityId).withBirthdate(new DateTime(), false).withFirstName("C first n")
		        .withLastName("C last n").withMiddleName("C middle n").withGender(Gender.MALE);
		c.withAddress(new Address("birthplace", new DateTime(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2),
		        DateTime.now(), null, "lat", "lon", "75210", "Sindh", "Pakistan"));
		c.withAttribute("ETHNICITY", "Mughal");
		c.withIdentifier("Program ID", "01001222");
		
		clientService.addClient(c);
		
		Client ce = clientService.getByBaseEntityId("testclient2");
		assertEquals("testclient2", ce.getBaseEntityId());
		assertTrue(Client.class.getSimpleName().equals(ce.getType()));
		assertEquals("birthplace", ce.getAddresses().get(0).getAddressType());
		assertEquals("Mughal", ce.getAttribute("ethnicity"));
		assertEquals("01001222", ce.getIdentifier("program id"));
		
		List<Client> ce2 = clientService.findAllByIdentifier("01001222");
		assertTrue(ce2.size() == 1);
		assertEquals("testclient2", ce2.get(0).getBaseEntityId());
		
		List<Client> ce3 = clientService.findAllByIdentifier("Program ID", "01001222");
		assertTrue(ce3.size() == 1);
		assertEquals("testclient2", ce3.get(0).getBaseEntityId());
	}
	
	@Test
	public void shouldFetchClientByAttribute() {
		String baseEntityId = "testclient2";
		Client c = new Client(baseEntityId).withBirthdate(new DateTime(), false).withFirstName("C first n")
		        .withLastName("C last n").withMiddleName("C middle n").withGender(Gender.MALE);
		c.withAddress(new Address("birthplace", new DateTime(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 2),
		        DateTime.now(), null, "lat", "lon", "75210", "Sindh", "Pakistan"));
		c.withAttribute("ETHNICITY", "Mughal");
		c.withIdentifier("Program ID", "01001222");
		
		clientService.addClient(c);
		
		c = new Client("testclient3").withBirthdate(new DateTime(), false).withFirstName("C first n")
		        .withLastName("C last n").withMiddleName("C middle n").withGender(Gender.MALE);
		c.withAttribute("ETHNICITY", "Mughal");
		c.addIdentifier("Program ID", "01001223");
		
		clientService.addClient(c);
		
		List<Client> ce = clientService.findAllByAttribute("ETHNICITY", "Mughal");
		assertTrue(ce.size() == 2);
		assertEquals("testclient2", ce.get(0).getBaseEntityId());
		assertEquals("testclient3", ce.get(1).getBaseEntityId());
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		return null;
	}
	
}
