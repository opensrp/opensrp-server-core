package org.opensrp.domain.postgres;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ClientFormExampleTest {

	@Test
	public void testClear() {
		ClientFormExample clientFormExample = new ClientFormExample();
		clientFormExample.or();
		clientFormExample.setOrderByClause("date_created DESC");
		clientFormExample.setDistinct(true);

		assertEquals(1, clientFormExample.getOredCriteria().size());
		assertTrue(clientFormExample.isDistinct());
		assertNotNull(clientFormExample.getOrderByClause());

		clientFormExample.clear();


		assertEquals(0, clientFormExample.getOredCriteria().size());
		assertFalse(clientFormExample.isDistinct());
		assertNull(clientFormExample.getOrderByClause());
	}

	@Test
	public void testCreateCriteria() {
		ClientFormExample clientFormExample = new ClientFormExample();
		clientFormExample.createCriteria();

		assertEquals(1, clientFormExample.getOredCriteria().size());
	}




}
