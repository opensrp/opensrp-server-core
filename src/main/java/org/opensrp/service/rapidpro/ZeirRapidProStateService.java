package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.RapidProStateSyncStatus;
import org.opensrp.domain.rapidpro.ZeirRapidProEntity;
import org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProClientConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirChildClientConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirGrowthMonitoringConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirGrowthMonitoringConverter.GMEvent;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirMotherClientConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirVaccinationConverter;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.util.RapidProUtils;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.opensrp.domain.rapidpro.ZeirRapidProEntity.CARETAKER;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntity.CHILD;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntity.SUPERVISOR;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.GROWTH_MONITORING_DATA;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.IDENTIFIER;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.LOCATION_ID;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.REGISTRATION_DATA;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.UPDATE_REGISTRATION_DATA;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.VACCINATION_DATA;

/**
 * Subclass for BaseRapidProStateService that has ZIER implementation on how to update RapidPro contacts from OpenSRP
 */
@Service
public class ZeirRapidProStateService extends BaseRapidProStateService {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private EventService eventService;

	private ClientService clientService;

	public ZeirRapidProStateService() {
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	/**
	 * <p>
	 * This method is responsible for syncing data from OpenSRP to RapidPro as well as updating particular contact fields.
	 * It is invoked after processing data from RapidPro.
	 * For Birth Registration/Vaccination and Growth Monitoring events, the baseEntityId is saved as the property_key and
	 * the formSubmissionId as the property_value in the rapidpro_state table.
	 * First time registration in OpenSRP will populate the uuid column of rapidpro_state table with a special value
	 * UNPROCESSED_UUID that will be updated with the actual UUID from RapidPro
	 * </p>
	 */
	public void postDataToRapidPro() {
		updateContactFields(CHILD, IDENTIFIER);
		updateContactFields(CHILD, UPDATE_REGISTRATION_DATA);
		updateContactFields(CARETAKER, UPDATE_REGISTRATION_DATA);
		updateContactFields(SUPERVISOR, LOCATION_ID);

		ZeirChildClientConverter childConverter = new ZeirChildClientConverter(this);
		ZeirVaccinationConverter vaccinationConverter = new ZeirVaccinationConverter();
		ZeirGrowthMonitoringConverter growthMonitoringConverter = new ZeirGrowthMonitoringConverter();

		List<RapidproState> rapidProChildren = getChildrenFromOpenSRPStates();
		rapidProChildren.addAll(getChildrenFromRapidProStates());
		logger.info("Found {} children in rapid pro list ", rapidProChildren.size());
		postChildData(rapidProChildren, childConverter, vaccinationConverter, growthMonitoringConverter);

		List<RapidproState> unsyncedMotherStates = getMotherRegistrationData();
		postMotherData(unsyncedMotherStates);

		postExistingChildData(childConverter, vaccinationConverter, growthMonitoringConverter);
	}

	private List<RapidproState> getMotherRegistrationData() {
		return getUnSyncedRapidProStates(CARETAKER.name(),
				REGISTRATION_DATA.name());
	}

	private List<RapidproState> getChildrenFromOpenSRPStates() {
		return getAllRapidProStates(CHILD.name(), REGISTRATION_DATA.name());
	}

	private List<RapidproState> getChildrenFromRapidProStates() {
		return getAllRapidProStates(CHILD.name(), IDENTIFIER.name());
	}

	private void postChildData(List<RapidproState> unSyncedChildStates,
			ZeirChildClientConverter childConverter,
			ZeirVaccinationConverter vaccinationConverter,
			ZeirGrowthMonitoringConverter growthMonitoringConverter) {

		if (unSyncedChildStates != null && !unSyncedChildStates.isEmpty()) {
			logger.warn("Syncing {} client(s) created from OpenSRP to RapidPro", unSyncedChildStates.size());
			for (RapidproState unSyncedChildState : unSyncedChildStates) {
				Client childClient;
				if (unSyncedChildState.getProperty().equalsIgnoreCase(IDENTIFIER.name())) {
					childClient = clientService.getByBaseEntityId(unSyncedChildState.getUuid());
					logger.info("Child is from RapidPro Base Entity id {}", childClient.getBaseEntityId());
				} else {
					childClient = clientService.getByBaseEntityId(unSyncedChildState.getPropertyKey());
					logger.info("Child is from OpenSRP. Base Entity id {}", childClient.getBaseEntityId());
				}

				RapidProContact childContact = childConverter.convertClientToContact(childClient);

				//Get vaccination and growth monitoring events and use them to update the child's RapidProContact
				List<RapidproState> vaccinationStates = getStatesByPropertyKey(CHILD.name(),
						VACCINATION_DATA.name(), childClient.getBaseEntityId());

				List<RapidproState> growthMonitoringStates = getStatesByPropertyKey(CHILD.name(),
						GROWTH_MONITORING_DATA.name(), childClient.getBaseEntityId());

				logger.warn("Found {} VACCINATION and {} GROWTH MONITORING UN_SYNCED events for child identified as {}",
						vaccinationStates.size(), growthMonitoringStates.size(), childClient.getBaseEntityId());

				if (RapidProConstants.UNPROCESSED_UUID.equalsIgnoreCase(unSyncedChildState.getUuid()) &&
						childClient.getRelationships() != null &&
						childClient.getRelationships().containsKey(RapidProConstants.MOTHER)) {

						String motherBaseEntityId = childClient.getRelationships().get(RapidProConstants.MOTHER).get(0);
						Client motherClient = clientService.getByBaseEntityId(motherBaseEntityId);

						RapidProFields childFields = childContact.getFields();
						if (childFields != null) {
							childFields.setMotherName(motherClient.fullName());
							childFields.setMotherPhone((String) motherClient.getAttributes()
									.getOrDefault(RapidProConstants.SMS_REMINDER_PHONE_FORMATTED, null));
						}
						processVaccinationStates(vaccinationStates, childContact, vaccinationConverter);
						processGrowthMonitoringStates(growthMonitoringStates, childContact, growthMonitoringConverter);
						postDataAndUpdateUuids(childContact, unSyncedChildState, vaccinationStates, growthMonitoringStates);
					}
			}
			logger.warn("Synced {} client(s) created from OpenSRP to RapidPro", unSyncedChildStates.size());
		} else {
			logger.warn("No new child client(s) created from OpenSRP available for sync to RapidPro");
		}
	}

	/**
	 * Some events may arrive late after the Child's registration data is synced. This method handles such scenario. E.g.
	 * is posting vaccination data for existing child.
	 *
	 * @param childConverter            convert child registration event to RapidPro contact
	 * @param vaccinationConverter      convert vaccination event properties to RapidPro field properties
	 * @param growthMonitoringConverter convert growth monitoring event properties to RapidPro field properties
	 */
	private void postExistingChildData(
			ZeirChildClientConverter childConverter,
			ZeirVaccinationConverter vaccinationConverter,
			ZeirGrowthMonitoringConverter growthMonitoringConverter) {

		//Select un synced data distinctly by baseEntityId.
		Set<String> baseEntityIds = getDistinctStatesByUuidAndSyncStatus(RapidProConstants.UNPROCESSED_UUID,
				RapidProStateSyncStatus.UN_SYNCED.name()).stream().map(RapidproState::getPropertyKey)
				.collect(Collectors.toSet());

		logger.info("Found {} distinct client(s) ", baseEntityIds.size());

		if (!baseEntityIds.isEmpty()) {
			for (String baseEntityId : baseEntityIds) {

				Client childClient = clientService.getByBaseEntityId(baseEntityId);
				RapidProContact childContact = childConverter.convertClientToContact(childClient);

				//Get un synced Growth Monitoring and Vaccination data for the child
				List<RapidproState> vaccinationStates = getStatesByPropertyKey(RapidProConstants.UNPROCESSED_UUID,
						CHILD.name(), VACCINATION_DATA.name(), baseEntityId);
				logger.info("Found {} vaccination event(s) data ", vaccinationStates.size());

				List<RapidproState> growthMonitoringStates = getStatesByPropertyKey(RapidProConstants.UNPROCESSED_UUID,
						CHILD.name(), GROWTH_MONITORING_DATA.name(), baseEntityId);
				logger.info("Found {} growth monitoring event(s) data", growthMonitoringStates.size());

				processVaccinationStates(vaccinationStates, childContact, vaccinationConverter);
				processGrowthMonitoringStates(growthMonitoringStates, childContact, growthMonitoringConverter);

				Optional<RapidproState> registrationState = getUuid(baseEntityId);
				if (registrationState.isPresent() && !RapidProConstants.UNPROCESSED_UUID.equalsIgnoreCase(
						registrationState.get().getUuid())) {
					postExistingChildData(registrationState.get().getUuid(), childContact, vaccinationStates,
							growthMonitoringStates);
				}
			}
		} else {
			logger.warn("No new vaccination/growth monitoring events generated from OpenSRP available for sync to RapidPro");
		}
	}

	/**
	 * Get the Rapid pro state for both kids registered from OpenSRP and Rapidpro
	 *
	 * @param baseEntityId The client base entity id in OpenSRP
	 * @return
	 */
	private Optional<RapidproState> getUuid(String baseEntityId) {
		Optional<RapidproState> rapidProStates = getRapidProStatesByUuid(baseEntityId, CHILD.name(),
				IDENTIFIER.name()).stream().findFirst();
		if (rapidProStates.isPresent()) {
			return rapidProStates;
		}
		return getStatesByPropertyKey(CHILD.name(), REGISTRATION_DATA.name(), baseEntityId).stream().findFirst();
	}

	private void postExistingChildData(String uuid, RapidProContact childContact,
			List<RapidproState> vaccinationStates, List<RapidproState> growthMonitoringStates) {
		List<Long> primaryKeys = getPrimaryKeys(vaccinationStates);
		primaryKeys.addAll(getPrimaryKeys(growthMonitoringStates));

		if (!primaryKeys.isEmpty()) {
			logger.warn("Updating vaccination/growth monitoring fields for contact identified by {}", uuid);
			synchronized (this) {
				try {
					JSONObject payload = extractRapidProFieldsJSONObjectFromContact(childContact);
					postAndUpdateStatus(primaryKeys, uuid, payload.toString(), true);
					logger.warn("Updated vaccination/growth monitoring fields for contact identified by " + uuid);
				}
				catch (JSONException jsonException) {
					logger.warn("Error creating fields Json", jsonException);
				}
				catch (JsonProcessingException jsonProcessingException) {
					logger.warn("Error fields JSON from child contact", jsonProcessingException);
				}
				catch (IOException exception) {
					logger.warn("Child Vaccination and Growth Monitoring data not posted", exception);
				}
			}
		}
	}

	private JSONObject extractRapidProFieldsJSONObjectFromContact(RapidProContact childContact) throws JsonProcessingException {
		String fieldsJson = objectMapper.writeValueAsString(childContact.getFields());
		return new JSONObject().put(RapidProConstants.FIELDS, new JSONObject(fieldsJson));
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
		synchronized (this) {
			logger.warn("Creating new RapidPro contact...");
			try (CloseableHttpResponse httpResponse = postToRapidPro(objectMapper.writeValueAsString(childContact),
					getContactUrl(false, null))) {
				if (httpResponse != null && httpResponse.getEntity() != null) {
					RapidProUtils.logResponseStatusCode(httpResponse, logger);
					final String rapidProContactJson = EntityUtils.toString(httpResponse.getEntity());
					RapidProContact rapidProContact = objectMapper.readValue(rapidProContactJson, RapidProContact.class);

					List<Long> primaryKeys = new ArrayList<>() {{
						add(registrationState.getId());
						addAll(getPrimaryKeys(vaccinationEvents));
						addAll(getPrimaryKeys(growthMonitoringEvents));
					}};
					logger.info("Updating RapidPro uuid for the created child contact: {}", rapidProContact.getUuid());
					if (updateUuids(primaryKeys, rapidProContact.getUuid())) {
						addContactToGroup(CHILD, rapidProContact.getUuid());
						logger.info("Successfully synced {} OpenSRP child client(s) data to RapidPro", primaryKeys.size());
					}

					// TODO: Update the synced status to SYNCED
				}
			}
			catch (IOException exception) {
				logger.warn("Child's data not posted to RapidPro", exception);
			}
		}
	}

	private List<Long> getPrimaryKeys(List<RapidproState> rapidProStates) {
		return rapidProStates.stream().map(RapidproState::getId).collect(Collectors.toList());
	}

	private synchronized void postMotherData(List<RapidproState> motherStates) {
		if (motherStates != null && !motherStates.isEmpty()) {
			ZeirMotherClientConverter motherConverter = new ZeirMotherClientConverter();
			for (RapidproState motherState : motherStates) {
				if (RapidProConstants.UNPROCESSED_UUID.equalsIgnoreCase(motherState.getUuid())) {
					Client motherClient = clientService.getByBaseEntityId(motherState.getPropertyKey());
					RapidProContact motherContact = motherConverter.convertClientToContact(motherClient);
					Optional<String> optionalMotherPhone = optionalMotherPhone(motherContact);
					if (optionalMotherPhone.isPresent()) {
						HttpGet contactRequest = RapidProUtils.contactByPhoneRequest(
								optionalMotherPhone.get().replace("tel:", ""), rapidProUrl, rapidProToken
						);
						RapidProContact existingMother = RapidProUtils.getRapidProContactByPhone(
								closeableHttpClient, contactRequest, objectMapper, logger);
						if (existingMother == null) {
							try (CloseableHttpResponse httpResponse = postToRapidPro(
									objectMapper.writeValueAsString(motherContact),
									getContactUrl(false, null))) {
								if (httpResponse != null && httpResponse.getEntity() != null) {
									RapidProUtils.logResponseStatusCode(httpResponse, logger);
									final String rapidProContactJson = EntityUtils.toString(
											httpResponse.getEntity());
									RapidProContact newMotherContact =
											objectMapper.readValue(rapidProContactJson, RapidProContact.class);
									logger.warn("Mother contact {} created and their UUID updated",
											newMotherContact.getUuid());
									updateUuids(Collections.singletonList(motherState.getId()), newMotherContact.getUuid());
									addContactToGroup(CARETAKER, newMotherContact.getUuid());
								}
							}
							catch (IOException exception) {
								logger.warn("Mother's data not posted to RapidPro", exception);
							}
						}
					}
				}
			}
		} else {
			logger.warn("No new mother client(s) created from OpenSRP available for sync to RapidPro");
		}
	}

	private Optional<String> optionalMotherPhone(RapidProContact motherContact) {
		if (motherContact == null) {
			return Optional.empty();
		}
		return motherContact.getUrns()
				.stream().filter(urn -> urn.startsWith("tel"))
				.collect(Collectors.toList())
				.stream().findFirst();
	}

	private void updateContactFields(ZeirRapidProEntity entity, ZeirRapidProEntityProperty property) {
		List<RapidproState> unSyncedStates = getUnSyncedRapidProStates(entity.name(), property.name()).stream()
				.limit(RapidProUtils.RAPIDPRO_DATA_LIMIT).collect(Collectors.toList());
		ZeirChildClientConverter childConverter = new ZeirChildClientConverter(this);
		ZeirMotherClientConverter motherConverter = new ZeirMotherClientConverter();
		while (!unSyncedStates.isEmpty()) {
			logger.warn("Syncing {} record(s) of type {}  from OpenSRP to RapidPro", unSyncedStates.size(), entity.name());
			for (RapidproState rapidproState : unSyncedStates) {
				synchronized (this) {
					try {
						JSONObject fields = getPayload(entity, property, rapidproState, childConverter, motherConverter);
						if (fields != null) {
							logger.warn("Fields during updates {}", fields);
							postAndUpdateStatus(Collections.singletonList(rapidproState.getId()),
									rapidproState.getUuid(), fields.toString(), true);
						}
						logger.warn("Syncing {} record(s) of type {} from OpenSRP to RapidPro", rapidproState.getUuid(),
								entity.name());
					}
					catch (IOException exception) {
						logger.error(exception);
					}
				}
			}

			unSyncedStates = getUnSyncedRapidProStates(entity.name(), property.name()).stream()
					.limit(RapidProUtils.RAPIDPRO_DATA_LIMIT).collect(Collectors.toList());
		}

		logger.warn("No OpenSRP record(s) of type {} available for sync to RapidPro", entity.name());
	}

	private JSONObject getPayload(ZeirRapidProEntity entity, ZeirRapidProEntityProperty property,
			RapidproState rapidproState, ZeirChildClientConverter childConverter,
			ZeirMotherClientConverter motherConverter) throws JsonProcessingException {
		JSONObject fields = null;
		switch (entity) {
			case CHILD:
				fields = new JSONObject();
				if (property == IDENTIFIER) {
					fields.put(RapidProConstants.OPENSRP_ID, rapidproState.getPropertyValue())
							.put(RapidProConstants.REGISTRATION_PROCESSED, "true")
							.put(RapidProConstants.SYSTEM_OF_REGISTRATION, RapidProConstants.MVACC);
					fields = new JSONObject().put(RapidProConstants.FIELDS, fields);
				} else if (property == UPDATE_REGISTRATION_DATA) {
					RapidProContact childRapidProContact = getContactFromClient(rapidproState, childConverter);
					fields = new JSONObject(objectMapper.writeValueAsString(childRapidProContact));
				}
				break;
			case SUPERVISOR:
				fields = new JSONObject().put(RapidProConstants.FIELDS,
						new JSONObject().put(RapidProConstants.FACILITY_LOCATION_ID, rapidproState.getPropertyValue()));
				break;
			case CARETAKER:
				if (property == UPDATE_REGISTRATION_DATA) {
					RapidProContact motherRapidProContact = getContactFromClient(rapidproState, motherConverter);
					fields = new JSONObject(objectMapper.writeValueAsString(motherRapidProContact));
				}
				break;
			default:
				break;
		}
		return fields;
	}

	private RapidProContact getContactFromClient(RapidproState rapidproState,
			BaseRapidProClientConverter rapidProClientConverter) {
		Client childClient = clientService.getByBaseEntityId(rapidproState.getPropertyKey());
		return rapidProClientConverter.convertClientToContact(childClient);
	}

	synchronized public void addContactToGroup(ZeirRapidProEntity entity, String uuid) throws IOException {
		JSONObject payload = new JSONObject();
		payload.put(RapidProConstants.CONTACTS, new JSONArray().put(uuid));
		payload.put(RapidProConstants.ACTION, RapidProConstants.ADD);
		String group = entity == CHILD ? RapidProConstants.CHILDREN : RapidProConstants.CARETAKERS;
		payload.put(RapidProConstants.GROUP, group);
		try (CloseableHttpResponse httpResponse = postToRapidPro(payload.toString(),
				RapidProUtils.getBaseUrl(rapidProUrl) + RapidProConstants.CONTACT_ACTION_URL_PATH)) {
			if (httpResponse != null) {
				RapidProUtils.logResponseStatusCode(httpResponse, logger);
				StatusLine statusLine = httpResponse.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
					logger.info("Contact identified as {} added to group named {} ", uuid, group);
				}
			}
		}
		catch (IOException exception) {
			logger.error(exception);
		}
	}
}

