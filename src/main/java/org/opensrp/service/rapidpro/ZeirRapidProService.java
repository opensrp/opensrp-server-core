package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.domain.rapidpro.RapidProStateToken;
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
import org.opensrp.util.constants.EventConstants;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Subclass of BaseRapidProService for ZEIR project. This implementation is for RapidPro/MVACC integration.
 */
@Service
public class ZeirRapidProService extends BaseRapidProService implements RapidProResponseCallback {

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
	public void handleContactResponse(String response, RapidProOnTaskComplete onTaskComplete) {
		String currentDateTime = Instant.now().toString();
		JSONArray results = getResults(response);

		if (results != null) {
			if (!reentrantLock.tryLock()) {
				logger.warn("Rapidpro results processing in progress...");
			}

			try {
				List<RapidProContact> rapidProContacts = getRapidProContacts(results);

				logger.info("Found " + (rapidProContacts.isEmpty() ? 0 : rapidProContacts.size()) + " modified contacts");

				for (RapidProContact rapidProContact : rapidProContacts) {
					try {
						RapidProContact supervisorContact =
								getSupervisorContact(rapidProContact.getFields().getSupervisorPhone());
						if (supervisorContact != null) {
							String locationId = getLocationId(supervisorContact);
							if (StringUtils.isNoneBlank(locationId)) {
								rapidProContact.getFields().setFacilityLocationId(locationId);
								processRegistrationEventClient(rapidProContact, rapidProContacts);
								processVaccinationEvent(rapidProContact);
								processGrowthMonitoringEvent(rapidProContact);
							}
						} else {
							processSupervisor(rapidProContact);
						}
					}
					catch (Exception exception) {
						logger.error(exception.getMessage(), exception);
						// Catch all exception thrown when attempting to save data to the database, keep track of contacts
						updateStateTokenFromContactDates(rapidProContacts);
					}
					finally {
						reentrantLock.unlock();
					}
				}

				configService.updateAppStateToken(RapidProStateToken.RAPIDPRO_STATE_TOKEN, currentDateTime);
				onTaskComplete.completeTask();
			}
			catch (JsonProcessingException jsonException) {
				logger.error(jsonException.getMessage(), jsonException);
			}
			finally {
				reentrantLock.unlock();
			}
		}
	}

	private String getLocationId(RapidProContact supervisorContact) {
		RapidProFields supervisorFields = supervisorContact.getFields();
		return getProviderLocationId(supervisorFields.getProvince(),
				supervisorFields.getDistrict(), supervisorFields.getFacility());
	}

	private void updateStateTokenFromContactDates(List<RapidProContact> rapidProContacts) {
		Instant currentDateTime = Instant.now();
		for (RapidProContact rapidProContact : rapidProContacts) {
			try {
				Instant modifiedOn = Instant.parse(rapidProContact.getModifiedOn());
				if (modifiedOn.isBefore(currentDateTime)) {
					currentDateTime = modifiedOn;
				}
			}
			catch (DateTimeParseException parseException) {
				logger.error("Error parsing RapidProContact modified date: ", parseException);
			}
		}
		configService.updateAppStateToken(RapidProStateToken.RAPIDPRO_STATE_TOKEN, currentDateTime.toString());
	}

	private void processSupervisor(RapidProContact rapidProContact) {
		//TODO implement processing for supervisor
	}

	private JSONArray getResults(String response) {
		JSONObject responseJson = new JSONObject(response);
		return responseJson.optJSONArray(RapidProConstants.RESULTS);
	}

	private List<RapidProContact> getRapidProContacts(JSONArray results) throws JsonProcessingException {
		return objectMapper.readValue(results.toString(), new TypeReference<>() {

		});
	}

	/**
	 * Create both mother and child registration event and client. Registration should only be done when both mother and
	 * child contacts are present otherwise proceed to process other events (Vaccination and Growth Monitoring)
	 *
	 * @param rapidProContact  Current contact
	 * @param rapidProContacts All RapidPro contacts used to filter mother contact
	 */
	public void processRegistrationEventClient(RapidProContact rapidProContact, List<RapidProContact> rapidProContacts) {
		RapidProFields fields = rapidProContact.getFields();
		if (RapidProConstants.CHILD.equalsIgnoreCase(fields.getPosition())) {
			RapidProContact motherContact = getMotherContact(fields.getMotherName(), fields.getMotherPhone(),
					rapidProContacts);
			if (motherContact != null) {
				processChildRegistration(rapidProContact, motherContact);

				Client existingMotherClient = clientService.getByBaseEntityId(motherContact.getUuid());
				if (existingMotherClient == null) {
					ZeirMotherClientConverter clientConverter = new ZeirMotherClientConverter();
					Client motherClient = clientConverter.convertContactToClient(motherContact);
					if (motherClient != null) {
						clientService.addorUpdate(motherClient);
						saveEvent(motherContact, new ZeirMotherRegistrationConverter());
					}
				}
			}
		}
	}

	private void saveEvent(RapidProContact rapidProContact, BaseRapidProEventConverter eventConverter) {
		Event event = eventConverter.convertContactToEvent(rapidProContact);
		eventService.addorUpdateEvent(event, rapidProContact.getFields().getSupervisorPhone());
	}

