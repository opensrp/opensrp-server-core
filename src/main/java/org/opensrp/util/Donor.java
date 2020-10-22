package org.opensrp.util;

import java.util.HashMap;
import java.util.Map;

public enum Donor {

	ADB("ADB"),
	NATCOM_BELGIUM("NatCom Belgium"),
	BMGF("BMGF"),
	GOVT_OF_CANADA("Govt of Canada"),
	NATCOM_CANADA("NatCom Canada"),
	NATCOM_DENMARK("NatCom Denmark"),
	ECW("ECW"),
	END_VIOLENCE_FUN("End Violence Fund"),
	ECHO("ECHO"),
	EC("EC"),
	NATCOM_FINLAN("NatCom Finland"),
	GOVT_OF_FRANCE("Govt of France"),
	NETCOM_FRANCE("NatCom France"),
	GAVI("GAVI"),
	NATCOM_GERMANY("NatCom Germany"),
	GOVT_OF_GERMANY("Govt of Germany"),
	NATCOM_ICELAND("NatCom Iceland"),
	NATCOM_ITALY("NatCom Italy"),
	GOVT_OF_JAPAN("Govt of Japan"),
	NATCOM_JAPAN("NatCom Japan"),
	NATCOM_LUXEMBOURG("NatCom Luxembourg"),
	MONACO("Monaco"),
	NATCOM_NETHERLANDS("NatCom Netherlands"),
	GOVT_OF_NORWAY("Govt of Norway"),
	NATCOM_NORWAY("NatCom Norway"),
	NUTRITION_INTL("Nutrition Intl"),
	NATCOM_POLAND("NatCom Poland"),
	GOVT_OF_KOREA("Govt of Korea"),
	NATCOM_SPAIN("NatCom Spain"),
	NATCOM_SWEDEN("NatCom Sweden"),
	NATCOM_SWITZERLAND("NatCom Switzerland"),
	GOVT_OF_UK("Govt of UK"),
	NATCOM_UK("NatCom UK"),
	NATCOM_USA("NatCom USA"),
	OFDA("OFDA"),
	CDC("CDC"),
	USAID("USAID"),
	USAID_FFP("USAID FFP"),
	WORLD_BANK("World Bank");

	String value;

	private static final Map<String, Donor> lookup = new HashMap<>();

	Donor(String value) {
		this.value = value;
	}

	static {
		for (Donor donor : Donor.values()) {
			lookup.put(donor.value, donor);
		}
	}

	public static boolean containsString(String section) {
		return lookup.get(section) != null;
	}

}

