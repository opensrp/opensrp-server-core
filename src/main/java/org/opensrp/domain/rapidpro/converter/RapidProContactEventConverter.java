package org.opensrp.domain.rapidpro.converter;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.smartregister.domain.Event;

import java.util.List;

public interface RapidProContactEventConverter {

	Event convertContactToEvent(RapidProContact rapidProContact);

	List<Event> convertContactToEvents(RapidProContact rapidProContact);

	RapidProContact convertEventToContact(Event event);
}
