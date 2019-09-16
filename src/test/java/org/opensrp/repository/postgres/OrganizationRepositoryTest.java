/**
 * 
 */
package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.Organization;
import org.opensrp.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Samuel Githengi created on 09/16/19
 */
public class OrganizationRepositoryTest extends BaseRepositoryTest {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private OrganizationRepository organizationRepository;

	@Override
	protected Set<String> getDatabaseScripts() {
		return Collections.singleton("organization.sql");
	}

	@Test
	public void testGetOrganization() throws JsonGenerationException, JsonMappingException, IOException {
		Organization organization = organizationRepository.get("fcc19470-d599-11e9-bb65-2a2ae2dbcce4");
		assertNotNull(organization);
		assertEquals(1, organization.getId(), 0);
		assertEquals("fcc19470-d599-11e9-bb65-2a2ae2dbcce4", organization.getIdentifier());
		assertEquals("The Luang", organization.getName());
		assertEquals(
				"{\"coding\":[[{\"system\":\"http://terminology.hl7.org/CodeSystem/organization-type\",\"code\":\"team\",\"display\":]\"Team\"}]}",
				new ObjectMapper().writeValueAsString(organization.getType()));
		assertNull(organization.getPartOf());

		assertNull(organizationRepository.get("121sd"));

	}

	@Test
	public void testAddOrganization() {
		String identifier = UUID.randomUUID().toString();
		assertNull(organizationRepository.get(identifier));

		Organization organization = new Organization();
		organization.setIdentifier(identifier);
		organization.setName("Ateam");
		organization.setPartOf(1l);
		organization.setActive(true);
		organizationRepository.add(organization);

		Organization savedOrganization = organizationRepository.get(identifier);

		assertNotNull(savedOrganization);
		assertNotNull(savedOrganization.getId());
		assertEquals(identifier, savedOrganization.getIdentifier());
		assertEquals("Ateam", savedOrganization.getName());
		assertNull(savedOrganization.getType());
		assertEquals(1, savedOrganization.getPartOf(), 0);

	}

	@Test
	public void testUpdateOrganization() {

		Organization organization = organizationRepository.getByPrimaryKey(3l);
		organization.setName("Ateam");
		organization.setPartOf(1l);
		organization.setActive(false);
		organizationRepository.update(organization);

		Organization updatedOrganization = organizationRepository.getByPrimaryKey(3l);

		assertNotNull(updatedOrganization);
		assertEquals(3l, updatedOrganization.getId(), 0);
		assertEquals("4c506c98-d3a9-11e9-bb65-2a2ae2dbcce4", updatedOrganization.getIdentifier());
		assertEquals("Ateam", updatedOrganization.getName());
		assertNull(updatedOrganization.getType());
		assertEquals(1l, updatedOrganization.getPartOf(), 0);

	}

	@Test
	public void testGetAll() {
		assertEquals(3, organizationRepository.getAll().size());

		Organization organization = new Organization();
		organization.setIdentifier(UUID.randomUUID().toString());
		organization.setName("Ateam");
		organization.setPartOf(1l);
		organization.setActive(true);
		organizationRepository.add(organization);

		assertEquals(4, organizationRepository.getAll().size());

	}

	@Test
	public void testSafeRemove() {

		Organization organization = organizationRepository.getByPrimaryKey(2l);
		organizationRepository.safeRemove(organization);

		assertNull(organizationRepository.getByPrimaryKey(2l));

		assertEquals(2, organizationRepository.getAll().size());

	}

	@Test
	public void testAssignLocationAndPlan() {

	}

	@Test
	public void testFindAssignedLocations() {
		List<AssignedLocations> assignedLocations = organizationRepository.findAssignedLocations(1l);
		assertEquals(2, assignedLocations.size());

		assignedLocations = organizationRepository.findAssignedLocations(2l);
		assertEquals(1, assignedLocations.size());
		assertEquals("304cbcd4-0850-404a-a8b1-486b02f7b84d", assignedLocations.get(0).getJurisdictionId());
		assertEquals("7f2ae03f-9569-5535-918c-9d976b3ae5f8", assignedLocations.get(0).getPlanId());
		assertEquals("2019-09-10", dateFormat.format(assignedLocations.get(0).getFromDate()));
		assertEquals("2021-09-10", dateFormat.format(assignedLocations.get(0).getToDate()));

	}

	@Test
	public void testFindAssignedLocationsMutipleIds() {
		List<AssignedLocations> assignedLocations = organizationRepository
				.findAssignedLocations(Collections.singletonList(1l));
		assertEquals(2, assignedLocations.size());

		assignedLocations = organizationRepository.findAssignedLocations(Collections.singletonList(2l));
		assertEquals(1, assignedLocations.size());
		assertEquals("304cbcd4-0850-404a-a8b1-486b02f7b84d", assignedLocations.get(0).getJurisdictionId());
		assertEquals("7f2ae03f-9569-5535-918c-9d976b3ae5f8", assignedLocations.get(0).getPlanId());
		assertEquals("2019-09-10", dateFormat.format(assignedLocations.get(0).getFromDate()));
		assertEquals("2021-09-10", dateFormat.format(assignedLocations.get(0).getToDate()));

		assignedLocations = organizationRepository.findAssignedLocations(Arrays.asList(1l, 2l));
		assertEquals(3, assignedLocations.size());
	}

}
