package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.zeir.*;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.opensrp.service.callback.RapidProResponseCallback;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ZeirRapidProService extends BaseRapidProService implements RapidProResponseCallback {

	/**
	 * Process the RapidPro contacts retrieved from the server. At this time of execution, the RapidProContact has already been
	 * updated with the location id of the provider. If location Id is not provided for some reasons for instance RapidPro
	 * server was down, this contact will be skipped and reprocessed later.
	 * This method receives RapidPro contacts and generate BirthRegistration, Vaccination and Growth Monitoring events based
	 * on the available data on the contacts field.
	 *
	 * @param response the response received from the RapidPro
	 */
	@Override
	public void handleContactResponse(String response, RapidProOnTaskComplete onTaskComplete) {
		JSONObject responseJson = new JSONObject(response);
		JSONArray results = responseJson.optJSONArray(RapidProConstants.RESULTS);

		if (results != null) {

			if (!reentrantLock.tryLock()) {
				logger.warn("Rapidpro results processing in progress...");
			}

			try {
				List<RapidProContact> rapidProContacts = objectMapper.readValue(results.toString(), new TypeReference<>() {

				});

				logger.info("Found " + (rapidProContacts.isEmpty() ? 0 : rapidProContacts.size()) + " modified contacts");

				for (RapidProContact rapidProContact : rapidProContacts) {
					processRegistrationEventClient(rapidProContact);
					processVaccinationEvent(rapidProContact);
					processGrowthMonitoringEvent(rapidProContact);
				}
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

	public void processRegistrationEventClient(RapidProContact rapidProContact) {
		saveClient(rapidProContact, new ZeirChildClientConverter());
		saveClient(rapidProContact, new ZeirMotherClientConverter());
		saveEvent(rapidProContact, new ZeirBirthRegistrationConverter());
	}

	public void processVaccinationEvent(RapidProContact rapidProContact) {
		saveEvent(rapidProContact, new ZeirVaccinationConverter());
	}

	public void processGrowthMonitoringEvent(RapidProContact rapidProContact) {
		saveEvent(rapidProContact, new ZeirGrowthMonitoringConverter());
	}

	public String getProviderLocationId(RapidProContact rapidProContact) {
		Long count = locationService.countAllLocations(0L);

		RapidProFields fields = rapidProContact.getFields();
		String province = fields.getProvince();
		String district = fields.getDistrict();
		String facility = fields.getFacility();

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
}
