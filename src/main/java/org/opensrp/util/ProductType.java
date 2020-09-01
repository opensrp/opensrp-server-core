package org.opensrp.util;

import java.util.HashMap;
import java.util.Map;

public enum ProductType {

	EQUIPMENT,
	CONSUMEABLE;

	private static final Map<String, ProductType> lookup = new HashMap<String, ProductType>();

	static {
		for (ProductType productType : ProductType.values()) {
			lookup.put(productType.name(), productType);
		}
	}

	public static ProductType get(String type) {
		return lookup.get(type);
	}
}
