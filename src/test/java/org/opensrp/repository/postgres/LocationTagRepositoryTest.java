package org.opensrp.repository.postgres;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensrp.domain.LocationTagMap;
import org.opensrp.domain.postgres.LocationTagExample;
import org.opensrp.repository.LocationTagRepository;
import org.smartregister.domain.LocationTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class LocationTagRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private LocationTagRepository locationTagRepository;

    @BeforeClass
    public static void bootStrap() {
        tableNames = Arrays.asList("core.location_tag", "core.location_tag_map");
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<>();
        scripts.add("location.sql");
        return scripts;
    }

    @Test
    public void testAddShouldAddNewLocationTag() {
        LocationTag locationTag1 = initTestLocationTag1();
        locationTagRepository.add(locationTag1);

        List<LocationTag> locationTags = locationTagRepository.getAll();
        assertNotNull(locationTags);
        assertEquals(1, locationTags.size());

        assertEquals(true, locationTags.get(0).getActive());
        assertEquals("Country", locationTags.get(0).getName());

    }

    @Test
    public void testAddShouldAddNewLocationTagMap() {

        LocationTag locationTag1 = initTestLocationTag6();
        locationTagRepository.add(locationTag1);

        List<LocationTag> locationTags = locationTagRepository.getAll();
        LocationTagMap locationTagMap = new LocationTagMap();
        locationTagMap.setLocationId(1l);
        locationTagMap.setLocationTagId(locationTags.get(0).getId());

        int insert = locationTagRepository.addLocationTagMap(locationTagMap);

        List<LocationTagMap> locationTagMaps = locationTagRepository.getLocationTagMapByExample(1l, locationTags.get(0)
                .getId());
        assertNotEquals(0, insert);

        assertEquals(1, locationTagMaps.get(0).getLocationId().longValue());
        assertEquals(locationTags.get(0).getId(), locationTagMaps.get(0).getLocationTagId());

    }

    @Test
    public void testDeleteShouldAddNewLocationTagMap() {

        LocationTag locationTag1 = initTestLocationTag6();
        locationTagRepository.add(locationTag1);

        List<LocationTag> locationTags = locationTagRepository.getAll();
        LocationTagMap locationTagMap = new LocationTagMap();
        locationTagMap.setLocationId(1l);
        locationTagMap.setLocationTagId(locationTags.get(0).getId());

        locationTagRepository.addLocationTagMap(locationTagMap);
        List<LocationTagMap> locationTagMaps = locationTagRepository.getLocationTagMapByExample(1l, locationTags.get(0)
                .getId());

        locationTagRepository
                .deleteLocationTagMapByLocationIdAndLocationTagId(1l, locationTagMaps.get(0).getLocationTagId());

        List<LocationTagMap> getDeletedLocationTagMaps = locationTagRepository.getLocationTagMapByExample(1l,
                locationTagMaps.get(0).getLocationTagId());

        assertEquals(0, getDeletedLocationTagMaps.size());
    }

    @Test(expected = DuplicateKeyException.class)
    public void testNotAddShouldAddNewLocationTagMap() {

        LocationTag locationTag1 = initTestLocationTag6();
        locationTagRepository.add(locationTag1);

        List<LocationTag> locationTags = locationTagRepository.getAll();
        LocationTagMap locationTagMap = new LocationTagMap();
        locationTagMap.setLocationId(1l);
        locationTagMap.setLocationTagId(locationTags.get(0).getId());

        locationTagRepository.addLocationTagMap(locationTagMap);
        locationTagRepository.addLocationTagMap(locationTagMap);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNotAddShouldAddNewLocationTagMapWithoutLocationTagIdandLocationId() {
        LocationTagMap locationTagMap = new LocationTagMap();
        locationTagRepository.addLocationTagMap(locationTagMap);

    }

    @Test(expected = DuplicateKeyException.class)
    public void testAddShouldNotAddDuplicateLocationTagMap() {
        LocationTag locationTag1 = initTestLocationTag1();
        locationTagRepository.add(locationTag1);
        locationTagRepository.add(locationTag1);

    }

    @Test
    public void testShouldDeleteLocationTagMapByLocationId() {
        LocationTag locationTag1 = initTestLocationTag6();
        locationTagRepository.add(locationTag1);

        List<LocationTag> locationTags = locationTagRepository.getAll();
        LocationTagMap locationTagMap = new LocationTagMap();
        locationTagMap.setLocationId(1l);
        locationTagMap.setLocationTagId(locationTags.get(0).getId());
        locationTagRepository.addLocationTagMap(locationTagMap);

        LocationTagMap locationTagMap1 = new LocationTagMap();
        locationTagMap1.setLocationId(2l);
        locationTagMap1.setLocationTagId(locationTags.get(0).getId());
        locationTagRepository.addLocationTagMap(locationTagMap1);

        locationTagRepository.deleteLocationTagMapByLocationId(1l);

        List<LocationTagMap> getDeletedLocationTagMaps = locationTagRepository.getLocationTagMapByExample(1l, locationTags
                .get(0).getId());
        assertNotEquals(1, getDeletedLocationTagMaps.size());
        List<LocationTagMap> getNotDeletedLocationTagMaps = locationTagRepository.getLocationTagMapByExample(2l,
                locationTags.get(0).getId());

        assertEquals(1, getNotDeletedLocationTagMaps.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldIllegalArgumentExceptionOndeleteLocationTagMapByLocationIdZero() {
        locationTagRepository.deleteLocationTagMapByLocationId(0l);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldIllegalArgumentExceptionOnDeleteLocationTagMapWitLocationIdZero() {
        locationTagRepository.deleteLocationTagMapByLocationIdAndLocationTagId(0l, 1l);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldIllegalArgumentExceptionOnDeleteLocationTagMapWitLocationIdNull() {
        locationTagRepository.deleteLocationTagMapByLocationIdAndLocationTagId(null, 1l);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldIllegalArgumentExceptionOnDeleteLocationTagMapWitLocationTagIdNull() {
        locationTagRepository.deleteLocationTagMapByLocationIdAndLocationTagId(1l, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShouldIllegalArgumentExceptionOnDeleteLocationTagMapWitLocationTagIdZero() {
        locationTagRepository.deleteLocationTagMapByLocationIdAndLocationTagId(1l, 0l);
    }

    @Test
    public void testAddShouldUpdateExistingLocationTag() {
        LocationTag locationTag2 = initTestLocationTag2();
        locationTagRepository.add(locationTag2);
        org.opensrp.domain.postgres.LocationTag getLocationTag = locationTagRepository.getLocationTagByName("Division");
        locationTag2.setId(getLocationTag.getId());
        locationTag2.setDescription("update second label tag name");
        locationTag2.setName("Division Tag");
        locationTagRepository.update(locationTag2);
        List<LocationTag> locationTags = locationTagRepository.getAll();
        assertNotNull(locationTags);
        assertEquals(1, locationTags.size());

        assertEquals(true, locationTags.get(0).getActive());
        assertEquals("Division Tag", locationTags.get(0).getName());

    }

    @Test(expected = DuplicateKeyException.class)
    public void testAddShouldNotUpdateExistingLocationTag() {
        LocationTag locationTag2 = initTestLocationTag2();
        locationTagRepository.add(locationTag2);
        locationTag2.setName("Division Tag");
        locationTagRepository.add(locationTag2);
        org.opensrp.domain.postgres.LocationTag getLocationTag = locationTagRepository.getLocationTagByName("Division");
        locationTag2.setId(getLocationTag.getId());
        locationTag2.setDescription("update second label tag name");
        locationTag2.setName("Division Tag");
        locationTagRepository.update(locationTag2);

    }

    @Test
    public void testAddShouldAInActiveExistingLocationTag() {
        LocationTag locationTag4 = initTestLocationTag4();
        locationTagRepository.add(locationTag4);
        org.opensrp.domain.postgres.LocationTag findLocationTag = locationTagRepository.getLocationTagByName("upazila");
        locationTagRepository.safeRemove(findLocationTag.getId());
        org.opensrp.domain.postgres.LocationTag getLocationTag = locationTagRepository.getLocationTagByName("upazila");
        assertNotNull(getLocationTag);
        assertEquals(false, getLocationTag.getActive());
        assertEquals("upazila", getLocationTag.getName());

    }

    @Test
    public void testAddShouldAInActiveExistingLocationTagById() {
        LocationTag locationTag2 = initTestLocationTag2();
        locationTagRepository.add(locationTag2);
        locationTagRepository.safeRemove(locationTag2);
        org.opensrp.domain.postgres.LocationTag getLocationTag = locationTagRepository.getLocationTagByName("Division");
        assertNotNull(getLocationTag);
        assertEquals(false, getLocationTag.getActive());
        assertEquals("Division", getLocationTag.getName());

    }

    @Test
    public void testAddShouldNotAddRecordIfLocationTagIsNull() {
        locationTagRepository.add(null);
        List<LocationTag> locationTags = locationTagRepository.getAll();
        assertTrue(locationTags.isEmpty());
    }

    @Test
    public void testAddShouldNotAddRecordIfLocationTagNameIsNullOrEmpty() {
        LocationTag locationTag1 = initTestLocationTag1();
        locationTag1.setName(null);
        locationTagRepository.add(locationTag1);

        locationTag1.setName("");
        locationTagRepository.add(locationTag1);
        List<LocationTag> locationTags = locationTagRepository.getAll();
        assertTrue(locationTags.isEmpty());
    }

    @Test
    public void testGetShouldGetLocationTagByName() {

        LocationTag locationTag1 = initTestLocationTag1();
        locationTagRepository.add(locationTag1);

        org.opensrp.domain.postgres.LocationTag locationTag = locationTagRepository.getLocationTagByName("Country");
        assertNotNull(locationTag);
        assertEquals("Country", locationTag.getName());
        assertEquals(true, locationTag.getActive());

    }

    @Test
    public void testGetShouldGetLocationTagByLocationTagExample() {

        LocationTag locationTag3 = initTestLocationTag3();
        locationTagRepository.add(locationTag3);
        LocationTagExample locationTagExample = new LocationTagExample();
        String d = "d";
        locationTagExample.createCriteria().andNameLike('%' + d + '%');
        List<LocationTag> locationTags = locationTagRepository.findByLocationTagExample(locationTagExample, 0, 100);

        assertEquals(1, locationTags.size());
        assertEquals("district", locationTags.get(0).getName());
        assertEquals(true, locationTags.get(0).getActive());

    }

    private LocationTag initTestLocationTag1() {
        LocationTag locationTag = new LocationTag();
        locationTag.setName("Country");
        locationTag.setDescription("first label tag name");
        locationTag.setActive(true);
        locationTag.setId(1L);
        return locationTag;
    }

    private LocationTag initTestLocationTag6() {
        LocationTag locationTag = new LocationTag();
        locationTag.setName("Ward");
        locationTag.setDescription("first label tag name");
        locationTag.setActive(true);
        locationTag.setId(2L);
        return locationTag;
    }

    private LocationTag initTestLocationTag2() {
        LocationTag locationTag = new LocationTag();
        locationTag.setName("Division");
        locationTag.setDescription("second label tag name");
        locationTag.setActive(true);
        locationTag.setId(3L);
        return locationTag;
    }

    private LocationTag initTestLocationTag3() {
        LocationTag locationTag = new LocationTag();
        locationTag.setName("district");
        locationTag.setDescription("third label tag name");
        locationTag.setActive(true);
        locationTag.setId(4L);
        return locationTag;
    }

    private LocationTag initTestLocationTag4() {
        LocationTag locationTag = new LocationTag();
        locationTag.setName("upazila");
        locationTag.setDescription("fourth label tag name");
        locationTag.setActive(true);
        locationTag.setId(5L);
        return locationTag;
    }

}
