package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.repository.IdentifierSourceRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.opensrp.util.IdentifierValidatorAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashSet;
import java.util.Set;


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
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setBaseCharacterSet("AB12");
        identifierSource.setMinLength(4);
        identifierSource.setMaxLength(5);
        identifierSource.setIdentifier("Test-Id-Source");
        identifierSourceService.add(identifierSource);
        assertEquals(2, identifierSourceService.findAllIdentifierSources().size());  // 1 added through script
        IdentifierSource savedIdentifierSource = identifierSourceService.findByIdentifier("Test-Id-Source");
        assertNotNull(savedIdentifierSource.getId());
    }

    @Test
    public void testFindByIdentifier() {
        IdentifierSource savedIdentifierSource = identifierSourceService.findByIdentifier("Test Identifier");
        assertNotNull(savedIdentifierSource.getId());
        assertEquals(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM, savedIdentifierSource.getIdentifierValidatorAlgorithm());
    }

    @Test
    public void testUpdate() {
        IdentifierSource updatedIdentifierSource = identifierSourceService.findByIdentifier("Test Identifier");
        updatedIdentifierSource.setDescription("Test Updated");
        identifierSourceService.update(updatedIdentifierSource);
        IdentifierSource idSource = identifierSourceService.findByIdentifier("Test Identifier");
        assertNotNull(idSource.getId());
        assertEquals(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM.name(), idSource.getIdentifierValidatorAlgorithm().name());
        assertEquals("Test Updated", idSource.getDescription());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddIdentifierSourceWithBlankIdentifier() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setBaseCharacterSet("AB12");
        identifierSource.setMinLength(4);
        identifierSource.setMaxLength(5);
        identifierSource.setIdentifier("");
        identifierSourceService.add(identifierSource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddIdentifierSourceWithEmptyBaseCharacterSet() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setBaseCharacterSet("");
        identifierSource.setMinLength(4);
        identifierSource.setMaxLength(5);
        identifierSource.setIdentifier("Test-1");
        identifierSourceService.add(identifierSource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddIdentifierSourceWithEmptyMinLength() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setBaseCharacterSet("AB12");
        identifierSource.setMaxLength(5);
        identifierSource.setIdentifier("Test-1");
        identifierSourceService.add(identifierSource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddIdentifierSourceWithEmptyMaxLength() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setBaseCharacterSet("AB12");
        identifierSource.setMinLength(4);
        identifierSource.setIdentifier("Test-1");
        identifierSourceService.add(identifierSource);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddIdentifierSourceWithInvalidMinLength() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setBaseCharacterSet("AB12");
        identifierSource.setMinLength(17);
        identifierSource.setMaxLength(10);
        identifierSource.setIdentifier("Test-1");
        identifierSourceService.add(identifierSource);
    }

    @Test
    public void testSaveSequenceValue() {
        IdentifierSource identifierSource = identifierSourceService.findByIdentifier("Test Identifier");
        identifierSourceService.saveSequenceValue(identifierSource, 1234l);
        IdentifierSource updatedIdentifierSource = identifierSourceService.findByIdentifier("Test Identifier");
        assertEquals(new Long(1234), updatedIdentifierSource.getSequenceValue());
    }

}
