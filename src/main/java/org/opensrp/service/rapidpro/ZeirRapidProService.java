package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.converter.zeir.*;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.opensrp.service.callback.RapidProResponseCallback;
import org.opensrp.util.constants.RapidProConstants;
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
}
