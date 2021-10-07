package org.opensrp.domain;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LocationTest {

	private Location location;

	@Before
	public void setUp(){
		location = new Location();
	}

	@Test
	public void testGetLocationId() {
		String id = "cc3386a7-1d58-41e4-9916-7e1f7908416b";
		location.setLocationId(id);

		assertEquals(id, location.getLocationId());
	}

	@Test
	public void testWithLocationId() {
		String id = "cc3386a7-1d58-41e4-9916-7e1f7908416b";
		Location location1 = location.withLocationId(id);

		assertEquals(location, location1);
		assertEquals(id, location1.getLocationId());
	}

}
