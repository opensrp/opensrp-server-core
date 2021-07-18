package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.ZeirRapidProEntity;
import org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirChildClientConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirGrowthMonitoringConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirGrowthMonitoringConverter.GMEvent;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirMotherClientConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirVaccinationConverter;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static org.opensrp.domain.rapidpro.ZeirRapidProEntity.CARETAKER;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntity.CHILD;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntity.SUPERVISOR;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.GROWTH_MONITORING_DATA;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.IDENTIFIER;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.LOCATION_ID;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.REGISTRATION_DATA;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.VACCINATION_DATA;

/**
 * Subclass for BaseRapidProStateService that has ZIER implementation on how to update RapidPro contacts from OpenSRP
 */
@Service
public class ZeirRapidProStateService extends BaseRapidProStateService {

	private final ReentrantLock reentrantLock = new ReentrantLock();

	private final ObjectMapper objectMapper = new ObjectMapper();

	private EventService eventService;

	private ClientService clientService;

	public ZeirRapidProStateService() {
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	/**
	 * <p>
	 * This method is responsible for updating RapidProContact with data coming from OpenSRP.
	 * It is invoked after processing data from RapidPro is completed.
	 * For Birth Registration/Vaccination and Growth Monitoring events, event baseEntityId is saved as the property_key and
	 * the form submissionId of the event as the property_value in the rapidpro_state table. The Birth Registration event
	 * will be used to fetch other events associated with the client. First time registration in OpenSRP will populate the
	 * uuid column of rapidpro_state table with a special value UNPROCESSED_UUID. This is necessary as the UUID is not known
	 * until the contact is created via POST endpoint in RapidPro.
	 * </p>
	 */
	public void postDataToRapidPro() {
		updateContactAndStatus(CHILD, IDENTIFIER);
		updateContactAndStatus(SUPERVISOR, LOCATION_ID);

		List<RapidproState> childStates = getUnSyncedRapidProStates(CHILD.name(), REGISTRATION_DATA.name());
		postChildData(childStates);

		List<RapidproState> motherStates = getUnSyncedRapidProStates(CARETAKER.name(), REGISTRATION_DATA.name());
		postMotherData(motherStates);
	}

	private void postChildData(List<RapidproState> childStates) {
		ZeirChildClientConverter childConverter = new ZeirChildClientConverter(this);
		ZeirVaccinationConverter vaccinationConverter = new ZeirVaccinationConverter();
		ZeirGrowthMonitoringConverter growthMonitoringConverter = new ZeirGrowthMonitoringConverter();

		if (childStates != null && !childStates.isEmpty()) {
			for (RapidproState childState : childStates) {

				Client childClient = clientService.getByBaseEntityId(childState.getPropertyKey());
				RapidProContact childContact = childConverter.convertClientToContact(childClient);

				//Get vaccination and growth monitoring events and use them to update the child's RapidProContact
				List<RapidproState> vaccinationStates = getStatesByPropertyKey(CHILD.name(),
						VACCINATION_DATA.name(), childClient.getBaseEntityId());

				List<RapidproState> growthMonitoringStates = getStatesByPropertyKey(CHILD.name(),
						GROWTH_MONITORING_DATA.name(), childClient.getBaseEntityId());

				if (RapidProConstants.UNPROCESSED_UUID.equalsIgnoreCase(childState.getUuid())) {
					if (childClient.getRelationships() != null && childClient.getRelationships()
							.containsKey(RapidProConstants.MOTHER)) {

						String motherBaseEntityId = childClient.getRelationships(RapidProConstants.MOTHER).get(0);
						Client motherClient = clientService.getByBaseEntityId(motherBaseEntityId);

						childContact.getFields().setMotherName(motherClient.fullName());
						childContact.getFields().setMotherPhone((String) motherClient.getAttributes()
								.getOrDefault(RapidProConstants.SMS_REMINDER_PHONE_FORMATTED, null));

						processVaccinationStates(vaccinationStates, childContact, vaccinationConverter);
						processGrowthMonitoringStates(growthMonitoringStates, childContact, growthMonitoringConverter);
						postDataAndUpdateUuids(childContact, childState, vaccinationStates, growthMonitoringStates);
					}
				} else {
					processVaccinationStates(vaccinationStates, childContact, vaccinationConverter);
					processGrowthMonitoringStates(growthMonitoringStates, childContact, growthMonitoringConverter);
					postExistingChildData(childState, childContact, vaccinationStates, growthMonitoringStates);
				}
			}
		}
	}

	private void postExistingChildData(RapidproState childState, RapidProContact childContact,
			List<RapidproState> vaccinationStates, List<RapidproState> growthMonitoringStates) {
		List<Long> primaryKeys = getPrimaryKeys(vaccinationStates);
		primaryKeys.addAll(getPrimaryKeys(growthMonitoringStates));

		if (!primaryKeys.isEmpty()) {
			try {
				if (!reentrantLock.tryLock()) {
					logger.warn("[POST_DATA] Task still running...");
				}
				String fieldsJson = objectMapper.writeValueAsString(childContact.getFields());
				JSONObject payload = new JSONObject().put(RapidProConstants.FIELDS, new JSONObject(fieldsJson));
				postAndUpdateStatus(primaryKeys, childState.getUuid(), payload.toString(), true);
			}
			catch (JSONException jsonException) {
				logger.warn("Error creating fields Json", jsonException);
			}
			catch (JsonProcessingException jsonProcessingException) {
				logger.warn("Error fields JSON from child contact", jsonProcessingException);
			}
			catch (IOException exception) {
				logger.warn("Error processing Vaccination and Growth Monitoring data for existing child", exception);
			}
			finally {
				reentrantLock.unlock();
			}
		}
	}

	private void processVaccinationStates(List<RapidproState> vaccinationStates, RapidProContact childContact,
			ZeirVaccinationConverter vaccinationConverter) {
		if (vaccinationStates != null && !vaccinationStates.isEmpty()) {
			for (RapidproState rapidProState : vaccinationStates) {
				Event vaccinationEvent = eventService.findByFormSubmissionId(rapidProState.getPropertyValue());
				if (vaccinationEvent != null) {
					vaccinationConverter.updateRapidProContact(childContact, vaccinationEvent);
				}
			}
		}
	}

	private void processGrowthMonitoringStates(List<RapidproState> growthMonitoringStates, RapidProContact childContact,
			ZeirGrowthMonitoringConverter growthMonitoringConverter) {
		if (growthMonitoringStates != null && !growthMonitoringStates.isEmpty()) {
			List<Event> gmEvents = new ArrayList<>();
			for (RapidproState rapidProState : growthMonitoringStates) {
				Event gmEvent = eventService.findByFormSubmissionId(rapidProState.getPropertyValue());
				gmEvents.add(gmEvent);
			}
			processGrowthMonitoringEvent(gmEvents, GMEvent.HEIGHT, childContact, growthMonitoringConverter);
			processGrowthMonitoringEvent(gmEvents, GMEvent.WEIGHT, childContact, growthMonitoringConverter);
		}
	}

	private void processGrowthMonitoringEvent(List<Event> gmEvents, GMEvent gmEvent, RapidProContact childContact,
			ZeirGrowthMonitoringConverter growthMonitoringConverter) {
		List<Event> filteredGmEvents = gmEvents.stream()
				.filter(it -> gmEvent.name().toLowerCase(Locale.ROOT)
						.equalsIgnoreCase(it.getEntityType())).collect(Collectors.toList());

		if (!filteredGmEvents.isEmpty()) {
			Event filteredGmEvent = filteredGmEvents.get(filteredGmEvents.size() - 1);
			growthMonitoringConverter.updateRapidProContact(childContact, filteredGmEvent);
		}
	}

	private void postDataAndUpdateUuids(RapidProContact childContact, RapidproState registrationState,
			List<RapidproState> vaccinationEvents, List<RapidproState> growthMonitoringEvents) {
		try {
			if (!reentrantLock.tryLock()) {
				logger.warn("[POST_DATA] Task still running...");
			}
			CloseableHttpResponse httpResponse = postToRapidPro(objectMapper.writeValueAsString(childContact),
					getContactUrl(false, null));

			if (httpResponse != null && httpResponse.getEntity() != null) {
				final String rapidProContactJson = EntityUtils.toString(httpResponse.getEntity());
				RapidProContact rapidProContact = objectMapper.readValue(rapidProContactJson, RapidProContact.class);

				List<Long> primaryKeys = new ArrayList<>() {{
					add(registrationState.getId());
					addAll(getPrimaryKeys(vaccinationEvents));
					addAll(getPrimaryKeys(growthMonitoringEvents));
				}};

				if (updateUuids(primaryKeys, rapidProContact.getUuid())) {
					logger.info("Successfully synced OpenSRP data to RapidPro");
				}
			}
		}
		catch (IOException exception) {
			logger.warn("Child's data not posted to RapidPro", exception);
		}
		finally {
			reentrantLock.unlock();
		}
	}

	private List<Long> getPrimaryKeys(List<RapidproState> rapidproStates) {
		return rapidproStates.stream().map(RapidproState::getId).collect(Collectors.toList());
	}

	private void postMotherData(List<RapidproState> motherStates) {
		ZeirMotherClientConverter motherConverter = new ZeirMotherClientConverter();
		for (RapidproState motherState : motherStates) {
			if (RapidProConstants.UNPROCESSED_UUID.equalsIgnoreCase(motherState.getUuid())) {
				Client motherClient = clientService.getByBaseEntityId(motherState.getPropertyKey());
				RapidProContact motherContact = motherConverter.convertClientToContact(motherClient);

				try {
					if (!reentrantLock.tryLock()) {
						logger.warn("[POST_DATA] Task still running...");
					}
					CloseableHttpResponse httpResponse = postToRapidPro(objectMapper.writeValueAsString(motherContact),
							getContactUrl(false, null));
					if (httpResponse != null && httpResponse.getEntity() != null) {
						updateUuids(Collections.singletonList(motherState.getId()), motherState.getUuid());
					}
				}
				catch (IOException exception) {
					logger.warn("Mother's data not posted to RapidPro", exception);
				}
				finally {
					reentrantLock.unlock();
				}
			}
		}
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	private void updateContactAndStatus(ZeirRapidProEntity entity, ZeirRapidProEntityProperty property) {
		List<RapidproState> unSyncedStates = getUnSyncedRapidProStates(entity.name(), property.name());

		if (unSyncedStates != null && !unSyncedStates.isEmpty()) {
			for (RapidproState rapidproState : unSyncedStates) {
				try {
					if (!reentrantLock.tryLock()) {
						logger.warn("Some instance of this" + this.getClass() + "process is still running");
					}
					JSONObject fields = getPayload(entity, property, rapidproState);
					if (fields != null) {
						JSONObject payload = new JSONObject().put(RapidProConstants.FIELDS, fields);
						postAndUpdateStatus(Collections.singletonList(rapidproState.getId()),
								rapidproState.getUuid(), payload.toString(), true);
					}
				}
				catch (IOException exception) {
					logger.error(exception);
				}
				finally {
					reentrantLock.unlock();
				}
			}
		}
	}

	private JSONObject getPayload(ZeirRapidProEntity entity, ZeirRapidProEntityProperty property,
			RapidproState rapidproState) {
		JSONObject fields = null;
		switch (entity) {
			case CHILD:
				fields = new JSONObject();
				if (property == IDENTIFIER) {
					fields.put(RapidProConstants.OPENSRP_ID, rapidproState.getPropertyValue())
							.put(RapidProConstants.REGISTRATION_PROCESSED, "true")
							.put(RapidProConstants.SYSTEM_OF_REGISTRATION, RapidProConstants.OPENSRP);
				}
				break;
			case SUPERVISOR:
				fields = new JSONObject().put(RapidProConstants.FACILITY_LOCATION_ID, rapidproState.getPropertyValue());
				break;
			case CARETAKER:
			default:
				break;
		}
		return fields;
	}
}

