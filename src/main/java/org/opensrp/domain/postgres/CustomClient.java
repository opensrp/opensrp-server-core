package org.opensrp.domain.postgres;

import java.util.Map;

public class CustomClient extends Client {
	
	private Map<?, ?> dynamicProperties;
	
	public Map<?, ?> getDynamicProperties() {
		return dynamicProperties;
	}
	
	public void setDynamicProperties(Map<?, ?> dynamicProperties) {
		this.dynamicProperties = dynamicProperties;
	}
	
}
