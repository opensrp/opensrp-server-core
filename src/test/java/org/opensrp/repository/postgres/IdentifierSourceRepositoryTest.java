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
import static org.junit.Assert.assertNotNull;

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
		assertEquals(identifierSource.getBaseCharacterSet(),"AB12");
		assertEquals(identifierSource.getMinLength(),new Integer(5));
		assertEquals(identifierSource.getMaxLength(),new Integer(10));
	}

	@Test
	public void testAdd() {
		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setIdentifier("Test-1");
		identifierSource.setBaseCharacterSet("AB12");
		identifierSource.setMinLength(4);
		identifierSource.setMaxLength(4);

		identifierSourceRepository.add(identifierSource);
		assertEquals(2, identifierSourceRepository.getAll().size());
		IdentifierSource addedIdentifierSource = identifierSourceRepository.findByIdentifier("Test-1");
		assertNotNull(addedIdentifierSource);
		assertEquals("AB12", addedIdentifierSource.getBaseCharacterSet());
	}

	@Test
	public void testUpdate() {
		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setIdentifier("Test-1");
		identifierSource.setBaseCharacterSet("AB12");
		identifierSource.setMinLength(4);
		identifierSource.setMaxLength(4);

		identifierSourceRepository.add(identifierSource);
		assertEquals(2, identifierSourceRepository.getAll().size());
		IdentifierSource addedIdentifierSource = identifierSourceRepository.findByIdentifier("Test-1");
		addedIdentifierSource.setIdentifier("UpdatedTest-1");

		identifierSourceRepository.update(addedIdentifierSource);
		IdentifierSource updatedIdentifierSource = identifierSourceRepository.findByIdentifier("UpdatedTest-1");
		assertNotNull(updatedIdentifierSource);
		assertEquals(updatedIdentifierSource.getIdentifier(), "UpdatedTest-1");
	}

	@Test
	public void testUpdateIdSourceWithSequenceValue() {
		IdentifierSource identifierSource = new IdentifierSource();
		identifierSource.setIdentifier("Test-1");
		identifierSource.setBaseCharacterSet("012345");
		identifierSource.setFirstIdentifierBase("2000000");
		identifierSource.setMinLength(7);
		identifierSource.setMaxLength(10);

		identifierSourceRepository.add(identifierSource);
		assertEquals(2, identifierSourceRepository.getAll().size());
		IdentifierSource addedIdentifierSource = identifierSourceRepository.findByIdentifier("Test-1");
		identifierSourceRepository.updateIdSourceWithSequenceValue(addedIdentifierSource, 1234l);
		IdentifierSource updatedIdentifierSource = identifierSourceRepository.findByIdentifier("Test-1");
		assertNotNull(updatedIdentifierSource);
		assertEquals(new Long(1234), updatedIdentifierSource.getSequenceValue());
	}

}
