package org.opensrp.domain.setting;

import java.util.List;

import org.opensrp.domain.BaseDataObject;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opensrp.search.SettingSearchBean;

public class SettingConfiguration extends SettingSearchBean {
	
	private static final long serialVersionUID = 1890883609898207738L;
	 
	private String documentId;
	
	@JsonProperty
	private String version; 

	@JsonProperty
	private List<Setting> settings;

	public String getVersion() {
		return version;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public List<Setting> getSettings() {
		return settings;
	}
	
	public void setSettings(List<Setting> settings) {
		this.settings = settings;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
}
