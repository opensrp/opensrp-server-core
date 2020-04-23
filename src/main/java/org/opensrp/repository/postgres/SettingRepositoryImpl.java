
package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.postgres.SettingsMetadataExample;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.repository.SettingRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomSettingMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomSettingMetadataMapper;
import org.opensrp.search.SettingSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("settingRepositoryPostgres")
public class SettingRepositoryImpl extends BaseRepositoryImpl<SettingConfiguration> implements SettingRepository {
	
	@Autowired
	private CustomSettingMapper settingMapper;
	
	@Autowired
	private CustomSettingMetadataMapper settingMetadataMapper;
	
	@Override
	public SettingConfiguration get(String id) {
		
		if (StringUtils.isBlank(id)) {
			return null;
		}
		Settings setting = settingMetadataMapper.selectByDocumentId(id);
		SettingsMetadataExample settingsMetadataExample = new SettingsMetadataExample();
		settingsMetadataExample.createCriteria().andDocumentIdEqualTo(id);
		List<SettingsMetadata> settingsMetadata = settingMetadataMapper.selectByExample(settingsMetadataExample);

		SettingConfiguration settingConfiguration = convert(setting);
		settingConfiguration.setSettings(convertToSettings(settingsMetadata));

		return settingConfiguration;
	}

	private List<Setting> convertToSettings(List<SettingsMetadata> settingsMetadata) {
		List<Setting> settings = new ArrayList<>();
		for (int i = 0; i < settings.size(); i++) {
			SettingsMetadata currSettingMetadata = settingsMetadata.get(i);
			Setting setting = new Setting();
			//				setting.setKey(currSettingMetadatagetKey()); // todo: uncomment this
//			setting.setDescription(currSettingMetadata.ge);// todo: uncomment this
			setting.setIdentifier(currSettingMetadata.getIdentifier());
			setting.setProviderId(currSettingMetadata.getProviderId());
			setting.setLocationId(currSettingMetadata.getLocationId());
			setting.setTeam(currSettingMetadata.getTeam());
			setting.setTeamId(currSettingMetadata.getTeamId());
			setting.setServerVersion(currSettingMetadata.getServerVersion());
			settings.add(setting);
		}
		return settings;
	}

