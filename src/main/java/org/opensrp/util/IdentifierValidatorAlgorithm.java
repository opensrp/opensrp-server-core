package org.opensrp.util;

import java.util.HashMap;
import java.util.Map;

public enum IdentifierValidatorAlgorithm {
	
	LUHN_CHECK_DIGIT_ALGORITHM("Luhn Check Digit Algorithm");
	
	private String algorithm;

	private static final Map<String, IdentifierValidatorAlgorithm> lookup = new HashMap<String, IdentifierValidatorAlgorithm>();

	static {
		for (IdentifierValidatorAlgorithm algo : IdentifierValidatorAlgorithm.values()) {
			lookup.put(algo.getAlgorithm(), algo);
		}
	}

	IdentifierValidatorAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public static IdentifierValidatorAlgorithm get(String algorithm) {
		return lookup.get(algorithm);
	}
}
