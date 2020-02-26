package org.opensrp.domain.viewconfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MainConfiguration extends BaseConfiguration {
	
	@JsonProperty
	private boolean enableJsonViews;
	
	public boolean getEnableJsonViews() {
		return enableJsonViews;
	}
	
	public void setEnableJsonViews(boolean enableJsonViews) {
		this.enableJsonViews = enableJsonViews;
	}
	
}
