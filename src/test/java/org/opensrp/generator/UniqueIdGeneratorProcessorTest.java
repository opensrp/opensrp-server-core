package org.opensrp.generator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opensrp.repository.UniqueIdRepository;
import org.opensrp.domain.UniqueId;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.service.IdentifierSourceService;
import org.opensrp.util.IdentifierValidatorAlgorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UniqueIdGeneratorProcessorTest {

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    @Mock
    private IdentifierSourceService identifierSourceService;

    private UniqueIdGeneratorProcessor uniqueIdGeneratorProcessor;

    @Before
    public void setUp() {
        initMocks(this);
        uniqueIdGeneratorProcessor = new UniqueIdGeneratorProcessor(uniqueIdRepository, identifierSourceService);
    }

    @Test
    public void testGetIdentifiers() {
        Set<String> reservedIds = new HashSet<>();
        when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(createUniqueId());
        when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
        doNothing().when(identifierSourceService).saveSequenceValue(any(IdentifierSource.class), anyLong());
        Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
        List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSource(), 5, "");
        assertTrue(ids.get(0).contains("-")); //Ensure check digit implementation
    }

    @Test
    public void testGetIdentifiersWithFirstIdentifierBase() {
        Set<String> reservedIds = new HashSet<>();
        when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(null);
        when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
        doNothing().when(identifierSourceService).saveSequenceValue(any(IdentifierSource.class), anyLong());
        Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
        List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceV2(), 5, "");
        assertTrue(ids.get(0).contains("-")); //Ensure check digit implementation
        assertTrue(ids.get(0).contains("AA11")); //Ensure first identifier base is returned as it is
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetIdentifiersWithInvalidFirstIdentifierBase() {
        Set<String> reservedIds = new HashSet<>();
        when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(null);
        when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
        doNothing().when(identifierSourceService).saveSequenceValue(any(IdentifierSource.class), anyLong());
        Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
        uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceV3(), 5, "");
    }

    @Test
    public void testGetIdentifiersWithIdSourceA() {
        Set<String> reservedIds = new HashSet<>();
        when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(createUniqueId());
        when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
        doNothing().when(identifierSourceService).saveSequenceValue(any(IdentifierSource.class), anyLong());
        Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
        List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceA(), 5, "");
        assertEquals(ids.size(), 5);
    }

    @Test
    public void testGetIdentifiersWithIdSourceB() {
        Set<String> reservedIds = new HashSet<>();
        when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(createUniqueId());
        when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
        doNothing().when(identifierSourceService).saveSequenceValue(any(IdentifierSource.class), anyLong());
        Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
        List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceB(), 5, "");
        assertEquals(ids.size(), 5);
    }

    @Test
    public void testGetIdentifiersWithIdSourceC() {
        Set<String> reservedIds = new HashSet<>();
        when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(createUniqueId());
        when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
        doNothing().when(identifierSourceService).saveSequenceValue(any(IdentifierSource.class), anyLong());
        Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
        List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceC(), 5, "");
        for (String id : ids) {
            assertTrue(id.matches("^((?!404).)*$"));
        }
    }

    @Test
    public void testGetIdentifiersWithIdSourceD() {
        Set<String> reservedIds = new HashSet<>();
        when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(createUniqueId());
        when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
        doNothing().when(identifierSourceService).saveSequenceValue(any(IdentifierSource.class), anyLong());
        Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
        List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceD(), 5, "");
        assertEquals(ids.size(), 5);
    }

    @Test
    public void testGetIdentifiersWithIdSourceF() {
        Set<String> reservedIds = new HashSet<>();
        when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(createUniqueId());
        when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
        doNothing().when(identifierSourceService).saveSequenceValue(any(IdentifierSource.class), anyLong());
        Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
        List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceF(), 5, "");
        assertEquals(ids.size(), 5);
    }

    @Test
    public void testGetIdentifiersWithIdSourceI() {
        Set<String> reservedIds = new HashSet<>();
        when(uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(anyLong())).thenReturn(createUniqueId());
        when(uniqueIdRepository.findReservedIdentifiers()).thenReturn(reservedIds);
        doNothing().when(identifierSourceService).saveSequenceValue(any(IdentifierSource.class), anyLong());
        Mockito.doNothing().when(uniqueIdRepository).add(any(UniqueId.class));
        List<String> ids = uniqueIdGeneratorProcessor.getIdentifiers(createIdentifierSourceI(), 5, "");
        for (String id : ids) {
            assertTrue(id.matches("[\\w-]*"));
        }
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

    private IdentifierSource createIdentifierSourceA() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setId(1l);
        identifierSource.setIdentifier("SOURCE-TEST-A");
        identifierSource.setBaseCharacterSet("0123456789");
        identifierSource.setMinLength(8);
        identifierSource.setMaxLength(10);
        identifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
        identifierSource.setPrefix("");
        identifierSource.setRegexFormat("");
        return identifierSource;
    }

    private IdentifierSource createIdentifierSourceB() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setId(1l);
        identifierSource.setIdentifier("SOURCE-TEST-B");
        identifierSource.setBaseCharacterSet("0123456789");
        identifierSource.setMinLength(8);
        identifierSource.setMaxLength(10);
        identifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
        identifierSource.setPrefix("DEPT");
        identifierSource.setRegexFormat("");
        return identifierSource;
    }

    private IdentifierSource createIdentifierSourceC() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setId(1l);
        identifierSource.setIdentifier("SOURCE-TEST-C");
        identifierSource.setBaseCharacterSet("0123456789");
        identifierSource.setMinLength(8);
        identifierSource.setMaxLength(10);
        identifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
        identifierSource.setPrefix("");
        identifierSource.setRegexFormat("^((?!404).)*$");
        return identifierSource;
    }

    private IdentifierSource createIdentifierSourceD() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setId(1l);
        identifierSource.setIdentifier("SOURCE-TEST-D");
        identifierSource.setBaseCharacterSet("0123456789");
        identifierSource.setMinLength(4);
        identifierSource.setMaxLength(5);
        identifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
        identifierSource.setPrefix("");
        identifierSource.setRegexFormat("");
        return identifierSource;
    }

    private IdentifierSource createIdentifierSourceF() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setId(1l);
        identifierSource.setIdentifier("SOURCE-TEST-F");
        identifierSource.setBaseCharacterSet("01234ABCDE");
        identifierSource.setMinLength(5);
        identifierSource.setMaxLength(8);
        identifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
        identifierSource.setPrefix("");
        identifierSource.setRegexFormat("");
        return identifierSource;
    }

    private IdentifierSource createIdentifierSourceI() {
        IdentifierSource identifierSource = new IdentifierSource();
        identifierSource.setId(1l);
        identifierSource.setIdentifier("SOURCE-TEST-I");
        identifierSource.setBaseCharacterSet("0123459789abcdefghij");
        identifierSource.setMinLength(4);
        identifierSource.setMaxLength(7);
        identifierSource.setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM);
        identifierSource.setPrefix("ICU");
        identifierSource.setRegexFormat("[\\w-]*");
        return identifierSource;
    }


}
