package org.opensrp.service;

import org.opensrp.domain.Organization;
import org.opensrp.domain.Practitioner;
import org.opensrp.domain.PractitionerRole;
import org.opensrp.domain.PractitionerRoleCode;
import org.opensrp.domain.CodeSystem;
import org.opensrp.domain.Code;
import org.opensrp.dto.CsvBulkImportDataSummary;
import org.opensrp.dto.FailedRecordSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ImportBulkDataService {

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private PractitionerService practitionerService;

	@Autowired
	private PractitionerRoleService practitionerRoleService;

	@Autowired
	private PhysicalLocationService physicalLocationService;

	private static Logger logger = LoggerFactory.getLogger(ImportBulkDataService.class.toString());

	private static final String LOCATION_ID_KEY = "Location Id";

	private static final String LOCATION_NAME_KEY = "Location Name";

	private static final String ORGANIZATION_NAME_KEY = "Organization Name";

	private static final String PLAN_ID_KEY = "Plan Id";

	private static final String ORGANIZATION_ID_KEY = "Organization Id";

	private static final String USER_ID_KEY = "User Id";

	private static final String USER_NAME_KEY = "User Name";

	private static final String NAME_KEY = "Name";

	private static final String ROLE_KEY = "Role";

	public CsvBulkImportDataSummary convertandPersistOrganizationdata(List<Map<String, String>> csvOrganizations) {

		CsvBulkImportDataSummary csvBulkImportDataSummary = new CsvBulkImportDataSummary();
		Integer rowsProcessed = 0;
		Integer rowsInCsv = csvOrganizations.size();
		csvBulkImportDataSummary.setNumberOfCsvRows(rowsInCsv);
		Integer rowCount = 0;
		List<FailedRecordSummary> failedRecordSummaries = new ArrayList<>();
		FailedRecordSummary failedRecordSummary;

		Organization organization;
		String organizationIdentifier;
		CodeSystem type;
		List<Code> codes;
		Code code;

		String locationId = "";
		String planId = "";
		String organizationName = "";
		Organization existingOrganization;
		try {
			for (Map<String, String> csvdata : csvOrganizations) {
				rowCount++;

				if (validateLocationData(csvdata)) {
					organization = new Organization();
					organizationIdentifier = UUID.randomUUID().toString();
					organization.setIdentifier(organizationIdentifier);
					type = new CodeSystem();
					codes = new ArrayList<>();
					code = new Code();
					code.setSystem("http://terminology.hl7.org/CodeSystem/organization-type");
					code.setCode("team");
					code.setDisplay("Team");
					codes.add(code);
					type.setCoding(codes);

					organization.setType(type);
					organization.setActive(Boolean.TRUE);

					organizationName = getValueFromMap(ORGANIZATION_NAME_KEY, csvdata);
					organization.setName(organizationName);

					locationId = getValueFromMap(LOCATION_ID_KEY, csvdata);

					planId = getValueFromMap(PLAN_ID_KEY, csvdata);
					existingOrganization = organizationService.findOrganizationByName(organizationName);
					if (existingOrganization == null) {
						organizationService.addOrganization(organization); // as it can not be edited
					}
					organizationService
							.assignLocationAndPlan(organizationIdentifier, locationId, planId, new Date(),
									null);  //handles update case as well
					rowsProcessed++;
				} else {
					failedRecordSummary = getFailedRecordSummaryObject(rowCount,
							"Validation failed, provided location name mismatches with the system");
					failedRecordSummaries.add(failedRecordSummary);
				}
			}
		}
		catch (Exception e) {
			logger.error("Exception occurred while persisting organization and assignment data : " + e.getMessage(), e);
			failedRecordSummary = getFailedRecordSummaryObject(rowCount, "Unknown error occurred");
			failedRecordSummaries.add(failedRecordSummary);
		}

		csvBulkImportDataSummary.setNumberOfRowsProcessed(rowsProcessed);
		csvBulkImportDataSummary.setFailedRecordSummaryList(failedRecordSummaries);
		return csvBulkImportDataSummary;

	}

	public CsvBulkImportDataSummary convertandPersistPractitionerdata(List<Map<String, String>> csvOrganizations) {

		CsvBulkImportDataSummary csvBulkImportDataSummary = new CsvBulkImportDataSummary();
		Integer rowsProcessed = 0;
		Integer rowsInCsv = csvOrganizations.size();
		csvBulkImportDataSummary.setNumberOfCsvRows(rowsInCsv);
		Integer rowCount = 0;
		List<FailedRecordSummary> failedRecordSummaries = new ArrayList<>();

		Practitioner practitioner;
		String practitionerIdentifier;
		String practitionerName;
		String userId;
		String userName;
		String organizationIdentifier;
		String code;

		String organizationIdFromCSV;
		Long organizationId;
		PractitionerRole practitionerRole;
		for (Map<String, String> csvdata : csvOrganizations) {
			try {
				rowCount++;
				organizationIdFromCSV = getValueFromMap(ORGANIZATION_ID_KEY, csvdata);
				organizationId = Long.valueOf(organizationIdFromCSV);

				organizationIdentifier = getOrganizationIdentifier(organizationId);
				userName = getValueFromMap(USER_NAME_KEY, csvdata);
				practitioner = practitionerService.getPractionerByUsername(userName);

				if (validateOrganizationFields(csvdata)) {
					practitionerIdentifier =
							practitioner != null ? practitioner.getIdentifier() : UUID.randomUUID().toString();
					if (practitioner == null) {
						practitioner = new Practitioner();
						practitioner.setIdentifier(practitionerIdentifier);
					}
					practitioner.setActive(Boolean.TRUE);
					if (csvdata.containsKey(NAME_KEY)) {
						practitionerName = getValueFromMap(NAME_KEY, csvdata);
					} else {
						practitionerName = getValueFromMap(USER_NAME_KEY, csvdata);
					}

					practitioner.setName(practitionerName);

					userId = getValueFromMap(USER_ID_KEY, csvdata);
					practitioner.setUserId(userId);

					userName = getValueFromMap(USER_NAME_KEY, csvdata);
					practitioner.setUsername(userName);

					practitionerRole = convertToPractitionerRole(practitionerIdentifier, csvdata);
					if (organizationIdentifier != null) {
						code = practitionerRole.getCode() != null ? practitionerRole.getCode().getText() : "Health Worker";
						practitionerRole.setOrganizationIdentifier(organizationIdentifier);
						practitionerService.addOrUpdatePractitioner(practitioner);
						practitionerRoleService
								.assignPractitionerRole(organizationId, practitionerIdentifier, code, practitionerRole);
						rowsProcessed++;
					}
				} else if (organizationIdentifier == null) {
					failedRecordSummaries.add(getFailedRecordSummaryObject(rowCount,
							"Failed to get organization against the given organization Id"));
				} else {
					failedRecordSummaries.add(getFailedRecordSummaryObject(rowCount,
							"Validation failed, provided organization name mismatches with the system"));
				}
			}
			catch (Exception e) {
				logger.error(
						"Exception occurred while persisting practitioner and pratitioner role data : " + e.getMessage() , e);
				failedRecordSummaries
						.add(getFailedRecordSummaryObject(rowCount, "Unknown error occurred"));
			}
		}
		csvBulkImportDataSummary.setNumberOfRowsProcessed(rowsProcessed);
		csvBulkImportDataSummary.setFailedRecordSummaryList(failedRecordSummaries);
		return csvBulkImportDataSummary;
	}

	private boolean validateLocationData(Map<String, String> csvdata) {
		String locationIdFromCSV;
		String locationNameFromCSV;
		PhysicalLocation physicalLocation;

		locationNameFromCSV = getValueFromMap(LOCATION_NAME_KEY, csvdata);

		if (csvdata.containsKey(LOCATION_ID_KEY)) {
			locationIdFromCSV = getValueFromMap(LOCATION_ID_KEY, csvdata);
			physicalLocation = physicalLocationService.getLocation(locationIdFromCSV, false);
			if (locationNameFromCSV != null && physicalLocation != null && physicalLocation.getProperties() != null
					&& physicalLocation.getProperties()
					.getName().equals(locationNameFromCSV)) {
				return true;
			}
		}
		return false;
	}

	private boolean validateOrganizationFields(Map<String, String> csvdata) {
		String organizationIdFromCsv;
		Long organizationId = 0l;
		String organizationNameFromCSV = "";
		Organization organization;

		organizationNameFromCSV = getValueFromMap(ORGANIZATION_NAME_KEY, csvdata);

		if (csvdata.containsKey(ORGANIZATION_ID_KEY)) {
			organizationIdFromCsv = csvdata.get(ORGANIZATION_ID_KEY);
			organizationId = Long.valueOf(organizationIdFromCsv);
			organization = organizationService.getOrganization(organizationId);
			if (organization != null && organization.getName().equals(organizationNameFromCSV)) {
				return true;
			}
		}
		return false;
	}

	private String getValueFromMap(String key, Map<String, String> map) {
		return map.get(key);
	}

	private String getOrganizationIdentifier(Long organizationId) {
		Organization organization = organizationService.getOrganization(organizationId);
		return organization != null ? organization.getIdentifier() : null;
	}

	private PractitionerRole convertToPractitionerRole(String practitionerIdentifier, Map<String, String> csvdata) {
		PractitionerRole practitionerRole = new PractitionerRole();
		String practitionerRoleIdentifier;
		PractitionerRoleCode practitionerRoleCode;
		String code;
		practitionerRole.setActive(Boolean.TRUE);
		practitionerRoleIdentifier = UUID.randomUUID().toString();
		practitionerRole.setIdentifier(practitionerRoleIdentifier);
		practitionerRole.setPractitionerIdentifier(practitionerIdentifier);
		practitionerRoleCode = new PractitionerRoleCode();
		if (csvdata.containsKey(ROLE_KEY)) {
			code = csvdata.get(ROLE_KEY);
		} else {
			code = "Health Worker";
		}

		practitionerRoleCode.setText(code);
		practitionerRole.setCode(practitionerRoleCode);
		return practitionerRole;
	}

	private FailedRecordSummary getFailedRecordSummaryObject(int rowNumber, String reasonOfFailure) {
		FailedRecordSummary failedRecordSummary = new FailedRecordSummary();
		List<String> failureReasons = new ArrayList<>();
		failedRecordSummary.setRowNumber(rowNumber);
		failureReasons.add(reasonOfFailure);
		failedRecordSummary.setReasonOfFailure(failureReasons);
		return failedRecordSummary;
	}
}
