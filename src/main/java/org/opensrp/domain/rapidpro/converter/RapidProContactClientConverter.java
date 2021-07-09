package org.opensrp.domain.rapidpro.converter;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.smartregister.domain.Client;

public interface RapidProContactClientConverter {

	Client convertContactToClient(RapidProContact rapidProContact);

	RapidProContact convertClientToContact(Client client);
}
