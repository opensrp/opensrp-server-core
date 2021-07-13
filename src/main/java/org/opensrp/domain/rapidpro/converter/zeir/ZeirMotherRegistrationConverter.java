package org.opensrp.domain.rapidpro.converter.zeir;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.converter.BaseRapidProEventConverter;
import org.smartregister.domain.Event;

public class ZeirMotherRegistrationConverter extends BaseRapidProEventConverter {

	@Override
	public Event convertContactToEvent(RapidProContact rapidProContacts) {
		return null;
	}

	@Override
	public RapidProContact convertEventToContact(Event event) {
		return null;
	}
}
