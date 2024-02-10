package org.opensrp.service.rapidpro;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.util.ArrayList;
import java.util.List;

@Service
public class RapidProEventService {

	private final Logger logger = LogManager.getLogger(getClass());

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

		if (currentClient != null && (event.getDetails() == null || (event.getDetails() != null && !event.getDetails()
				.containsKey(RapidProConstants.SYSTEM_OF_REGISTRATION)))) {
			if (EventConstants.NEW_WOMAN_REGISTRATION_EVENT.equalsIgnoreCase(event.getEventType()) ||
					EventConstants.UPDATE_MOTHER_DETAILS.equalsIgnoreCase(event.getEventType())) {
				if (optInForSMSFromOpenSRP(currentClient)) {
					saveRapidProState(event);
				}
			} else {
				Client motherClient = getMotherClient(currentClient);
				boolean optInForSMSFromOpenSRP = optInForSMSFromOpenSRP(motherClient);
				if ((EventConstants.BIRTH_REGISTRATION_EVENT.equalsIgnoreCase(event.getEventType()) ||
						EventConstants.UPDATE_BIRTH_REGISTRATION.equalsIgnoreCase(event.getEventType()))
						&& optInForSMSFromOpenSRP) {
					saveRapidProState(event);
				} else if (EventConstants.VACCINATION_EVENT.equalsIgnoreCase(event.getEventType()) ||
						EventConstants.GROWTH_MONITORING_EVENT.equalsIgnoreCase(event.getEventType()) &&
								(registeredFromMVACC(motherClient) || subscribedForSMSReminder(motherClient))
				) {
					saveRapidProState(event);
				}
			}
		}
	}

	private Client getMotherClient(Client childClient) {
		Client motherClient = null;
		if (childClient.getRelationships() != null &&
				childClient.getRelationships().containsKey(RapidProConstants.MOTHER)) {
			String motherEntityId = childClient.getRelationships().get(RapidProConstants.MOTHER).get(0);
			motherClient = clientService.getByBaseEntityId(motherEntityId);
		}
		return motherClient;
	}

	private boolean optInForSMSFromOpenSRP(Client motherClient) {
		if (motherClient == null) {
			return false;
		}
		return !registeredFromMVACC(motherClient) && subscribedForSMSReminder(motherClient);
	}

	private boolean subscribedForSMSReminder(Client motherClient) {
		String smsReminder = (String) motherClient.getAttribute(RapidProConstants.SMS_REMINDER);
		String formattedPhone = (String) motherClient.getAttribute(RapidProConstants.SMS_REMINDER_PHONE_FORMATTED);
		return StringUtils.isNotBlank(smsReminder) &&
				RapidProConstants.YES.equalsIgnoreCase(smsReminder) &&
				StringUtils.isNotBlank(formattedPhone) &&
				!"0".equals(formattedPhone);
	}

	private boolean registeredFromMVACC(Client motherClient) {
		if (motherClient != null) {
			String systemOfReg = (String) motherClient.getAttribute(RapidProConstants.SYSTEM_OF_REGISTRATION);
			return StringUtils.isNotBlank(systemOfReg) && RapidProConstants.MVACC.equalsIgnoreCase(systemOfReg);
		}
		return false;
	}

	private void saveRapidProState(Event event) {
		String property;
		String entity = ZeirRapidProEntity.CHILD.name();
		switch (event.getEventType()) {
			case EventConstants.BIRTH_REGISTRATION_EVENT:
			case EventConstants.UPDATE_BIRTH_REGISTRATION:
				property = ZeirRapidProEntityProperty.REGISTRATION_DATA.name();
				break;
			case EventConstants.NEW_WOMAN_REGISTRATION_EVENT:
			case EventConstants.UPDATE_MOTHER_DETAILS:
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
				property = "";
				break;
		}

		// Child registered in ZEIR
		List<RapidproState> clientStates = rapidProStateService.getRapidProState(ZeirRapidProEntity.CHILD.name(),
				ZeirRapidProEntityProperty.REGISTRATION_DATA.name(), event.getBaseEntityId());

		// Mother registered in ZEIR
		List<RapidproState> motherClientStates = rapidProStateService.getRapidProState(ZeirRapidProEntity.CARETAKER.name(),
				ZeirRapidProEntityProperty.REGISTRATION_DATA.name(), event.getBaseEntityId());

		//Child registered in Rapidpro (uses the uuid (base_entity_id),  entity, property)
		List<RapidproState> clientStatesRapidPro = rapidProStateService.getRapidProStatesByUuid(event.getBaseEntityId(),
				ZeirRapidProEntity.CHILD.name(), ZeirRapidProEntityProperty.IDENTIFIER.name());

		//Mother registered in Rapidpro (uses the uuid (base_entity_id),  entity, property)
		List<RapidproState> motherClientStatesRapidPro =
				rapidProStateService.getRapidProStatesByUuid(event.getBaseEntityId(),
						ZeirRapidProEntity.CARETAKER.name(), ZeirRapidProEntityProperty.IDENTIFIER.name());

		List<RapidproState> combinedStates = new ArrayList<>();
		combinedStates.addAll(clientStates);
		combinedStates.addAll(motherClientStates);
		combinedStates.addAll(clientStatesRapidPro);
		combinedStates.addAll(motherClientStatesRapidPro);

		switch (event.getEventType()) {
			case EventConstants.UPDATE_BIRTH_REGISTRATION:
			case EventConstants.UPDATE_MOTHER_DETAILS:
				if (!combinedStates.isEmpty()) {
					property = ZeirRapidProEntityProperty.UPDATE_REGISTRATION_DATA.name();
					saveRapidProState(event, property, entity, combinedStates.get(0).getUuid());
				} else {
					saveRapidProState(event, property, entity, RapidProConstants.UNPROCESSED_UUID);
				}
				break;
			case EventConstants.NEW_WOMAN_REGISTRATION_EVENT:
			case EventConstants.BIRTH_REGISTRATION_EVENT:
			case EventConstants.VACCINATION_EVENT:
			case EventConstants.GROWTH_MONITORING_EVENT:
				saveRapidProState(event, property, entity, RapidProConstants.UNPROCESSED_UUID);
				break;
			default:
				break;
		}
	}

	private void saveRapidProState(Event event, String property, String entity, String uuid) {
		if (StringUtils.isNotBlank(property)) {
			RapidproState rapidproState = new RapidproState();
			rapidproState.setUuid(uuid);
			rapidproState.setEntity(entity);
			rapidproState.setProperty(property);
			rapidproState.setPropertyKey(event.getBaseEntityId());
			rapidproState.setPropertyValue(event.getFormSubmissionId());
			rapidproState.setSyncStatus(RapidProStateSyncStatus.UN_SYNCED.name());
			rapidProStateService.saveRapidProState(rapidproState);
		} else {
			logger.warn("{}, {} for client identified by {} has already been registered/synced to RapidPro", entity,
					property, event.getBaseEntityId());
		}
	}
}
