package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.generator.UniqueIdGeneratorProcessor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class UniqueIdentifierServiceTest {

	@Mock
	private UniqueIdGeneratorProcessor uniqueIdGeneratorProcessor;

	private UniqueIdentifierService uniqueIdentifierService;

	@Before
	public void setUp() {
		uniqueIdentifierService = new UniqueIdentifierService();
	}

	@Test
	public void testGenerateIdentifiers() {
		List<String> expectedIds = new ArrayList<>();
		expectedIds.add("B2A1-4");
		IdentifierSource identifierSource = createIdentifierSource();
		when(uniqueIdGeneratorProcessor.getIdentifiers(any(IdentifierSource.class),anyInt(),anyString())).thenReturn(expectedIds);
		List<String> actualIds = uniqueIdentifierService.generateIdentifiers(identifierSource,1,"test");
		assertEquals(actualIds.size(),expectedIds.size());
		assertEquals(actualIds.get(0),expectedIds.get(0));
	}

	IdentifierSource createIdentifierSource() {
		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setIdentifier("Test-1");
		identifierSource.setBaseCharacterSet("AB12");
		identifierSource.setMinLength(4);
		identifierSource.setMaxLength(4);
		return identifierSource;
	}
}
