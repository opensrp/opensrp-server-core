package org.opensrp.domain;

import com.google.gson.annotations.SerializedName;

public class TaskUpdate {

	@SerializedName("identifier")
	private String identifier;

	@SerializedName("status")
	private String status;

	@SerializedName("businessStatus")
	private String businessStatus;

	@SerializedName("serverVersion")
	private Long serverVersion;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBusinessStatus() {
		return businessStatus;
	}

	public void setBusinessStatus(String businessStatus) {
		this.businessStatus = businessStatus;
	}

	public Long getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}

}
