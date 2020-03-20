package org.opensrp.repository;

import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.search.SettingSearchBean;

import java.util.List;

public interface SettingRepository extends BaseRepository<SettingConfiguration> {
	
	List<SettingConfiguration> findAllSettings();
	
	List<SettingConfiguration> findSettings(SettingSearchBean settingQueryBean);
	
	List<SettingConfiguration> findByEmptyServerVersion();
	
	SettingsMetadata getSettingMetadataByDocumentId(String documentId);
	
	Settings getSettingById(Long id);
	
}