	private void processChildRegistration(RapidProContact childContact, RapidProContact motherContact) {
		Client existingChildClient = clientService.getByBaseEntityId(childContact.getUuid());

		if (existingChildClient == null) {
			ZeirChildClientConverter clientConverter = new ZeirChildClientConverter();
			Client childClient = clientConverter.convertContactToClient(childContact);
			if (childClient != null) {
				childClient.getRelationships()
						.put(RapidProConstants.MOTHER, Collections.singletonList(motherContact.getUuid()));
				clientService.addorUpdate(childClient);
				saveEvent(childContact, new ZeirBirthRegistrationConverter());
			}
		}
	}

	private RapidProContact getMotherContact(String motherName, String motherPhone, List<RapidProContact> rapidProContacts) {
		List<RapidProContact> motherList = rapidProContacts.stream()
				.filter(it ->
						RapidProConstants.CARETAKER.equalsIgnoreCase(it.getFields().getPosition()) &&
								motherName.equalsIgnoreCase(it.getFields().getMotherName()) &&
								motherPhone.equalsIgnoreCase(it.getFields().getMotherPhone()))
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
			ZeirVaccinationConverter eventConverter = new ZeirVaccinationConverter();
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
			ZeirGrowthMonitoringConverter eventConverter = new ZeirGrowthMonitoringConverter();
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
		}
		if (value != null && dateModified != null) {
			if (filteredExistingGMEvents.isEmpty()) {
				saveEvents(fields, filterGMEvents(processedGMEvents, gmEvent));
			} else {
				Instant contactGMEventDate = Instant.parse(dateModified);
				Instant lastGMEventDate = getLastGMEventDate(filteredExistingGMEvents);
				if (contactGMEventDate.isAfter(lastGMEventDate)) {
					saveEvents(fields, filterGMEvents(processedGMEvents, gmEvent));
				}
			}
		}
	}

	private void saveEvents(RapidProFields fields, List<Event> events) {
		for (Event processedGMEvent : events) {
			eventService.addorUpdateEvent(processedGMEvent, fields.getSupervisorPhone());
		}
	}

	private Instant getLastGMEventDate(List<Event> filteredGMEvents) {
		Instant currentInstant = Instant.ofEpochMilli(filteredGMEvents.get(0).getEventDate().toInstant().getMillis());
		for (Event filteredEvent : filteredGMEvents) {
			Instant existingGMEventInstant = Instant.ofEpochMilli(filteredEvent.getEventDate().toInstant().getMillis());
			if (existingGMEventInstant.isAfter(currentInstant)) {
				currentInstant = existingGMEventInstant;
			}
		}
		return currentInstant;
	}

	private List<Event> filterGMEvents(List<Event> previousGMEvents, GMEvent gmEvent) {
		return previousGMEvents.stream()
				.filter(it -> it.getEntityType().equalsIgnoreCase(gmEvent.name()))
				.collect(Collectors.toList());
	}

	public String getProviderLocationId(String province, String district, String facility) {
		if (StringUtils.isBlank(province) || StringUtils.isBlank(province) || StringUtils.isBlank(province)) {
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

			return facilities.get(0).getProperties().getUid();
		}
		return null;
	}

	private List<PhysicalLocation> getDistrictLocations(Long count, String district, List<PhysicalLocation> provinces) {
		for (PhysicalLocation province : provinces) {
			if (locationTagExists(province.getLocationTags(), RapidProConstants.PROVINCE)) {
				String provinceUuid = province.getProperties().getUid();
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
					String districtUuid = districts.get(0).getProperties().getUid();
					return locationService.findLocationByIdWithChildren(false, districtUuid, count.intValue());
				}
			}
		}
		return null;
	}

	public void queryContacts(RapidProOnTaskComplete onTaskComplete) {
		String dateModified = (String) configService.getAppStateTokenByName(RapidProStateToken.RAPIDPRO_STATE_TOKEN)
				.getValue();

		String baseUrl = getBaseUrl();
		String url = !dateModified.equalsIgnoreCase("#") ? baseUrl + "/contacts.json?after=" + dateModified :
				baseUrl + "/contacts.json?before=" + Instant.now().toString();

		HttpGet contactsRequest = (HttpGet) setupRapidproRequest(url, new HttpGet());

		try {
			HttpResponse httpResponse = httpClient.execute(contactsRequest);
			if (httpResponse != null && httpResponse.getEntity() != null) {
				handleContactResponse(EntityUtils.toString(httpResponse.getEntity()), onTaskComplete);
			}
		}
		catch (IOException exception) {
			logger.error(exception.getMessage(), exception);
		}
	}

	public RapidProContact getSupervisorContact(String phone) {

		if (StringUtils.isBlank(phone) || StringUtils.isEmpty(phone)) {
			return null;
		}

		HttpGet contactsRequest = (HttpGet) setupRapidproRequest(getBaseUrl() + "/contacts.json?urn=tel" + phone,
				new HttpGet());

		try {
			HttpResponse httpResponse = httpClient.execute(contactsRequest);
			if (httpResponse != null && httpResponse.getEntity() != null) {
				JSONArray results = getResults(EntityUtils.toString(httpResponse.getEntity()));
				List<RapidProContact> rapidProContacts = getRapidProContacts(results);
				if (rapidProContacts == null || rapidProContacts.isEmpty()) {
					return null;
				}
				return rapidProContacts.get(0);
			}
		}
		catch (IOException exception) {
			logger.error(exception.getMessage(), exception);
		}
		return null;
	}
}
