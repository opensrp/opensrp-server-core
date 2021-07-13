package org.opensrp.service.rapidpro;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class RapidProServiceTest extends BaseRepositoryTest {

	@Autowired
	private RapidProService rapidProService;

	@Mock
	private ZeirRapidProService zeirRapidProService;

	@Mock
	private RapidProOnTaskComplete onTaskComplete;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		rapidProService.setZeirRapidProService(zeirRapidProService);
	}

	@Test
	public void testQueryContacts() {
		rapidProService.queryContacts(onTaskComplete);
		Mockito.verify(zeirRapidProService, Mockito.atLeastOnce()).queryContacts(onTaskComplete);
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		return null;
	}
}
