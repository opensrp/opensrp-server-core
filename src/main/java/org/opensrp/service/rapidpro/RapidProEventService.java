package org.opensrp.service.rapidpro;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.RapidProStateSyncStatus;
import org.opensrp.domain.rapidpro.ZeirRapidProEntity;
import org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty;
import org.opensrp.service.ClientService;
import org.opensrp.util.constants.EventConstants;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RapidProEventService {

	@Value("#{opensrp['rapidpro.project']}")
	protected String rapidProProject;

	private ZeirRapidProStateService rapidProStateService;

	private ClientService clientService;

	@Autowired
	public void setRapidProStateService(ZeirRapidProStateService rapidProStateService) {
		this.rapidProStateService = rapidProStateService;
	}

	@Autowired
	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	public void processEvent(Event event) {
		if (RapidProConstants.RapidProProjects.ZEIR_RAPIDPRO.equalsIgnoreCase(rapidProProject)) {
			handleZeirEvent(event);
		}
	}

	public void handleZeirEvent(Event event) {
		Client currentClient = clientService.getByBaseEntityId(event.getBaseEntityId());
		if (currentClient != null) {
			if (EventConstants.BIRTH_REGISTRATION_EVENT.equalsIgnoreCase(event.getEventType()) ||
					EventConstants.VACCINATION_EVENT.equalsIgnoreCase(event.getEventType()) ||
					EventConstants.GROWTH_MONITORING_EVENT.equalsIgnoreCase(event.getEventType())) {

				if (currentClient.getRelationships() != null &&
						currentClient.getRelationships().containsKey(RapidProConstants.MOTHER)) {
					String motherEntityId = currentClient.getRelationships().get(RapidProConstants.MOTHER).get(0);
					Client motherClient = clientService.getByBaseEntityId(motherEntityId);
					if (motherNotRegisteredFromMvacc(motherClient)) {
						saveRapidProState(event);
					}
				}

			} else if (EventConstants.NEW_WOMAN_REGISTRATION_EVENT.equalsIgnoreCase(event.getEventType())) {
				if (motherNotRegisteredFromMvacc(currentClient)) {
					saveRapidProState(event);
				}
			}
		}
	}

	private boolean motherNotRegisteredFromMvacc(Client motherClient) {
		if (motherClient == null) {
			return false;
		}
		String systemOfReg = (String) motherClient.getAttribute(RapidProConstants.SYSTEM_OF_REGISTRATION);
		String smsReminder = (String) motherClient.getAttribute(RapidProConstants.SMS_REMINDER);
		String formattedPhone = (String) motherClient.getAttribute(RapidProConstants.SMS_REMINDER_PHONE_FORMATTED);
		return StringUtils.isNotBlank(systemOfReg) && !RapidProConstants.MVACC.equalsIgnoreCase(systemOfReg) &&
				StringUtils.isNotBlank(smsReminder) && RapidProConstants.YES.equalsIgnoreCase(smsReminder) &&
				StringUtils.isNotBlank(formattedPhone) && !"0".equals(formattedPhone);
	}

	private void saveRapidProState(Event event) {
		String property;
		String entity = ZeirRapidProEntity.CHILD.name();
		String eventType = event.getEventType();
		switch (eventType) {
			case EventConstants.BIRTH_REGISTRATION_EVENT:
				property = ZeirRapidProEntityProperty.REGISTRATION_DATA.name();
				break;
			case EventConstants.NEW_WOMAN_REGISTRATION_EVENT:
				entity = ZeirRapidProEntity.CARETAKER.name();
				property = ZeirRapidProEntityProperty.REGISTRATION_DATA.name();
				break;
			case EventConstants.VACCINATION_EVENT:
				property = ZeirRapidProEntityProperty.VACCINATION_DATA.name();
				break;
			case EventConstants.GROWTH_MONITORING_EVENT:
				property = ZeirRapidProEntityProperty.GROWTH_MONITORING_DATA.name();
				break;
			default:
				property = null;
				break;
		}

		if (StringUtils.isNotBlank(property)) {
			RapidproState rapidproState = new RapidproState();
			rapidproState.setUuid(RapidProConstants.UNPROCESSED_UUID);
			rapidproState.setEntity(entity);
			rapidproState.setProperty(property);
			rapidproState.setPropertyKey(event.getBaseEntityId());
			rapidproState.setPropertyValue(event.getFormSubmissionId());
			rapidproState.setSyncStatus(RapidProStateSyncStatus.UN_SYNCED.name());
			rapidProStateService.saveRapidProState(rapidproState);
		}
	}
}
