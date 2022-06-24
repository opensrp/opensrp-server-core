package org.opensrp.util;

import java.util.HashMap;
import java.util.Map;

public enum IdentifierValidatorAlgorithm {

    LUHN_CHECK_DIGIT_ALGORITHM;

    private static final Map<String, IdentifierValidatorAlgorithm> lookup = new HashMap<String, IdentifierValidatorAlgorithm>();

    static {
        for (IdentifierValidatorAlgorithm algo : IdentifierValidatorAlgorithm.values()) {
            lookup.put(algo.name(), algo);
        }
    }

    public static IdentifierValidatorAlgorithm get(String algorithm) {
        return lookup.get(algorithm);
    }
}
