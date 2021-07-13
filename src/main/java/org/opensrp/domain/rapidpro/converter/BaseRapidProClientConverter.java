package org.opensrp.domain.rapidpro.converter;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProFields;
import org.opensrp.service.IdentifierSourceService;
import org.opensrp.service.UniqueIdentifierService;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class BaseRapidProClientConverter implements RapidProContactClientConverter {

	private IdentifierSourceService identifierSourceService;

	private UniqueIdentifierService identifierService;

	@Autowired
	public void setIdentifierService(UniqueIdentifierService identifierService) {
		this.identifierService = identifierService;
	}

	@Autowired
	public void setIdentifierSourceService(IdentifierSourceService identifierSourceService) {
		this.identifierSourceService = identifierSourceService;
	}

	protected void setCommonZeirClientAttributes(RapidProContact rapidProContact, Client client) {
		RapidProFields fields = rapidProContact.getFields();
		client.addAttribute(RapidProConstants.IS_REGISTRATION_COMPLETE, false);
		client.addAttribute(RapidProConstants.SYSTEM_OF_REGISTRATION, RapidProConstants.MVACC);
		if (StringUtils.isNoneBlank(rapidProContact.getName())) {
			String[] nameSplit = rapidProContact.getName().split(" ");
			if (nameSplit.length == 1) {
				client.setFirstName(nameSplit[0]);
			} else if (nameSplit.length > 1) {
				client.setFirstName(nameSplit[0]);
				client.setLastName(nameSplit[1]);
			}
		}
		client.setLocationId(fields.getFacilityLocationId());
	}

	protected String getIdentifier(RapidProContact rapidProContact) {
		List<IdentifierSource> identifierSources = identifierSourceService.findAllIdentifierSources();
		if (identifierSources != null && !identifierSources.isEmpty()) {
			IdentifierSource identifierSource = identifierSources.get(0);
			List<String> uniqueIds = identifierService
					.generateIdentifiers(identifierSource, 1, rapidProContact.getFields().getSupervisorPhone());

			if (uniqueIds != null && !uniqueIds.isEmpty()) {
				String zeirId = uniqueIds.get(0);
				identifierService.markIdentifierAsUsed(zeirId);
				return zeirId;
			}
		}
		return null;
	}
}
