package org.opensrp.repository.postgres;

import static org.opensrp.util.Utils.isEmptyList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.TreeNode;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.postgres.SettingsMetadataExample;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.exception.DatabaseException;
import org.opensrp.repository.SettingRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomSettingMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomSettingMetadataMapper;
import org.opensrp.search.SettingSearchBean;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository("settingRepositoryPostgres")
public class SettingRepositoryImpl extends BaseRepositoryImpl<SettingConfiguration> implements SettingRepository {

	private static final Logger logger = LogManager.getLogger(SettingRepositoryImpl.class);

	private final List<String> reformattedLocationHierarchy = new ArrayList<>();

	@Autowired
	private CustomSettingMapper settingMapper;

	@Autowired
	private CustomSettingMetadataMapper settingMetadataMapper;

	private String locationUuid;

	@Autowired
	private DataSource openSRPDataSource;

	@Override
	public SettingConfiguration get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		SettingSearchBean settingQueryBean = new SettingSearchBean();
		settingQueryBean.setDocumentId(id);

		return findSetting(settingQueryBean, null);
	}
	
	private void updateServerVersion(Settings pgSettings, SettingConfiguration entity) {
		long serverVersion = settingMapper.selectServerVersionByPrimaryKey(pgSettings.getId());
		entity.setServerVersion(serverVersion);
		pgSettings.setJson(entity);
		pgSettings.setServerVersion(null);
		int rowsAffected = settingMapper.updateByPrimaryKeySelective(pgSettings);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
	}

	@Override
	public void add(SettingConfiguration entity) {
		//todo not required.
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

		int rowsAffected = settingMapper.updateByPrimaryKeyAndGenerateServerVersion(pgSetting);
		if (rowsAffected < 1) {
			return;
		}

		updateServerVersion(pgSetting, entity);
		entity.setSettings(settings);// re-inject settings block
		List<SettingsMetadata> metadata = createMetadata(entity, id);
		List<SettingsMetadata> settingsMetadataList = new ArrayList<>();

		for (SettingsMetadata settingsMetadata : metadata) {
			if (StringUtils.isBlank(settingsMetadata.getSettingValue())) {
				deleteExistingSettingsMetadataByKeyAndIdentifier(settingsMetadata.getIdentifier(), settingsMetadata.getSettingKey(),
						settingsMetadata.getLocationId()); 	//This method is called to delete existing settings metadata records where value field is empty
			}
			if (!checkIfMetadataExists(settingsMetadata) && StringUtils.isNotBlank(settingsMetadata.getSettingValue())) {
				settingsMetadataList.add(settingsMetadata);
			}
		}

		if (!settingsMetadataList.isEmpty()) {
			settingMetadataMapper.insertMany(settingsMetadataList);
		}
		settingMetadataMapper.updateMany(metadata);
	}

	@Override
	public List<SettingConfiguration> getAll() {
		return convertToSettingConfigurations(
				settingMetadataMapper.selectMany(new SettingsMetadataExample(), 0, DEFAULT_FETCH_SIZE));
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
	public List<SettingConfiguration> findSettings(SettingSearchBean settingQueryBean,
			Map<String, TreeNode<String, Location>> treeNodeHashMap) {
		Integer limit;
		limit = settingQueryBean.getLimit() == null || settingQueryBean.getLimit() == 0? DEFAULT_FETCH_SIZE : settingQueryBean.getLimit();
		return findSettings(settingQueryBean, limit, treeNodeHashMap);
	}

	@Override
	public SettingConfiguration findSetting(SettingSearchBean settingQueryBean,
			Map<String, TreeNode<String, Location>> treeNodeHashMap) {
		List<SettingConfiguration> settingConfigurations = findSettings(settingQueryBean, treeNodeHashMap);
		return settingConfigurations.isEmpty() ? null : settingConfigurations.get(0);
	}

	public List<SettingConfiguration> findSettings(SettingSearchBean settingQueryBean, int limit,
			Map<String, TreeNode<String, Location>> treeNodeHashMap) {
		List<SettingConfiguration> settingConfigurations;
		if (settingQueryBean.isResolveSettings()) {
			settingConfigurations = settingQueryBean.isETL() ?
					resolveSettingsMetadata(findSettingsMetadata(settingQueryBean, treeNodeHashMap, limit),
							settingQueryBean.isV1Settings(), settingQueryBean.isResolveSettings()) :
					resolveSettings(findSettingsAndSettingsMetadata(settingQueryBean, treeNodeHashMap, limit),
							settingQueryBean.isV1Settings(), settingQueryBean.isResolveSettings());
		} else {
			settingConfigurations = settingQueryBean.isETL() ?
					convertSettingsMetadataToSettingConfigurations(findSettingsMetadata(settingQueryBean, treeNodeHashMap, limit),
							settingQueryBean.isV1Settings(), settingQueryBean.isResolveSettings()) :
					convertToSettingConfigurations(findSettingsAndSettingsMetadata(settingQueryBean, treeNodeHashMap, limit),
							settingQueryBean.isV1Settings(), settingQueryBean.isResolveSettings());
		}
		return settingConfigurations;
	}

	public SettingsAndSettingsMetadataJoined findSettingsAndSettingsMetadata(SettingSearchBean settingQueryBean,
			Map<String, TreeNode<String, Location>> treeNodeHashMap) {
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoined = findSettingsAndSettingsMetadata(
				settingQueryBean, treeNodeHashMap, 1);
		return isEmptyList(settingsAndSettingsMetadataJoined) ? null : settingsAndSettingsMetadataJoined.get(0);
	}

	public List<SettingsAndSettingsMetadataJoined> findSettingsAndSettingsMetadata(SettingSearchBean settingQueryBean,
			Map<String, TreeNode<String, Location>> treeNodeHashMap, int limit) {
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		SettingsMetadataExample.Criteria criteria = metadataExample.createCriteria();
		String locationId = settingQueryBean.getLocationId();
		Long primaryKey = settingQueryBean.getPrimaryKey();
		updateCriteria(criteria, settingQueryBean);

		if (primaryKey != null) {
			metadataExample.or(metadataExample.createCriteria().andSettingsIdEqualTo(primaryKey));
		}

		if (StringUtils.isBlank(settingQueryBean.getId())) {
			criteria.andServerVersionGreaterThanOrEqualTo(settingQueryBean.getServerVersion());
		}

		if (settingQueryBean.getMetadataVersion() != null) {
			criteria.andMetadataVersionGreaterThan(settingQueryBean.getMetadataVersion());
		}

		if(settingQueryBean.getOrderByType() != null && settingQueryBean.getOrderByFieldName() != null) {
			metadataExample.setOrderByClause(getOrderByClause(settingQueryBean.getOrderByFieldName().name(),settingQueryBean.getOrderByType().name()));
		}

		if (settingQueryBean.isResolveSettings()) {
			return fetchSettingsPerLocation(settingQueryBean, metadataExample, treeNodeHashMap, limit, criteria);
		} else {
			if (StringUtils.isNotEmpty(locationId)) {
				criteria.andLocationIdEqualTo(locationId);
			}
			return settingMetadataMapper.selectMany(metadataExample, 0, limit);
		}
	}

	public List<SettingsMetadata> findSettingsMetadata(SettingSearchBean settingQueryBean, Map<String, TreeNode<String, Location>> treeNodeHashMap, int limit) {
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		SettingsMetadataExample.Criteria criteria = metadataExample.createCriteria();
		String locationId = settingQueryBean.getLocationId();
		Long primaryKey = settingQueryBean.getPrimaryKey();
		updateCriteria(criteria, settingQueryBean);

		if (primaryKey != null) {
			metadataExample.or(metadataExample.createCriteria().andSettingsIdEqualTo(primaryKey));
		}

		if (StringUtils.isBlank(settingQueryBean.getId())) {
			criteria.andServerVersionGreaterThanOrEqualTo(settingQueryBean.getServerVersion());
		}

		if (settingQueryBean.getMetadataVersion() != null) {
			criteria.andMetadataVersionGreaterThan(settingQueryBean.getMetadataVersion());
		}

		if (settingQueryBean.getOrderByType() != null && settingQueryBean.getOrderByFieldName() != null) {
			metadataExample.setOrderByClause(getOrderByClause(settingQueryBean.getOrderByFieldName().name(), settingQueryBean.getOrderByType().name()));
		}

		if (settingQueryBean.isResolveSettings()) {
			return fetchSettingsMetadataPerLocation(settingQueryBean, metadataExample, treeNodeHashMap, limit, criteria);
		} else {
			if (StringUtils.isNotEmpty(locationId)) {
				criteria.andLocationIdEqualTo(locationId);
			}
			return settingMetadataMapper.selectManySettingsMetadata(metadataExample, 0, limit);
		}
	}

	private SettingsMetadataExample.Criteria updateCriteria(SettingsMetadataExample.Criteria criteria,
			SettingSearchBean settingQueryBean) {
		String providerId = settingQueryBean.getProviderId();
		String team = settingQueryBean.getTeam();
		String teamId = settingQueryBean.getTeamId();
		String documentId = settingQueryBean.getDocumentId();
		String identifier = settingQueryBean.getIdentifier();
		String locationId = settingQueryBean.getLocationId();
		String id = settingQueryBean.getId();

		if (StringUtils.isBlank(providerId) && StringUtils.isBlank(locationId) && StringUtils.isBlank(team)
				&& StringUtils.isBlank(teamId) && StringUtils.isBlank(documentId) && StringUtils.isBlank(identifier)
				&& StringUtils.isBlank(id)) {
			criteria.andTeamIdIsNull().andTeamIsNull().andProviderIdIsNull().andLocationIdIsNull().andDocumentIdIsNotNull()
					.andIdentifierIsNotNull().andIdIsNotNull();
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
			if (StringUtils.isNotBlank(id)) {
				criteria.andIdEqualTo(Long.valueOf(id));
			}
		}

		return criteria;
	}

	private List<SettingsAndSettingsMetadataJoined> fetchSettingsPerLocation(SettingSearchBean settingSearchBean,
			SettingsMetadataExample metadataExample, Map<String, TreeNode<String, Location>> treeNodeHashMap, int limit,
			SettingsMetadataExample.Criteria criteria) {

		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = new ArrayList<>();
		if (StringUtils.isNotBlank(settingSearchBean.getLocationId())) {
			locationUuid = settingSearchBean.getLocationId();

			reformattedLocationHierarchy.clear();
			if (treeNodeHashMap != null && treeNodeHashMap.size() > 0) {
				reformattedLocationHierarchy(treeNodeHashMap);
			}

			if (reformattedLocationHierarchy.size() > 0) {
				criteria.andLocationIdIn(reformattedLocationHierarchy);
				settingsAndSettingsMetadataJoinedList = settingMetadataMapper.selectMany(metadataExample, 0, limit);
			}
		}

		return settingsAndSettingsMetadataJoinedList;
	}

	private List<SettingsMetadata> fetchSettingsMetadataPerLocation(SettingSearchBean settingSearchBean,
																	SettingsMetadataExample metadataExample, Map<String, TreeNode<String, Location>> treeNodeHashMap, int limit,
																	SettingsMetadataExample.Criteria criteria) {

		List<SettingsMetadata> settingsMetadataList = new ArrayList<>();
		if (StringUtils.isNotBlank(settingSearchBean.getLocationId())) {
			locationUuid = settingSearchBean.getLocationId();

			reformattedLocationHierarchy.clear();
			if (treeNodeHashMap != null && treeNodeHashMap.size() > 0) {
				reformattedLocationHierarchy(treeNodeHashMap);
			}

			if (reformattedLocationHierarchy.size() > 0) {
				criteria.andLocationIdIn(reformattedLocationHierarchy);
				settingsMetadataList = settingMetadataMapper.selectManySettingsMetadata(metadataExample, 0, limit);
			}
		}

		return settingsMetadataList;
	}

	private void reformattedLocationHierarchy(Map<String, TreeNode<String, Location>> parentLocation) {
		Map.Entry<String, TreeNode<String, Location>> stringMapEntry = parentLocation.entrySet().iterator().next();
		String locationKey = stringMapEntry.getKey();
		if (StringUtils.isNotBlank(locationKey)) {
			reformattedLocationHierarchy.add(locationKey);
		}

		if (!locationUuid.equals(locationKey)) {
			TreeNode<String, Location> childLocation = stringMapEntry.getValue();
			Map<String, TreeNode<String, Location>> newLocation = childLocation.getChildren();
			reformattedLocationHierarchy(newLocation);
		}
	}

	private List<SettingConfiguration> resolveSettings(
			List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoined, boolean isV1Settings,
			boolean resolveSettings) {
		Map<String, SettingConfiguration> stringConfigurationMap = new HashMap<>();
		Map<String, Setting> stringSettingsMap = new HashMap<>();

		List<SettingConfiguration> configurations = convertToSettingConfigurations(settingsAndSettingsMetadataJoined,
				isV1Settings, resolveSettings);

		for (SettingConfiguration configuration : configurations) {
			String configKey =
					configuration.getId() + "_" + configuration.getLocationId() + "_" + configuration.getIdentifier();
			stringConfigurationMap.put(configKey, configuration);

			List<Setting> settingsList = configuration.getSettings();
			for (Setting setting : settingsList) {
				String settingKey = setting.getKey() + "_" + configuration.getIdentifier();
				stringSettingsMap.put(settingKey, setting);
			}
		}

		return reconcileConfigurations(stringConfigurationMap, stringSettingsMap);
	}

	private List<SettingConfiguration> resolveSettingsMetadata(
			List<SettingsMetadata> settingsMetadataList, boolean isV1Settings,
			boolean resolveSettings) {
		Map<String, SettingConfiguration> stringConfigurationMap = new HashMap<>();
		Map<String, Setting> stringSettingsMap = new HashMap<>();

		List<SettingConfiguration> configurations = convertSettingsMetadataToSettingConfigurations(settingsMetadataList,
				isV1Settings, resolveSettings);

		for (SettingConfiguration configuration : configurations) {
			String configKey =
					configuration.getId() + "_" + configuration.getLocationId() + "_" + configuration.getIdentifier();
			stringConfigurationMap.put(configKey, configuration);

			List<Setting> settingsList = configuration.getSettings();
			for (Setting setting : settingsList) {
				String settingKey = setting.getKey() + "_" + configuration.getIdentifier();
				stringSettingsMap.put(settingKey, setting);
			}
		}

		return reconcileConfigurations(stringConfigurationMap, stringSettingsMap);
	}

	private List<SettingConfiguration> reconcileConfigurations(Map<String, SettingConfiguration> stringConfigurationMap,
			Map<String, Setting> stringSettingsMap) {
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
				findSettingsAndSettingsMetadata(settingSearchBean, null);
		Settings pgSetting =
				settingsAndSettingsMetadataJoined == null ? null : settingsAndSettingsMetadataJoined.getSettings();
		if (pgSetting == null) {
			return null;
		}
		return pgSetting.getId();
	}

	private SettingSearchBean createDocumentIdSearchBean(SettingConfiguration settingConfiguration) {
		SettingSearchBean settingSearchBean = new SettingSearchBean();
		Object uniqueId = getUniqueField(settingConfiguration);
		if (uniqueId == null) {
			return settingSearchBean;
		}

		String settingId = settingConfiguration.getId();

		if (StringUtils.isNotBlank(settingId)) {
			settingSearchBean.setDocumentId(uniqueId.toString());
		} else {
			settingSearchBean.setIdentifier(uniqueId.toString());
		}

		return settingSearchBean;
	}

	@Override
	protected Object getUniqueField(SettingConfiguration settingConfiguration) {
		if (settingConfiguration == null) {
			return null;
		}
		String settingId = settingConfiguration.getId();
		String settingIdentifier = settingConfiguration.getIdentifier();

		return StringUtils.isNotBlank(settingId) ? settingId : settingIdentifier;
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

	private List<SettingConfiguration> convertToSettingConfigurations(
			List<SettingsAndSettingsMetadataJoined> jointSettings) {
		return convertToSettingConfigurations(jointSettings, false, false);
	}

	private List<SettingConfiguration> convertToSettingConfigurations(
			List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoined, boolean isV1Settings,
			boolean resolveSettings) {
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
			settingConfiguration.getSettings()
					.add(convertToSetting(jointSetting.getSettingsMetadata(), isV1Settings, resolveSettings));
			settingConfiguration.setDocumentId(settingConfiguration.getSettings().get(0).getDocumentId());
			settingConfiguration.setIdentifier(settingConfiguration.getSettings().get(0).getSettingIdentifier());
		}
		settingConfigurations.addAll(settingConfigurationMap.values());
		return settingConfigurations;
	}

	private List<SettingConfiguration> convertSettingsMetadataToSettingConfigurations(
			List<SettingsMetadata> settingsMetadataList, boolean isV1Settings,
			boolean resolveSettings) {
		List<SettingConfiguration> settingConfigurations = new ArrayList<>();
		if (settingsMetadataList == null || settingsMetadataList.isEmpty()) {
			return settingConfigurations;
		}

		SettingConfiguration settingConfiguration = new SettingConfiguration();
		settingConfiguration.setSettings(new ArrayList<>());
		for (SettingsMetadata settingsMetadata : settingsMetadataList) {
			settingConfiguration.getSettings().add(convertToSetting(settingsMetadata, isV1Settings, resolveSettings));
			settingConfiguration.setDocumentId(settingConfiguration.getSettings().get(0).getDocumentId());
			settingConfiguration.setIdentifier(settingConfiguration.getSettings().get(0).getSettingIdentifier());
		}
		settingConfigurations.add(settingConfiguration);
		return settingConfigurations;
	}

	private Setting convertToSetting(SettingsMetadata settingsMetadata, boolean isV1Settings, boolean resolveSettings) {
		Setting setting = new Setting();
		getSettingValue(settingsMetadata, setting);
		setting.setKey(settingsMetadata.getSettingKey());
		setting.setSettingMetadataId(String.valueOf(settingsMetadata.getId()));
		setting.setUuid(settingsMetadata.getUuid());
		if (resolveSettings && !locationUuid.equals(settingsMetadata.getLocationId())) {
			setting.setInheritedFrom(settingsMetadata.getLocationId());
		} else {
			setting.setInheritedFrom(settingsMetadata.getInheritedFrom());
		}
		setting.setDescription(settingsMetadata.getSettingDescription());
		setting.setLabel(settingsMetadata.getSettingLabel());
		setting.setSettingIdentifier(settingsMetadata.getIdentifier());
		if (settingsMetadata.getMetadataVersion() != null) {
			setting.setMetadataVersion(settingsMetadata.getMetadataVersion());
		}
		if (!isV1Settings) {
			setting.setProviderId(settingsMetadata.getProviderId());
			setting.setSettingsId(String.valueOf(settingsMetadata.getSettingsId()));
			setting.setTeamId(settingsMetadata.getTeamId());
			setting.setTeam(settingsMetadata.getTeam());
			setting.setLocationId(settingsMetadata.getLocationId());
			setting.setType(settingsMetadata.getSettingType());
			if (settingsMetadata.getServerVersion() != null) {
				setting.setServerVersion(settingsMetadata.getServerVersion());
			}
			setting.setDocumentId(settingsMetadata.getDocumentId());
		}
		return setting;
	}

	private Setting convertToSetting(SettingsMetadata settingsMetadata, boolean isV1Settings) {
		return convertToSetting(settingsMetadata, isV1Settings, false);
	}

	private void getSettingValue(SettingsMetadata settingsMetadata, Setting setting) {
		String value = settingsMetadata.getSettingValue();
		if (StringUtils.isNotBlank(value)) {
			try {
				JSONArray jsonArray = new JSONArray(value);
				setting.setValues(jsonArray);
			}
			catch (JSONException e) {
				setting.setValue(value);
			}
		}
	}

	@Override
	public String addOrUpdate(Setting setting) {
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
		settingConfiguration.setDocumentId(setting.getDocumentId());
		if (StringUtils.isNotBlank(setting.getSettingsId())) {
			settingConfiguration.setId(setting.getSettingsId());
		}
		settingConfiguration.setV1Settings(false);

		if (StringUtils.isNotBlank(setting.getId())) {
			update(settingConfiguration);
		} else {
			return addSettings(settingConfiguration);
		}

		return null;
	}

	@Override
	public void delete(Long settingId) {
		settingMetadataMapper.deleteByPrimaryKey(settingId);
	}

	@Override
	public String addSettings(SettingConfiguration entity) {
		if (entity == null || entity.getSettings() == null || entity.getIdentifier() == null) {
			return null;
		}

		Long id = retrievePrimaryKey(entity);
		List<Setting> settings;
		Settings pgSettings;

		if (id == null) {
			if (entity.getId() == null || entity.getId().isEmpty()) {
				entity.setId(UUID.randomUUID().toString());
			}

			setRevision(entity);

			settings = entity.getSettings();
			entity.setSettings(null); // strip out the settings block
			pgSettings = convert(entity, id);
			if (pgSettings == null) {
				return null;
			}

			int rowsAffected = settingMapper.insertSelectiveAndSetId(pgSettings);
			if (rowsAffected < 1 || pgSettings.getId() == null) {
				return null;
			}
			
			updateServerVersion(pgSettings, entity);
		} else {
			settings = entity.getSettings();
			pgSettings = convert(entity, id);
		}

		return checkWhetherMetadataExistsBeforeSave(entity, settings, pgSettings);
	}

	private String checkWhetherMetadataExistsBeforeSave(SettingConfiguration entity, List<Setting> settings,
			Settings pgSettings) {
		entity.setSettings(settings); // re-inject settings block
		List<SettingsMetadata> settingsMetadata = createMetadata(entity, pgSettings.getId());
		List<SettingsMetadata> settingsMetadataList = new ArrayList<>();

		for (SettingsMetadata metadata : settingsMetadata) {
			if (!checkIfMetadataExists(metadata) && StringUtils.isNotBlank(metadata.getSettingValue())) { // Add a check to restrict persistence of settings metadata with empty value
				settingsMetadataList.add(metadata);
			}
		}

		return insertSettingMetadata(settingsMetadataList);
	}

	private String insertSettingMetadata(List<SettingsMetadata> settingsMetadataList) {
		String insertSettingMetadata = "INSERT INTO core.settings_metadata ( settings_id, document_id, identifier, "
				+ "server_version, team, team_id, provider_id, location_id, uuid, json, setting_type, setting_value, "
				+ "setting_key, setting_description, setting_label, inherited_from) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"
				+ "?) ON conflict DO NOTHING";

		Connection connection = null;

		List<String> notSavedSettings = new ArrayList<>();

		try {
			connection = DataSourceUtils.getConnection(openSRPDataSource);
			PreparedStatement preparedStatement = connection.prepareStatement(insertSettingMetadata);
			for (SettingsMetadata settingsMetadata : settingsMetadataList) {

				ObjectMapper objectMapper = new ObjectMapper();
				String json;
				try {
					json = objectMapper.writeValueAsString(settingsMetadata.getJson());

					PGobject pGobject = new PGobject();
					pGobject.setType("json");
					pGobject.setValue(json);

					preparedStatement.setLong(1, settingsMetadata.getSettingsId());
					preparedStatement.setString(2, settingsMetadata.getDocumentId());
					preparedStatement.setString(3, settingsMetadata.getIdentifier());
					preparedStatement.setLong(4, settingsMetadata.getServerVersion());
					preparedStatement.setString(5, settingsMetadata.getTeam());
					preparedStatement.setString(6, settingsMetadata.getTeamId());
					preparedStatement.setString(7, settingsMetadata.getProviderId());
					preparedStatement.setString(8, settingsMetadata.getLocationId());
					preparedStatement.setString(9, settingsMetadata.getUuid());
					preparedStatement.setObject(10, pGobject);
					preparedStatement.setString(11, settingsMetadata.getSettingType());
					preparedStatement.setString(12, settingsMetadata.getSettingValue());
					preparedStatement.setString(13, settingsMetadata.getSettingKey());
					preparedStatement.setString(14, settingsMetadata.getSettingDescription());
					preparedStatement.setString(15, settingsMetadata.getSettingLabel());
					preparedStatement.setString(16, settingsMetadata.getInheritedFrom());
					preparedStatement.addBatch();
				}
				catch (JsonProcessingException e) {
					e.printStackTrace();
					notSavedSettings.add(settingsMetadata.getSettingKey());
				}
			}
			preparedStatement.executeBatch();
			preparedStatement.close();
			connection.close();
		}
		catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new DatabaseException(e.getMessage());
		}
		finally {
			if (connection != null) {
				DataSourceUtils.releaseConnection(connection, openSRPDataSource);
			}
		}

		String responce = "";
		if (notSavedSettings.size() > 0) {
			responce = notSavedSettings.toString();
		}

		return responce;
	}

	private boolean checkIfMetadataExists(SettingsMetadata settingsMetadata) {
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		SettingsMetadataExample.Criteria criteria = metadataExample.createCriteria();

		String locationId = settingsMetadata.getLocationId();
		String teamId = settingsMetadata.getTeamId();
		String settingsId = String.valueOf(settingsMetadata.getSettingsId());
		String settingKey = settingsMetadata.getSettingKey();
		String documentId = settingsMetadata.getDocumentId();

		if (StringUtils.isNotBlank(locationId)) {
			criteria.andLocationIdEqualTo(locationId);
		}
		if (StringUtils.isNotBlank(teamId)) {
			criteria.andTeamIdEqualTo(teamId);
		}

		if (StringUtils.isNotBlank(settingsId)) {
			criteria.andSettingsIdEqualTo(Long.valueOf(settingsId));
		}

		if (StringUtils.isNotBlank(documentId)) {
			criteria.andDocumentIdEqualTo(documentId);
		}

		if (StringUtils.isNotBlank(settingKey)) {
			criteria.andSettingKeyEqualTo(settingKey);
		}
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = settingMetadataMapper
				.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE);
		return settingsAndSettingsMetadataJoinedList.size() > 0;
	}

	private List<SettingsMetadata> createMetadata(SettingConfiguration settingConfiguration, Long id) {
		List<SettingsMetadata> settingsMetadata = new ArrayList<>();
		if (settingConfiguration != null) {
			List<Setting> settings = settingConfiguration.getSettings();

			try {
				for (Setting setting : settings) {
					SettingsMetadata metadata = new SettingsMetadata();
					metadata.setSettingKey(setting.getKey());
					if (StringUtils.isNotBlank(setting.getValue())) {
						metadata.setSettingValue(setting.getValue());
					}

					if (setting.getValues() != null && setting.getValues().length() > 0) {
						metadata.setSettingValue(String.valueOf(setting.getValues()));
					}
					metadata.setSettingDescription(setting.getDescription());
					metadata.setSettingLabel(setting.getLabel());
					metadata.setSettingsId(id);
					metadata.setSettingType(setting.getType());
					metadata.setUuid(setting.getUuid() != null ? setting.getUuid() : UUID.randomUUID().toString());
					metadata.setInheritedFrom(setting.getInheritedFrom());
					metadata.setDocumentId(
							settingConfiguration.getId() != null ?
									settingConfiguration.getId() :
									UUID.randomUUID().toString());
					metadata.setIdentifier(settingConfiguration.getIdentifier());
					checkIdentityAttributtes(settingConfiguration, metadata);
					metadata.setServerVersion(settingConfiguration.getServerVersion());
					metadata.setMetadataVersion(settingConfiguration.getMetadataVersion());
					metadata.setJson(convertToSetting(metadata, false)); //always want to create the json on the settings
					// creation

					settingsMetadata.add(metadata);
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return settingsMetadata;
	}

	private void checkIdentityAttributtes(SettingConfiguration settingConfiguration, SettingsMetadata metadata) {
		if (StringUtils.isNotBlank(settingConfiguration.getProviderId())) {
			metadata.setProviderId(settingConfiguration.getProviderId());
		}

		if (StringUtils.isNotBlank(settingConfiguration.getLocationId())) {
			metadata.setLocationId(settingConfiguration.getLocationId());
		}

		if (StringUtils.isNotBlank(settingConfiguration.getTeam())) {
			metadata.setTeam(settingConfiguration.getTeam());
		}

		if (StringUtils.isNotBlank(settingConfiguration.getTeamId())) {
			metadata.setTeamId(settingConfiguration.getTeamId());
		}
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

		SettingConfiguration settingConfiguration = findSetting(settingQueryBean, null);
		return settingConfiguration == null ? null : convert(settingConfiguration, id);
	}

	public List<String> getReformattedLocationHierarchy() {
		return reformattedLocationHierarchy;
	}

	@Override
	public List<SettingsAndSettingsMetadataJoined> findSettingsAndSettingsMetadataByIdentifier(String identifier) {
		SettingsMetadataExample metadataExample = new SettingsMetadataExample();
		SettingsMetadataExample.Criteria criteria = metadataExample.createCriteria();
		criteria.andIdentifierEqualTo(identifier);
		return settingMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE);
	}

	/**
	 * This method deletes existing settings metadata by settings key, identifier and locationId
	 * @param identifier is the settings identifer
	 * @param key is the settings key
	 * @param locationId is used as an optional param to delete settings metadata records
	 */
	private void deleteExistingSettingsMetadataByKeyAndIdentifier(String identifier, String key, String locationId) {
		SettingsMetadataExample metadataExample;
		metadataExample = new SettingsMetadataExample();
		SettingsMetadataExample.Criteria criteria = metadataExample.createCriteria();

		if (StringUtils.isNotBlank(locationId)) {
			criteria.andLocationIdEqualTo(locationId);
		}

		criteria.andIdentifierEqualTo(identifier).andSettingKeyEqualTo(key);
		settingMetadataMapper.deleteByExample(metadataExample);
	}

}
