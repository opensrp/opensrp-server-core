package org.opensrp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.JsonPathException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.api.domain.Event;
import org.opensrp.api.domain.Obs;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.dto.ExportFlagProblemEventImageMetadata;
import org.opensrp.repository.postgres.handler.BaseTypeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.opensrp.util.constants.EventDataExportConstants.OBS;
import static org.opensrp.util.constants.EventDataExportConstants.FORM_SUBMISSION_FIELD;
import static org.opensrp.util.constants.EventDataExportConstants.NOT_GOOD;
import static org.opensrp.util.constants.EventDataExportConstants.MISUSE;
import static org.opensrp.util.constants.EventDataExportConstants.SETTINGS_CONFIGURATION_EVENT_TYPE_TO_SETTINGS_IDENTIFIER;

@Component
public class ExportEventDataMapper {

	@Autowired
	private SettingService settingService;

    private ObjectMapper objectMapper = BaseTypeHandler.createObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);


	private static Logger logger = LogManager.getLogger(ExportEventDataMapper.class.toString());

	public List<Object> getExportEventDataAfterMapping(Object jsonObject, String eventType, boolean returnHeader,
			boolean isSettingsExists) throws JsonProcessingException {
		Map<String, String> columnNamesAndLabels = getColumnNamesAndLabelsByEventType(eventType);

		String json = "";
		if (jsonObject != null && !jsonObject.equals("")) {
			json = objectMapper.writeValueAsString(jsonObject);
		}

		List<Object> headerData = new ArrayList<>();
		List<Object> rowData = new ArrayList<>();
		if (columnNamesAndLabels != null && columnNamesAndLabels.size() > 0 && returnHeader && isSettingsExists) {
			for (Map.Entry<String, String> columnNameAndLabel : columnNamesAndLabels.entrySet()) {
				headerData.add(columnNameAndLabel.getKey());
			}
			return headerData;
		} else if (columnNamesAndLabels != null && !returnHeader && isSettingsExists) {
			for (Map.Entry<String, String> columnNameAndLabel : columnNamesAndLabels.entrySet()) {
				try {
					Object fieldValue = JsonPath.read(json, columnNameAndLabel.getValue());
					rowData.add(fieldValue);
				}
				catch (JsonPathException jsonPathException) {
					logger.error("Key does not exists ", jsonPathException.getMessage());
					rowData.add(null);
				}
			}
			return rowData;
		} else if (columnNamesAndLabels != null && !returnHeader && !isSettingsExists) {
			Event event = null;
			if(!json.equals("")) {
				event = objectMapper.readValue(json, Event.class);
				for (Obs obs : event.getObs()) {
					Object fieldValue = obs.getValues();
					rowData.add(fieldValue);
				}
				return rowData;
			}
			return null;
		} else { //for header without settings configurations
			Event event = null;
			if (!json.equals("")) {
				event = objectMapper.readValue(json, Event.class);
				for (Obs obs : event.getObs()) {
					Object fieldValue = obs.getFormSubmissionField() != null ? obs.getFormSubmissionField() : obs.getFieldCode();
					rowData.add(fieldValue);
				}
				return rowData;
			}
			return null;
		}
	}

	public ExportFlagProblemEventImageMetadata getFlagProblemEventImagesMetadata(Object jsonObject, String stockIdExpression,
			String servicePointNameExpression, String productNameExpression) throws JsonProcessingException {
		String json = "";
		ExportFlagProblemEventImageMetadata exportFlagProblemEventImageMetadata = new ExportFlagProblemEventImageMetadata();
		if (jsonObject != null) {
			json = objectMapper.writeValueAsString(jsonObject);
		}
		JSONObject eventJsonObject = new JSONObject(json);
		if (checkIfImageExists(eventJsonObject)) {
			String stockId = "";
			String servicePointName = "";
			String productName = "";
			Object fieldValue;
			try {
				fieldValue = JsonPath.read(json, stockIdExpression);
				stockId = (String) fieldValue;
				exportFlagProblemEventImageMetadata.setStockId(stockId);
			}
			catch (JsonPathException jsonPathException) {
				logger.error("Key does not exist" + jsonPathException.getMessage());
			}

			try {
				fieldValue = JsonPath.read(json, servicePointNameExpression);
				servicePointName = (String) fieldValue;
				exportFlagProblemEventImageMetadata.setServicePointName(servicePointName);
			}
			catch (JsonPathException jsonPathException) {
				logger.error("Key does not exist" + jsonPathException.getMessage());
			}

			try {
				fieldValue = JsonPath.read(json, productNameExpression);
				productName = (String) fieldValue;
				exportFlagProblemEventImageMetadata.setProductName(productName);
			}
			catch (JsonPathException jsonPathException) {
				logger.error("Key does not exist" + jsonPathException.getMessage());
			}

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
		JSONArray obsArray = jsonObject.optJSONArray(OBS);
		JSONObject properties;
		if (obsArray != null) {
			for (int i = 0; i < obsArray.length(); i++) {
				if (obsArray.get(i) != null) {
					properties = (JSONObject) obsArray.get(i);
					if (properties.has(FORM_SUBMISSION_FIELD) && (properties.get(FORM_SUBMISSION_FIELD).equals(NOT_GOOD) ||
							properties.get(FORM_SUBMISSION_FIELD).equals(MISUSE))) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
