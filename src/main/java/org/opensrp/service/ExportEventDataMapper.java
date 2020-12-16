package org.opensrp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.JsonPath;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.api.domain.Event;
import org.opensrp.api.domain.Obs;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.dto.ExportFlagProblemEventImageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.utils.DateTimeTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.opensrp.util.constants.EventDataExportConstants.*;

@Component
public class ExportEventDataMapper {

	@Autowired
	private SettingService settingService;

	private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
			.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

	private static Logger logger = LoggerFactory.getLogger(ExportEventDataMapper.class.toString());

	public List<Object> getExportEventDataAfterMapping(Object jsonObject, String eventType, boolean returnHeader,
			boolean isSettingsExists) {
		Map<String, String> columnNamesAndLabels = getColumnNamesAndLabelsByEventType(eventType);

		String json = "";
		if (jsonObject != null) {
			json = gson.toJson(jsonObject);
		}

		List<Object> headerData = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		if (columnNamesAndLabels != null && columnNamesAndLabels.size() > 0 && returnHeader && isSettingsExists) {
			for (Map.Entry<String, String> columnNameAndLabel : columnNamesAndLabels.entrySet()) {
				headerData.add(columnNameAndLabel.getKey());
			}
			return headerData;
		} else if (columnNamesAndLabels != null && !returnHeader && isSettingsExists) {
			JSONObject eventJsonObject = new JSONObject(json);
			for (Map.Entry<String, String> columnNameAndLabel : columnNamesAndLabels.entrySet()) {
				// TODO : What if the key does not exists

				Object fieldValue = JsonPath.read(json, columnNameAndLabel.getValue());
				rowData.add(fieldValue);
			}
			return rowData;
		} else if (columnNamesAndLabels != null && !returnHeader && !isSettingsExists) {
			Event event = gson.fromJson(json, Event.class);
			for (Obs obs : event.getObs()) {
				Object fieldValue = obs.getValues();
				rowData.add(fieldValue);
			}
			return rowData;
		} else { //for header without settings configurations
			Event event = null;
			if (json != "") {
				event = gson.fromJson(json, Event.class);
				for (Obs obs : event.getObs()) {
					Object fieldValue = obs.getFormSubmissionField();
					rowData.add(fieldValue);
				}
				return rowData;
			}
			return null;
		}
	}

	public ExportFlagProblemEventImageMetadata getFlagProblemEventImagesMetadata(Object jsonObject, String stockIdExpression, String servicePointIdExpression) {
		String json = "";
		ExportFlagProblemEventImageMetadata exportFlagProblemEventImageMetadata = new ExportFlagProblemEventImageMetadata();
		if (jsonObject != null) {
			json = gson.toJson(jsonObject);
		}
		JSONObject eventJsonObject = new JSONObject(json);
		if(checkIfImageExists(eventJsonObject)) {
			String stockId = "";
			String servicePointId = "";
			Object fieldValue;
			fieldValue = JsonPath.read(json, stockIdExpression);
			stockId = (String) fieldValue;
			exportFlagProblemEventImageMetadata.setStockId(stockId);

			fieldValue = JsonPath.read(json, servicePointIdExpression);
			servicePointId = (String) fieldValue;
			exportFlagProblemEventImageMetadata.setServicePointId(servicePointId);
			return exportFlagProblemEventImageMetadata;
		}
		return null;

	}

	public Map<String, String> getColumnNamesAndLabelsByEventType(String eventType) {

		String settingsConfigurationIdentifier = getSettingsConfigurationIdentifierByEventType(eventType);

		Map<String, String> columnsLabelsAndKeys = new LinkedHashMap<>();
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

		Map<String, String> eventTypeToSettingsConfigurationsIdentifier = new HashMap<>();
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = settingService
				.findSettingsByIdentifier(SETTINGS_CONFIGURATION_EVENT_TYPE_TO_SETTINGS_IDENTIFIER);

		if (settingsAndSettingsMetadataJoinedList != null) {
			for (SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined : settingsAndSettingsMetadataJoinedList) {
				if (settingsAndSettingsMetadataJoined.getSettingsMetadata() != null
						&& settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingKey() != null
						&& settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingValue() != null) {
					eventTypeToSettingsConfigurationsIdentifier
							.put(settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingKey(),
									settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingValue());
				}
			}
		}

		if (eventTypeToSettingsConfigurationsIdentifier != null && eventTypeToSettingsConfigurationsIdentifier.size() > 0) {
			return eventTypeToSettingsConfigurationsIdentifier.get(eventType);
		}
		return null;
	}

	private boolean checkIfImageExists(JSONObject jsonObject) {
		JSONArray obsArray = jsonObject.optJSONArray("obs");
		JSONObject properties ;
		if(obsArray != null) {
			for(int i = 0; i < obsArray.length(); i++) {
				if(obsArray.get(i) != null) {
					properties = (JSONObject) obsArray.get(i);
					if(properties.has("formSubmissionField") && (properties.get("formSubmissionField").equals("not_good") ||
							properties.get("formSubmissionField").equals("misuse"))) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
