package org.opensrp.util;

import org.junit.Test;
import org.opensrp.util.IdentifierValidatorAlgorithm;

import static org.junit.Assert.assertEquals;

public class IdentifierValidatorAlgorithmTest {

    @Test
    public void testGetName() {
        assertEquals("LUHN_CHECK_DIGIT_ALGORITHM", IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM.name());
    }

    @Test
    public void testGet() {
        String algo = "LUHN_CHECK_DIGIT_ALGORITHM";
        assertEquals(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM, IdentifierValidatorAlgorithm.get(algo));
    }

}
