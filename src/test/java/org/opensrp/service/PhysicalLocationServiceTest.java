package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.opensrp.domain.Geometry.GeometryType;
import org.opensrp.domain.LocationProperty.PropertyStatus;
import org.opensrp.domain.PhysicalLocation;
import org.opensrp.domain.PhysicalLocationTest;
import org.opensrp.repository.LocationRepository;
import org.opensrp.search.LocationSearchBean;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.gson.JsonArray;

@RunWith(PowerMockRunner.class)
public class PhysicalLocationServiceTest {

	private PhysicalLocationService locationService;

	private LocationRepository locationRepository;

	private ArgumentCaptor<PhysicalLocation> argumentCaptor = ArgumentCaptor.forClass(PhysicalLocation.class);

	@Before
	public void setUp() {
		locationRepository = mock(LocationRepository.class);
		locationService = new PhysicalLocationService();
		locationService.setLocationRepository(locationRepository);
	}

	@Test
	public void testGetLocation() {
		when(locationRepository.get("3734")).thenReturn(createLocation());
		PhysicalLocation parentLocation = locationService.getLocation("3734");
		verify(locationRepository).get("3734");
		verifyNoMoreInteractions(locationRepository);

		assertEquals("3734", parentLocation.getId());
		assertEquals("Feature", parentLocation.getType());
		assertEquals(GeometryType.MULTI_POLYGON, parentLocation.getGeometry().getType());

		assertEquals("21", parentLocation.getProperties().getParentId());
		assertEquals("01_5", parentLocation.getProperties().getName());
		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", parentLocation.getProperties().getUid());
		assertEquals("3734", parentLocation.getProperties().getCode());

		assertFalse(parentLocation.getGeometry().getCoordinates().isJsonNull());
		JsonArray coordinates = parentLocation.getGeometry().getCoordinates().get(0).getAsJsonArray().get(0)
				.getAsJsonArray();
		assertEquals(267, coordinates.size());

		JsonArray coordinate1 = coordinates.get(0).getAsJsonArray();
		assertEquals(32.59989007736522, coordinate1.get(0).getAsDouble(), 0);
		assertEquals(-14.167432040756012, coordinate1.get(1).getAsDouble(), 0);

		JsonArray coordinate67 = coordinates.get(66).getAsJsonArray();
		assertEquals(32.5988341383848, coordinate67.get(0).getAsDouble(), 0);
		assertEquals(-14.171814074659776, coordinate67.get(1).getAsDouble(), 0);

	}

