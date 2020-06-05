package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.domain.UniqueId;
import org.opensrp.repository.UniqueIdRepository;
import org.opensrp.generator.UniqueIdGeneratorProcessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class UniqueIdentifierServiceTest {

	@Mock
	private UniqueIdGeneratorProcessor uniqueIdGeneratorProcessor;

	@Mock
	private UniqueIdRepository uniqueIdRepository;

	private UniqueIdentifierService uniqueIdentifierService;

	@Before
	public void setUp() {
		initMocks(this);
		uniqueIdentifierService = new UniqueIdentifierService(uniqueIdGeneratorProcessor);
	}

	@Test
	public void testGenerateIdentifiers() {
		List<String> expectedIds = new ArrayList<>();
		expectedIds.add("B2A1-4");
		Set<String> reservedIds = new HashSet<>();
		IdentifierSource identifierSource = createIdentifierSource();
		when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyString())).thenReturn(createUniqueId());
		when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
		when(uniqueIdGeneratorProcessor.getIdentifiers(any(IdentifierSource.class),anyInt(),anyString())).thenReturn(expectedIds);
		List<String> actualIds = uniqueIdentifierService.generateIdentifiers(identifierSource,1,"test");
		assertEquals(actualIds.size(),expectedIds.size());
		assertEquals(actualIds.get(0),expectedIds.get(0));
	}

	private IdentifierSource createIdentifierSource() {
		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setId(1l);
		identifierSource.setIdentifier("Test-1");
		identifierSource.setBaseCharacterSet("AB12");
		identifierSource.setMinLength(4);
		identifierSource.setMaxLength(4);
		return identifierSource;
	}

	private UniqueId createUniqueId() {
		UniqueId uniqueId = new UniqueId();
		uniqueId.setId(120l);
		return uniqueId;
	}
}
