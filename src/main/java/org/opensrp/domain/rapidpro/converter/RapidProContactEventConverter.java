package org.opensrp.domain.rapidpro.converter;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.smartregister.domain.Event;

public interface RapidProContactEventConverter {

	Event convertContactToEvent(RapidProContact rapidProContact);

	RapidProContact convertEventToContact(Event event);
}
