package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Event;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.postgres.SettingsMetadataExample;
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
		org.opensrp.domain.postgres.Settings setting = settingMetadataMapper.selectByDocumentId(id);
		
		return convert(setting);
	}
	
	@Override
	public void update(SettingConfiguration entity) {
		if (entity == null || entity.getId() == null || entity.getIdentifier() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		
		if (id == null) { // Setting not exists
			return;
		}
		
		setRevision(entity);
		
		org.opensrp.domain.postgres.Settings pgSetting = convert(entity, id);
		
		if (pgSetting == null) {
			return;
		}
		
		SettingsMetadata metadata = createMetadata(entity, id);
		if (metadata == null) {
			return;
		}
		
		int rowsAffected = settingMapper.updateByPrimaryKey(pgSetting);
		if (rowsAffected < 1) {
			return;
		}
		
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		metadataExample.createCriteria().andSettingsIdEqualTo(id);
		metadata.setId(settingMetadataMapper.selectByExample(metadataExample).get(0).getId());
		settingMetadataMapper.updateByPrimaryKey(metadata);
		
	}
	
	@Override
	public List<SettingConfiguration> getAll() {
		return convert(settingMetadataMapper.selectMany(new SettingsMetadataExample(), 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public void safeRemove(SettingConfiguration entity) {
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
		
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		org.opensrp.domain.postgres.SettingsMetadataExample.Criteria criteria = metadataExample.createCriteria();
		
		if (StringUtils.isNotEmpty(settingQueryBean.getProviderId())) {
			criteria.andProviderIdEqualTo(settingQueryBean.getProviderId());
		}
		if (StringUtils.isNotEmpty(settingQueryBean.getLocationId())) {
			criteria.andLocationIdEqualTo(settingQueryBean.getLocationId());
		}
		if (StringUtils.isNotEmpty(settingQueryBean.getTeam())) {
			criteria.andTeamEqualTo(settingQueryBean.getTeam());
		}
		if (StringUtils.isNotEmpty(settingQueryBean.getTeamId())) {
			criteria.andTeamIdEqualTo(settingQueryBean.getTeamId());
		}
		if (settingQueryBean.getServerVersion() != null) {
			criteria.andServerVersionGreaterThanOrEqualTo(settingQueryBean.getServerVersion());
		}
		
		metadataExample.or(metadataExample.createCriteria().andTeamIdIsNull().andTeamIsNull().andProviderIdIsNull()
		        .andLocationIdIsNull().andServerVersionGreaterThanOrEqualTo(settingQueryBean.getServerVersion()));
		
		if (!criteria.isValid()) {
			throw new IllegalArgumentException("Atleast one search filter must be specified");
		}
		
		return convert(settingMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE));
		
	}
	
	@Override
	public List<SettingConfiguration> findByEmptyServerVersion() {
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
		
		org.opensrp.domain.postgres.Settings pgSetting = settingMetadataMapper.selectByDocumentId(documentId);
		
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
	
	private org.opensrp.domain.postgres.Settings convert(SettingConfiguration entity, Long id) {
		if (entity == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Settings pgSetting = new org.opensrp.domain.postgres.Settings();
		pgSetting.setId(id);
		pgSetting.setJson(entity);
		
		return pgSetting;
	}
	
	private List<SettingConfiguration> convert(List<org.opensrp.domain.postgres.Settings> settings) {
		if (settings == null || settings.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<SettingConfiguration> settingValues = new ArrayList<>();
		for (org.opensrp.domain.postgres.Settings setting : settings) {
			SettingConfiguration convertedSetting = convert(setting);
			if (convertedSetting != null) {
				settingValues.add(convertedSetting);
			}
		}
		return settingValues;
	}
	
	private SettingConfiguration convert(org.opensrp.domain.postgres.Settings setting) {
		if (setting == null || setting.getJson() == null) {
			return null;
		}
		return (SettingConfiguration) setting.getJson();
	}
	
	private SettingsMetadata createMetadata(SettingConfiguration entity, Long id) {
		
		try {
			
			SettingsMetadata metadata = new SettingsMetadata();
			metadata.setSettingsId(id);
			metadata.setDocumentId(entity.getId() != null ? entity.getId() : UUID.randomUUID().toString());
			metadata.setIdentifier(entity.getIdentifier());
			metadata.setProviderId(entity.getProviderId());
			metadata.setLocationId(entity.getLocationId());
			metadata.setTeam(entity.getTeam());
			metadata.setTeamId(entity.getTeamId());
			metadata.setServerVersion(entity.getServerVersion());
			
			return metadata;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Override
	public void add(SettingConfiguration entity) {
		
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
		
		Settings settings = convert(entity, id);
		if (settings == null) {
			return;
		}
		
		int rowsAffected = settingMapper.insertSelectiveAndSetId(settings);
		if (rowsAffected < 1 || settings.getId() == null) {
			return;
		}
		
		SettingsMetadata settingsMetadata = createMetadata(entity, settings.getId());
		if (settingsMetadata != null) {
			settingMetadataMapper.insertSelective(settingsMetadata);
		}
		
	}
	
	@Override
	public SettingsMetadata getSettingMetadataByDocumentId(String documentId) {
		SettingsMetadataExample example = new SettingsMetadataExample();
		example.createCriteria().andDocumentIdEqualTo(documentId);
		
		List<SettingsMetadata> settingsMetadata = settingMetadataMapper.selectByExample(example);
		
		return !settingsMetadata.isEmpty() ? settingsMetadata.get(0) : null;
		
	}
	
	@Override
	public Settings getSettingById(Long id) {
		
		return settingMapper.selectByPrimaryKey(id);
		
	}
}
