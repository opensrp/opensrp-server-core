package org.opensrp.repository.postgres;

import org.junit.Test;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.repository.IdentifierSourceRepository;
import org.opensrp.util.IdentifierValidatorAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class IdentifierSourceRepositoryTest extends BaseRepositoryTest  {

	@Autowired
	@Qualifier("identifierSourceRepositoryPostgres")
	private IdentifierSourceRepository identifierSourceRepository;

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("identifier_source.sql");
		return scripts;
	}
	
	@Test
	public void testFindByIdentifier() {
		IdentifierSource identifierSource = identifierSourceRepository.findByIdentifier("Test Identifier");
		assertEquals(identifierSource.getIdentifierValidatorAlgorithm(), IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
		assertEquals(identifierSource.getBaseCharacterSet(),"baseCharacterSet");
		assertEquals(identifierSource.getMinLength(),new Integer(5));
		assertEquals(identifierSource.getMaxLength(),new Integer(10));
	}
	
}
