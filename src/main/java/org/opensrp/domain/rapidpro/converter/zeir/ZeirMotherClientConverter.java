package org.opensrp.domain.rapidpro.converter.zeir;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProClientConverter;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;

import java.util.List;

public class ZeirMotherClientConverter extends BaseRapidProClientConverter {

	@Override
	public Client convertContactToClient(RapidProContact rapidProContact) {
		RapidProFields fields = rapidProContact.getFields();
		if (RapidProConstants.CARETAKER.equalsIgnoreCase(fields.getPosition())) {
			Client motherClient = new Client(rapidProContact.getUuid());
			setCommonZeirClientAttributes(rapidProContact, motherClient);
			addZeirClientIdentifier(rapidProContact, motherClient, RapidProConstants.M_ZEIR_ID);
			List<String> urns = rapidProContact.getUrns();
			if (urns != null && isValidPhoneNumber(urns.get(0))) {
				motherClient.addAttribute(RapidProConstants.MOTHER_GUARDIAN_NUMBER, urns.get(0));
			}
		}
		return null;
	}

	@Override
	public RapidProContact convertClientToContact(Client client) {
		return null;
	}
}
