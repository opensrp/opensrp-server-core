package org.opensrp.domain.rapidpro.converter.zeir;

import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.rapidpro.RapidProStateSyncStatus;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProClientConverter;
import org.opensrp.service.IdentifierSourceService;
import org.opensrp.service.UniqueIdentifierService;
import org.opensrp.service.rapidpro.ZeirRapidProStateService;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;

import java.util.List;

import static org.opensrp.domain.rapidpro.ZeirRapidProEntity.CARETAKER;
import static org.opensrp.domain.rapidpro.ZeirRapidProEntityProperty.REGISTRATION_DATA;

public class ZeirMotherClientConverter extends BaseRapidProClientConverter {

	private ZeirRapidProStateService zeirRapidProStateService;

	public ZeirMotherClientConverter(ZeirRapidProStateService zeirRapidProStateService) {
		this.zeirRapidProStateService = zeirRapidProStateService;
	}

	public ZeirMotherClientConverter(IdentifierSourceService identifierSourceService,
			UniqueIdentifierService uniqueIdentifierService) {
		super(identifierSourceService, uniqueIdentifierService);
	}

	@Override
	public Client convertContactToClient(RapidProContact rapidProContact) {
		RapidProFields fields = rapidProContact.getFields();
		if (RapidProConstants.CARETAKER.equalsIgnoreCase(fields.getPosition())) {
			Client motherClient = new Client(rapidProContact.getUuid());
			addCommonZeirProperties(rapidProContact, motherClient);
			addZeirClientIdentifier(rapidProContact, motherClient, RapidProConstants.M_ZEIR_ID);
			List<String> urns = rapidProContact.getUrns();
			final String prefix = "tel:+260";
			if (urns != null && !urns.isEmpty()) {
				urns.forEach(urn -> {
					if (urn.startsWith(prefix)) {
						String formattedPhone = urn.replace(prefix, "0");
						motherClient.addAttribute(RapidProConstants.MOTHER_GUARDIAN_NUMBER, formattedPhone);
						motherClient.addAttribute(RapidProConstants.SMS_REMINDER, RapidProConstants.YES);
						motherClient.addAttribute(RapidProConstants.SMS_REMINDER_PHONE, formattedPhone);
					}
				});
			}
			return motherClient;
		}
		return null;
	}

	@Override
	public RapidProContact convertClientToContact(Client client) {
		return null;
	}

	public void saveUnProcessedMother(Client motherClient, String formSubmissionId) {
		RapidproState rapidProState = new RapidproState();
		rapidProState.setUuid(RapidProConstants.UNPROCESSED_UUID);
		rapidProState.setEntity(CARETAKER.name());
		rapidProState.setProperty(REGISTRATION_DATA.name());
		rapidProState.setPropertyKey(motherClient.getBaseEntityId());
		rapidProState.setPropertyValue(formSubmissionId);
		rapidProState.setSyncStatus(RapidProStateSyncStatus.UN_SYNCED.name());
		zeirRapidProStateService.saveRapidProState(rapidProState);
	}
}
