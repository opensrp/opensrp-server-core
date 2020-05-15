package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.repository.IdentifierSourceRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.util.IdentifierValidatorAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class IdentifierSourceServiceTest extends BaseRepositoryTest {
	
	private IdentifierSourceService identifierSourceService;

	@Autowired
	@Qualifier("identifierSourceRepositoryPostgres")
	private IdentifierSourceRepository identifierSourceRepository;

	@Before
	public void setUpPostgresRepository() {
		identifierSourceService = new IdentifierSourceService(identifierSourceRepository);
	}

	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("identifier_source.sql");
		return scripts;
	}


	@Test
	public void testAddIdentifierSource() {
		IdentifierSource identifierSource = new IdentifierSource("Test Identifier 1", "Test", IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM, "baseCharacterSet",
			"firstIdentifierBase","1","A",5,10,"blacklistedRegex");
		
		identifierSourceService.addOrUpdate(identifierSource);
		assertEquals(2,identifierSourceService.findAllIdentifierSources().size());  // 1 added through script 
		IdentifierSource savedIdentifierSource = identifierSourceService.findByIdentifier("Test Identifier 1");
		assertNotNull(savedIdentifierSource.getId());
	}
	
	@Test
	public void testFindByIdentifier() {
		IdentifierSource savedIdentifierSource = identifierSourceService.findByIdentifier("Test Identifier 1");
		assertNotNull(savedIdentifierSource.getId());
		assertEquals(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM.name(), savedIdentifierSource.getIdentifierValidatorAlgorithm());
	}

	@Test
	public void testUpdate() {
		IdentifierSource identifierSource = new IdentifierSource("Test Identifier 1", "Test Updated", IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM, "baseCharacterSet",
				"firstIdentifierBase","1","A",5,10,"blacklistedRegex");
        
		identifierSource.setId(1l);
		identifierSourceService.addOrUpdate(identifierSource);
		IdentifierSource updatedIdentifierSource = identifierSourceService.findByIdentifier("Test Identifier 1");
		assertNotNull(updatedIdentifierSource.getId());
		assertEquals(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM.name(), updatedIdentifierSource.getIdentifierValidatorAlgorithm().name());
		assertEquals("Test Updated", updatedIdentifierSource.getDescription());
		assertTrue(updatedIdentifierSource.getMaxLength() >= updatedIdentifierSource.getMaxLength());
	}
	
}
