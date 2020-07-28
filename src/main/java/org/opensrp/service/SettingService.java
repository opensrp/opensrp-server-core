package org.opensrp.service;

import org.opensrp.api.domain.Location;
import org.opensrp.api.util.TreeNode;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.repository.SettingRepository;
import org.opensrp.repository.postgres.handler.SettingTypeHandler;
import org.opensrp.search.SettingSearchBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

@Service
public class SettingService {

	private static Logger logger = LoggerFactory.getLogger(SettingService.class.toString());

	private SettingRepository settingRepository;

	@Autowired
	public void setSettingRepository(SettingRepository settingRepository) {
		this.settingRepository = settingRepository;
	}

	/**
	 * Initiates the find settings functionality
	 *
	 * @param settingQueryBean {@link SettingSearchBean} -- has the required parameters for the search
	 * @return
	 */
	public List<SettingConfiguration> findSettings(SettingSearchBean settingQueryBean,
			Map<String, TreeNode<String, Location>> treeNodeHashMap) {
		return settingRepository.findSettings(settingQueryBean, treeNodeHashMap);
	}

	/**
	 * Used to add the server version to payloads
	 */
	public void addServerVersion() {
		try {
			List<SettingConfiguration> settingConfigurations = settingRepository.findByEmptyServerVersion();
			logger.info("RUNNING addServerVersion settings size: " + settingConfigurations.size());
			long currentTimeMillis = System.currentTimeMillis();
			for (SettingConfiguration settingConfiguration : settingConfigurations) {
				try {
					Thread.sleep(1);
					settingConfiguration.setServerVersion(currentTimeMillis);
					settingRepository.update(settingConfiguration);
					currentTimeMillis += 1;
				}
				catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Used by the v1 setting endpoint to create the settings configuration {@link SettingConfiguration} & save the settings
	 *
	 * @param jsonSettingConfiguration {@link String} -- the string representation of the settings configuration
	 * @return
	 */
	public synchronized String saveSetting(String jsonSettingConfiguration) {
		SettingTypeHandler settingTypeHandler = new SettingTypeHandler();
		SettingConfiguration settingConfigurations = null;
		try {
			settingConfigurations = settingTypeHandler.mapper
					.readValue(jsonSettingConfiguration, SettingConfiguration.class);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		settingConfigurations.setServerVersion(Calendar.getInstance().getTimeInMillis());
		settingConfigurations.setV1Settings(true);

		if (settingConfigurations.getId() != null && settingRepository.get(settingConfigurations.getId()) != null) {
			settingRepository.update(settingConfigurations);

		} else {
			settingRepository.add(settingConfigurations);
		}

		return settingConfigurations.getIdentifier();

	}

	/**
	 * Gets a single setting object from the v2 endpoint to save
	 *
	 * @param setting {@link Setting}
	 */
	public void addOrUpdateSettings(Setting setting) {
		if (setting != null) {
			setting.setServerVersion(Calendar.getInstance().getTimeInMillis());
			settingRepository.addOrUpdate(setting);
		}
	}

	/**
	 * Performs a settings delete using the v2 endpoint
	 *
	 * @param id {@link Long} -- settings id
	 */
	public void deleteSetting(Long id) {
		if (id != null) {
			settingRepository.delete(id);
		}
	}

}
