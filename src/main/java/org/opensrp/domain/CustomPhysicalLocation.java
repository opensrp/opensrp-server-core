package org.opensrp.domain;

import java.util.Map;

public class CustomPhysicalLocation extends PhysicalLocation {
	
	private Map<String, Object> customProperties;
	
	public Map<String, Object> getCustomProperties() {
		return customProperties;
	}
	
	@SuppressWarnings("unchecked")
	public void setCustomProperties(Map<?, ?> map) {
		this.customProperties = (Map<String, Object>) map;
	}
	
}
