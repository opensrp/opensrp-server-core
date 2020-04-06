package org.opensrp.domain.postgres;

import java.util.Map;

public class CustomLocation extends Location {
	
	private Map<?, ?> customProperties;
	
	public Map<?, ?> getCustomProperties() {
		return customProperties;
	}
	
	public void setCustomProperties(Map<?, ?> customProperties) {
		this.customProperties = customProperties;
	}
	
}
