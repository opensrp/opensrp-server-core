package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.joda.time.DateTime;
import org.junit.Test;
import org.opensrp.domain.Geometry;
import org.opensrp.domain.Geometry.GeometryType;
import org.opensrp.domain.LocationProperty;
import org.opensrp.domain.LocationProperty.PropertyStatus;
import org.opensrp.domain.PhysicalLocation;
import org.opensrp.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonArray;

public class LocationRepositoryTest extends BaseRepositoryTest {

	@Autowired
	private LocationRepository locationRepository;

	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("location.sql");
		scripts.add("structure.sql");
		return scripts;
	}

	@Test
	public void testGet() {
		PhysicalLocation location = locationRepository.get("3734");
		assertNotNull(location);
		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", location.getProperties().getUid());
		assertEquals("21", location.getProperties().getParentId());
		assertEquals("Intervention Unit", location.getProperties().getType());
		assertEquals(PropertyStatus.ACTIVE, location.getProperties().getStatus());
		assertEquals(1542378347104l,location.getServerVersion().longValue());

		JsonArray coordinates = location.getGeometry().getCoordinates().get(0).getAsJsonArray().get(0).getAsJsonArray();
		assertEquals(267, coordinates.size());

		JsonArray coordinate1 = coordinates.get(0).getAsJsonArray();
		assertEquals(32.59989007736522, coordinate1.get(0).getAsDouble(), 0);
		assertEquals(-14.167432040756012, coordinate1.get(1).getAsDouble(), 0);

		JsonArray coordinate67 = coordinates.get(66).getAsJsonArray();
		assertEquals(32.5988341383848, coordinate67.get(0).getAsDouble(), 0);
		assertEquals(-14.171814074659776, coordinate67.get(1).getAsDouble(), 0);
	}

	@Test
	public void testGetWithNullOrEmptyParams() {
		assertNull(locationRepository.get(""));

		assertNull(locationRepository.get(null));

	}

	@Test
	public void testGetNotExistingLocation() {
		assertNull(locationRepository.get("1212121"));
	}

	@Test
	public void testGetStructure() {
		PhysicalLocation structure = locationRepository.getStructure("90397");
		assertNotNull(structure);
		assertEquals("90397", structure.getId());

		assertEquals(GeometryType.POLYGON, structure.getGeometry().getType());
		assertFalse(structure.getGeometry().getCoordinates().isJsonNull());

		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", structure.getProperties().getUid());
		assertEquals("21384443", structure.getProperties().getCode());
		assertEquals("Residential Structure", structure.getProperties().getType());
		assertEquals(PropertyStatus.ACTIVE, structure.getProperties().getStatus());
		assertEquals("3734", structure.getProperties().getParentId());
		assertNull(structure.getProperties().getName());
		assertEquals(5, structure.getProperties().getGeographicLevel());
		assertEquals(new DateTime("2017-01-10"), structure.getProperties().getEffectiveStartDate());
		assertNull(structure.getProperties().getEffectiveEndDate());
		assertEquals(0, structure.getProperties().getVersion());
	}

	@Test
	public void testGetStructureWithNullOrEmptyParams() {
		assertNull(locationRepository.getStructure(""));

		assertNull(locationRepository.getStructure(null));

	}

	@Test
	public void testGetStructureNotExistingLocation() {
		assertNull(locationRepository.getStructure("1212121"));
	}

	@Test
	public void testAddLocation() {
		String uuid = UUID.randomUUID().toString();
		PhysicalLocation physicalLocation = createLocation(uuid);
		locationRepository.add(physicalLocation);
		PhysicalLocation savedLocation = locationRepository.get("223232");

		assertNotNull(savedLocation);
		assertEquals("Feature", savedLocation.getType());

		assertNull(locationRepository.getStructure("223232"));

	}

	@Test
	public void testAddLocationWithoutId() {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setJurisdiction(true);
		locationRepository.add(physicalLocation);

		assertEquals(1, locationRepository.getAll().size());
		assertEquals(1, locationRepository.getAllStructures().size());

	}

	@Test
	public void testAddLocationExistingShouldNotChangeObject() {

		PhysicalLocation physicalLocation = locationRepository.get("3734");
		physicalLocation.getProperties().setName("MY Operational Area");
		physicalLocation.setJurisdiction(true);

		locationRepository.add(physicalLocation);

		physicalLocation = locationRepository.get("3734");
		assertNotEquals("MY Operational Area", physicalLocation.getProperties().getName());

		assertEquals(1, locationRepository.getAll().size());
		assertEquals(1, locationRepository.getAllStructures().size());

	}

	@Test
	public void testAddStructure() {
		String uuid = UUID.randomUUID().toString();
		PhysicalLocation physicalLocation = createStructure(uuid);

		locationRepository.add(physicalLocation);
		PhysicalLocation savedLocation = locationRepository.getStructure("121212");

		assertNotNull(savedLocation);
		assertEquals("Feature", savedLocation.getType());
		assertEquals(GeometryType.POLYGON, savedLocation.getGeometry().getType());
		assertEquals(PropertyStatus.ACTIVE, savedLocation.getProperties().getStatus());
		assertEquals(uuid, savedLocation.getProperties().getUid());

		assertNull(locationRepository.get("121212"));

	}

	@Test
	public void testAddStructureWithoutId() {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		locationRepository.add(physicalLocation);

		assertEquals(1, locationRepository.getAll().size());
		assertEquals(1, locationRepository.getAllStructures().size());

	}

	@Test
	public void testAddStructureExistingShouldNotChangeObject() {

		PhysicalLocation physicalLocation = locationRepository.getStructure("90397");
		physicalLocation.getProperties().setName("Mwangala Household");
		locationRepository.add(physicalLocation);

		physicalLocation = locationRepository.getStructure("90397");
		assertNotEquals("Mwangala Household", physicalLocation.getProperties().getName());

		assertEquals(1, locationRepository.getAll().size());
		assertEquals(1, locationRepository.getAllStructures().size());

	}

	@Test
	public void testUpdateLocation() {
		PhysicalLocation physicalLocation = locationRepository.get("3734");
		physicalLocation.getGeometry().setType(GeometryType.POLYGON);
		physicalLocation.getProperties().setStatus(PropertyStatus.PENDING_REVIEW);
		physicalLocation.getProperties().setGeographicLevel(3);
		physicalLocation.setJurisdiction(true);
		locationRepository.update(physicalLocation);
		PhysicalLocation updatedLocation = locationRepository.get("3734");

		assertNotNull(updatedLocation);
		assertEquals(GeometryType.POLYGON, updatedLocation.getGeometry().getType());
		assertEquals(PropertyStatus.PENDING_REVIEW, updatedLocation.getProperties().getStatus());
		assertEquals(3, updatedLocation.getProperties().getGeographicLevel());

		assertNull(locationRepository.getStructure("3734"));

	}

	@Test
	public void testUpdateLocationWithoutId() {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		locationRepository.add(physicalLocation);

		assertEquals(1, locationRepository.getAll().size());
		assertEquals(1, locationRepository.getAllStructures().size());

	}

	@Test
	public void testUpdateLocationNonExistingShouldNotChangeObject() {

		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setId("223232");
		physicalLocation.setType("Feature");
		physicalLocation.setJurisdiction(true);
		locationRepository.update(physicalLocation);

		assertNull(locationRepository.get("223232"));
		assertNull(locationRepository.getStructure("223232"));

	}

	@Test
	public void testUpdateStructure() {
		PhysicalLocation structure = locationRepository.getStructure("90397");
		structure.getProperties().setCode("12121");
		structure.getProperties().setParentId("11");
		locationRepository.update(structure);

		PhysicalLocation updatedStructure = locationRepository.getStructure("90397");

		assertNotNull(updatedStructure);
		assertEquals("12121", updatedStructure.getProperties().getCode());
		assertEquals("11", updatedStructure.getProperties().getParentId());

		assertNull(locationRepository.get("90397"));

	}

	@Test
	public void testUpdateStructureWithoutId() {
		PhysicalLocation structure = new PhysicalLocation();
		locationRepository.add(structure);

		assertEquals(1, locationRepository.getAll().size());
		assertEquals(1, locationRepository.getAllStructures().size());

	}

	@Test
	public void testUpdateStructureExistingShouldNotChangeObject() {

		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setId("223232");
		physicalLocation.setType("Feature");
		locationRepository.update(physicalLocation);

		assertNull(locationRepository.get("223232"));
		assertNull(locationRepository.getStructure("223232"));

	}

	@Test
	public void testGetAll() {
		List<PhysicalLocation> locations = locationRepository.getAll();
		assertEquals(1, locations.size());

		locationRepository.safeRemove(locationRepository.get("3734"));

		assertTrue(locationRepository.getAll().isEmpty());

		String uuid = UUID.randomUUID().toString();
		locationRepository.add(createLocation(uuid));

		locations = locationRepository.getAll();

		assertEquals(1, locations.size());
		assertEquals("223232", locations.get(0).getId());
		assertEquals(uuid, locations.get(0).getProperties().getUid());

	}

	@Test
	public void testSafeRemoveLocation() {

		assertNotNull(locationRepository.get("3734"));

		locationRepository.safeRemove(locationRepository.get("3734"));

		assertNull(locationRepository.get("3734"));
	}

	@Test
	public void testSafeRemoveStructure() {

		assertNotNull(locationRepository.getStructure("90397"));

		locationRepository.safeRemove(locationRepository.getStructure("90397"));

		assertNull(locationRepository.getStructure("90397"));
	}

	@Test
	public void testSafeRemoveNonExistentLocation() {
		locationRepository.safeRemove(null);
		locationRepository.safeRemove(new PhysicalLocation());
		assertEquals(1, locationRepository.getAll().size());

		locationRepository.safeRemove(locationRepository.get("671198"));
		assertEquals(1, locationRepository.getAll().size());

	}

	@Test
	public void testFindLocationsByServerVersion() {

		List<PhysicalLocation> locations = locationRepository.findLocationsByServerVersion(1542378347106l);
		assertTrue(locations.isEmpty());

		locations = locationRepository.findLocationsByServerVersion(1l);
		System.out.println(ReflectionToStringBuilder.toString(locations.get(0)));
		assertEquals(1, locations.size());
		assertEquals("3734", locations.get(0).getId());
		assertTrue(locations.get(0).getServerVersion() >= 1l);

		locations.get(0).setServerVersion(null);
		locationRepository.update(locations.get(0));

		locations = locationRepository.findLocationsByServerVersion(1l);
		assertTrue(locations.isEmpty());

	}

	@Test
	public void testFindStructuresByParentAndServerVersion() {

		List<PhysicalLocation> locations = locationRepository.findStructuresByParentAndServerVersion("3734",
				1542376382859l);
		assertTrue(locations.isEmpty());

		locations = locationRepository.findStructuresByParentAndServerVersion("3734", 1542376382851l);
		assertEquals(1, locations.size());
		assertEquals("90397", locations.get(0).getId());
		assertEquals("3734", locations.get(0).getProperties().getParentId());
		assertEquals(1542376382851l, locations.get(0).getServerVersion().longValue());
		assertTrue(locations.get(0).getServerVersion() >= 1l);

		locations.get(0).setServerVersion(null);
		locationRepository.update(locations.get(0));

		locations = locationRepository.findStructuresByParentAndServerVersion("3734", 0l);
		assertTrue(locations.isEmpty());
	}

	@Test
	public void testFindByEmptyServerVersion() {

		List<PhysicalLocation> locations = locationRepository.findByEmptyServerVersion();
		assertTrue(locations.isEmpty());

		PhysicalLocation location = locationRepository.get("3734");
		location.setServerVersion(null);
		locationRepository.update(location);

		locations = locationRepository.findByEmptyServerVersion();

	}

	@Test
	public void testFindStructuresByEmptyServerVersion() {

		List<PhysicalLocation> locations = locationRepository.findStructuresByEmptyServerVersion();
		assertTrue(locations.isEmpty());

		PhysicalLocation location = locationRepository.getStructure("90397");
		location.setServerVersion(null);
		locationRepository.update(location);

		locations = locationRepository.findByEmptyServerVersion();

	}

	private PhysicalLocation createLocation(String uuid) {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setId("223232");
		physicalLocation.setType("Feature");
		physicalLocation.setJurisdiction(true);
		Geometry geometry = new Geometry();
		geometry.setType(GeometryType.MULTI_POLYGON);
		physicalLocation.setGeometry(geometry);
		LocationProperty properties = new LocationProperty();
		properties.setStatus(PropertyStatus.INACTIVE);
		properties.setUid(uuid);
		physicalLocation.setProperties(properties);
		return physicalLocation;

	}

	private PhysicalLocation createStructure(String uuid) {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		physicalLocation.setId("121212");
		physicalLocation.setType("Feature");
		Geometry geometry = new Geometry();
		geometry.setType(GeometryType.POLYGON);
		physicalLocation.setGeometry(geometry);
		LocationProperty properties = new LocationProperty();
		properties.setStatus(PropertyStatus.ACTIVE);
		properties.setUid(uuid);
		physicalLocation.setProperties(properties);
		return physicalLocation;
	}

}
