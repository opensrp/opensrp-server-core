package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.RapidProStateSyncStatus;
import org.opensrp.domain.rapidpro.RapidProStateToken;
import org.opensrp.domain.rapidpro.ZeirRapidProEntity;
import org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProEventConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirBirthRegistrationConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirChildClientConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirGrowthMonitoringConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirGrowthMonitoringConverter.GMEvent;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirMotherClientConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirMotherRegistrationConverter;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirVaccinationConverter;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.opensrp.service.callback.RapidProResponseCallback;
import org.opensrp.util.DateParserUtils;
import org.opensrp.util.RapidProUtils;
import org.opensrp.util.constants.EventConstants;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.opensrp.domain.rapidpro.ZeirRapidProEntity.SUPERVISOR;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.LOCATION_ID;
import static org.opensrp.util.RapidProUtils.getBaseUrl;
import static org.opensrp.util.RapidProUtils.setupRapidproRequest;

/**
 * Subclass of BaseRapidProService for ZEIR project. This implementation is for RapidPro/MVACC integration.
 */
@Service
public class ZeirRapidProService extends BaseRapidProService implements RapidProResponseCallback {

	public static final String ZAMBIA_COUNTRY_CODE = "+260";

	/**
	 * Process the RapidPro contacts retrieved from the server. At this time of execution, the RapidProContact has already been
	 * updated with the location id of the provider. If location Id is not provided for some reasons for instance RapidPro
	 * server was down, this contact will be skipped and reprocessed later.
	 * This method receives RapidPro contacts and generate BirthRegistration, Vaccination and Growth Monitoring events based
	 * on the available data on the contacts field.
	 *
	 * @param response       the response received from the RapidPro
	 * @param onTaskComplete callback method invoked when processing contact is completed
	 */
	@Override
	public synchronized void handleContactResponse(String response, RapidProOnTaskComplete onTaskComplete) {
		if (StringUtils.isBlank(response)) {
			return;
		}
		JSONObject responseJson = new JSONObject(response);
		JSONArray results = RapidProUtils.getResults(responseJson);

		if (results != null) {
			List<RapidProContact> rapidProContacts = parseRapidProContactResponse(results);
			if (!rapidProContacts.isEmpty()) {
				Instant earliestDateModified = getFirstContactDateModified(rapidProContacts);
				Instant latestDateModified = earliestDateModified;
				for (RapidProContact rapidProContact : rapidProContacts) {
					try {
						Pair<Instant, Instant> dateModifierPair = updateLastModifiedDate(rapidProContact,
								earliestDateModified, latestDateModified);
						earliestDateModified = dateModifierPair.getLeft();
						latestDateModified = dateModifierPair.getRight();
						processRapidProContacts(rapidProContacts, rapidProContact);
					}
					catch (Exception exception) {
						logger.error(exception.getMessage(), exception);
						//Use the earliest date modified to not miss any contact
						configService.updateAppStateToken(RapidProStateToken.RAPIDPRO_STATE_TOKEN,
								earliestDateModified.toString());
						return;
					}
				}
				//Use the latest date modified for the processed contacts
				configService.updateAppStateToken(RapidProStateToken.RAPIDPRO_STATE_TOKEN,
						latestDateModified.toString());
				onTaskComplete.completeTask();
			}
		}
		if (responseJson.isNull(RapidProConstants.NEXT)) {
			return;
		}
		try (CloseableHttpResponse httpResponse = closeableHttpClient.execute(getContactRequest())) {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				handleContactResponse(EntityUtils.toString(httpResponse.getEntity()), onTaskComplete);
			}
		}
		catch (IOException exception) {
			logger.error(exception.getMessage(), exception);
		}
	}

	private Pair<Instant, Instant> updateLastModifiedDate(RapidProContact rapidProContact, Instant earliestDateModified,
			Instant latestDateModified) throws DateTimeParseException {
		//Update last modified date to use the earliest
		Instant earliestDateModifiedCopy = earliestDateModified;
		Instant latestDateModifiedCopy = latestDateModified;

		Instant modifiedOn = Instant.parse(rapidProContact.getModifiedOn());
		if (modifiedOn.isBefore(earliestDateModified)) {
			earliestDateModifiedCopy = modifiedOn;
		}
		if (modifiedOn.isAfter(latestDateModified)) {
			latestDateModifiedCopy = modifiedOn;
		}
		return Pair.of(earliestDateModifiedCopy, latestDateModifiedCopy);
	}

	private List<RapidProContact> parseRapidProContactResponse(JSONArray results) {
		List<RapidProContact> rapidProContacts = new ArrayList<>();
		try {
			rapidProContacts = RapidProUtils.getRapidProContacts(results, objectMapper);
			logger.info("Found " + rapidProContacts.size() + " modified RapidPro contacts");
		}
		catch (JsonProcessingException jsonProcessingException) {
			logger.error(jsonProcessingException);
		}

		return rapidProContacts;
	}

	private Instant getFirstContactDateModified(List<RapidProContact> rapidProContacts) {
		Instant firstContactDateModified;
		try {
			firstContactDateModified = Instant.parse(rapidProContacts.get(0).getModifiedOn());
		}
		catch (DateTimeParseException parseException) {
			firstContactDateModified = Instant.now();
		}
		return firstContactDateModified;
	}

	private void processRapidProContacts(List<RapidProContact> rapidProContacts, RapidProContact rapidProContact) {
		//Only process child contacts
		RapidProFields fields = rapidProContact.getFields();
		if (StringUtils.isBlank(fields.getSupervisorPhone()) &&
				StringUtils.isNotBlank(fields.getPosition())) {
			logger.error("Supervisor phone not provided for contact of type {}", fields.getPosition());
		}
		if (StringUtils.isNotBlank(fields.getSupervisorPhone()) && RapidProConstants.CHILD
				.equalsIgnoreCase(fields.getPosition())) {
			String locationId = getLocationId(rapidProContact, rapidProContacts);
			if (StringUtils.isBlank(locationId)) {
				logger.error(
						"Supervisor identified with phone number '{}' not tagged to an exising OpenSRP location",
						fields.getSupervisorPhone());
			}
			if (StringUtils.isNotBlank(locationId)) {
				updateExistingClientUuid(rapidProContact, ZeirRapidProEntity.CHILD);
				fields.setFacilityLocationId(locationId);
				processRegistrationAndRelatedEvents(rapidProContact, rapidProContacts);
			}
		}//TODO Add implementation for processing supervisor for instance when their location is updated;
	}

	private void updateExistingClientUuid(RapidProContact rapidProContact, ZeirRapidProEntity entity) {
		RapidproState rapidproState = rapidProStateService.getRapidProStateByUuid(rapidProContact.getUuid(),
				entity.name(), ZeirRapidProEntityProperty.REGISTRATION_DATA.name());
		//Use client baseEntityId (that is saved as property_key for REGISTRATION_DATA property of entity CHILD/CARETAKER)
		if (rapidproState != null) {
			rapidProContact.setUuid(rapidproState.getPropertyKey());
		}
	}

	/**
	 * Return the location id of the supervisor, first check if the location id was saved, if not confirm if the supervisor is
	 * amongst the returned contacts and fetch their location id from the database otherwise make a get request to find a
	 * supervisor with matching phone number and query the database for their location
	 *
	 * @param rapidProContact  child contact
	 * @param rapidProContacts all contacts
	 * @return location id of the supervisor
	 */
	public String getLocationId(RapidProContact rapidProContact, List<RapidProContact> rapidProContacts) {
		RapidProFields fields = rapidProContact.getFields();
		if (StringUtils.isNoneBlank(fields.getFacilityLocationId())) {
			return fields.getFacilityLocationId();
		}

		String supervisorPhone = fields.getSupervisorPhone();
		List<RapidproState> rapidProState =
				rapidProStateService.getRapidProState(SUPERVISOR.name(), LOCATION_ID.name(), supervisorPhone);

		if (rapidProState != null && !rapidProState.isEmpty()) {
			return rapidProState.get(rapidProState.size() - 1).getPropertyValue();
		}

		RapidProContact supervisorContact = getSupervisorContact(supervisorPhone, rapidProContacts);
		if (supervisorContact == null) {
			return null;
		}
		return getProviderLocationId(supervisorContact, supervisorPhone);
	}

	/**
	 * Create both mother and child registration event and client. Registration should only be done when both mother and
	 * child contacts are present otherwise proceed to process other events (Vaccination and Growth Monitoring)
	 *
	 * @param childContact     Current contact
	 * @param rapidProContacts All RapidPro contacts used to filter mother contact
	 */
	public void processRegistrationAndRelatedEvents(RapidProContact childContact, List<RapidProContact> rapidProContacts) {
		RapidProFields fields = childContact.getFields();
		if (RapidProConstants.CHILD.equalsIgnoreCase(fields.getPosition())) {
			RapidProContact motherContact =
					getMotherContact(fields.getMotherPhone(), rapidProContacts);

			if (motherContact != null) {
				updateExistingClientUuid(motherContact, ZeirRapidProEntity.CARETAKER);
				motherContact.getFields().setFacilityLocationId(fields.getFacilityLocationId());
				processChildRegistration(childContact, motherContact);

				Client existingMotherClient = clientService.getByBaseEntityId(motherContact.getUuid());
				if (existingMotherClient == null) {
					ZeirMotherClientConverter clientConverter =
							new ZeirMotherClientConverter(identifierSourceService, uniqueIdentifierService);
					Client motherClient = clientConverter.convertContactToClient(motherContact);
					if (motherClient != null) {
						clientService.addClient(motherClient);
						saveEvent(motherContact, new ZeirMotherRegistrationConverter(organizationService));
					}
				}
				processVaccinationEvent(childContact);
				processGrowthMonitoringEvent(childContact);
			}
		}
	}

	private void saveEvent(RapidProContact rapidProContact, BaseRapidProEventConverter eventConverter) {
		Event event = eventConverter.convertContactToEvent(rapidProContact);
		event.addDetails(RapidProConstants.SYSTEM_OF_REGISTRATION, RapidProConstants.MVACC);
		eventService.addEvent(event, rapidProContact.getFields().getSupervisorPhone());
	}

	private void processChildRegistration(RapidProContact childContact, RapidProContact motherContact) {
		Client existingChildClient = clientService.getByBaseEntityId(childContact.getUuid());

		if (existingChildClient == null) {
			ZeirChildClientConverter clientConverter =
					new ZeirChildClientConverter(identifierSourceService, uniqueIdentifierService, rapidProStateService);
			Client childClient = clientConverter.convertContactToClient(childContact);
			if (childClient != null) {
				childClient.withRelationships(new HashMap<>() {{
					put(RapidProConstants.MOTHER, Collections.singletonList(motherContact.getUuid()));
				}});

				clientService.addClient(childClient);
				saveEvent(childContact, new ZeirBirthRegistrationConverter(organizationService));
			}
		}
	}

	private RapidProContact getMotherContact(String motherPhone, List<RapidProContact> rapidProContacts) {
		if (StringUtils.isBlank(motherPhone)) {
			return null;
		}
		List<RapidProContact> motherList = rapidProContacts.stream()
				.filter(rapidProContact ->
						RapidProConstants.CARETAKER.equalsIgnoreCase(rapidProContact.getFields().getPosition()) &&
								rapidProContact.getUrns() != null &&
								rapidProContact.getUrns().stream().anyMatch(urn ->
										urn.contains(motherPhone.startsWith(ZAMBIA_COUNTRY_CODE) ?
												StringUtils.removeStart(motherPhone, ZAMBIA_COUNTRY_CODE) : motherPhone)))
				.collect(Collectors.toList());
		if (motherList.isEmpty()) {
			return null;
		}
		return motherList.get(0);
	}

	/**
	 * Vaccines are only administered once. Filter the processed vaccination events from the previous ones. Only new vaccination
	 * events are saved
	 *
	 * @param rapidProContact child contact from rapidpro
	 */
	public void processVaccinationEvent(RapidProContact rapidProContact) {
		if (RapidProConstants.CHILD.equalsIgnoreCase(rapidProContact.getFields().getPosition())) {
			ZeirVaccinationConverter eventConverter = new ZeirVaccinationConverter(organizationService);
			List<Event> processedVaccineEvents = eventConverter.convertContactToEvents(rapidProContact);

			if (!processedVaccineEvents.isEmpty()) {
				List<Event> existingVaccinationEvents =
						eventService.findByBaseEntityAndType(rapidProContact.getUuid(), EventConstants.VACCINATION_EVENT);

				//Remove all previously administered vaccines
				Set<String> existingVaccines = getVaccinesDoses(existingVaccinationEvents);
				Set<String> processedVaccines = getVaccinesDoses(processedVaccineEvents);
				processedVaccines.removeAll(existingVaccines);

				List<Event> newVaccinationEvents = processedVaccineEvents
						.stream()
						.filter(event -> event.getObs().stream().map(Obs::getFormSubmissionField)
								.allMatch(processedVaccines::contains))
						.collect(Collectors.toList());

				if (!newVaccinationEvents.isEmpty()) {
					saveEvents(rapidProContact.getFields(), newVaccinationEvents);
				}
			}
		}
	}

	private Set<String> getVaccinesDoses(List<Event> vaccineEvents) {
		return vaccineEvents.stream()
				.flatMap(event -> event.getObs().stream().map(Obs::getFormSubmissionField))
				.collect(Collectors.toSet());
	}

	public void processGrowthMonitoringEvent(RapidProContact rapidProContact) {
		RapidProFields fields = rapidProContact.getFields();
		if (RapidProConstants.CHILD.equalsIgnoreCase(fields.getPosition())) {
			ZeirGrowthMonitoringConverter eventConverter = new ZeirGrowthMonitoringConverter(organizationService);
			List<Event> processedGMEvents = eventConverter.convertContactToEvents(rapidProContact);
			List<Event> existingGMEvents =
					eventService.findByBaseEntityAndType(rapidProContact.getUuid(), EventConstants.GROWTH_MONITORING_EVENT);
			processGMEvent(fields, processedGMEvents, existingGMEvents, GMEvent.HEIGHT);
			processGMEvent(fields, processedGMEvents, existingGMEvents, GMEvent.WEIGHT);
		}
	}

	private void processGMEvent(RapidProFields fields, List<Event> processedGMEvents, List<Event> existingGMEvents,
			GMEvent gmEvent) {
		List<Event> filteredExistingGMEvents = filterGMEvents(existingGMEvents, gmEvent);
		String value = null;
		String dateModified = null;
		switch (gmEvent) {
			case HEIGHT:
				value = fields.getHeight();
				dateModified = fields.getGmHeightDateModified();
				break;
			case WEIGHT:
				value = fields.getWeight();
				dateModified = fields.getGmWeightDateModified();
				break;
			default:
				break;
		}
		if (value != null && dateModified != null) {
			if (filteredExistingGMEvents.isEmpty()) {
				saveEvents(fields, filterGMEvents(processedGMEvents, gmEvent));
			} else {
				DateTime contactGMEventDate = DateParserUtils.parseZoneDateTime(dateModified);
				DateTime lastGMEventDate = getLastGMEventDate(filteredExistingGMEvents);
				if (contactGMEventDate.isAfter(lastGMEventDate)) {
					saveEvents(fields, filterGMEvents(processedGMEvents, gmEvent));
				}
			}
		}
	}

	private void saveEvents(RapidProFields fields, List<Event> events) {
		for (Event processedGMEvent : events) {
			processedGMEvent.addDetails(RapidProConstants.SYSTEM_OF_REGISTRATION, RapidProConstants.MVACC);
			eventService.addEvent(processedGMEvent, fields.getSupervisorPhone());
		}
	}

	private DateTime getLastGMEventDate(List<Event> filteredGMEvents) {
		DateTime eventDate = filteredGMEvents.get(0).getDateCreated();
		for (Event filteredEvent : filteredGMEvents) {
			DateTime existingGMEventInstant = filteredEvent.getDateCreated();
			if (existingGMEventInstant.isAfter(eventDate)) {
				eventDate = existingGMEventInstant;
			}
		}
		return eventDate;
	}

	private List<Event> filterGMEvents(List<Event> previousGMEvents, GMEvent gmEvent) {
		return previousGMEvents.stream()
				.filter(it -> it.getEntityType().equalsIgnoreCase(gmEvent.name()))
				.collect(Collectors.toList());
	}

	public String getProviderLocationId(RapidProContact supervisorContact, String supervisorPhone) {
		RapidProFields supervisorFields = supervisorContact.getFields();
		String province = supervisorFields.getProvince();
		String district = supervisorFields.getDistrict();
		String facility = supervisorFields.getFacility();

		if (StringUtils.isBlank(province) || StringUtils.isBlank(district) || StringUtils.isBlank(province)) {
			return null;
		}

		Long count = locationService.countAllLocations(0L);

		List<PhysicalLocation> provinceLocations = locationService.findLocationsByName(province);
		List<PhysicalLocation> districtFacilities = getDistrictLocations(count, district, provinceLocations);

		if (districtFacilities != null) {
			List<PhysicalLocation> facilities = findLocationsWithNameAndTag(districtFacilities, facility,
					RapidProConstants.HEALTH_FACILITY);

			if (facilities.size() != 1) {
				return null;
			}

			String facilityLocationId = facilities.get(0).getId();
			RapidproState rapidProState = new RapidproState();
			rapidProState.setUuid(supervisorContact.getUuid());
			rapidProState.setEntity(SUPERVISOR.name());
			rapidProState.setProperty(LOCATION_ID.name());
			rapidProState.setPropertyKey(supervisorPhone);
			rapidProState.setPropertyValue(facilityLocationId);
			rapidProState.setSyncStatus(RapidProStateSyncStatus.UN_SYNCED.name());
			rapidProStateService.saveRapidProState(rapidProState);

			return facilityLocationId;
		}
		return null;
	}

	private List<PhysicalLocation> getDistrictLocations(Long count, String district, List<PhysicalLocation> provinces) {
		for (PhysicalLocation province : provinces) {
			if (locationTagExists(province.getLocationTags(), RapidProConstants.PROVINCE)) {
				String provinceUuid = province.getId();
				List<PhysicalLocation> districtLocations =
						locationService.findLocationByIdWithChildren(false, provinceUuid, count.intValue());

				if (districtLocations != null && !districtLocations.isEmpty()) {
					List<PhysicalLocation> districts = findLocationsWithNameAndTag(districtLocations, district,
							RapidProConstants.DISTRICT);
					if (districts.isEmpty())
						continue;

					if (districts.size() > 1) {
						throw new IllegalStateException("Found " + districts.size() + " districts with the same name "
								+ "in the same province ( " + province.getProperties().getName() + ")");
					}
					String districtUuid = districts.get(0).getId();
					return locationService.findLocationByIdWithChildren(false, districtUuid, count.intValue());
				}
			}
		}
		return null;
	}

	@Override
	public void queryContacts(RapidProOnTaskComplete onTaskComplete) {
		try (CloseableHttpResponse httpResponse = closeableHttpClient.execute(getContactRequest())) {
			if (httpResponse != null && httpResponse.getEntity() != null) {
				RapidProUtils.logResponseStatusCode(httpResponse, logger);
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					handleContactResponse(EntityUtils.toString(httpResponse.getEntity()), onTaskComplete);
				}
			}
		}
		catch (IOException exception) {
			logger.error(exception.getMessage(), exception.fillInStackTrace().toString());
		}
	}

	public HttpGet getContactRequest() {
		String dateModified = (String) configService.getAppStateTokenByName(RapidProStateToken.RAPIDPRO_STATE_TOKEN)
				.getValue();
		String baseUrl = getBaseUrl(rapidProUrl);
		String url = !dateModified.equalsIgnoreCase("#") ? baseUrl + "/contacts.json?after=" + dateModified :
				baseUrl + "/contacts.json?after=" + ZonedDateTime.now().minusMonths(6).toInstant().toString();

		return (HttpGet) setupRapidproRequest(url, new HttpGet(), rapidProToken);
	}

	public RapidProContact getSupervisorContact(String phone, List<RapidProContact> downloadedContacts) {

		if (StringUtils.isBlank(phone) || StringUtils.isEmpty(phone)) {
			return null;
		}
		List<RapidProContact> supervisors = downloadedContacts.stream()
				.filter(supervisor ->
				{
					RapidProFields fields = supervisor.getFields();
					return !RapidProConstants.CARETAKER.equalsIgnoreCase(fields.getPosition()) &&
							!RapidProConstants.CHILD.equalsIgnoreCase(fields.getPosition()) &&
							supervisor.getUrns().stream().anyMatch(urn -> urn.contains(phone));
				})
				.collect(Collectors.toList());

		if (!supervisors.isEmpty()) {
			return supervisors.get(supervisors.size() - 1);
		}

		HttpGet contactRequest = RapidProUtils.contactByPhoneRequest(phone, rapidProUrl, rapidProToken);
		return RapidProUtils.getRapidProContactByPhone(closeableHttpClient, contactRequest, objectMapper, logger);
	}
}
