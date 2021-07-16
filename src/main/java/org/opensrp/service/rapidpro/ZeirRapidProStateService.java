package org.opensrp.service.rapidpro;

import org.json.JSONObject;
import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.ZeirRapidProEntity;
import org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty;
import org.opensrp.util.constants.RapidProConstants;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Subclass for BaseRapidProStateService that has ZIER implementation on how to update RapidPro contacts from OpenSRP
 */
@Service
public class ZeirRapidProStateService extends BaseRapidProStateService {

	private final ReentrantLock reentrantLock = new ReentrantLock();

	public void updateRapidProContactsFields() {
		updateRapidProContacts(ZeirRapidProEntity.CHILD, ZeirRapidProEntityProperty.IDENTIFIER);
		updateRapidProContacts(ZeirRapidProEntity.SUPERVISOR, ZeirRapidProEntityProperty.LOCATION_ID);
	}

	private void updateRapidProContacts(ZeirRapidProEntity entity, ZeirRapidProEntityProperty property) {
		List<RapidproState> unSyncedStates = getUnSyncedRapidProStates(entity.name(), property.name());

		if (unSyncedStates != null && !unSyncedStates.isEmpty()) {
			for (RapidproState rapidproState : unSyncedStates) {
				try {
					if (!reentrantLock.tryLock()) {
						logger.error("Some instance of this process is still running");
						JSONObject payload =
								new JSONObject().put(RapidProConstants.FIELDS, getPayload(entity, rapidproState));
						updateRapidProContact(rapidproState, payload.toString());
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

	private JSONObject getPayload(ZeirRapidProEntity entity, RapidproState rapidproState) {
		JSONObject fields = new JSONObject();
		switch (entity) {
			case CHILD:
				fields.put(RapidProConstants.OPENSRP_ID, rapidproState.getPropertyValue());
				fields.put(RapidProConstants.REGISTRATION_PROCESSED, "true");
				fields.put(RapidProConstants.SYSTEM_OF_REGISTRATION, RapidProConstants.OPENSRP);
				break;
			case SUPERVISOR:
				fields.put(RapidProConstants.FACILITY_LOCATION_ID, rapidproState.getPropertyValue());
				break;
			case CARETAKER:
			default:
				break;
		}
		return fields;
	}
}