	@Test
	public void testGetStructure() {

		when(locationRepository.getStructure("90397")).thenReturn(createStructure());
		PhysicalLocation structure = locationService.getStructure("90397");
		verify(locationRepository).getStructure("90397");
		verifyNoMoreInteractions(locationRepository);

		assertEquals("Feature", structure.getType());
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
	public void testGetAllLocations() {
		List<PhysicalLocation> expected = new ArrayList<>();
		expected.add(createLocation());
		when(locationRepository.getAll()).thenReturn(expected);
		List<PhysicalLocation> locations = locationService.getAllLocations();
		verify(locationRepository).getAll();
		verifyNoMoreInteractions(locationRepository);

		assertEquals(1, locations.size());
		PhysicalLocation parentLocation = locations.get(0);
		assertEquals("3734", parentLocation.getId());
		assertEquals("Feature", parentLocation.getType());
		assertEquals(GeometryType.MULTI_POLYGON, parentLocation.getGeometry().getType());

		assertEquals("21", parentLocation.getProperties().getParentId());
		assertEquals("01_5", parentLocation.getProperties().getName());
		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", parentLocation.getProperties().getUid());
		assertEquals("3734", parentLocation.getProperties().getCode());
	}

	@Test
	public void testAddOrUpdateShouldAddLocation() {
		when(locationRepository.get("3734")).thenReturn(null);
		locationService.addOrUpdate(createLocation());
		verify(locationRepository).add(argumentCaptor.capture());

		PhysicalLocation parentLocation = argumentCaptor.getValue();
		assertEquals("3734", parentLocation.getId());
		assertEquals("Feature", parentLocation.getType());
		assertEquals(GeometryType.MULTI_POLYGON, parentLocation.getGeometry().getType());

		assertEquals("21", parentLocation.getProperties().getParentId());
		assertEquals("01_5", parentLocation.getProperties().getName());
		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", parentLocation.getProperties().getUid());
		assertEquals("3734", parentLocation.getProperties().getCode());

	}

	@Test
	public void testAddOrUpdateShouldUpdateLocation() {
		PhysicalLocation physicalLocation = createLocation();
		when(locationRepository.get("3734")).thenReturn(physicalLocation);
		locationService.addOrUpdate(createLocation());
		verify(locationRepository).update(argumentCaptor.capture());

		PhysicalLocation parentLocation = argumentCaptor.getValue();
		assertEquals("3734", parentLocation.getId());
		assertEquals("Feature", parentLocation.getType());
		assertEquals(GeometryType.MULTI_POLYGON, parentLocation.getGeometry().getType());

		assertEquals("21", parentLocation.getProperties().getParentId());
		assertEquals("01_5", parentLocation.getProperties().getName());
		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", parentLocation.getProperties().getUid());
		assertEquals("3734", parentLocation.getProperties().getCode());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddOrUpdateWithNullId() {
		PhysicalLocation physicalLocation = createLocation();
		physicalLocation.setId(null);
		locationService.addOrUpdate(physicalLocation);

	}

	@Test
	public void testAddOrUpdateShouldAddStructure() {
		when(locationRepository.getStructure("90397")).thenReturn(null);
		locationService.addOrUpdate(createStructure());
		verify(locationRepository).add(argumentCaptor.capture());

		PhysicalLocation structure = argumentCaptor.getValue();

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
	public void testAddOrUpdateShouldUpdateStructure() {
		PhysicalLocation physicalLocation = createStructure();
		when(locationRepository.getStructure("90397")).thenReturn(physicalLocation);
		locationService.addOrUpdate(physicalLocation);
		verify(locationRepository).update(argumentCaptor.capture());

		PhysicalLocation structure = argumentCaptor.getValue();

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
	public void testAdd() {
		PhysicalLocation expected = createStructure();
		locationService.add(expected);
		verify(locationRepository).add(argumentCaptor.capture());
		assertNull(argumentCaptor.getValue().getServerVersion());

		PhysicalLocation structure = argumentCaptor.getValue();

		assertEquals(GeometryType.POLYGON, structure.getGeometry().getType());
		assertFalse(structure.getGeometry().getCoordinates().isJsonNull());

		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", structure.getProperties().getUid());
		assertEquals("21384443", structure.getProperties().getCode());
		assertEquals("Residential Structure", structure.getProperties().getType());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddWithNullId() {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		locationService.add(physicalLocation);
	}

	@Test
	public void testUpdate() {

		PhysicalLocation expected = createLocation();
		locationService.update(expected);
		verify(locationRepository).update(argumentCaptor.capture());
		assertNull(argumentCaptor.getValue().getServerVersion());

		PhysicalLocation parentLocation = argumentCaptor.getValue();
		assertEquals("3734", parentLocation.getId());
		assertEquals("Feature", parentLocation.getType());
		assertEquals(GeometryType.MULTI_POLYGON, parentLocation.getGeometry().getType());

		assertEquals("21", parentLocation.getProperties().getParentId());
		assertEquals("01_5", parentLocation.getProperties().getName());
		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", parentLocation.getProperties().getUid());
		assertEquals("3734", parentLocation.getProperties().getCode());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateWithNullId() {
		PhysicalLocation physicalLocation = new PhysicalLocation();
		locationService.update(physicalLocation);
	}

	@Test
	public void testFindLocationsByServerVersion() {
		List<PhysicalLocation> expected = new ArrayList<>();
		expected.add(createLocation());
		when(locationRepository.findLocationsByServerVersion(123l)).thenReturn(expected);

		List<PhysicalLocation> locations = locationService.findLocationsByServerVersion(123l);
		verify(locationRepository).findLocationsByServerVersion(123l);
		verifyNoMoreInteractions(locationRepository);

		assertEquals(1, locations.size());
		PhysicalLocation parentLocation = locations.get(0);
		assertEquals("3734", parentLocation.getId());
		assertEquals("Feature", parentLocation.getType());
		assertEquals(GeometryType.MULTI_POLYGON, parentLocation.getGeometry().getType());

		assertEquals("21", parentLocation.getProperties().getParentId());
		assertEquals("01_5", parentLocation.getProperties().getName());
		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", parentLocation.getProperties().getUid());
		assertEquals("3734", parentLocation.getProperties().getCode());

	}

	@Test
	public void testFindLocationsByNames() {
		LocationSearchBean locationSearchBean = new LocationSearchBean();
		locationSearchBean.setName("01_5");
		List<PhysicalLocation> expected = new ArrayList<>();
		expected.add(createLocation());
		when(locationService.findLocationsByNames(locationSearchBean)).thenReturn(expected);
		List<PhysicalLocation> locations = locationService.findLocationsByNames(locationSearchBean);
		assertEquals(1, locations.size());
		PhysicalLocation location = locations.get(0);
		assertEquals("01_5", location.getProperties().getName());
		assertEquals("Feature", location.getType());
		assertEquals("3734", location.getId());
		assertEquals(GeometryType.MULTI_POLYGON, location.getGeometry().getType());

//		search with more than one name
		locationSearchBean.setName("01_5,other_location_name");
		when(locationService.findLocationsByNames(locationSearchBean)).thenReturn(expected);
		locations = locationService.findLocationsByNames(locationSearchBean);
		assertEquals(1, locations.size());
		location = locations.get(0);
		assertEquals("01_5", location.getProperties().getName());
		assertEquals("Feature", location.getType());
		assertEquals("3734", location.getId());
		assertEquals(GeometryType.MULTI_POLYGON, location.getGeometry().getType());


	}

	@Test
	public void testFindStructuresByParentAndServerVersion() {

		List<PhysicalLocation> expected = new ArrayList<>();
		expected.add(createStructure());

		when(locationRepository.findStructuresByParentAndServerVersion("3734", 15622112121L)).thenReturn(expected);

		List<PhysicalLocation> locations = locationService.findStructuresByParentAndServerVersion("3734", 15622112121L);
		verify(locationRepository).findStructuresByParentAndServerVersion("3734", 15622112121L);
		verifyNoMoreInteractions(locationRepository);

		assertEquals(1, locations.size());
		PhysicalLocation structure = locations.get(0);

		assertEquals("41587456-b7c8-4c4e-b433-23a786f742fc", structure.getProperties().getUid());
		assertEquals("21384443", structure.getProperties().getCode());
		assertEquals("Residential Structure", structure.getProperties().getType());

	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindStructuresByParentAndServerVersionWithoutParentID() {

		locationService.findStructuresByParentAndServerVersion("", 15622112121L);

	}

	@Test
	public void testAddServerVersion() {

		List<PhysicalLocation> expectedLocations = new ArrayList<>();
		expectedLocations.add(createLocation());

		List<PhysicalLocation> expectedStructures = new ArrayList<>();
		expectedStructures.add(createStructure());

		when(locationRepository.findByEmptyServerVersion()).thenReturn(expectedLocations);
		when(locationRepository.findStructuresByEmptyServerVersion()).thenReturn(expectedStructures);

		long now = System.currentTimeMillis();
		locationService.addServerVersion();

		verify(locationRepository).findByEmptyServerVersion();
		verify(locationRepository).findStructuresByEmptyServerVersion();

		verify(locationRepository, times(2)).update(argumentCaptor.capture());
		assertEquals(2, argumentCaptor.getAllValues().size());
		for (PhysicalLocation location : argumentCaptor.getAllValues())
			assertTrue(location.getServerVersion() >= now);

	}

	@Test
	public void testSaveLocations() {

		List<PhysicalLocation> expectedLocations = new ArrayList<>();
		PhysicalLocation physicalLocation = createStructure();
		physicalLocation.setId("12323");
		expectedLocations.add(physicalLocation);
		expectedLocations.add(createStructure());
		expectedLocations.add(createStructure());

		when(locationRepository.get("12323")).thenReturn(physicalLocation);
		locationService.saveLocations(expectedLocations, true);

		verify(locationRepository, times(2)).add(argumentCaptor.capture());
		verify(locationRepository, times(1)).update(argumentCaptor.capture());
		for (PhysicalLocation location : argumentCaptor.getAllValues()) {
			assertTrue(location.isJurisdiction());
			assertNull(location.getServerVersion());
		}

	}

	private PhysicalLocation createLocation() {
		PhysicalLocation parentLocation = PhysicalLocationTest.gson.fromJson(PhysicalLocationTest.parentJson,
				PhysicalLocation.class);
		parentLocation.setJurisdiction(true);
		return parentLocation;
	}

	private PhysicalLocation createStructure() {
		return PhysicalLocationTest.gson.fromJson(PhysicalLocationTest.structureJson, PhysicalLocation.class);
	}

}
