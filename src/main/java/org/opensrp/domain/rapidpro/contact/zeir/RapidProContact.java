package org.opensrp.domain.rapidpro.contact.zeir;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class RapidProContact implements Serializable {

	@JsonProperty("uuid")
	private String uuid;

	@JsonProperty("name")
	private String name;

	@JsonProperty("language")
	private String language;

	@JsonProperty("urns")
	private List<String> urns;

	@JsonProperty("groups")
	private List<RapidProGroup> rapidProGroups;

	@JsonProperty("fields")
	private RapidProFields rapidProFields;

	@JsonProperty("blocked")
	private boolean blocked;

	@JsonProperty("stopped")
	private boolean stopped;

	@JsonProperty("created_on")
	private String createdOn;

	@JsonProperty("modified_on")
	private String modifiedOn;

	@JsonProperty("last_seen_on")
	private String lastSeenOn;

	public String getUuid() {
		return this.uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<String> getUrns() {
		return this.urns;
	}

	public void setUrns(List<String> urns) {
		this.urns = urns;
	}

	public List<RapidProGroup> getRapidProGroups() {
		return this.rapidProGroups;
	}

	public void setRapidProGroups(List<RapidProGroup> rapidProGroups) {
		this.rapidProGroups = rapidProGroups;
	}

	public RapidProFields getFields() {
		return this.rapidProFields;
	}

	public void setFields(RapidProFields fields) {
		this.rapidProFields = fields;
	}

	public boolean getBlocked() {
		return this.blocked;
	}

	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}

	public boolean getStopped() {
		return this.stopped;
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public String getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(String createdOn) {
		this.createdOn = createdOn;
	}

	public String getModifiedOn() {
		return this.modifiedOn;
	}

	public void setModifiedOn(String modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public String getLastSeenOn() {
		return this.lastSeenOn;
	}

	public void setLastSeenOn(String lastSeenOn) {
		this.lastSeenOn = lastSeenOn;
	}
}
