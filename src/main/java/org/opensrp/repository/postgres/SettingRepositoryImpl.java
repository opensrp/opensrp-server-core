package org.opensrp.repository.postgres;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.opensrp.util.Utils.isEmptyList;

@Repository ("settingRepositoryPostgres")
public class SettingRepositoryImpl extends BaseRepositoryImpl<SettingConfiguration> implements SettingRepository {
	private static final Logger logger = LoggerFactory.getLogger(SettingRepositoryImpl.class);
	private final List<String> reformattedLocationHierarchy = new ArrayList<>();
	
	@Autowired
	private CustomSettingMapper settingMapper;
	
	@Autowired
	private CustomSettingMetadataMapper settingMetadataMapper;
	
	@Autowired
	private OpenmrsLocationService openmrsLocationService;
	private String locationUuid;
	
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
		SettingsMetadata settingsMetadata = metadata.get(0);
		//We update the metadata data block with the new metadata so that the server version is updated.
		if (!entity.isV1Settings()) {
			metadata = reconcileMetadata(getAvailableMetadataUsingSettingsId(entity, id), settingsMetadata);
		}
		
		if (metadata == null) {
			return;
		}
		settingMetadataMapper.updateMany(metadata);
	}
	
	private List<SettingsMetadata> reconcileMetadata(List<SettingsMetadata> settingsMetadataList,
	                                                 SettingsMetadata settingsMetadata) {
		for (int i = 0; i < settingsMetadataList.size(); i++) {
			if (settingsMetadataList.get(i).getUuid().equals(settingsMetadata.getUuid())) {
				settingsMetadataList.remove(i);
				settingsMetadataList.add(settingsMetadata);
				break;
			}
		}
		
		return settingsMetadataList;
	}
	
	private List<SettingsMetadata> getAvailableMetadataUsingSettingsId(SettingConfiguration entity, Long id) {
		List<SettingsMetadata> settingsMetadata;
		SettingSearchBean settingSearchBean = createDocumentIdSearchBean(entity);
		SettingConfiguration settingConfiguration = findSetting(settingSearchBean);
		settingsMetadata = createMetadata(settingConfiguration, id);
		
		return settingsMetadata;
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
		return findSettings(settingQueryBean, DEFAULT_FETCH_SIZE);
	}
	
	public SettingConfiguration findSetting(SettingSearchBean settingQueryBean) {
		List<SettingConfiguration> settingConfigurations = findSettings(settingQueryBean);
		return settingConfigurations.isEmpty() ? null : settingConfigurations.get(0);
	}
	
	public List<SettingConfiguration> findSettings(SettingSearchBean settingQueryBean, int limit) {
		if (settingQueryBean.isResolveSettings()) {
			return resolveSettings(findSettingsAndSettingsMetadata(settingQueryBean, limit),
					settingQueryBean.isV1Settings());
		} else {
			return convertToSettingConfigurations(findSettingsAndSettingsMetadata(settingQueryBean, limit), settingQueryBean.isV1Settings());
		}
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
		String identifier = settingQueryBean.getIdentifier();
		
		if (StringUtils.isBlank(providerId) && StringUtils.isBlank(locationId) && StringUtils.isBlank(team)
				&& StringUtils.isBlank(teamId) && StringUtils.isBlank(documentId) && StringUtils.isBlank(identifier)) {
			criteria.andTeamIdIsNull().andTeamIsNull().andProviderIdIsNull().andLocationIdIsNull().andDocumentIdIsNotNull().andIdentifierIsNotNull();
		} else {
			if (StringUtils.isNotBlank(providerId)) {
				criteria.andProviderIdEqualTo(providerId);
			}
			if (StringUtils.isNotBlank(team)) {
				criteria.andTeamEqualTo(team);
			}
			if (StringUtils.isNotBlank(teamId)) {
				criteria.andTeamIdEqualTo(teamId);
			}
			if (StringUtils.isNotBlank(documentId)) {
				criteria.andDocumentIdEqualTo(documentId);
			}
			if (StringUtils.isNotBlank(identifier)) {
				criteria.andIdentifierEqualTo(identifier);
			}
		}
		
		if (primaryKey != null) {
			metadataExample.or(metadataExample.createCriteria().andSettingsIdEqualTo(primaryKey));
		}
		criteria.andServerVersionGreaterThanOrEqualTo(settingQueryBean.getServerVersion());
		
		if (settingQueryBean.isResolveSettings()) {
			return fetchSettingsPerLocation(settingQueryBean, metadataExample, criteria, limit);
		} else {
			if (StringUtils.isNotEmpty(locationId)) {
				criteria.andLocationIdEqualTo(locationId);
			}
			return settingMetadataMapper.selectMany(metadataExample, 0, limit);
		}
	}
	
	private List<SettingsAndSettingsMetadataJoined> fetchSettingsPerLocation(SettingSearchBean settingSearchBean,
	                                                                         SettingsMetadataExample metadataExample,
	                                                                         SettingsMetadataExample.Criteria criteria,
	                                                                         int limit) {
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = new ArrayList<>();
		locationUuid = settingSearchBean.getLocationId();
		
		Map<String, TreeNode<String, Location>> parentLocationTree = getChildParentLocationTree(locationUuid,
				settingSearchBean, criteria);
		if (parentLocationTree != null && parentLocationTree.size() > 0) {
			reformattedLocationHierarchy(parentLocationTree);
		}
		
		if (reformattedLocationHierarchy.size() > 0) {
			metadataExample.or(metadataExample.createCriteria().andLocationIdIn(reformattedLocationHierarchy));
			settingsAndSettingsMetadataJoinedList = settingMetadataMapper.selectMany(metadataExample, 0, limit);
		}
		
		return settingsAndSettingsMetadataJoinedList;
	}
	
	private List<SettingConfiguration> resolveSettings(List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoined, boolean isV1Settings) {
		Map<String, SettingConfiguration> stringConfigurationMap = new HashMap<>();
		Map<String, Setting> stringSettingsMap = new HashMap<>();
		
		List<SettingConfiguration> configurations = convertToSettingConfigurations(settingsAndSettingsMetadataJoined, isV1Settings);
		
		for (SettingConfiguration configuration : configurations) {
			String configKey = configuration.getId() + "_" + configuration.getLocationId() + "_" + configuration.getIdentifier();
			stringConfigurationMap.put(configKey, configuration);
			
			List<Setting> settingsList = configuration.getSettings();
			for (Setting setting : settingsList) {
				String settingKey = setting.getKey() + "_" + configuration.getIdentifier();
				stringSettingsMap.put(settingKey, setting);
			}
		}
		
		
		return reconcileConfigurations(stringConfigurationMap, stringSettingsMap);
	}
	
	private List<SettingConfiguration> reconcileConfigurations(Map<String, SettingConfiguration> stringConfigurationMap, Map<String, Setting> stringSettingsMap) {
		List<SettingConfiguration> settingConfigurations = new ArrayList<>();
		for (Map.Entry configElement : stringConfigurationMap.entrySet()) {
			List<Setting> settingList = new ArrayList<>();
			SettingConfiguration configuration = (SettingConfiguration) configElement.getValue();
			configuration.setSettings(new ArrayList<>());
			
			for (Map.Entry settingsElement : stringSettingsMap.entrySet()) {
				if (((String) settingsElement.getKey()).contains(configuration.getIdentifier())) {
					settingList.add((Setting) settingsElement.getValue());
				}
			}
			
			configuration.setSettings(settingList);
			settingConfigurations.add(configuration);
		}
		
		return settingConfigurations;
	}
	
	
	private Map<String, TreeNode<String, Location>> getChildParentLocationTree(String locationId,
	                                                                           SettingSearchBean settingSearchBean, SettingsMetadataExample.Criteria criteria) {
		String locationTreeString = new Gson().toJson(openmrsLocationService.getLocationTreeOf(locationId));
		LocationTree locationTree = new Gson().fromJson(locationTreeString, LocationTree.class);
		Map<String, TreeNode<String, Location>> treeNodeHashMap = new HashMap<>();
		if (locationTree != null) {
			treeNodeHashMap = locationTree.getLocationsHierarchy();
		}
		if (settingSearchBean.getIdentifier() != null) {
			criteria.andIdentifierEqualTo(settingSearchBean.getIdentifier());
		}
		
		
		return treeNodeHashMap;
	}
	
	
	private void reformattedLocationHierarchy(Map<String, TreeNode<String, Location>> parentLocation) {
		Map.Entry<String, TreeNode<String, Location>> stringMapEntry = parentLocation.entrySet().iterator().next();
		String locationKey = stringMapEntry.getKey();
		if (StringUtils.isNotBlank(locationKey)) {
			reformattedLocationHierarchy.add(locationKey);
		}
		
		TreeNode<String, Location> childLocation = stringMapEntry.getValue();
		Map<String, TreeNode<String, Location>> newLocation = childLocation.getChildren();
		
		if (!Objects.equals(locationUuid, locationKey)) {
			reformattedLocationHierarchy(newLocation);
		}
	}
	
	@Override
	public List<SettingConfiguration> findByEmptyServerVersion() {
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		metadataExample.createCriteria().andServerVersionIsNull();
		metadataExample.or(metadataExample.createCriteria().andServerVersionEqualTo(0L));
		return convertToSettingConfigurations(settingMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	protected Long retrievePrimaryKey(SettingConfiguration settingConfiguration) {
		SettingSearchBean settingSearchBean = createDocumentIdSearchBean(settingConfiguration);
		SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined =
				findSettingsAndSettingsMetadata(settingSearchBean);
		Settings pgSetting = settingsAndSettingsMetadataJoined == null ? null : settingsAndSettingsMetadataJoined.getSettings();
		if (pgSetting == null) { return null;}
		return pgSetting.getId();
	}
	
	private SettingSearchBean createDocumentIdSearchBean(SettingConfiguration settingConfiguration) {
		SettingSearchBean settingSearchBean = new SettingSearchBean();
		Object uniqueId = getUniqueField(settingConfiguration);
		if (uniqueId == null) {
			return settingSearchBean;
		}
		String documentId = uniqueId.toString();
		
		settingSearchBean.setDocumentId(documentId);
		settingSearchBean.setServerVersion(0L);
		
		return settingSearchBean;
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
	
	private List<SettingConfiguration> convertToSettingConfigurations(List<SettingsAndSettingsMetadataJoined> jointSettings) {
		return convertToSettingConfigurations(jointSettings, false);
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
			settingConfiguration.setIdentifier(settingConfiguration.getSettings().get(0).getSettingIdentifier());
		}
		settingConfigurations.addAll(settingConfigurationMap.values());
		return settingConfigurations;
	}
	
	private Setting convertToSetting(SettingsMetadata settingsMetadata, boolean isV1Settings) {
		Setting setting = new Setting();
		setting.setValue(settingsMetadata.getSettingValue());
		setting.setKey(settingsMetadata.getSettingKey());
		setting.setSettingMetadataId(String.valueOf(settingsMetadata.getId()));
		setting.setUuid(settingsMetadata.getUuid());
		setting.setInheritedFrom(settingsMetadata.getInheritedFrom());
		setting.setDescription(settingsMetadata.getSettingDescription());
		setting.setLabel(settingsMetadata.getSettingLabel());
		setting.setSettingIdentifier(settingsMetadata.getIdentifier());
		if (!isV1Settings) {
			setting.setProviderId(settingsMetadata.getProviderId());
			setting.setSettingsId(String.valueOf(settingsMetadata.getSettingsId()));
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
		settingConfiguration.setDocumentId(setting.getDocumentId());
		if (StringUtils.isNotBlank(setting.getSettingsId())) {
			settingConfiguration.setId(setting.getSettingsId());
		}
		settingConfiguration.setV1Settings(false);
		
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
		List<Setting> settings;
		Settings pgSettings;
		
		if (id == null) {
			if (entity.getId() == null) {
				entity.setId(UUID.randomUUID().toString());
			}
			
			setRevision(entity);
			
			settings = entity.getSettings();
			entity.setSettings(null); // strip out the settings block
			pgSettings = convert(entity, id);
			if (pgSettings == null) {
				return;
			}
			
			int rowsAffected = settingMapper.insertSelectiveAndSetId(pgSettings);
			if (rowsAffected < 1 || pgSettings.getId() == null) {
				return;
			}
		} else {
			settings = entity.getSettings();
			pgSettings = convert(entity, id);
		}
		
		checkWhetherMetadataExistsBeforeSave(entity, settings, pgSettings);
	}
	
	private void checkWhetherMetadataExistsBeforeSave(SettingConfiguration entity, List<Setting> settings, Settings pgSettings) {
		entity.setSettings(settings); // re-inject settings block
		List<SettingsMetadata> settingsMetadata = createMetadata(entity, pgSettings.getId());
		List<SettingsMetadata> settingsMetadataList = new ArrayList<>();
		
		for (SettingsMetadata metadata : settingsMetadata) {
			if (!checkIfMetadataExists(metadata)) {
				settingsMetadataList.add(metadata);
			}
		}
		
		//We update the metadata data block with the new metadata so that the server version is updated.
		if (!entity.isV1Settings() && pgSettings.getId() != null) {
			settingsMetadataList.addAll(getAvailableMetadataUsingSettingsId(entity, pgSettings.getId()));
		}
		
		settingMetadataMapper.insertMany(settingsMetadataList);
	}
	
	private boolean checkIfMetadataExists(SettingsMetadata settingsMetadata) {
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		SettingsMetadataExample.Criteria criteria = metadataExample.createCriteria();
		
		String locationId = settingsMetadata.getLocationId();
		String teamId = settingsMetadata.getTeamId();
		String settingsId = String.valueOf(settingsMetadata.getSettingsId());
		String settingKey = settingsMetadata.getSettingKey();
		
		if (StringUtils.isNotBlank(locationId)) {
			criteria.andLocationIdEqualTo(locationId);
		}
		if (StringUtils.isNotBlank(teamId)) {
			criteria.andTeamIdEqualTo(teamId);
		}
		;
		if (StringUtils.isNotBlank(settingsId)) {
			criteria.andSettingsIdEqualTo(Long.valueOf(settingsId));
		}
		;
		if (StringUtils.isNotBlank(locationId)) {
			criteria.andSettingKeyEqualTo(settingKey);
		}
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = settingMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE);
		return settingsAndSettingsMetadataJoinedList.size() > 0;
	}
	
	private List<SettingsMetadata> createMetadata(SettingConfiguration settingConfiguration, Long id) {
		List<SettingsMetadata> settingsMetadata = new ArrayList<>();
		List<Setting> settings = settingConfiguration.getSettings();
		
		try {
			for (Setting setting : settings) {
				SettingsMetadata metadata = new SettingsMetadata();
				metadata.setSettingKey(setting.getKey());
				metadata.setSettingValue(setting.getValue());
				metadata.setSettingDescription(setting.getDescription());
				metadata.setSettingLabel(setting.getLabel());
				metadata.setSettingsId(id);
				metadata.setSettingType(setting.getType());
				metadata.setUuid(setting.getUuid() != null ? setting.getUuid() : UUID.randomUUID().toString());
				metadata.setInheritedFrom(setting.getInheritedFrom());
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
