package org.opensrp.generator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opensrp.repository.UniqueIdRepository;
import org.opensrp.domain.UniqueId;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.util.IdentifierValidatorAlgorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UniqueIdGeneratorProcessorTest {

	@Mock
	private UniqueIdRepository uniqueIdRepository;

	private UniqueIdGeneratorProcessor uniqueIdGeneratorProcessor;

	@Before
	public void setUp() {
		initMocks(this);
		uniqueIdGeneratorProcessor = new UniqueIdGeneratorProcessor(uniqueIdRepository);
	}

	@Test
	public void testGetIdentifiers() {
		Set<String> reservedIds = new HashSet<>();
		when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(createUniqueId());
		when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
		Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
		List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSource(),5,"");
		assertTrue(ids.get(0).contains("-")); //Ensure check digit implementation
	}

	@Test
	public void testGetIdentifiersWithFirstIdentifierBase() {
		Set<String> reservedIds = new HashSet<>();
		when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(null);
		when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
		Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
		List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceV2(),5,"");
		assertTrue(ids.get(0).contains("-")); //Ensure check digit implementation
		assertTrue(ids.get(0).contains("AA11")); //Ensure first identifier base is returned as it is
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetIdentifiersWithInvalidFirstIdentifierBase() {
		Set<String> reservedIds = new HashSet<>();
		when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(null);
		when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
		Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
		uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceV3(),5,"");
	}

	private UniqueId createUniqueId() {
		UniqueId uniqueId = new UniqueId();
		uniqueId.setId(120l);
		return uniqueId;
	}

	private IdentifierSource createIdentifierSource() {
		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setId(1l);
		identifierSource.setIdentifier("Test-1");
		identifierSource.setBaseCharacterSet("AB12");
		identifierSource.setMinLength(4);
		identifierSource.setMaxLength(4);
		identifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
		return identifierSource;
	}

	private IdentifierSource createIdentifierSourceV2() {
		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setId(1l);
		identifierSource.setIdentifier("Test-1");
		identifierSource.setBaseCharacterSet("AB12");
		identifierSource.setMinLength(4);
		identifierSource.setMaxLength(4);
		identifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
		identifierSource.setFirstIdentifierBase("AA11");
		return identifierSource;
	}

	private IdentifierSource createIdentifierSourceV3() {
		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setId(1l);
		identifierSource.setIdentifier("Test-1");
		identifierSource.setBaseCharacterSet("AB12");
		identifierSource.setMinLength(4);
		identifierSource.setMaxLength(4);
		identifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
		identifierSource.setFirstIdentifierBase("Aa11");
		return identifierSource;
	}

}
