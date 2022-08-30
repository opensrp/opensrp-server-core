package org.opensrp.domain.setting;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.json.JSONArray;
import org.opensrp.search.SettingSearchBean;

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
    private String uuid;

    @JsonProperty
    private String settingsId;

    @JsonProperty
    private String settingIdentifier;

    @JsonProperty()
    private String settingMetadataId;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public JSONArray getValues() {
        return values;
    }

    public void setValues(JSONArray values) {
        this.values = values;
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

    public String getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(String settingsId) {
        this.settingsId = settingsId;
    }

    public String getSettingIdentifier() {
        return settingIdentifier;
    }

    public void setSettingIdentifier(String settingIdentifier) {
        this.settingIdentifier = settingIdentifier;
    }

    public String getSettingMetadataId() {
        return settingMetadataId;
    }

    public void setSettingMetadataId(String settingMetadataId) {
        this.settingMetadataId = settingMetadataId;
    }
}
