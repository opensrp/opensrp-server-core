package org.opensrp.domain.rapidpro.converter;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.service.IdentifierSourceService;
import org.opensrp.service.UniqueIdentifierService;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;

import java.util.List;

public abstract class BaseRapidProClientConverter implements RapidProContactClientConverter {

	protected IdentifierSourceService identifierSourceService;

	protected UniqueIdentifierService uniqueIdentifierService;

	public BaseRapidProClientConverter() {
	}

	public BaseRapidProClientConverter(IdentifierSourceService identifierSourceService,
			UniqueIdentifierService uniqueIdentifierService) {
		this.identifierSourceService = identifierSourceService;
		this.uniqueIdentifierService = uniqueIdentifierService;
	}

	protected void addCommonZeirProperties(RapidProContact rapidProContact, Client client) {
		RapidProFields fields = rapidProContact.getFields();
		client.addAttribute(RapidProConstants.IS_REGISTRATION_COMPLETE, false);
		client.addAttribute(RapidProConstants.SYSTEM_OF_REGISTRATION, RapidProConstants.MVACC);
		if (StringUtils.isNoneBlank(rapidProContact.getName())) {
			String[] nameSplit = rapidProContact.getName().split(" ");
			if (nameSplit.length == 1) {
				client.setFirstName(nameSplit[0].trim());
			} else if (nameSplit.length > 1) {
				client.setFirstName(nameSplit[0].trim());
				client.setLastName(nameSplit[1].trim());
			}
		}
		client.setLocationId(fields.getFacilityLocationId());
	}

	protected void addZeirClientIdentifier(RapidProContact rapidProContact, Client client, String identifierType) {
		List<IdentifierSource> identifierSources = identifierSourceService.findAllIdentifierSources();
		if (identifierSources != null && !identifierSources.isEmpty()) {
			IdentifierSource identifierSource = identifierSources.get(0);
			List<String> uniqueIds = uniqueIdentifierService
					.generateIdentifiers(identifierSource, 1, rapidProContact.getFields().getSupervisorPhone());

			if (uniqueIds != null && !uniqueIds.isEmpty()) {
				String zeirId = uniqueIds.get(0);
				client.getIdentifiers().put(identifierType,
						RapidProConstants.ZEIR_ID.equalsIgnoreCase(identifierType) ? zeirId.replaceAll("-", "") : zeirId);
				uniqueIdentifierService.markIdentifierAsUsed(zeirId);
			}
		}
	}

	protected void addCommonClientProperties(Client client, RapidProContact rapidProContact) {
		RapidProFields fields = rapidProContact.getFields();
		rapidProContact.setName(client.fullName());
		if (StringUtils.isNotBlank(client.getGender())) {
			fields.setSex(client.getGender());
		}
		if (client.getBirthdate() != null) {
			DateTime dateTimeISO = client.getBirthdate().toDateTimeISO();
			if (dateTimeISO != null)
				fields.setDob(dateTimeISO.toString());
		}
		fields.setFacilityLocationId(client.getLocationId());
		if (client.getVoided() || client.getDeathdate() != null) {
			rapidProContact.setStopped(true);
			rapidProContact.setBlocked(true);
		}
	}
}
