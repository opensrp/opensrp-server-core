package org.opensrp.repository.postgres;

import org.junit.Test;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.repository.SettingRepository;
import org.opensrp.search.SettingSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SettingRepositoryTest extends BaseRepositoryTest {

	@Autowired
	@Qualifier("settingRepositoryPostgres")
	private SettingRepository settingRepository;

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<>();
		scripts.add("setting.sql");
		return scripts;
	}

	@Test
	public void testFindAllSettings() {

		List<SettingConfiguration> settings = settingRepository.findAllSettings();
		assertEquals(14, settings.size());

	}

	@Test
	public void testFindByEmptyServerVersion() {

		List<SettingConfiguration> settings = settingRepository.findByEmptyServerVersion();
		assertEquals(3, settings.size());

	}

	@Test
	public void testGetSettingById() {
		Settings setting = settingRepository.getSettingById(1L);
		assertNotNull(setting);
		assertNotNull(setting.getJson());

		SettingConfiguration settingConfiguration = settingRepository.get("151");
		assertNotNull(settingConfiguration);
		assertEquals("global_configs1", settingConfiguration.getIdentifier());
	}

	@Test
	public void testFindByCriteria() {
		SettingSearchBean settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);

		List<SettingConfiguration> settings = settingRepository.findSettings(settingQueryBean, null);
		assertEquals(1, settings.size());
		assertEquals(3, settings.get(0).getSettings().size());

		settingQueryBean.setTeamId("7e104eee-ec8a-4733-bcf7-c02c51cf43f4");
		settingQueryBean.setServerVersion(0L);
		settings = settingRepository.findSettings(settingQueryBean, null);

		assertEquals(1, settings.size());

		settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setLocationId("44de66fb-e6c6-4bae-92bb-386dfe626eba");
		settings = settingRepository.findSettings(settingQueryBean, null);

		assertEquals(1, settings.size());

		settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setProviderId("demo");
		settings = settingRepository.findSettings(settingQueryBean, null);

		assertEquals(2, settings.size());

		settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setProviderId("demo");
		settingQueryBean.setTeamId("7e104eee-ec8a-4733-bcf7-c02c51cf43f4");
		settings = settingRepository.findSettings(settingQueryBean, null);

		assertEquals(1, settings.size());

		settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setTeam("my-team");
		settings = settingRepository.findSettings(settingQueryBean, null);

		assertEquals(5, settings.size());

	}

	@Test
	public void tesFindByCriteriaAndResolveSettings() {
		SettingSearchBean settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);

		List<SettingConfiguration> settings = settingRepository.findSettings(settingQueryBean, null);
		assertEquals(1, settings.size());
		assertEquals(3, settings.get(0).getSettings().size());
	}

	@Test
	public void testGetAllSettings() {
		List<SettingConfiguration> settings = settingRepository.getAll();
		assertEquals(14, settings.size());

	}

	@Test
	public void testGetSettingMetadataByDocumentId() {
		String documentId = "affc7614-a51b-4b5f-877a-ad932b38bf4b";
		SettingsMetadata settingMetadata = settingRepository.getSettingMetadataByDocumentId(documentId);
		assertNotNull(settingMetadata);
		assertEquals(documentId, settingMetadata.getDocumentId());
		assertEquals("lion_king_cast_2", settingMetadata.getIdentifier());
	}

	@Test
	public void testAddAndUpdateShouldAddOrUpdate() {
		SettingConfiguration expectedSettingConfiguration = new SettingConfiguration();
		expectedSettingConfiguration.setTeamId("test_team");
		expectedSettingConfiguration.setTeam("test_team");
		expectedSettingConfiguration.setId("test_id");
		expectedSettingConfiguration.setIdentifier("test_identifier");
		expectedSettingConfiguration.setServerVersion(0L);

		List<Setting> settings = new ArrayList<>();
		Map<String, Setting> settingMap = new HashMap<>();

		// add
		Setting setting = new Setting();
		setting.setKey("key1");
		setting.setValue("value1");
		setting.setDescription("description1");
		settings.add(setting);
		settingMap.put("key1", setting);

		setting = new Setting();
		setting.setKey("key2");
		setting.setValue("value2");
		setting.setDescription("description2");
		settings.add(setting);
		settingMap.put("key2", setting);

		setting = new Setting();
		setting.setKey("key3");
		setting.setValue("value3");
		setting.setDescription("description3");
		settings.add(setting);
		settingMap.put("key3", setting);

		expectedSettingConfiguration.setSettings(settings);
		settingRepository.add(expectedSettingConfiguration);

		SettingConfiguration actualSettingConfiguration = settingRepository.get("test_id");
		assertNotNull(actualSettingConfiguration);
		assertEquals(3, actualSettingConfiguration.getSettings().size());
		assertEquals(expectedSettingConfiguration.getTeam(), actualSettingConfiguration.getTeam());
		assertEquals(expectedSettingConfiguration.getId(), actualSettingConfiguration.getId());
		assertEquals(expectedSettingConfiguration.getIdentifier(), actualSettingConfiguration.getIdentifier());
		verifySettingsAreSame(settingMap, actualSettingConfiguration.getSettings());

		// update
		settings.clear();
		expectedSettingConfiguration = new SettingConfiguration();
		expectedSettingConfiguration.setTeamId("test_team_40");
		expectedSettingConfiguration.setTeam("test_team");
		expectedSettingConfiguration.setId("test_id");
		expectedSettingConfiguration.setIdentifier("test_identifier_40");
		expectedSettingConfiguration.setServerVersion(0L);

		settingMap.clear();
		setting.setKey("key1");
		setting.setValue("value10");
		setting.setDescription("description10");
		settings.add(setting);
		settingMap.put("key1", setting);

		setting = new Setting();
		setting.setKey("key2");
		setting.setValue("value20");
		setting.setDescription("description20");
		settings.add(setting);
		settingMap.put("key2", setting);

		setting = new Setting();
		setting.setKey("key3");
		setting.setValue("value30");
		setting.setDescription("description30");
		settings.add(setting);
		settingMap.put("key3", setting);
		expectedSettingConfiguration.setSettings(settings);

		settingRepository.update(expectedSettingConfiguration);
		actualSettingConfiguration = settingRepository.get("test_id");
		assertNotNull(actualSettingConfiguration);
		assertEquals(3, actualSettingConfiguration.getSettings().size());
		assertEquals(expectedSettingConfiguration.getTeam(), actualSettingConfiguration.getTeam());
		assertEquals(expectedSettingConfiguration.getId(), actualSettingConfiguration.getId());
		assertEquals(expectedSettingConfiguration.getTeamId(), "test_team_40");
		assertEquals(expectedSettingConfiguration.getIdentifier(), "test_identifier_40");
		verifySettingsAreSame(settingMap, expectedSettingConfiguration.getSettings());
	}

	@Test
	public void testGetAllSettingMetadataByDocumentIdShouldGetAllMetadata() {
		List<SettingsMetadata> settingsMetadataList = settingRepository.getAllSettingMetadataByDocumentId("151");
		assertEquals(3, settingsMetadataList.size());
		Set<String> identifiers = new HashSet<>();
		identifiers.add("global_configs1");
		identifiers.add("global_configs12");
		identifiers.add("global_configs13");
		for (SettingsMetadata settingsMetadata : settingsMetadataList) {
			assertTrue(identifiers.contains(settingsMetadata.getIdentifier()));
		}
	}

	@Test
	public void testAddSettingShouldAddSetting() {
		Setting setting = new Setting();
		setting.setDescription("description");
		setting.setLocationId("location_id");
		setting.setTeam("team");
		setting.setTeamId("team_id");
		setting.setProviderId("provider_id");
		setting.setDocumentId("document_id_32932");
		setting.setKey("key_32932");
		setting.setValue("value");
		setting.setInheritedFrom("location_id_2");
		setting.setIdentifier("setting_identifier_32932");
		setting.setServerVersion(0L);
		settingRepository.addOrUpdate(setting);

		Map<String, Setting> expectedSettings = new HashMap<>();
		expectedSettings.put("key_32932", setting);

		SettingSearchBean settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setIdentifier("setting_identifier_32932");
		List<SettingConfiguration> settings = settingRepository.findSettings(settingQueryBean, null);
		List<Setting> actualSettings = settings.get(0).getSettings();
		assertNotNull(actualSettings);
		verifySettingsAreSame(expectedSettings, actualSettings);
	}

	private void verifySettingsAreSame(Map<String, Setting> settingMap, List<Setting> settings) {
		for (Setting actualSetting : settings) {
			Setting expectedSetting = settingMap.get(actualSetting.getKey());
			assertEquals(expectedSetting.getKey(), actualSetting.getKey());
			assertEquals(expectedSetting.getDescription(), actualSetting.getDescription());
			assertEquals(expectedSetting.getValue(), actualSetting.getValue());
		}
	}
}
