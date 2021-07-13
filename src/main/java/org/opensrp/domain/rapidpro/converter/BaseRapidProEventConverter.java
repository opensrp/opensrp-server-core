package org.opensrp.domain.rapidpro.converter;

import org.opensrp.domain.postgres.Organization;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.service.OrganizationService;
import org.smartregister.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		event.setTeam(organization.getName());
		event.setTeamId(organization.getIdentifier());
	}
}
