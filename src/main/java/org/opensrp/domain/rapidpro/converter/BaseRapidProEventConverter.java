package org.opensrp.domain.rapidpro.converter;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.common.util.DateUtil;
import org.opensrp.domain.postgres.Organization;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.converter.zeir.ZeirVaccine;
import org.opensrp.service.OrganizationService;
import org.opensrp.util.DateParserUtils;
import org.opensrp.util.constants.RapidProConstants;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class BaseRapidProEventConverter implements RapidProContactEventConverter {

	protected OrganizationService organizationService;

	public BaseRapidProEventConverter(OrganizationService organizationService) {
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
		Date vaccineDate = DateParserUtils.parseZoneDateTime(value).toDate();
		return createObs(padOpenMRSCode(fieldCode), DateUtil.yyyyMMdd.format(vaccineDate))
				.withParentCode(padOpenMRSCode(parentCode))
				.withFormSubmissionField(formSubmissionField)
				.withFieldDataType(RapidProConstants.DATE);
	}

	protected Obs vaccineDoseObs(String parentCode, String fieldCode, String formSubmissionField, ZeirVaccine vaccine) {
		return createObs(padOpenMRSCode(fieldCode), getVaccineSequence(vaccine))
				.withParentCode(padOpenMRSCode(parentCode))
				.withFormSubmissionField(formSubmissionField + RapidProConstants.DOSE)
				.withFieldCode(fieldCode)
				.withFieldDataType(RapidProConstants.CALCULATE);
	}

	protected String getVaccineSequence(ZeirVaccine vaccine) {
		String[] splitVaccine = vaccine.name().split("_");
		if (splitVaccine.length <= 1) {
			return "1";
		} else {
			String lastItem = splitVaccine[splitVaccine.length - 1];
			if (StringUtils.isNumeric(lastItem)) {
				return String.valueOf(Integer.parseInt(lastItem) + 1);
			} else {
				return "1";
			}
		}
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
