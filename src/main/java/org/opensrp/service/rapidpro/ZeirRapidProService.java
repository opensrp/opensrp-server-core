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
import org.opensrp.domain.rapidpro.converter.zeir.*;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.opensrp.service.callback.RapidProResponseCallback;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
							RapidProFields supervisorFields = supervisorContact.getFields();
							String locationId = getProviderLocationId(supervisorFields.getProvince(),
									supervisorFields.getDistrict(),
									supervisorFields.getFacility());
							rapidProContact.getFields().setFacilityLocationId(locationId);

							processRegistrationEventClient(rapidProContact, rapidProContacts);
							processVaccinationEvent(rapidProContact);
							processGrowthMonitoringEvent(rapidProContact);
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

				Client motherClient = clientService.getByBaseEntityId(motherContact.getUuid());
				if (motherClient == null) {
					ZeirMotherClientConverter clientConverter = new ZeirMotherClientConverter();
					Client client = clientConverter.convertContactToClient(motherContact);
					clientService.addorUpdate(client);
					saveEvent(motherContact, new ZeirMotherRegistrationConverter());
				}
			}
		}
	}

	private void saveEvent(RapidProContact rapidProContact, BaseRapidProEventConverter eventConverter) {
		Event event = eventConverter.convertContactToEvent(rapidProContact);
		eventService.addorUpdateEvent(event, rapidProContact.getFields().getSupervisorPhone());
	}

	private void processChildRegistration(RapidProContact childContact, RapidProContact motherContact) {
		Client existingClient = clientService.getByBaseEntityId(childContact.getUuid());

		if (existingClient == null) {
			ZeirChildClientConverter clientConverter = new ZeirChildClientConverter();
			Client client = clientConverter.convertContactToClient(childContact);
			client.getRelationships().put(RapidProConstants.MOTHER, Collections.singletonList(motherContact.getUuid()));
			clientService.addorUpdate(client);
			saveEvent(childContact, new ZeirBirthRegistrationConverter());
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

	public void processVaccinationEvent(RapidProContact rapidProContact) {
		if (RapidProConstants.CHILD.equalsIgnoreCase(rapidProContact.getFields().getPosition())) {
			ZeirVaccinationConverter eventConverter = new ZeirVaccinationConverter();
			Event event = eventConverter.convertContactToEvent(rapidProContact);
			eventService.addorUpdateEvent(event, rapidProContact.getFields().getSupervisorPhone());
		}
	}

	public void processGrowthMonitoringEvent(RapidProContact rapidProContact) {
		if (RapidProConstants.CHILD.equalsIgnoreCase(rapidProContact.getFields().getPosition())) {
			ZeirGrowthMonitoringConverter eventConverter = new ZeirGrowthMonitoringConverter();
			Event event = eventConverter.convertContactToEvent(rapidProContact);
			eventService.addorUpdateEvent(event, rapidProContact.getFields().getSupervisorPhone());
		}
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
		String dateModified = (String) configService.getAppStateTokenByName(RapidProStateToken.RAPIDPRO_STATE_TOKEN).getValue();

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
