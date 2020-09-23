package org.opensrp.service;

import org.opensrp.domain.*;
import org.opensrp.dto.CsvBulkImportDataSummary;
import org.opensrp.dto.FailedRecordSummary;
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
		Set<AssignedLocations> assignedLocations;
		AssignedLocations assignedLocation;

		String locationId = "";
		String planId = "";
		String locationName = "";
		String organizationName = "";
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

					assignedLocations = new HashSet<>();
					assignedLocation = new AssignedLocations();
					assignedLocation.setOrganizationId(organizationIdentifier);
					assignedLocation.setToDate(new Date());

					organizationName = getValueFromMap(ORGANIZATION_NAME_KEY, csvdata);
					organization.setName(organizationName);

					locationId = getValueFromMap(LOCATION_ID_KEY, csvdata);
					assignedLocation.setJurisdictionId(locationId);

					planId = getValueFromMap(PLAN_ID_KEY, csvdata);
					assignedLocation.setPlanId(planId);

					assignedLocations.add(assignedLocation);
					organization.setAssignedLocations(assignedLocations);

					organizationService.addOrganization(organization);
					organizationService
							.assignLocationAndPlan(organizationIdentifier, locationId, planId, new Date(),
									null);
					rowsProcessed++;
				} else {
					failedRecordSummary = new FailedRecordSummary();
					failedRecordSummary.setRowNumber(rowCount);
					failedRecordSummary
							.setReasonOfFailure("Validation failed, provided location name mismatches with the system");
					failedRecordSummaries.add(failedRecordSummary);
				}
			}
		}
		catch (Exception e) {
			failedRecordSummary = new FailedRecordSummary();
			failedRecordSummary.setRowNumber(rowCount);
			failedRecordSummary.setReasonOfFailure("Exception occurred due to : " + e.getMessage());
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
		FailedRecordSummary failedRecordSummary;

		Practitioner practitioner = new Practitioner();
		String practitionerIdentifier;
		String practitionerName;
		String userId;
		String userName;

		String practitionerRoleIdentifier;
		String organizationIdentifier;

		PractitionerRoleCode practitionerRoleCode;
		String code;

		String organizationIdFromCSV = "";
		Long organizationId;
		Organization organization;

		for (Map<String, String> csvdata : csvOrganizations) {
			try {
				rowCount++;
				if (validateOrganizationFields(csvdata)) {
					practitionerIdentifier = UUID.randomUUID().toString();
					practitioner.setName(practitionerIdentifier);
					practitioner.setActive(Boolean.TRUE);
					//					practitionerName = getValueFromMap(NAME_KEY, csvdata);
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

					practitioner.setIdentifier(practitionerIdentifier);

					PractitionerRole practitionerRole = new PractitionerRole();
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
					organizationIdFromCSV = getValueFromMap(ORGANIZATION_ID_KEY, csvdata);
					organizationId = Long.valueOf(organizationIdFromCSV);

					organization = organizationService.getOrganization(organizationId);
					organizationIdentifier = organization != null ? organization.getIdentifier() : null;
					if (organizationIdentifier != null) {
						practitionerRole.setOrganizationIdentifier(organizationIdentifier);
						practitionerService.addOrUpdatePractitioner(practitioner);
						practitionerRoleService.addOrUpdatePractitionerRole(practitionerRole);
						rowsProcessed++;
					} else {
						failedRecordSummary = new FailedRecordSummary();
						failedRecordSummary.setRowNumber(rowCount);
						failedRecordSummary
								.setReasonOfFailure("Failed to get organization against the given organization Id");
						failedRecordSummaries.add(failedRecordSummary);
					}
				} else {
					failedRecordSummary = new FailedRecordSummary();
					failedRecordSummary.setRowNumber(rowCount);
					failedRecordSummary
							.setReasonOfFailure("Validation failed, provided organization name mismatches with the system");
					failedRecordSummaries.add(failedRecordSummary);
				}
			}
			catch (Exception e) {
				failedRecordSummary = new FailedRecordSummary();
				failedRecordSummary.setRowNumber(rowCount);
				failedRecordSummary.setReasonOfFailure("Exception occurred due to : " + e.getMessage());
				failedRecordSummaries.add(failedRecordSummary);
			}
		}
		csvBulkImportDataSummary.setNumberOfRowsProcessed(rowsProcessed);
		csvBulkImportDataSummary.setFailedRecordSummaryList(failedRecordSummaries);
		return csvBulkImportDataSummary;
	}

	private Boolean validateLocationData(Map<String, String> csvdata) {
		String locationIdFromCSV;
		Long locationId = 0l;
		String locationNameFromCSV = "";
		PhysicalLocation physicalLocation;

		locationNameFromCSV = getValueFromMap(LOCATION_NAME_KEY, csvdata);

		if (csvdata.containsKey(LOCATION_ID_KEY)) {
			locationIdFromCSV = getValueFromMap(LOCATION_ID_KEY, csvdata);
			//			locationId = Long.valueOf(locationIdFromCSV);
			physicalLocation = physicalLocationService.getLocation(locationIdFromCSV, false);
			if (physicalLocation != null && physicalLocation.getProperties() != null && physicalLocation.getProperties()
					.getName().equals(locationNameFromCSV)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private Boolean validateOrganizationFields(Map<String, String> csvdata) {
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
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	private String getValueFromMap(String key, Map<String, String> map) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		return null;
	}
}
