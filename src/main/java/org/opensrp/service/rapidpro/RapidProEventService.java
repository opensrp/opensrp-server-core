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

		Client client = clientService.getByBaseEntityId(event.getBaseEntityId());

		if (client != null && client.getAttributes() != null &&
				client.getAttributes().containsKey(RapidProConstants.SMS_REMINDER_PHONE)) {
			String property = null;
			String eventType = event.getEventType();
			switch (eventType) {
				case EventConstants.BIRTH_REGISTRATION_EVENT:
					property = ZeirRapidProEntityProperty.REGISTRATION_DATA.name();
					break;
				case EventConstants.VACCINATION_EVENT:
					property = ZeirRapidProEntityProperty.VACCINATION_DATA.name();
					break;
				case EventConstants.GROWTH_MONITORING_EVENT:
					property = ZeirRapidProEntityProperty.GROWTH_MONITORING_DATA.name();
					break;
				default:
					break;
			}

			if (StringUtils.isNotBlank(property)) {
				RapidproState rapidproState = new RapidproState();
				rapidproState.setUuid(RapidProConstants.UNPROCESSED_UUID);
				rapidproState.setEntity(ZeirRapidProEntity.CHILD.name());
				rapidproState.setProperty(property);
				rapidproState.setPropertyValue(event.getFormSubmissionId());
				rapidproState.setSyncStatus(RapidProStateSyncStatus.UN_SYNCED.name());
				rapidProStateService.saveRapidProState(rapidproState);
			}
		}
	}
}
