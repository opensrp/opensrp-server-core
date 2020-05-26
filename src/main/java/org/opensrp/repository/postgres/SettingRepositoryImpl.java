package org.opensrp.repository.postgres;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.opensrp.util.Utils.isEmptyList;

@Repository ("settingRepositoryPostgres")
public class SettingRepositoryImpl extends BaseRepositoryImpl<SettingConfiguration> implements SettingRepository {
	
	@Autowired
	private CustomSettingMapper settingMapper;
	
	@Autowired
	private CustomSettingMetadataMapper settingMetadataMapper;
	
	@Autowired
	private OpenmrsLocationService openmrsLocationService;
	
	@Override
	public SettingConfiguration get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		SettingSearchBean settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setDocumentId(id);
		
		return findSetting(settingQueryBean);
	}
	
	@Transactional
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
	public List<SettingConfiguration> getAll() {
		return convertToSettingConfigurations(settingMetadataMapper.selectMany(new SettingsMetadataExample(), 0, DEFAULT_FETCH_SIZE));
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
		logger.debug(settingQueryBean.toString() + " ------------------------------- find settings from the endpoint");
		return findSettings(settingQueryBean, DEFAULT_FETCH_SIZE);
	}
	
	public SettingConfiguration findSetting(SettingSearchBean settingQueryBean) {
		List<SettingConfiguration> settingConfigurations = findSettings(settingQueryBean);
		return settingConfigurations.isEmpty() ? null : settingConfigurations.get(0);
	}
	
	public List<SettingConfiguration> findSettings(SettingSearchBean settingQueryBean, int limit) {
		boolean isV1Settings = settingQueryBean.isV1Settings();
		List<SettingsAndSettingsMetadataJoined> joinedList = findSettingsAndSettingsMetadata(settingQueryBean, limit);
		logger.debug(joinedList.toString()+ "------------------------ find setting after the settings are fetched");
		return convertToSettingConfigurations(joinedList, isV1Settings);
	}
	
	public SettingsAndSettingsMetadataJoined findSettingsAndSettingsMetadata(SettingSearchBean settingQueryBean) {
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoined = findSettingsAndSettingsMetadata(settingQueryBean, 1);
		return isEmptyList(settingsAndSettingsMetadataJoined) ? null : settingsAndSettingsMetadataJoined.get(0);
	}
	
	public List<SettingsAndSettingsMetadataJoined> findSettingsAndSettingsMetadata(SettingSearchBean settingQueryBean, int limit) {
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		SettingsMetadataExample.Criteria criteria = metadataExample.createCriteria();
		
		String providerId = settingQueryBean.getProviderId();
		String locationId = settingQueryBean.getLocationId();
		String team = settingQueryBean.getTeam();
		String teamId = settingQueryBean.getTeamId();
		String documentId = settingQueryBean.getDocumentId();
		Long primaryKey = settingQueryBean.getPrimaryKey();
		
		if (StringUtils.isBlank(providerId) && StringUtils.isBlank(locationId) && StringUtils.isBlank(team)
				&& StringUtils.isBlank(teamId) && StringUtils.isBlank(documentId)) {
			criteria.andTeamIdIsNull().andTeamIsNull().andProviderIdIsNull().andLocationIdIsNull();
		} else {
			if (StringUtils.isNotEmpty(providerId)) {
				criteria.andProviderIdEqualTo(providerId);
			}
			if (StringUtils.isNotEmpty(team)) {
				criteria.andTeamEqualTo(team);
			}
			if (StringUtils.isNotEmpty(teamId)) {
				criteria.andTeamIdEqualTo(teamId);
			}
			if (StringUtils.isNotEmpty(documentId)) {
				criteria.andDocumentIdEqualTo(documentId);
			}
		}
		
		if (primaryKey != null) {
			metadataExample.or(metadataExample.createCriteria().andSettingsIdEqualTo(primaryKey));
		}
		criteria.andServerVersionGreaterThanOrEqualTo(settingQueryBean.getServerVersion());
		
		if (settingQueryBean.isResolveSettings()) {
			return resolveSettingsPerLocation(settingQueryBean, criteria, metadataExample, limit);
		} else {
			if (StringUtils.isNotEmpty(locationId)) {
				criteria.andLocationIdEqualTo(locationId);
			}
			return settingMetadataMapper.selectMany(metadataExample, 0, limit);
		}
	}
	
	private List<SettingsAndSettingsMetadataJoined> resolveSettingsPerLocation(SettingSearchBean settingSearchBean,
	                                                                           SettingsMetadataExample.Criteria criteria,
	                                                                           SettingsMetadataExample metadataExample,
	                                                                           int limit) {
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = new ArrayList<>();
		String locationId = settingSearchBean.getLocationId();
		LocationTree locationTree = new LocationTree();
		
		Map<String, Set<String>> childParent = getChildParentLocationTree(locationId);
		List<String> reformattedLocationHierarchy = new ArrayList<>();
		if (childParent != null && childParent.size() > 0) {
			reformattedLocationHierarchy(childParent, locationId, reformattedLocationHierarchy);
		}
		
		if (reformattedLocationHierarchy.size() > 0) {
			for (String locationUuid : reformattedLocationHierarchy) {
				criteria.andLocationIdEqualTo(locationUuid);
				List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoined = settingMetadataMapper.selectMany(metadataExample, 0, limit);
			}
		}
		
		return settingsAndSettingsMetadataJoinedList;
	}
	
	private Map<String, Set<String>> getChildParentLocationTree(String locationId) {
		String locationTreeString = new Gson().toJson(openmrsLocationService.getLocationTreeOf(locationId));
		LocationTree locationTree = new Gson().fromJson(locationTreeString, LocationTree.class);
		Map<String, Set<String>> childParent = new HashMap<>();
		if (locationTree != null) {
			childParent = locationTree.getChildParent();
		}
		
		return childParent;
	}
	
	
	private void reformattedLocationHierarchy(Map<String, Set<String>> childParent, String locationId, List<String> locationHierarchy) {
		for (Map.Entry<String, Set<String>> stringSetEntry : childParent.entrySet()) {
			String location = (String) ((Map.Entry) stringSetEntry).getValue();
			String locationKey = (String) ((Map.Entry) stringSetEntry).getKey();
			if (StringUtils.contains(location, locationId)) {
				locationHierarchy.add(location);
				locationId = locationKey;
			}
			
			if (locationHierarchy.size() <= (childParent.size() + 1))
				reformattedLocationHierarchy(childParent, locationId, locationHierarchy);
		}
	}
	
	@Override
	public List<SettingConfiguration> findByEmptyServerVersion() {
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		metadataExample.createCriteria().andServerVersionIsNull();
		metadataExample.or(metadataExample.createCriteria().andServerVersionEqualTo(0l));
		return convertToSettingConfigurations(settingMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	protected Long retrievePrimaryKey(SettingConfiguration settingConfiguration) {
		Object uniqueId = getUniqueField(settingConfiguration);
		if (uniqueId == null) {
			return null;
		}
		
		String documentId = uniqueId.toString();
		
		SettingSearchBean settingSearchBean = new SettingSearchBean();
		settingSearchBean.setDocumentId(documentId);
		settingSearchBean.setServerVersion(0l);
		
		SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined = findSettingsAndSettingsMetadata(settingSearchBean);
		Settings pgSetting = settingsAndSettingsMetadataJoined == null ? null : settingsAndSettingsMetadataJoined.getSettings();
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
	
	private SettingConfiguration convert(Settings settings) {
		return (SettingConfiguration) settings.getJson();
	}
	
	private List<SettingConfiguration> convertToSettingConfigurations(List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoined, boolean isV1Settings) {
		List<SettingConfiguration> settingConfigurations = new ArrayList<>();
		if (settingsAndSettingsMetadataJoined == null || settingsAndSettingsMetadataJoined.isEmpty()) {
			return settingConfigurations;
		}
		
		Map<Long, SettingConfiguration> settingConfigurationMap = new HashMap<>();
		for (SettingsAndSettingsMetadataJoined jointSetting : settingsAndSettingsMetadataJoined) {
			SettingConfiguration settingConfiguration = settingConfigurationMap.get(jointSetting.getSettings().getId());
			if (settingConfiguration == null) {
				settingConfiguration = convert(jointSetting.getSettings());
				settingConfigurationMap.put(jointSetting.getSettings().getId(), settingConfiguration);
				settingConfiguration.setSettings(new ArrayList<>());
			}
			settingConfiguration.getSettings().add(convertToSetting(jointSetting.getSettingsMetadata(), isV1Settings));
			settingConfiguration.setDocumentId(settingConfiguration.getSettings().get(0).getDocumentId());
		}
		settingConfigurations.addAll(settingConfigurationMap.values());
		return settingConfigurations;
	}
	
	private List<SettingConfiguration> convertToSettingConfigurations(List<SettingsAndSettingsMetadataJoined> jointSettings) {
		return convertToSettingConfigurations(jointSettings, false);
	}
	
	private Setting convertToSetting(SettingsMetadata settingsMetadata, boolean isV1Settings) {
		logger.debug(isV1Settings + "--------------------------------------------- the convert string " +
				"method");
		if (settingsMetadata.getJson() != null) {
			return (Setting) settingsMetadata.getJson();
		}
		Setting setting = new Setting();
		setting.setValue(settingsMetadata.getSettingValue());
		setting.setKey(settingsMetadata.getSettingKey());
		setting.setId(String.valueOf(settingsMetadata.getId()));
		setting.setUuid(settingsMetadata.getUuid());
		setting.setInheritedFrom(settingsMetadata.getInheritedFrom());
		setting.setDescription(settingsMetadata.getSettingDescription());
		if (!isV1Settings) {
			setting.setProviderId(settingsMetadata.getProviderId());
			setting.setTeamId(settingsMetadata.getTeamId());
			setting.setTeam(settingsMetadata.getTeam());
			setting.setLocationId(settingsMetadata.getLocationId());
			setting.setType(settingsMetadata.getSettingType());
			setting.setServerVersion(settingsMetadata.getServerVersion());
			setting.setDocumentId(settingsMetadata.getDocumentId());
		}
		return setting;
	}
	
	private List<Setting> convertToSettings(List<SettingsMetadata> settingsMetadata, boolean isV1Settings) {
		List<Setting> settings = new ArrayList<>();
		for (int i = 0; i < settingsMetadata.size(); i++) {
			SettingsMetadata currSettingMetadata = settingsMetadata.get(i);
			settings.add(convertToSetting(currSettingMetadata, isV1Settings));
		}
		return settings;
	}
	
	@Override
	public void addOrUpdate(Setting setting) {
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
		settingConfiguration.setServerVersion(setting.getServerVersion());
		settingConfiguration.setIdentifier(setting.getIdentifier());
		settingConfiguration.setDocumentId(setting.getDocumentId());
		settingConfiguration.setId(setting.getId());
		settingConfiguration.setV1Settings(true);
		
		if (StringUtils.isNotBlank(setting.getId())) {
			update(settingConfiguration);
		} else {
			add(settingConfiguration);
		}
	}
	
	@Override
	public void delete(Long settingId) {
		settingMetadataMapper.deleteByPrimaryKey(settingId);
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
		settingMetadataMapper.insertMany(settingsMetadata);
	}
	
	private List<SettingsMetadata> createMetadata(SettingConfiguration settingConfiguration, Long id) {
		List<SettingsMetadata> settingsMetadata = new ArrayList();
		List<Setting> settings = settingConfiguration.getSettings();
		try {
			for (Setting currSetting : settings) {
				SettingsMetadata metadata = new SettingsMetadata();
				metadata.setSettingKey(currSetting.getKey());
				metadata.setSettingValue(currSetting.getValue());
				metadata.setSettingDescription(currSetting.getDescription());
				metadata.setSettingsId(id);
				if (settingConfiguration.isV1Settings()) {
					metadata.setSettingType(currSetting.getType());
				} else {
					metadata.setSettingType(settingConfiguration.getType());
				}
				metadata.setUuid(currSetting.getUuid() != null ? currSetting.getUuid() : UUID.randomUUID().toString());
				metadata.setInheritedFrom(currSetting.getInheritedFrom());
				metadata.setDocumentId(settingConfiguration.getId() != null ? settingConfiguration.getId() : UUID.randomUUID().toString());
				metadata.setIdentifier(settingConfiguration.getIdentifier());
				metadata.setProviderId(settingConfiguration.getProviderId());
				metadata.setLocationId(settingConfiguration.getLocationId());
				metadata.setTeam(settingConfiguration.getTeam());
				metadata.setTeamId(settingConfiguration.getTeamId());
				metadata.setServerVersion(settingConfiguration.getServerVersion());
				metadata.setJson(convertToSetting(metadata, false)); //always want to create the json on the settings
				// creation
				settingsMetadata.add(metadata);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return settingsMetadata;
	}
	
	@Override
	@Deprecated
	public SettingsMetadata getSettingMetadataByDocumentId(String documentId) {
		List<SettingsMetadata> settingsMetadata = getAllSettingMetadataByDocumentId(documentId);
		return !settingsMetadata.isEmpty() ? settingsMetadata.get(0) : null;
	}
	
	public List<SettingsMetadata> getAllSettingMetadataByDocumentId(String documentId) {
		SettingsMetadataExample example = new SettingsMetadataExample();
		example.createCriteria().andDocumentIdEqualTo(documentId);
		return settingMetadataMapper.selectByExample(example);
	}
	
	@Override
	public Settings getSettingById(Long id) {
		SettingSearchBean settingQueryBean = new SettingSearchBean();
		settingQueryBean.setServerVersion(0L);
		settingQueryBean.setPrimaryKey(id);
		
		SettingConfiguration settingConfiguration = findSetting(settingQueryBean);
		return settingConfiguration == null ? null : convert(settingConfiguration, id);
	}
}
