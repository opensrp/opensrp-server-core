package org.opensrp.domain.rapidpro.converter.zeir;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProEventConverter;
import org.opensrp.util.constants.EventConstants;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ZeirGrowthMonitoringConverter extends BaseRapidProEventConverter {

	public enum GMEvent {
		HEIGHT, WEIGHT
	}

	@Override
	public Event convertContactToEvent(RapidProContact rapidProContact) {
		return null;
	}

	@Override
	public RapidProContact convertEventToContact(Event event) {
		return null;
	}

	@Override
	public List<Event> convertContactToEvents(RapidProContact rapidProContact) {
		RapidProFields fields = rapidProContact.getFields();
		if (RapidProConstants.CHILD.equalsIgnoreCase(fields.getPosition())) {
			return new ArrayList<>() {

				{
					add(createGMEvent(rapidProContact, GMEvent.HEIGHT));
					add(createGMEvent(rapidProContact, GMEvent.WEIGHT));
				}
			};
		}
		return null;
	}

	private Event createGMEvent(RapidProContact rapidProContact, GMEvent gmEvent) {
		Event event = new Event();
		addCommonEventProperties(rapidProContact, event);
		event.setEventType(EventConstants.GROWTH_MONITORING_EVENT);
		event.setEntityType(gmEvent.name().toLowerCase(Locale.ROOT));
		RapidProFields fields = rapidProContact.getFields();
		switch (gmEvent) {
			case HEIGHT:
				event.withObs(createObs(padOpenMRSCode("5090"), fields.getHeight())
						.withFormSubmissionField(RapidProConstants.HEIGHT_CM)
						.withFieldDataType(RapidProConstants.DECIMAL));
				break;
			case WEIGHT:
				event.withObs(createObs(padOpenMRSCode("5089"), fields.getWeight())
						.withFormSubmissionField(RapidProConstants.WEIGHT_KGS)
						.withFieldDataType(RapidProConstants.DECIMAL));
				break;
			default:
				break;
		}
		return event;
	}

}
