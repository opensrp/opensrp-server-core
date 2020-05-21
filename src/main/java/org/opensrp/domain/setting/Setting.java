package org.opensrp.domain.setting;


import org.json.JSONArray;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import org.opensrp.search.SettingSearchBean;

@JsonSubTypes({ @Type(value = SettingConfiguration.class, name = "Setting") })
public class Setting extends SettingSearchBean {
	
	@JsonProperty
	private String key;
	
	@JsonProperty
	private String value;

	@JsonProperty
	private JSONArray values;
	
	@JsonProperty
	private String label;
	
	@JsonProperty
	private String description;
	
	@JsonProperty
	private String inheritedFrom;
	
	@JsonProperty
	private  String uuid;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getDescription() {
		return description;
	}

	public JSONArray getValues() {
		return values;
	}

	public void setValues(JSONArray values) {
		this.values = values;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getInheritedFrom() {
		return inheritedFrom;
	}
	
	public void setInheritedFrom(String inheritedFrom) {
		this.inheritedFrom = inheritedFrom;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