	@Override
	public void update(SettingConfiguration entity) {// todo: modify this
		if (entity == null || entity.getId() == null || entity.getIdentifier() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		
		if (id == null) { // Setting not exists
			return;
		}
		
		setRevision(entity);
		
		Settings pgSetting = convert(entity, id);
		List<Setting> settings = entity.getSettings();
		entity.setSettings(null); // strip out the settings block
		if (pgSetting == null) {
			return;
		}

		int rowsAffected = settingMapper.updateByPrimaryKey(pgSetting);
		if (rowsAffected < 1) {
			return;
		}

		entity.setSettings(settings); // re-inject settings block
		List<SettingsMetadata> metadata = createMetadata(entity, id);
		if (metadata == null) {
			return;
		}
		settingMetadataMapper.updateMany(metadata);
	}
	
	@Override
	public List<SettingConfiguration> getAll() {// todo: modify this
		return convert(settingMetadataMapper.selectMany(new SettingsMetadataExample(), 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public void safeRemove(SettingConfiguration entity) {// todo: modify this
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		metadataExample.createCriteria().andSettingsIdEqualTo(id);
		int rowsAffected = settingMetadataMapper.deleteByExample(metadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		settingMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public List<SettingConfiguration> findAllSettings() {
		return getAll();
	}
	
	@Override
	public List<SettingConfiguration> findSettings(SettingSearchBean settingQueryBean) {
		// todo: modify this
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		SettingsMetadataExample.Criteria criteria = metadataExample.createCriteria();

		String providerId = settingQueryBean.getProviderId();
		String locationId = settingQueryBean.getLocationId();
		String team = settingQueryBean.getTeam();
		String teamId = settingQueryBean.getTeamId();
		if (StringUtils.isBlank(providerId) && StringUtils.isBlank(locationId) && StringUtils.isBlank(team) && StringUtils.isBlank(teamId)) {
			criteria.andTeamIdIsNull().andTeamIsNull().andProviderIdIsNull().andLocationIdIsNull();
		} else {
			if (StringUtils.isNotEmpty(providerId)) {
				criteria.andProviderIdEqualTo(providerId);
			}
			if (StringUtils.isNotEmpty(locationId)) {
				criteria.andLocationIdEqualTo(locationId);
			}
			if (StringUtils.isNotEmpty(team)) {
				criteria.andTeamEqualTo(team);
			}
			if (StringUtils.isNotEmpty(teamId)) {
				criteria.andTeamIdEqualTo(teamId);
			}
		}
		criteria.andServerVersionGreaterThanOrEqualTo(settingQueryBean.getServerVersion());

		return convert(settingMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<SettingConfiguration> findByEmptyServerVersion() { // todo:modify this
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		metadataExample.createCriteria().andServerVersionIsNull();
		metadataExample.or(metadataExample.createCriteria().andServerVersionEqualTo(0l));
		return convert(settingMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	protected Long retrievePrimaryKey(SettingConfiguration settingConfiguration) {
		Object uniqueId = getUniqueField(settingConfiguration);
		if (uniqueId == null) {
			return null;
		}
		
		String documentId = uniqueId.toString();
		
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		metadataExample.createCriteria().andDocumentIdEqualTo(documentId);
		
		Settings pgSetting = settingMetadataMapper.selectByDocumentId(documentId);
		
		if (pgSetting == null) {
			return null;
		}
		return pgSetting.getId();
	}
	
	@Override
	protected Object getUniqueField(SettingConfiguration settingConfiguration) {
		if (settingConfiguration == null) {
			return null;
		}
		return settingConfiguration.getId();
	}
	
	private Settings convert(SettingConfiguration entity, Long id) {
		if (entity == null) {
			return null;
		}

		Settings pgSetting = new Settings();
		pgSetting.setId(id);
		pgSetting.setJson(entity);
		
		return pgSetting;
	}
	
	private List<SettingConfiguration> convert(List<Settings> settings) {
		if (settings == null || settings.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<SettingConfiguration> settingValues = new ArrayList<>();
		for (Settings setting : settings) {
			SettingConfiguration convertedSetting = convert(setting);
			if (convertedSetting != null) {
				settingValues.add(convertedSetting);
			}
		}
		return settingValues;
	}
	
	private SettingConfiguration convert(Settings setting) {
		if (setting == null || setting.getJson() == null) {
			return null;
		}
		return (SettingConfiguration) setting.getJson();
	}
	
	private List<SettingsMetadata> createMetadata(SettingConfiguration entity, Long id) {
		List<SettingsMetadata> settingsMetadata = new ArrayList();
		List<Setting> settings = entity.getSettings();
		try {
			for (int i = 0; i < settings.size(); i++) {
				Setting currSetting = settings.get(i);
				SettingsMetadata metadata = new SettingsMetadata();
//				metadata.setKey(currSetting.getKey()); // todo: uncomment this
				metadata.setSettingsId(id);
				metadata.setDocumentId(entity.getId() != null ? entity.getId() : UUID.randomUUID().toString());
				metadata.setIdentifier(entity.getIdentifier());
				metadata.setProviderId(entity.getProviderId());
				metadata.setLocationId(entity.getLocationId());
				metadata.setTeam(entity.getTeam());
				metadata.setTeamId(entity.getTeamId());
				metadata.setServerVersion(entity.getServerVersion());
				settingsMetadata.add(metadata);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return settingsMetadata;
	}


	public void add(Setting setting) {
		List<Setting> settings = new ArrayList<>();
		settings.add(setting);
		SettingConfiguration settingConfiguration = new SettingConfiguration();
		settingConfiguration.setChildLocationId(setting.getChildLocationId());
		settingConfiguration.setLocationId(setting.getLocationId());
		settingConfiguration.setProviderId(setting.getProviderId());
		settingConfiguration.setTeam(setting.getTeam());
		settingConfiguration.setTeamId(setting.getTeamId());
		settingConfiguration.setIdentifier(setting.getIdentifier());
		settingConfiguration.setType(setting.getType());
		settingConfiguration.setSettings(settings);
		add(settingConfiguration);
	}

	@Override
	public void add(SettingConfiguration entity) {
		// todo: modify this
		if (entity == null || entity.getSettings() == null || entity.getIdentifier() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		
		if (id != null) { // Setting already exists
			return;
		}
		
		if (entity.getId() == null) {
			entity.setId(UUID.randomUUID().toString());
		}
		
		setRevision(entity);

		List<Setting> settings = entity.getSettings();
		entity.setSettings(null); // strip out the settings block
		Settings pgSettings = convert(entity, id);
		if (pgSettings == null) {
			return;
		}

		int rowsAffected = settingMapper.insertSelectiveAndSetId(pgSettings);
		if (rowsAffected < 1 || pgSettings.getId() == null) {
			return;
		}

		entity.setSettings(settings); // re-inject settings block
		List<SettingsMetadata> settingsMetadata = createMetadata(entity, pgSettings.getId());
		if (settingsMetadata != null) {
			settingMetadataMapper.insertMany(settingsMetadata);
		}
	}
	
	@Override
	public SettingsMetadata getSettingMetadataByDocumentId(String documentId) { // todo: modify this
		SettingsMetadataExample example = new SettingsMetadataExample();
		example.createCriteria().andDocumentIdEqualTo(documentId);
		
		List<SettingsMetadata> settingsMetadata = settingMetadataMapper.selectByExample(example);
		
		return !settingsMetadata.isEmpty() ? settingsMetadata.get(0) : null;
		
	}
	
	@Override
	public Settings getSettingById(Long id) {
		// todo: modify this
		return settingMapper.selectByPrimaryKey(id);
		
	}
}
