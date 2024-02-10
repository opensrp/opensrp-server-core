package org.opensrp.domain.rapidpro.converter.zeir;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProClientConverter;
import org.opensrp.service.IdentifierSourceService;
import org.opensrp.service.UniqueIdentifierService;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;

import java.util.Collections;
import java.util.List;

public class ZeirMotherClientConverter extends BaseRapidProClientConverter {

	public ZeirMotherClientConverter() {
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
		RapidProContact motherContact = new RapidProContact();
		RapidProFields motherFields = new RapidProFields();
		motherContact.setFields(motherFields);
		addCommonClientProperties(client, motherContact);
		motherFields.setPosition(RapidProConstants.CARETAKER);
		String motherPhone = (String) client.getAttribute(RapidProConstants.SMS_REMINDER_PHONE_FORMATTED);
		//Phone number required in ISO format
		if (StringUtils.isNotBlank(motherPhone)) {
			String formattedPhone = "tel:" + (motherPhone.startsWith("+") ? motherPhone : "+" + motherPhone);
			motherContact.setUrns(Collections.singletonList(formattedPhone));
		}
		return motherContact;
	}
}
