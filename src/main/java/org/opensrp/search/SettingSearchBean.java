
package org.opensrp.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opensrp.domain.BaseDataObject;

public class SettingSearchBean extends BaseDataObject {

	@JsonProperty
	private String identifier;

	@JsonProperty
	private String teamId;

	@JsonProperty
	private String team;

	@JsonProperty
	private String providerId;

	@JsonProperty
	private String locationId;

	@JsonProperty
	private String childLocationId;

	private String documentId;

	private Long serverVersion;
	
	public String getProviderId() {
		return providerId;
	}
	
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	public String getLocationId() {
		return locationId;
	}
	
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	public String getTeam() {
		return team;
	}
	
	public void setTeam(String team) {
		this.team = team;
	}
	
	public String getTeamId() {
		return teamId;
	}
	
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}
	
	public Long getServerVersion() {
		return serverVersion;
	}
	
	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getChildLocationId() {
		return childLocationId;
	}

	public void setChildLocationId(String childLocationId) {
		this.childLocationId = childLocationId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
}
