package org.opensrp.util;

import java.util.HashMap;
import java.util.Map;

public enum UNICEFSection {

	HEALTH("Health"),
	WASH("WASH"),
	NUTRITION("Nutrition"),
	EDUCATION("Education"),
	CHILD_PROTECTION("Child Protection"),
	SOCIAL_POLICY("Social Policy"),
	C4D("C4D"),
	DRR("DRR");

	String value;
	private static final Map<String, UNICEFSection> lookup = new HashMap<>();


	UNICEFSection(String value) {
		this.value = value;
	}

	static {
		for (UNICEFSection unicefSection : UNICEFSection.values()) {
			lookup.put(unicefSection.value, unicefSection);
		}
	}

	/**
	 * Given a string, look it up in our enum map and check if it exists.
	 * @param section String that we are looking for.
	 * @return True if the string is found.
	 */
	public static boolean containsString(String section) {
		return lookup.get(section) != null;
	}


}
