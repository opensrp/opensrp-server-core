package org.opensrp.service.rapidpro;

import org.json.JSONObject;
import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.ZeirRapidProEntity;
import org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.util.constants.RapidProConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Subclass for BaseRapidProStateService that has ZIER implementation on how to update RapidPro contacts from OpenSRP
 */
@Service
public class ZeirRapidProStateService extends BaseRapidProStateService {

	private final ReentrantLock reentrantLock = new ReentrantLock();

	private EventService eventService;

	private ClientService clientService;

	public void updateRapidProContactsFields() {
		updateRapidProContacts(ZeirRapidProEntity.CHILD, ZeirRapidProEntityProperty.IDENTIFIER);
		updateRapidProContacts(ZeirRapidProEntity.SUPERVISOR, ZeirRapidProEntityProperty.LOCATION_ID);
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	private void updateRapidProContacts(ZeirRapidProEntity entity, ZeirRapidProEntityProperty property) {
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
						postRapidProContact(rapidproState, payload.toString(), true);
					}
				}
				catch (Exception exception) {
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
				if (property == ZeirRapidProEntityProperty.IDENTIFIER) {
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

	private JSONObject getBirthRegistrationPayload(RapidproState rapidproState) {
		return null;
	}

	private JSONObject getVaccinationPayload(RapidproState rapidproState) {
		return null;
	}

	private JSONObject getGrowthMonitoringPayload(RapidproState rapidproState) {
		return null;
	}
}

