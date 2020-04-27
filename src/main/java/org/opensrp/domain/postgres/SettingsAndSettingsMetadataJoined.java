package org.opensrp.domain.postgres;

/**
 * Created by Vincent Karuri on 24/04/2020
 */
public class SettingsAndSettingsMetadataJoined {
	private Settings settings;
	private SettingsMetadata settingsMetadata;

	public Settings getSettings() {
		return settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
	}

	public SettingsMetadata getSettingsMetadata() {
		return settingsMetadata;
	}

	public void setSettingsMetadata(SettingsMetadata settingsMetadata) {
		this.settingsMetadata = settingsMetadata;
	}
}
