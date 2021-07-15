package org.opensrp.domain.rapidpro.converter.zeir;

import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.domain.rapidpro.converter.BaseRapidProEventConverter;
import org.opensrp.service.OrganizationService;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ZeirVaccinationConverter extends BaseRapidProEventConverter {

	public ZeirVaccinationConverter(OrganizationService organizationService) {
		super(organizationService);
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
		List<Event> vaccineEvents = new ArrayList<>();
		if (RapidProConstants.CHILD.equalsIgnoreCase(fields.getPosition())) {
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.OPV_0, fields.getOpv0());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.BCG, fields.getBcg());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.OPV_1, fields.getOpv1());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.PENTA_1, fields.getDpt1());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.PCV_1, fields.getPcv1());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.ROTA_1, fields.getRota1());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.OPV_2, fields.getOpv2());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.PENTA_2, fields.getDpt2());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.PCV_2, fields.getPcv2());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.ROTA_2, fields.getRota2());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.OPV_3, fields.getOpv3());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.PCV_3, fields.getPcv3());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.PENTA_3, fields.getDpt3());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.IPV, fields.getIpv());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.MR_1, fields.getMeasles());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.OPV_4, fields.getOpv4());
			createVaccineEvent(rapidProContact, vaccineEvents, ZeirVaccine.MR_2, fields.getMeasles2());
		}
		return vaccineEvents;
	}

	private void createVaccineEvent(RapidProContact rapidProContact, List<Event> vaccineEvents,
			ZeirVaccine vaccine, String value) {

		if (value == null) {
			return;
		}

		Event vaccineEvent = new Event();
		addCommonEventProperties(rapidProContact, vaccineEvent);
		String formSubmissionField = vaccine.name().toLowerCase(Locale.ROOT);
		Obs vaccineObs = null;
		Obs vaccineDoseObs = null;

		switch (vaccine) {
			case OPV_0:
			case OPV_1:
			case OPV_2:
			case OPV_3:
			case OPV_4:
				vaccineObs = vaccineObs("783", "1410", formSubmissionField, value);
				vaccineDoseObs = vaccineDoseObs("783", "1418", formSubmissionField, value);
				break;
			case BCG:
				vaccineObs = vaccineObs("886", "1410", formSubmissionField, value);
				vaccineDoseObs = vaccineDoseObs("886", "1418", formSubmissionField, value);
				break;
			case PENTA_1:
			case PENTA_2:
			case PENTA_3:
				vaccineObs = vaccineObs("1685", "1410", formSubmissionField, value);
				vaccineDoseObs = vaccineDoseObs("1685", "1418", formSubmissionField, value);
				break;
			case PCV_1:
			case PCV_2:
			case PCV_3:
				vaccineObs = vaccineObs("162342", "1410", formSubmissionField, value);
				vaccineDoseObs = vaccineDoseObs("162342", "1418", formSubmissionField, value);
				break;
			case ROTA_1:
			case ROTA_2:
				vaccineObs = vaccineObs("159698", "1410", formSubmissionField, value);
				vaccineDoseObs = vaccineDoseObs("159698", "1418", formSubmissionField, value);
				break;
			case IPV:
				vaccineObs = vaccineObs("", "ipv", formSubmissionField, value);
				vaccineDoseObs = vaccineDoseObs("", "ipv_calculation", formSubmissionField, value);
				break;
			case MR_1:
			case MR_2:
				vaccineObs = vaccineObs("36", "1410", formSubmissionField, value);
				vaccineDoseObs = vaccineDoseObs("36", "1418", formSubmissionField, value);
				break;
			default:
				break;
		}
		if (vaccineObs != null && vaccineDoseObs != null) {
			vaccineEvents.add(vaccineEvent.withObs(vaccineObs).withObs(vaccineDoseObs));
		}
	}
}
