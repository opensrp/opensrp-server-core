package org.opensrp.repository.postgres;

import org.junit.Test;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.repository.SettingRepository;
import org.opensrp.search.SettingSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SettingRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("settingRepositoryPostgres")
	private SettingRepository settingRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("setting.sql");
		return scripts;
	}
	
	@Test
	public void testFindAllSettings() {
		
		List<SettingConfiguration> settings = settingRepository.findAllSettings();
		assertEquals(9, settings.size());
		
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
		
		SettingConfiguration settingConfiguration = settingRepository.get("settings-document-id-2");
		assertNotNull(settingConfiguration);
		assertEquals("population_characteristics", settingConfiguration.getIdentifier());
		
	}
	
	@Test
	public void testFindByCriteria() {
		SettingSearchBean settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		
		List<SettingConfiguration> settings = settingRepository.findSettings(settingQueryBean);
		assertEquals(6, settings.size());
		
		settingQueryBean.setTeamId("7e104eee-ec8a-4733-bcf7-c02c51cf43f4");
		settingQueryBean.setServerVersion(0L);
		settings = settingRepository.findSettings(settingQueryBean);
		
		assertEquals(1, settings.size());
		
		settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setLocationId("44de66fb-e6c6-4bae-92bb-386dfe626eba");
		settings = settingRepository.findSettings(settingQueryBean);
		
		assertEquals(1, settings.size());
		
		settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setProviderId("demo");
		settings = settingRepository.findSettings(settingQueryBean);
		
		assertEquals(2, settings.size());
		
		settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setProviderId("demo");
		settingQueryBean.setTeamId("7e104eee-ec8a-4733-bcf7-c02c51cf43f4");
		settings = settingRepository.findSettings(settingQueryBean);
		
		assertEquals(1, settings.size());
		
		settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setTeam("my-team");
		settings = settingRepository.findSettings(settingQueryBean);
		
		assertEquals(1, settings.size());
		
	}
	
	@Test
	public void testGetAllSettings() {
		
		List<SettingConfiguration> settings = settingRepository.getAll();
		assertEquals(9, settings.size());
		
	}
	
	@Test
	public void testGetSettingMetadataByDocumentId() {
		String documentId = "affc7614-a51b-4b5f-877a-ad932b38bf4b";
		SettingsMetadata settingMetadata = settingRepository.getSettingMetadataByDocumentId(documentId);
		assertNotNull(settingMetadata);
		assertEquals(documentId, settingMetadata.getDocumentId());
		assertEquals("lion_king_cast_2", settingMetadata.getIdentifier());
		
	}
}
