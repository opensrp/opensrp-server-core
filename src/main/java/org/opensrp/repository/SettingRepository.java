package org.opensrp.repository;

import org.opensrp.api.domain.Location;
import org.opensrp.api.util.TreeNode;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.search.SettingSearchBean;

import java.util.List;
import java.util.Map;

public interface SettingRepository extends BaseRepository<SettingConfiguration> {

	List<SettingConfiguration> findAllSettings();
	
	List<SettingConfiguration> findSettings(SettingSearchBean settingQueryBean,
			Map<String, TreeNode<String, Location>> treeNodeHashMap );
	
	List<SettingConfiguration> findByEmptyServerVersion();
	
	SettingsMetadata getSettingMetadataByDocumentId(String documentId);

	SettingConfiguration findSetting(SettingSearchBean settingQueryBean,Map<String, TreeNode<String, Location>> treeNodeHashMap );

	List<SettingsMetadata> getAllSettingMetadataByDocumentId(String documentId);
	
	Settings getSettingById(Long id);

	void addOrUpdate(Setting entity);
	
	void delete(Long settingId);
}
