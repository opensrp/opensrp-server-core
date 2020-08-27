package org.opensrp.repository.postgres;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.repository.SettingRepository;
import org.opensrp.repository.postgres.handler.SettingTypeHandler;
import org.opensrp.search.SettingSearchBean;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
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
		settingRepository.addSettings(expectedSettingConfiguration);

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

		Setting setting1 = new Setting();
		setting1.setKey("key4");
		setting1.setValue("value40");
		setting1.setDescription("description40");
		settings.add(setting1);
		settingMap.put("key4", setting1);

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
		setting.setSettingsId("document_id_32932");
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

	@Test
	public void testAddGlobalSettingShouldAddSetting() {
		Setting setting = new Setting();
		setting.setDescription("description");
		setting.setDocumentId("document_id_32932");
		setting.setKey("key_32932");
		setting.setValue("value");
		setting.setLabel("label");
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

	@Test
	public void testSaveGlobalSettingsUsingV1endpoint() throws IOException {
		String popCharacteristicsGlobal = "{\"identifier\":\"population_characteristics\",\"settings\":[{\"description\":\"The proportion of women in the adult population (18 years or older), with a BMI less than 18.5, is 20% or higher.\",\"label\":\"Undernourished prevalence 20% or higher\",\"value\":\"false\",\"key\":\"pop_undernourish\"},{\"description\":\"The proportion of pregnant women in the population with anaemia (haemoglobin level less than 11 g/dl) is 40% or higher.\",\"label\":\"Anaemia prevalence 40% or higher\",\"value\":\"false\",\"key\":\"pop_anaemia_40\"}],\"type\":\"SettingConfiguration\"}";
		String testGlobalsSaveV1 = "{\"identifier\":\"test_globals_save_v1\",\"settings\":[{\"description\":\"Is "
				+ "an ultrasound machine available and functional at your facility and a trained health worker available to use it?\",\"label\":\"Ultrasound available\",\"type\":\"SettingConfiguration\",\"value\":false,\"key\":\"site_ultrasound\"},{\"description\":\"Does your facility use an automated blood pressure (BP) measurement tool?\",\"label\":\"Automated BP measurement tool\",\"type\":\"SettingConfiguration\",\"value\":false,\"key\":\"site_bp_tool\"}],\"type\":\"SettingConfiguration\"}";
		FileInputStream fis = new FileInputStream("src/test/resources/settings.json");
		String largeSettingPayload = IOUtils.toString(fis, StandardCharsets.UTF_8);

		SettingTypeHandler settingTypeHandler = new SettingTypeHandler();
		SettingConfiguration popCharacteristicsSettingConfig = settingTypeHandler.mapper
				.readValue(popCharacteristicsGlobal, SettingConfiguration.class);
		SettingConfiguration siteCharacteristicsSettingConfig = settingTypeHandler.mapper
				.readValue(testGlobalsSaveV1, SettingConfiguration.class);
		SettingConfiguration largeSettingPayloadSettingConfig = settingTypeHandler.mapper
				.readValue(largeSettingPayload, SettingConfiguration.class);

		popCharacteristicsSettingConfig.setServerVersion(Calendar.getInstance().getTimeInMillis());
		popCharacteristicsSettingConfig.setV1Settings(true);

		siteCharacteristicsSettingConfig.setServerVersion(Calendar.getInstance().getTimeInMillis());
		siteCharacteristicsSettingConfig.setV1Settings(true);

		largeSettingPayloadSettingConfig.setServerVersion(Calendar.getInstance().getTimeInMillis());
		largeSettingPayloadSettingConfig.setV1Settings(true);

		settingRepository.addSettings(popCharacteristicsSettingConfig);
		settingRepository.addSettings(siteCharacteristicsSettingConfig);
		settingRepository.addSettings(largeSettingPayloadSettingConfig);

		SettingSearchBean settingQueryBeanTwo = new SettingSearchBean();
		settingQueryBeanTwo.setServerVersion(0L);
		List<SettingConfiguration> allGlobalSettings = settingRepository.findSettings(settingQueryBeanTwo, null);
		assertEquals(3, allGlobalSettings.size());

		String textGlobalSaveV1Update = "{\"type\":\"SettingConfiguration\",\"serverVersion\":1597999833442,\"identifier\":\"test_globals_save_v1\",\"settings\":[{\"type\":\"SettingConfiguration\",\"serverVersion\":1597999833442,\"documentId\":\"ff3efba8-cda1-4f88-a271-96afb1d4fd63\",\"key\":\"site_ultrasound\",\"value\":\"false\",\"label\":\"Ultrasound available\",\"description\":\"Is an ultrasound machine available and functional at your facility and a trained health worker available to use it?\",\"uuid\":\"e01cce9e-02cd-4443-880c-09d483597cca\",\"settingsId\":\"16\",\"settingIdentifier\":\"test_globals_save_v1\",\"settingMetadataId\":\"19\"},{\"type\":\"SettingConfiguration\",\"serverVersion\":1597999833442,\"documentId\":\"ff3efba8-cda1-4f88-a271-96afb1d4fd63\",\"key\":\"site_bp_tool\",\"value\":\"true\",\"label\":\"Automated BP measurement tool\",\"description\":\"Does your facility use an automated blood pressure (BP) measurement tool?\",\"uuid\":\"3298a9c0-57f3-41e7-ba5d-320274443db4\",\"settingsId\":\"16\",\"settingIdentifier\":\"test_globals_save_v1\",\"settingMetadataId\":\"20\"},{\"key\":\"site_bp_tool_update\",\"value\":\"true\",\"label\":\"Automated BP measurement tool\",\"description\":\"Does your facility use an automated blood pressure (BP) measurement tool?\"}],\"_rev\":\"v1\"}";
		SettingConfiguration testUpdateSettings = settingTypeHandler.mapper
				.readValue(textGlobalSaveV1Update, SettingConfiguration.class);
		testUpdateSettings.setServerVersion(Calendar.getInstance().getTimeInMillis());
		testUpdateSettings.setV1Settings(true);
		testUpdateSettings.setId(allGlobalSettings.get(0).getId());

		settingRepository.update(testUpdateSettings);

		SettingSearchBean settingQueryBeanThree = new SettingSearchBean();
		settingQueryBeanThree.setServerVersion(0L);
		List<SettingConfiguration> allGlobalSettingsTwo = settingRepository.findSettings(settingQueryBeanThree, null);
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(allGlobalSettingsTwo.get(0));
		assertEquals(3, allGlobalSettingsTwo.size());
	}

	private void verifySettingsAreSame(Map<String, Setting> settingMap, List<Setting> settings) {
		for (Setting actualSetting : settings) {
			Setting expectedSetting = settingMap.get(actualSetting.getKey());
			assertEquals(expectedSetting.getKey(), actualSetting.getKey());
			assertEquals(expectedSetting.getDescription(), actualSetting.getDescription());
			assertEquals(expectedSetting.getValue(), actualSetting.getValue());
		}
	}

	@Test
	public void testReformattedLocationHierarchyWithChildLocationId() throws Exception {
		LocationTree locationTree = new Gson().fromJson(
				"{\"locationsHierarchy\":{\"map\":{\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":{\"id\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"label\":\"Uganda\",\"node\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"tags\":[\"Country\"],\"voided\":false},\"children\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":{\"id\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"label\":\"Kampala\",\"node\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false},\"tags\":[\"District\"],\"voided\":false},\"children\":{\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":{\"id\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"label\":\"KCCA\",\"node\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false},\"voided\":false},\"tags\":[\"County\"],\"voided\":false},\"children\":{\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":{\"id\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"label\":\"Central Division\",\"node\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"voided\":false},\"voided\":false},\"tags\":[\"Sub-county\"],\"voided\":false},\"children\":{\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":{\"id\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"label\":\"Bukesa Urban Health Centre\",\"node\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"voided\":false},\"voided\":false},\"tags\":[\"Health Facility\"],\"voided\":false},\"children\":{\"982eb3f3-b7e3-450f-a38e-d067f2345212\":{\"id\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"label\":\"Jambula Girls School\",\"node\":{\"locationId\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"name\":\"Jambula Girls School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false},\"voided\":false},\"tags\":[\"School\"],\"voided\":false},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"},\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\":{\"id\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"label\":\"Nsalo Secondary School\",\"node\":{\"locationId\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"name\":\"Nsalo Secondary School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false},\"voided\":false},\"tags\":[\"School\"],\"voided\":false},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"}},\"parent\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"}},\"parent\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"}},\"parent\":\"8340315f-48e4-4768-a1ce-414532b4c49b\"}},\"parent\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\"}}}},\"parentChildren\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":[\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"],\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":[\"8340315f-48e4-4768-a1ce-414532b4c49b\"],\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":[\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"],\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":[\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"],\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":[\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"]}}}",
				LocationTree.class);
		Map<String, TreeNode<String, Location>> treeNodeHashMap = new HashMap<>();
		if (locationTree != null) {
			treeNodeHashMap = locationTree.getLocationsHierarchy();
		}

		assertNotNull(treeNodeHashMap);
		SettingRepositoryImpl settingRepository = new SettingRepositoryImpl();

		Whitebox.setInternalState(settingRepository, "locationUuid", "44de66fb-e6c6-4bae-92bb-386dfe626eba");
		Whitebox.invokeMethod(settingRepository, "reformattedLocationHierarchy", treeNodeHashMap);
		List<String> reformattedLocationHierarchy = settingRepository.getReformattedLocationHierarchy();
		assertNotNull(reformattedLocationHierarchy);
		assertEquals(5, reformattedLocationHierarchy.size());
	}

	@Test
	public void testReformattedLocationHierarchyWithParentLocationId() throws Exception {
		LocationTree locationTree = new Gson().fromJson(
				"{\"locationsHierarchy\":{\"map\":{\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":{\"id\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"label\":\"Uganda\",\"node\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"tags\":[\"Country\"],\"voided\":false},\"children\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":{\"id\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"label\":\"Kampala\",\"node\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false},\"tags\":[\"District\"],\"voided\":false},\"children\":{\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":{\"id\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"label\":\"KCCA\",\"node\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false},\"voided\":false},\"tags\":[\"County\"],\"voided\":false},\"children\":{\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":{\"id\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"label\":\"Central Division\",\"node\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"voided\":false},\"voided\":false},\"tags\":[\"Sub-county\"],\"voided\":false},\"children\":{\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":{\"id\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"label\":\"Bukesa Urban Health Centre\",\"node\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"voided\":false},\"voided\":false},\"tags\":[\"Health Facility\"],\"voided\":false},\"children\":{\"982eb3f3-b7e3-450f-a38e-d067f2345212\":{\"id\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"label\":\"Jambula Girls School\",\"node\":{\"locationId\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"name\":\"Jambula Girls School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false},\"voided\":false},\"tags\":[\"School\"],\"voided\":false},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"},\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\":{\"id\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"label\":\"Nsalo Secondary School\",\"node\":{\"locationId\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"name\":\"Nsalo Secondary School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false},\"voided\":false},\"tags\":[\"School\"],\"voided\":false},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"}},\"parent\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"}},\"parent\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"}},\"parent\":\"8340315f-48e4-4768-a1ce-414532b4c49b\"}},\"parent\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\"}}}},\"parentChildren\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":[\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"],\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":[\"8340315f-48e4-4768-a1ce-414532b4c49b\"],\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":[\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"],\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":[\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"],\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":[\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"]}}}",
				LocationTree.class);
		Map<String, TreeNode<String, Location>> treeNodeHashMap = new HashMap<>();
		if (locationTree != null) {
			treeNodeHashMap = locationTree.getLocationsHierarchy();
		}

		assertNotNull(treeNodeHashMap);
		SettingRepositoryImpl settingRepository = new SettingRepositoryImpl();

		Whitebox.setInternalState(settingRepository, "locationUuid", "02ebbc84-5e29-4cd5-9b79-c594058923e9");
		Whitebox.invokeMethod(settingRepository, "reformattedLocationHierarchy", treeNodeHashMap);
		List<String> reformattedLocationHierarchy = settingRepository.getReformattedLocationHierarchy();
		assertNotNull(reformattedLocationHierarchy);
		assertEquals(1, reformattedLocationHierarchy.size());
	}

}
