package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.TreeNode;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.repository.SettingRepository;
import org.opensrp.repository.postgres.handler.SettingTypeHandler;
import org.opensrp.search.SettingSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SettingService {

    private static Logger logger = LogManager.getLogger(SettingService.class.toString());

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
    public List<SettingConfiguration> findSettings(SettingSearchBean settingQueryBean,
                                                   Map<String, TreeNode<String, Location>> treeNodeHashMap) {
        return settingRepository.findSettings(settingQueryBean, treeNodeHashMap);
    }


    /**
     * Used by the v1 setting endpoint to create the settings configuration {@link SettingConfiguration} & save the settings
     *
     * @param jsonSettingConfiguration {@link String} -- the string representation of the settings configuration
     * @return
     */
    @PreAuthorize("hasRole('SETTINGS_VIEW_CREATE') or hasRole('SETTINGS_VIEW_UPDATE')")
    public synchronized String saveSetting(String jsonSettingConfiguration) {
        SettingTypeHandler settingTypeHandler = new SettingTypeHandler();
        SettingConfiguration settingConfigurations = null;
        try {
            settingConfigurations = settingTypeHandler.mapper
                    .readValue(jsonSettingConfiguration, SettingConfiguration.class);
        } catch (IOException e) {
            logger.error("error reading json ", e);
        }

        settingConfigurations.setV1Settings(true);

        SettingConfiguration existingConfiguration = null;

        if (StringUtils.isNotBlank(settingConfigurations.getId())) {
            existingConfiguration = settingRepository.get(settingConfigurations.getId());
        } else if (StringUtils.isNotBlank(settingConfigurations.getIdentifier())) {
            SettingSearchBean settingQueryBean = new SettingSearchBean();
            settingQueryBean.setIdentifier(settingConfigurations.getIdentifier());
            existingConfiguration = settingRepository.findSetting(settingQueryBean, null);
        }

        String settingsResponse = null;
        if (existingConfiguration != null) {
            Map<String, String> uuidMap = new HashMap<>();
            for (Setting setting : existingConfiguration.getSettings()) {
                uuidMap.put(setting.getKey(), setting.getUuid());
            }
            settingConfigurations.getSettings().stream().filter(s -> StringUtils.isBlank(s.getUuid()))
                    .forEach(s -> s.setUuid(uuidMap.get(s.getKey())));
            if (StringUtils.isBlank(settingConfigurations.getId())) {
                settingConfigurations.setId(existingConfiguration.getId());
            }
            settingRepository.update(settingConfigurations);

        } else {
            settingsResponse = settingRepository.addSettings(settingConfigurations);
        }

        String response = settingConfigurations.getIdentifier();
        if (StringUtils.isNotBlank(settingsResponse)) {
            response = response + String.format("%s%s", " The following settings might not be saved ", settingsResponse);
        }

        return response;

    }

    /**
     * Gets a single setting object from the v2 endpoint to save
     *
     * @param setting {@link Setting}
     * @return
     */

    @PreAuthorize("hasRole('SETTINGS_VIEW_CREATE') or hasRole('SETTINGS_VIEW_UPDATE')")
    public String addOrUpdateSettings(Setting setting) {
        String settingsResponse = null;

        if (setting != null) {
            settingsResponse = settingRepository.addOrUpdate(setting);
        }

        return settingsResponse;
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

    public List<SettingsAndSettingsMetadataJoined> findSettingsByIdentifier(String identifier) {
        return settingRepository.findSettingsAndSettingsMetadataByIdentifier(identifier);
    }

}
