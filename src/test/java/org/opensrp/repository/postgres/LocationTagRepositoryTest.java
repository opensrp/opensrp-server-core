package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensrp.domain.LocationTag;
import org.opensrp.domain.Practitioner;
import org.opensrp.repository.LocationTagRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationTagRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	private LocationTagRepository locationTagRepository;
	
	@BeforeClass
	public static void bootStrap() {
		tableNames.add("core.location_tag");
	}
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<>();
		return scripts;
	}
	
	@Test
	public void testAddShouldAddNewPractitioner() {
		LocationTag locationTag1 = initTestLocationTag1();
		locationTagRepository.add(locationTag1);
		
		List<LocationTag> locationTags = locationTagRepository.getAll();
		assertNotNull(locationTags);
		assertEquals(1, locationTags.size());
		
		assertEquals(true, locationTags.get(0).getActive());
		assertEquals("Country", locationTags.get(0).getName());
		
	}
	
	private LocationTag initTestLocationTag1() {
		LocationTag locationTag = new LocationTag();
		locationTag.setName("Country");
		locationTag.setDescription("first label tag name");
		locationTag.setActive(true);
		return locationTag;
	}
	
	private Practitioner initTestPractitioner2() {
		Practitioner practitioner = new Practitioner();
		practitioner.setIdentifier("practitoner-2-identifier");
		practitioner.setActive(false);
		practitioner.setName("Second Practitioner");
		practitioner.setUsername("Practioner2");
		practitioner.setUserId("user2");
		return practitioner;
	}
	
	private boolean testIfAllIdsExists(List<Practitioner> practitioners, Set<String> ids) {
		for (Practitioner practitioner : practitioners) {
			ids.remove(practitioner.getIdentifier());
		}
		return ids.size() == 0;
	}
	
}
