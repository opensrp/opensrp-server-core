package org.opensrp.domain.rapidpro.converter.zeir;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProEventConverter;
import org.opensrp.util.constants.EventConstants;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ZeirMotherRegistrationConverter extends BaseRapidProEventConverter {

	@Override
	public Event convertContactToEvent(RapidProContact rapidProContact) {
		RapidProFields fields = rapidProContact.getFields();
		if (RapidProConstants.CARETAKER.equalsIgnoreCase(fields.getPosition())) {
			Event event = new Event();
			event.setEventType(EventConstants.NEW_WOMAN_REGISTRATION_EVENT);
			event.setEntityType(RapidProConstants.MOTHER.toLowerCase(Locale.ROOT));
			event.addDetails(RapidProConstants.DATA_STRATEGY, RapidProConstants.NORMAL);
			addCommonEventProperties(rapidProContact, event);

			//chw_name obs and chw_phone_number obs
			Obs chwNameObs = createObs(RapidProConstants.CHW_NAME, fields.getSupervisor());
			Obs chwPhoneNumberObs = createObs(RapidProConstants.CHW_PHONE_NUMBER, fields.getSupervisorPhone());
			event.withObs(chwNameObs).withObs(chwPhoneNumberObs);
			return event;
		}
		return null;
	}

	@Override
	public RapidProContact convertEventToContact(Event event) {
		return null;
	}

}
