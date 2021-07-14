package org.opensrp.domain.rapidpro.converter;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.common.util.DateUtil;
import org.opensrp.domain.postgres.Organization;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.service.OrganizationService;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
	}

	protected Obs vaccineObs(String parentCode, String fieldCode, String formSubmissionField, String value) {
		Date vaccineDate = new DateTime(Instant.parse(value).toEpochMilli()).toDate();
		return createObs(padOpenMRSCode(fieldCode), DateUtil.yyyyMMdd.format(vaccineDate))
				.withParentCode(padOpenMRSCode(parentCode))
				.withFormSubmissionField(formSubmissionField)
				.withFieldDataType(RapidProConstants.DATE);
	}

	protected Obs vaccineDoseObs(String parentCode, String fieldCode, String formSubmissionField, String value) {
		return createObs(padOpenMRSCode(fieldCode), value)
				.withParentCode(padOpenMRSCode(parentCode))
				.withFormSubmissionField(formSubmissionField)
				.withFieldCode(fieldCode + RapidProConstants.DOSE)
				.withFieldDataType(RapidProConstants.CALCULATE);
	}

	protected Obs createObs(String fieldCode, String value) {
		return new Obs()
				.withFieldType(RapidProConstants.CONCEPT)
				.withFieldDataType(RapidProConstants.TEXT)
				.withFieldCode(fieldCode)
				.withValues(Collections.singletonList(value))
				.withFormSubmissionField(fieldCode)
				.withsaveObsAsArray(false);
	}

	/**
	 * Pad the given code with character 'A' to meet the required size. OpenMRS concepts should be at least 20 characters long
	 *
	 * @param code to be padded
	 * @return padded string or the original string
	 * @see <a href=https://wiki.openmrs.org/display/docs/OpenMRS+Concept+Source+Registry>OpenMRS Concept Source Registry</a>
	 */
	protected String padOpenMRSCode(String code) {
		if (StringUtils.isNumeric(code)) {
			return StringUtils.rightPad(code, 36, "A");
		}
		return code;
	}

	@Override
	public List<Event> convertContactToEvents(RapidProContact rapidProContact) {
		return null;
	}
}
