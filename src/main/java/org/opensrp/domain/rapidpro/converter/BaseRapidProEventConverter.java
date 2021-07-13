package org.opensrp.domain.rapidpro.converter;

import org.opensrp.domain.postgres.Organization;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.service.OrganizationService;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public abstract class BaseRapidProEventConverter implements RapidProContactEventConverter {

	protected OrganizationService organizationService;

	@Autowired
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	protected void addCommonEventProperties(RapidProContact rapidProContact, Event event) {
		event.setBaseEntityId(rapidProContact.getUuid());
		event.setFormSubmissionId(UUID.randomUUID().toString());
		event.setLocationId(rapidProContact.getFields().getFacilityLocationId());
		event.setProviderId(rapidProContact.getFields().getSupervisorPhone());
		Organization organization = organizationService
				.getOrganizationByLocationId(rapidProContact.getFields().getFacilityLocationId());
		if (organization != null) {
			event.setTeam(organization.getName());
			event.setTeamId(organization.getIdentifier());
		}
		event.addDetails(RapidProConstants.DATA_STRATEGY, RapidProConstants.NORMAL);
	}

	protected Obs createObs(String fieldCode, String value) {
		return new Obs()
				.withFieldType(RapidProConstants.CONCEPT)
				.withFieldCode(fieldCode)
				.withFormSubmissionField(fieldCode)
				.withsaveObsAsArray(false)
				.withValues(Collections.singletonList(value));
	}
}
