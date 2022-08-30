package org.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.opensrp.domain.Location;
import org.smartregister.domain.BaseDataObject;

public final class AssertionUtil {

    private AssertionUtil() {

    }

    public static <T> void assertTwoListAreSameIgnoringOrder(List<T> expectedList, List<T> actualList) {
        assertEquals(expectedList.size(), actualList.size());
        assertTrue("expected: " + expectedList.toString() + "\n" + "actual: " + actualList.toString(),
                expectedList.containsAll(actualList) && actualList.containsAll(expectedList));
    }

    public static <T extends BaseDataObject> void assertNewObjectCreation(T expectedObject, T actualObject) {
        assertNotNull(actualObject.getDateCreated());

        assertEquals(expectedObject, actualObject);

    }

    public static <T extends BaseDataObject> void assertObjectUpdate(T expectedObject, T actualObject) {
        assertNotNull(actualObject.getDateEdited());

        assertEquals(expectedObject, actualObject);

    }

    public static void assertTwoDifferentTypeLocationSame(Location expectedLocation,
                                                          org.opensrp.api.domain.Location actualLocation) {
        assertEquals(expectedLocation.getLocationId(), actualLocation.getLocationId());
        assertEquals(expectedLocation.getTags(), actualLocation.getTags());
        assertEquals(expectedLocation.getAttributes(), actualLocation.getAttributes());
        //assertEquals(expectedLocation.getAddress(), actualLocation.getAddress());
        assertEquals(expectedLocation.getName(), actualLocation.getName());
        assertEquals(expectedLocation.getIdentifiers(), actualLocation.getIdentifiers());
        if (expectedLocation.getParentLocation() != null) {
            assertNotNull(actualLocation.getParentLocation());
            assertTwoDifferentTypeLocationSame(expectedLocation.getParentLocation(), actualLocation.getParentLocation());
        }
    }

}
