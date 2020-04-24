package org.opensrp.domain.postgres;

import java.util.List;

/**
 * Created by Vincent Karuri on 24/04/2020
 */
public class SettingsAndSettingsMetadataJoined {
	private Settings settings;
	private List<SettingsMetadata> settingsMetadata;

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public List<SettingsMetadata> getSettingsMetadata() {
		return settingsMetadata;
	}

	public void setSettingsMetadata(List<SettingsMetadata> settingsMetadata) {
		this.settingsMetadata = settingsMetadata;
	}
}
