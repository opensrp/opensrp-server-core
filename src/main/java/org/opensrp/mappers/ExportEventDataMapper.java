package org.opensrp.mappers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;
import org.joda.time.DateTime;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.service.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.utils.DateTimeTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opensrp.util.constants.EventDataExportConstants.*;

@Component
public class ExportEventDataMapper {

	@Autowired
	private SettingService settingService;

	private static Map<String, String> eventTypeToSettingsIdentifier = new HashMap<>();

	private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
			.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

	private static Logger logger = LoggerFactory.getLogger(ExportEventDataMapper.class.toString());

	static {
		eventTypeToSettingsIdentifier.put(EVENT_LOOKS_GOOD, SETTINGS_CONFIGURATION_EVENT_TYPE_LOOKS_GOOD);
		eventTypeToSettingsIdentifier.put(EVENT_FLAG_PROBLEM, SETTINGS_CONFIGURATION_EVENT_TYPE_FLAG_PROBLEM);
		eventTypeToSettingsIdentifier.put(EVENT_FIX_PROBLEM, SETTINGS_CONFIGURATION_EVENT_TYPE_FIX_PROBLEM);
		eventTypeToSettingsIdentifier.put(EVENT_RECORDS_GPS, SETTINGS_CONFIGURATION_EVENT_TYPE_RECORDS_GPS);
		eventTypeToSettingsIdentifier.put(EVENT_SERVICE_POINT_CHECK, SETTINGS_CONFIGURATION_EVENT_TYPE_SERVICE_POINT_CHECK);
	}

	public List<Object> getExportEventDataAfterMapping(Object jsonObject, String eventType, boolean returnHeader) {
		Map<String, String> columnNamesAndLabels = getColumnNamesAndLabelsByEventType(eventType);
		if (columnNamesAndLabels != null) {
			String json = "";
			if (jsonObject != null) {
				json = gson.toJson(jsonObject);
			}

			List<Object> headerData = new ArrayList<>();
			List<Object> rowData = new ArrayList<>();
			List<List<String>> allRows = new ArrayList<>();
			if (returnHeader == true) {
				for (Map.Entry<String, String> columnNameAndLabel : columnNamesAndLabels.entrySet()) {
					headerData.add(columnNameAndLabel.getKey());
				}
				return headerData;

			} else {
				for (Map.Entry<String, String> columnNameAndLabel : columnNamesAndLabels.entrySet()) {
					Object fieldValue = JsonPath.read(json, columnNameAndLabel.getValue());
					rowData.add(fieldValue);
				}
				return rowData;
			}
		}
		return null;
	}

	public Map<String, String> getColumnNamesAndLabelsByEventType(String eventType) {

		String settingsConfigurationIdentifier = getSettingsConfigurationIdentifierByEventType(eventType);

		Map<String, String> columnsLabelsAndKeys = new HashMap<>();
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = settingService
				.findSettingsByIdentifier(settingsConfigurationIdentifier);

		if (settingsAndSettingsMetadataJoinedList != null) {
			for (SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined : settingsAndSettingsMetadataJoinedList) {
				if (settingsAndSettingsMetadataJoined.getSettingsMetadata() != null
						&& settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingKey() != null
						&& settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingLabel() != null) {
					columnsLabelsAndKeys.put(settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingLabel(),
							settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingKey());
				}
			}
		}
		return columnsLabelsAndKeys;
	}

	private String getSettingsConfigurationIdentifierByEventType(String eventType) {
		return eventTypeToSettingsIdentifier.get(eventType);
	}
}
