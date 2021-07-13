package org.opensrp.domain.rapidpro.converter;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.smartregister.domain.Event;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public abstract class BaseRapidProEventConverter implements RapidProContactEventConverter {

	protected void addCommonEventProperties(RapidProContact rapidProContact, Event event) {
		event.setBaseEntityId(rapidProContact.getUuid());
		event.setFormSubmissionId(UUID.randomUUID().toString());
		event.setLocationId(rapidProContact.getFields().getFacilityLocationId());
		event.setProviderId(rapidProContact.getFields().getSupervisorPhone());
	}
}
