package org.opensrp.domain.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opensrp.search.SettingSearchBean;

import java.util.List;

public class SettingConfiguration extends SettingSearchBean {

    private static final long serialVersionUID = 1890883609898207738L;

    @JsonProperty
    private List<Setting> settings;

    @JsonProperty
    private String configurationIdentifier;

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }

    public String getConfigurationIdentifier() {
        return configurationIdentifier;
    }

    public void setConfigurationIdentifier(String configurationIdentifier) {
        this.configurationIdentifier = configurationIdentifier;
    }
}
