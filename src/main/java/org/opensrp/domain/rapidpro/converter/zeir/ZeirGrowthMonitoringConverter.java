package org.opensrp.domain.rapidpro.converter.zeir;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.converter.BaseRapidProEventConverter;
import org.smartregister.domain.Event;

public class ZeirGrowthMonitoringConverter extends BaseRapidProEventConverter {

	@Override
	public Event convertContactToEvent(RapidProContact rapidProContact) {
		return null;
	}

	@Override
	public RapidProContact convertEventToContact(Event event) {
		return null;
	}
}
