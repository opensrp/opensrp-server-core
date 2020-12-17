package org.opensrp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.opensrp.api.domain.Event;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.dto.ExportFlagProblemEventImageMetadata;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.smartregister.utils.DateTimeTypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.repository.postgres.EventsRepositoryTest.createFlagProblemEvent;
import static org.opensrp.util.constants.EventDataExportConstants.SETTINGS_CONFIGURATION_EVENT_TYPE_TO_SETTINGS_IDENTIFIER;

public class ExportEventDataMapperTest extends BaseRepositoryTest  {

	@InjectMocks
	private ExportEventDataMapper exportEventDataMapper;

	@Mock
	private SettingService settingService;

	@Autowired
	@Qualifier("eventsRepositoryPostgres")
	private EventsRepository eventsRepository;

	private Set<String> scripts = new HashSet<String>();

	private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
			.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

	@Before
	public void setUpPostgresRepository() {
		initMocks(this);
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		scripts.add("event.sql");
		scripts.add("client.sql");
		return scripts;
	}


	@Test
	public void testGetExportEventDataAfterMappingForHeaderWithoutSettings() {
		eventsRepository.add(createFlagProblemEvent());
		List<org.opensrp.domain.postgres.Event> events = eventsRepository.getEventData("335ef7a3-7f35-58aa-8263-4419464946d8", "flag_problem", null, null);
		String json = "";
		if (events != null  && events.get(0) != null && events.get(0).getJson() != null) {
			json = gson.toJson(events.get(0).getJson());
		}
		Event event = gson.fromJson(json, Event.class);
		List<Object> result = exportEventDataMapper.getExportEventDataAfterMapping(events.get(0).getJson(),"flag_problem",true,false);
	    assertNotNull(result);
	    int eventObsSize = event.getObs().size();
	    int resultSize = result.size();
	    assertEquals(eventObsSize, resultSize);
	    assertEquals(event.getObs().get(0).getFormSubmissionField(), result.get(0));
	    assertEquals(event.getObs().get(eventObsSize - 1).getFormSubmissionField(), result.get(resultSize - 1));
	}

	@Test
	public void testGetExportEventDataAfterMappingForRowDataWithoutSettings() {
		eventsRepository.add(createFlagProblemEvent());
		List<org.opensrp.domain.postgres.Event> events = eventsRepository.getEventData("335ef7a3-7f35-58aa-8263-4419464946d8", "flag_problem", null, null);
		String json = "";
		if (events != null  && events.get(0) != null && events.get(0).getJson() != null) {
			json = gson.toJson(events.get(0).getJson());
		}
		Event event = gson.fromJson(json, Event.class);
		List<Object> result = exportEventDataMapper.getExportEventDataAfterMapping(events.get(0).getJson(),"flag_problem",false,false);
		assertNotNull(result);
		int eventObsSize = event.getObs().size();
		int resultSize = result.size();
		assertEquals(eventObsSize, resultSize);
		assertEquals(event.getObs().get(0).getValues(), result.get(0));
		assertEquals(event.getObs().get(eventObsSize - 1).getValues(), result.get(resultSize - 1));
	}

	@Test
	public void testGetExportEventDataAfterMappingForHeaderWithSettings() {
		eventsRepository.add(createFlagProblemEvent());
		List<org.opensrp.domain.postgres.Event> events = eventsRepository.getEventData("335ef7a3-7f35-58aa-8263-4419464946d8", "flag_problem", null, null);
		when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_EVENT_TYPE_TO_SETTINGS_IDENTIFIER)).thenReturn(createSettingsMetaDataAgainstIdentfier());
		doReturn(createSettingsConfigurations()).when(settingService).findSettingsByIdentifier("event_type_flag_problem");
		List<Object> result = exportEventDataMapper.getExportEventDataAfterMapping(events.get(0).getJson(),"flag_problem",true,true);
		assertNotNull(result);
		assertEquals("Location id", result.get(0));
		assertEquals("Location name", result.get(1));
	}

	@Test
	public void testGetExportEventDataAfterMappingForRowDataWithSettings() {
		eventsRepository.add(createFlagProblemEvent());
		List<org.opensrp.domain.postgres.Event> events = eventsRepository.getEventData("335ef7a3-7f35-58aa-8263-4419464946d8", "flag_problem", null, null);
		when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_EVENT_TYPE_TO_SETTINGS_IDENTIFIER)).thenReturn(createSettingsMetaDataAgainstIdentfier());
		doReturn(createSettingsConfigurations()).when(settingService).findSettingsByIdentifier("event_type_flag_problem");
		List<Object> result = exportEventDataMapper.getExportEventDataAfterMapping(events.get(0).getJson(),"flag_problem",false,true);
		assertNotNull(result);
		assertEquals("f3199af5-2eaf-46df-87c9-40d59606a2fb", result.get(0));
		assertEquals("EPP Ambodisatrana 2", result.get(1));
	}

	@Test
	public void testGetFlagProblemEventImagesMetadata() {
		eventsRepository.add(createFlagProblemEvent());
		List<org.opensrp.domain.postgres.Event> events = eventsRepository.getEventData("335ef7a3-7f35-58aa-8263-4419464946d8", "flag_problem", null, null);
		ExportFlagProblemEventImageMetadata exportFlagProblemEventImageMetadata = exportEventDataMapper.getFlagProblemEventImagesMetadata(events.get(0).getJson(),"$.baseEntityId", "$.details.locationName", "$.details.productName");
	    assertNotNull(exportFlagProblemEventImageMetadata);
	    assertEquals("ddcaf383-882e-448b-b701-8b72cb0d4d7a", exportFlagProblemEventImageMetadata.getStockId());
	    assertEquals("EPP Ambodisatrana 2", exportFlagProblemEventImageMetadata.getServicePointName());
	    assertEquals("Midwifery Kit", exportFlagProblemEventImageMetadata.getProductName());
	}


	private List<SettingsAndSettingsMetadataJoined> createSettingsConfigurations() {
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = new ArrayList<>();
		SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined = new SettingsAndSettingsMetadataJoined();
        SettingsMetadata settingsMetadata = new SettingsMetadata();
		settingsMetadata.setSettingLabel("Location id");
		settingsMetadata.setSettingKey("$.locationId");
		settingsAndSettingsMetadataJoined.setSettingsMetadata(settingsMetadata);
		settingsAndSettingsMetadataJoinedList.add(settingsAndSettingsMetadataJoined);
		settingsMetadata = new SettingsMetadata();
		settingsAndSettingsMetadataJoined = new SettingsAndSettingsMetadataJoined();
		settingsMetadata.setSettingLabel("Location name");
		settingsMetadata.setSettingKey("$.details.locationName");
		settingsAndSettingsMetadataJoined.setSettingsMetadata(settingsMetadata);
		settingsAndSettingsMetadataJoinedList.add(settingsAndSettingsMetadataJoined);
		return settingsAndSettingsMetadataJoinedList;
	}

	private List<SettingsAndSettingsMetadataJoined> createSettingsMetaDataAgainstIdentfier() {
		SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined = new SettingsAndSettingsMetadataJoined();
		SettingsMetadata settingsMetadata = new SettingsMetadata();
		settingsMetadata.setSettingKey("flag_problem");
		settingsMetadata.setSettingValue("event_type_flag_problem");
		settingsAndSettingsMetadataJoined.setSettingsMetadata(settingsMetadata);
		return Collections.singletonList(settingsAndSettingsMetadataJoined);
	}
}
