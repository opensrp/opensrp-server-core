package org.opensrp.service;

import org.opensrp.api.domain.Location;
import org.opensrp.api.util.TreeNode;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SETTINGS_VIEW;
import org.opensrp.repository.SettingRepository;
import org.opensrp.repository.postgres.handler.SettingTypeHandler;
import org.opensrp.search.SettingSearchBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
	@PreAuthorize("hasRole('SETTINGS_VIEW_VIEW')")
	public List<SETTINGS_VIEW> findSettings(SettingSearchBean settingQueryBean,
			Map<String, TreeNode<String, Location>> treeNodeHashMap) {
		return settingRepository.findSettings(settingQueryBean,treeNodeHashMap);
	}

	/**
	 * Used to add the server version to payloads
	 */
	public void addServerVersion() {
		try {
			List<SETTINGS_VIEW> SETTINGS_VIEWs = settingRepository.findByEmptyServerVersion();
			logger.info("RUNNING addServerVersion settings size: " + SETTINGS_VIEWs.size());
			long currentTimeMillis = System.currentTimeMillis();
			for (SETTINGS_VIEW SETTINGS_VIEW : SETTINGS_VIEWs) {
				try {
					Thread.sleep(1);
					SETTINGS_VIEW.setServerVersion(currentTimeMillis);
					settingRepository.update(SETTINGS_VIEW);
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
	 * Used by the v1 setting endpoint to create the settings configuration {@link SETTINGS_VIEW} & save the settings
	 *
	 * @param jsonSETTINGS_VIEW {@link String} -- the string representation of the settings configuration
	 * @return
	 */
	@PreAuthorize("hasRole('SETTINGS_VIEW_CREATE') or hasRole('SETTINGS_VIEW_UPDATE')")
	public synchronized String saveSetting(String jsonSETTINGS_VIEW) {
		SettingTypeHandler settingTypeHandler = new SettingTypeHandler();
		SETTINGS_VIEW SETTINGS_VIEWs = null;
		try {
			SETTINGS_VIEWs = settingTypeHandler.mapper
					.readValue(jsonSETTINGS_VIEW, SETTINGS_VIEW.class);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		SETTINGS_VIEWs.setServerVersion(Calendar.getInstance().getTimeInMillis());
		SETTINGS_VIEWs.setV1Settings(true);

		if (SETTINGS_VIEWs.getId() != null && settingRepository.get(SETTINGS_VIEWs.getId()) != null) {
			settingRepository.update(SETTINGS_VIEWs);

		} else {
			settingRepository.add(SETTINGS_VIEWs);
		}

		return SETTINGS_VIEWs.getIdentifier();

	}

	/**
	 * Gets a single setting object from the v2 endpoint to save
	 *
	 * @param setting {@link Setting}
	 */
	@PreAuthorize("hasRole('SETTINGS_VIEW_CREATE') or hasRole('SETTINGS_VIEW_UPDATE')")
	public void addOrUpdateSettings(Setting setting) {
		if (setting != null) {
			settingRepository.addOrUpdate(setting);
		}
	}

	/**
	 * Performs a settings delete using the v2 endpoint
	 *
	 * @param id {@link Long} -- settings id
	 */
	@PreAuthorize("hasRole('SETTINGS_VIEW_DELETE')")
	public void deleteSetting(Long id) {
		if (id != null) {
			settingRepository.delete(id);
		}
	}

}
