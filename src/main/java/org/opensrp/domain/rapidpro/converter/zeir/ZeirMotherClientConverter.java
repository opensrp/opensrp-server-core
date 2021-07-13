package org.opensrp.domain.rapidpro.converter.zeir;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.converter.BaseRapidProClientConverter;
import org.smartregister.domain.Client;

public class ZeirMotherClientConverter extends BaseRapidProClientConverter {

	@Override
	public Client convertContactToClient(RapidProContact rapidProContact) {
		return null;
	}

	@Override
	public RapidProContact convertClientToContact(Client client) {
		return null;
	}
}
