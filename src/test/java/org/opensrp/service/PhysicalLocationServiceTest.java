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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opensrp.domain.Geometry.GeometryType;
import org.opensrp.domain.LocationProperty.PropertyStatus;
import org.opensrp.domain.PhysicalLocation;
import org.opensrp.domain.PhysicalLocationTest;
import org.opensrp.domain.StructureDetails;
import org.opensrp.repository.LocationRepository;
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
		when(locationRepository.get("3734", true)).thenReturn(createLocation());
		PhysicalLocation parentLocation = locationService.getLocation("3734", true);
		verify(locationRepository).get("3734", true);
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
	public void testGetStructure() throws ParseException {

		when(locationRepository.getStructure("90397", true)).thenReturn(createStructure());
		PhysicalLocation structure = locationService.getStructure("90397", true);
		verify(locationRepository).getStructure("90397", true);
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
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-10"),
				structure.getProperties().getEffectiveStartDate());
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
		when(locationRepository.get("3734", true)).thenReturn(physicalLocation);
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
	public void testAddOrUpdateShouldAddStructure() throws ParseException {
		when(locationRepository.getStructure("90397", true)).thenReturn(null);
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
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-10"),
				structure.getProperties().getEffectiveStartDate());
		assertNull(structure.getProperties().getEffectiveEndDate());
		assertEquals(0, structure.getProperties().getVersion());
	}

	@Test
	public void testAddOrUpdateShouldUpdateStructure() throws ParseException {
		PhysicalLocation physicalLocation = createStructure();
		when(locationRepository.getStructure("90397", true)).thenReturn(physicalLocation);
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
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-10"),
				structure.getProperties().getEffectiveStartDate());
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
		String locationNames = "01_5";
		List<PhysicalLocation> expected = new ArrayList<>();
		List<PhysicalLocation> locations;

		locations = locationService.findLocationsByNames(locationNames, 0l);
		assertEquals(0, locations.size());

		expected.add(createLocation());
		when(locationService.findLocationsByNames(locationNames, 0l)).thenReturn(expected);
		locations = locationService.findLocationsByNames(locationNames, 0l);
		assertEquals(1, locations.size());
		PhysicalLocation location = locations.get(0);
		assertEquals("01_5", location.getProperties().getName());
		assertEquals("Feature", location.getType());
		assertEquals("3734", location.getId());
		assertEquals(GeometryType.MULTI_POLYGON, location.getGeometry().getType());

//		search with more than one name
		locationNames = "01_5,other_location_name";
		when(locationService.findLocationsByNames(locationNames, 0l)).thenReturn(expected);
		locations = locationService.findLocationsByNames(locationNames, 0l);
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

		when(locationRepository.findStructuresByParentAndServerVersion("3734,001", 15622112121L)).thenReturn(expected);
		locations = locationService.findStructuresByParentAndServerVersion("3734,001", 15622112121L);
		verify(locationRepository).findStructuresByParentAndServerVersion("3734,001", 15622112121L);
		verifyNoMoreInteractions(locationRepository);
		assertEquals(1, locations.size());
		structure = locations.get(0);
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

		when(locationRepository.get("12323", true)).thenReturn(physicalLocation);
		locationService.saveLocations(expectedLocations, true);

		verify(locationRepository, times(2)).add(argumentCaptor.capture());
		verify(locationRepository, times(1)).update(argumentCaptor.capture());
		for (PhysicalLocation location : argumentCaptor.getAllValues()) {
			assertTrue(location.isJurisdiction());
			assertNull(location.getServerVersion());
		}

	}

	@Test
	public void testFindStructuresWithinRadius() {

		double latitude = -14.1619809;
		double longitude = 32.5978597;

		Collection<StructureDetails> expectedDetails = new ArrayList<>();

		StructureDetails structure = new StructureDetails(UUID.randomUUID().toString(), "3221", "Mosquito Point");
		expectedDetails.add(structure);

		when(locationRepository.findStructureAndFamilyDetails(Mockito.anyDouble(), Mockito.anyDouble(),
				Mockito.anyDouble())).thenReturn(expectedDetails);
		Collection<StructureDetails> detailsFromService = locationService.findStructuresWithinRadius(latitude,
				longitude, 1000);

		assertEquals(expectedDetails, detailsFromService);
		verify(locationRepository).findStructureAndFamilyDetails(latitude, longitude, 1000);

	}

	@Test
	public void testFindLocationsByProperties() {
		List<PhysicalLocation> expectedLocations = Collections.singletonList(createLocation());

		String parentId = UUID.randomUUID().toString();
		Map<String, String> properties = new HashMap<>();
		when(locationRepository.findLocationsByProperties(true, parentId, properties)).thenReturn(expectedLocations);
		List<PhysicalLocation> locations = locationService.findLocationsByProperties(true, parentId, properties);
		verify(locationRepository).findLocationsByProperties(true, parentId, properties);
		assertEquals(1, locations.size());
		assertEquals(expectedLocations, locations);

	}

	@Test
	public void testFindStructuresByProperties() {
		List<PhysicalLocation> expectedLocations = Collections.singletonList(createStructure());
		String parentId = UUID.randomUUID().toString();
		Map<String, String> properties = new HashMap<>();
		when(locationRepository.findStructuresByProperties(false, parentId, properties)).thenReturn(expectedLocations);
		List<PhysicalLocation> locations = locationService.findStructuresByProperties(false, parentId, properties);
		verify(locationRepository).findStructuresByProperties(false, parentId, properties);
		assertEquals(1, locations.size());
		assertEquals(expectedLocations, locations);
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
