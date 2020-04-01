package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.LocationTag;
import org.opensrp.domain.LocationTagMap;
import org.opensrp.domain.postgres.LocationTagExample;
import org.opensrp.repository.LocationTagRepository;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class LocationTagServiceTest {
	
	private LocationTagService locationTagService;
	
	private LocationTagRepository locationTagRepository;
	
	@Before
	public void setUp() {
		locationTagRepository = mock(LocationTagRepository.class);
		locationTagService = new LocationTagService();
		locationTagService.setLocationTagRepository(locationTagRepository);
		
	}
	
	@Test
	public void testgetAllLocationTags() {
		List<LocationTag> expectedLocationTags = new ArrayList<>();
		expectedLocationTags.add(initTestLocationTag());
		when(locationTagRepository.getAll()).thenReturn(expectedLocationTags);
		
		List<LocationTag> actutalLocationTags = locationTagService.getAllLocationTags();
		verify(locationTagRepository).getAll();
		assertEquals(1, actutalLocationTags.size());
		assertEquals("Country", actutalLocationTags.get(0).getName());
	}
	
	@Test
	public void testGetLocationTagByLocationTagName() {
		List<LocationTag> expectedLocationTags = new ArrayList<>();
		expectedLocationTags.add(initTestLocationTag());
		LocationTagExample locationTagExample = new LocationTagExample();
		when(locationTagRepository.findByLocationTagExample(locationTagExample, 0, 1)).thenReturn(expectedLocationTags);
		
		locationTagExample.createCriteria().andNameLike("C");
		List<LocationTag> actutalLocationTag = locationTagService.findByLocationTagExample(locationTagExample, 0, 1);
		verify(locationTagRepository).findByLocationTagExample(locationTagExample, 0, 1);
		assertEquals(1, actutalLocationTag.size());
		assertEquals("Country", actutalLocationTag.get(0).getName());
		
	}
	
	@Test
	public void testGetLocationTagByLocationTagId() {
		List<LocationTag> expectedLocationTags = new ArrayList<>();
		expectedLocationTags.add(initTestLocationTag());
		LocationTagExample locationTagExample = new LocationTagExample();
		when(locationTagRepository.findByLocationTagExample(locationTagExample, 0, 1)).thenReturn(expectedLocationTags);
		
		locationTagExample.createCriteria().andIdEqualTo(1l);
		List<LocationTag> actutalLocationTag = locationTagService.findByLocationTagExample(locationTagExample, 0, 1);
		verify(locationTagRepository).findByLocationTagExample(locationTagExample, 0, 1);
		assertEquals(1, actutalLocationTag.size());
		assertEquals("Country", actutalLocationTag.get(0).getName());
		assertEquals("1", actutalLocationTag.get(0).getId() + "");
		
	}
	
	@Test
	public void testGetLocationTagByLocationTagActive() {
		List<LocationTag> expectedLocationTags = new ArrayList<>();
		expectedLocationTags.add(initTestLocationTag());
		LocationTagExample locationTagExample = new LocationTagExample();
		when(locationTagRepository.findByLocationTagExample(locationTagExample, 0, 1)).thenReturn(expectedLocationTags);
		
		locationTagExample.createCriteria().andIdEqualTo(1l);
		List<LocationTag> actutalLocationTag = locationTagService.findByLocationTagExample(locationTagExample, 0, 1);
		verify(locationTagRepository).findByLocationTagExample(locationTagExample, 0, 1);
		assertEquals(1, actutalLocationTag.size());
		assertEquals("Country", actutalLocationTag.get(0).getName());
		assertEquals(true, actutalLocationTag.get(0).getActive());
		
	}
	
	@Test
	public void testAddOrUpdateShouldCallRepostoryAddMethod() {
		LocationTag locationTag = initTestLocationTag1();
		locationTagService.addOrUpdateLocationTag(locationTag);
		verify(locationTagRepository).add(eq(locationTag));
	}
	
	@Test
	public void testAddOrUpdateShouldCallRepostoryUpdateMethod() {
		LocationTag locationTag = initTestLocationTag();
		locationTagService.addOrUpdateLocationTag(locationTag);
		verify(locationTagRepository).update(eq(locationTag));
	}
	
	@Test
	public void testDeleteShouldCallRepostorySafeRemoveMethodByLocationTag() {
		when(locationTagRepository.get(anyString())).thenReturn(initTestLocationTag());
		LocationTag locationTag = initTestLocationTag();
		locationTagService.deleteLocationTag(locationTag);
		verify(locationTagRepository).safeRemove(eq(locationTag));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddOrUpdateLocationTagWithoutLocationTagName() {
		LocationTag locationTag = initTestLocationTag();
		locationTag.setName(null);
		locationTagService.addOrUpdateLocationTag(locationTag);
		verify(locationTagRepository, never()).add(eq(locationTag));
		verify(locationTagRepository, never()).update(eq(locationTag));
		
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDeleteWithoutLocationTagName() {
		LocationTag locationTag = initTestLocationTag();
		locationTag.setName(null);
		locationTagService.deleteLocationTag(locationTag);
		verify(locationTagRepository, never()).safeRemove(eq(locationTag));
	}
	
	private LocationTag initTestLocationTag() {
		LocationTag locationTag = new LocationTag();
		locationTag.setName("Country");
		locationTag.setDescription("first label tag name");
		locationTag.setActive(true);
		locationTag.setId(1l);
		return locationTag;
	}
	
	private LocationTag initTestLocationTag1() {
		LocationTag locationTag = new LocationTag();
		locationTag.setName("Country");
		locationTag.setDescription("first label tag name");
		locationTag.setActive(true);
		locationTag.setId(0l);
		return locationTag;
	}
	
	private LocationTagMap initTestLocationTagMap() {
		LocationTagMap locationTagMap = new LocationTagMap();
		locationTagMap.setLocationId(1l);
		locationTagMap.setLocationTagId(1l);
		return locationTagMap;
	}
	
	@Test
	public void testShouldCallRepostoryAddLocationTagMapMethod() {
		when(locationTagRepository.get(anyString())).thenReturn(null);
		LocationTagMap locationTagMap = initTestLocationTagMap();
		
		locationTagService.addLocationTagMap(locationTagMap);
		verify(locationTagRepository).addLocationTagMap(eq(locationTagMap));
	}
	
	@Test
	public void testShouldFindLocationTagMapByExample() {
		List<LocationTagMap> expectedLocationTagMaps = new ArrayList<>();
		expectedLocationTagMaps.add(initTestLocationTagMap());
		
		when(locationTagRepository.getLocationTagMapByExample(1l, 1l)).thenReturn(expectedLocationTagMaps);
		
		List<LocationTagMap> actutalLocationTag = locationTagService.findLocationTagMapByCriteria(1l, 1l);
		verify(locationTagRepository).getLocationTagMapByExample(1l, 1l);
		assertEquals(1, actutalLocationTag.size());
		assertEquals(1, actutalLocationTag.get(0).getLocationId().longValue());
		assertEquals(1, actutalLocationTag.get(0).getLocationTagId().longValue());
		
	}
	
	@Test
	public void testDeleteShouldCallRepostoryDeleteLocationTagMapByLocationIdAndLocationTagId() {
		locationTagService.deleteLocationTagMapByLocationIdAndLocationTagId(1l, 1l);
		verify(locationTagRepository).deleteLocationTagMapByLocationIdAndLocationTagId(1l, 1l);
	}
}
