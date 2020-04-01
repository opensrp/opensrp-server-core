package org.opensrp.domain.postgres;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ClientFormMetadataExampleTest {

	@Test
	public void testClear() {
		ClientFormMetadataExample clientFormMetadataExample = new ClientFormMetadataExample();
		clientFormMetadataExample.or();
		clientFormMetadataExample.setOrderByClause("date_created DESC");
		clientFormMetadataExample.setDistinct(true);

		assertEquals(1, clientFormMetadataExample.getOredCriteria().size());
		assertTrue(clientFormMetadataExample.isDistinct());
		assertNotNull(clientFormMetadataExample.getOrderByClause());

		clientFormMetadataExample.clear();


		assertEquals(0, clientFormMetadataExample.getOredCriteria().size());
		assertFalse(clientFormMetadataExample.isDistinct());
		assertNull(clientFormMetadataExample.getOrderByClause());
	}

	@Test
	public void testCreateCriteria() {
		ClientFormMetadataExample clientFormExample = new ClientFormMetadataExample();
		clientFormExample.createCriteria();

		assertEquals(1, clientFormExample.getOredCriteria().size());
	}




}
