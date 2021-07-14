package org.opensrp.domain.rapidpro.converter.zeir;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProEventConverter;
import org.opensrp.util.constants.EventConstants;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Event;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ZeirBirthRegistrationConverter extends BaseRapidProEventConverter {

	@Override
	public Event convertContactToEvent(RapidProContact rapidProContact) {
		RapidProFields fields = rapidProContact.getFields();
		if (RapidProConstants.CHILD.equalsIgnoreCase(fields.getPosition())) {
			Event event = new Event();
			event.setEventType(EventConstants.BIRTH_REGISTRATION_EVENT);
			event.setEntityType(RapidProConstants.CHILD.toLowerCase(Locale.ROOT));
			event.addDetails(RapidProConstants.DATA_STRATEGY, RapidProConstants.NORMAL);
			addCommonEventProperties(rapidProContact, event);
			return event;
		}
		return null;
	}

	@Override
	public RapidProContact convertEventToContact(Event event) {
		return null;
	}
}
