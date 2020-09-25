package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.opensrp.domain.Organization;
import org.opensrp.domain.Practitioner;
import org.opensrp.domain.PractitionerRole;
import org.opensrp.dto.CsvBulkImportDataSummary;
import org.smartregister.domain.LocationProperty;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class ImportBulkDataServiceTest {

	@Mock
	private OrganizationService organizationService;

	@Mock
	private PractitionerService practitionerService;

	@Mock
	private PractitionerRoleService practitionerRoleService;

	@Mock
	private PhysicalLocationService physicalLocationService;

	@InjectMocks
	private ImportBulkDataService importBulkDataService;

	private static final String LOCATION_ID_KEY = "Location Id";

	private static final String LOCATION_NAME_KEY = "Location Name";

	private static final String ORGANIZATION_NAME_KEY = "Organization Name";

	private static final String PLAN_ID_KEY = "Plan Id";

	private static final String ORGANIZATION_ID_KEY = "Organization Id";

	private static final String USER_ID_KEY = "User Id";

	private static final String USER_NAME_KEY = "User Name";

	private static final String NAME_KEY = "Name";

	private static final String ROLE_KEY = "Role";

	private static final String LOCATION_VALIDATION_FAILED = "Validation failed, provided location name mismatches with the system";

	private static final String ORGANIZATION_NOT_FOUND = "Failed to get organization against the given organization Id";

	private static final String ORGANIZATION_NAME_MISMATCH_ERROR = "Validation failed, provided organization name mismatches with the system";

	@Before
	public void setUp() {
		initMocks(this);
		//		importBulkDataService = new ImportBulkDataService();
	}

	//	@Override
	//	protected Set<String> getDatabaseScripts() {
	//		Set<String> scripts = new HashSet<String>();
	//		scripts.add("identifier_source.sql");
	//		return scripts;
	//	}

	@Test
	public void testConvertandPersistOrganizationdata() {
		List<Map<String, String>> csvOrganizations = new ArrayList<>();
		Map<String, String> csvdata = new HashMap<>();
		csvdata.put(ORGANIZATION_NAME_KEY, "test-org-A");
		csvdata.put(LOCATION_ID_KEY, "1234");
		csvdata.put(LOCATION_NAME_KEY, "test-loc-A");
		csvdata.put(PLAN_ID_KEY, "test-plan-A");
		csvOrganizations.add(csvdata);

		PhysicalLocation physicalLocation = new PhysicalLocation();
		LocationProperty locationProperty = new LocationProperty();
		locationProperty.setName("test-loc-A");
		physicalLocation.setProperties(locationProperty);

		when(physicalLocationService.getLocation(anyString(), anyBoolean())).thenReturn(physicalLocation);
		doNothing().when(organizationService).addOrganization(any(Organization.class));
		doNothing().when(organizationService).assignLocationAndPlan(anyString(), anyString(), anyString(), any(Date.class),
				nullable(Date.class));  //handles update case as well
		CsvBulkImportDataSummary csvBulkImportDataSummary = importBulkDataService
				.convertandPersistOrganizationdata(csvOrganizations);
		System.out.println(csvBulkImportDataSummary);
		assertEquals(new Integer(1), csvBulkImportDataSummary.getNumberOfCsvRows());
		assertEquals(new Integer(1), csvBulkImportDataSummary.getNumberOfRowsProcessed());
		assertEquals(0, csvBulkImportDataSummary.getFailedRecordSummaryList().size());
	}

	@Test
	public void testConvertandPersistOrganizationdataWithValidationFailed() {
		List<Map<String, String>> csvOrganizations = new ArrayList<>();
		Map<String, String> csvdata = new HashMap<>();
		csvdata.put(ORGANIZATION_NAME_KEY, "test-org-A");
		csvdata.put(LOCATION_ID_KEY, "1234");
		csvdata.put(LOCATION_NAME_KEY, "test-loc-A");
		csvdata.put(PLAN_ID_KEY, "test-plan-A");
		csvOrganizations.add(csvdata);

		PhysicalLocation physicalLocation = new PhysicalLocation();
		LocationProperty locationProperty = new LocationProperty();
		locationProperty.setName("test-loc-B");
		physicalLocation.setProperties(locationProperty);

		when(physicalLocationService.getLocation(anyString(), anyBoolean())).thenReturn(physicalLocation);
		CsvBulkImportDataSummary csvBulkImportDataSummary = importBulkDataService
				.convertandPersistOrganizationdata(csvOrganizations);

		assertEquals(new Integer(1), csvBulkImportDataSummary.getNumberOfCsvRows());
		assertEquals(new Integer(0), csvBulkImportDataSummary.getNumberOfRowsProcessed());
		assertEquals(1, csvBulkImportDataSummary.getFailedRecordSummaryList().size());
		assertEquals(new Integer(1), csvBulkImportDataSummary.getFailedRecordSummaryList().get(0).getRowNumber());
		assertEquals(LOCATION_VALIDATION_FAILED,
				csvBulkImportDataSummary.getFailedRecordSummaryList().get(0).getReasonOfFailure());
	}

	@Test
	public void testConvertandPersistPractitionerdata() {
		List<Map<String, String>> csvPractitioners = new ArrayList<>();
		Map<String, String> csvdata = new HashMap<>();
		csvdata.put(ORGANIZATION_ID_KEY, "1");
		csvdata.put(ORGANIZATION_NAME_KEY, "test-org-A");
		csvdata.put(USER_NAME_KEY, "John");
		csvdata.put(NAME_KEY, "Jack");
		csvdata.put(USER_ID_KEY, "test-123");
		csvdata.put(ROLE_KEY, "Health coach");
		csvPractitioners.add(csvdata);

		Practitioner practitioner = new Practitioner();
		practitioner.setIdentifier("a30f1e8b-ed9c-4e0c-a2fd-218c603fe5ec");

		Organization organization = new Organization();
		organization.setName("test-org-A");
		organization.setIdentifier("a30f1e8b-ed9c-4e0c-a2fd-218c603fe523");

		when(practitionerService.getPractionerByUsername(anyString())).thenReturn(practitioner);
		when(organizationService.getOrganization(anyLong())).thenReturn(organization);
		when(practitionerService.addOrUpdatePractitioner(any(Practitioner.class))).thenReturn(practitioner);
		doNothing().when(practitionerRoleService).assignPractitionerRole(anyLong(), anyString(), anyString(), any(
				PractitionerRole.class));

		CsvBulkImportDataSummary csvBulkImportDataSummary = importBulkDataService
				.convertandPersistPractitionerdata(csvPractitioners);

		assertEquals(new Integer(1), csvBulkImportDataSummary.getNumberOfCsvRows());
		assertEquals(new Integer(1), csvBulkImportDataSummary.getNumberOfRowsProcessed());
		assertEquals(0, csvBulkImportDataSummary.getFailedRecordSummaryList().size());

	}

	@Test
	public void testConvertandPersistPractitionerdataWithInvalidOrganizationId() {
		List<Map<String, String>> csvPractitioners = new ArrayList<>();
		Map<String, String> csvdata = new HashMap<>();
		csvdata.put(ORGANIZATION_ID_KEY, "1");
		csvdata.put(ORGANIZATION_NAME_KEY, "test-org-A");
		csvdata.put(USER_NAME_KEY, "John");
		csvdata.put(NAME_KEY, "Jack");
		csvdata.put(USER_ID_KEY, "test-123");
		csvdata.put(ROLE_KEY, "Health coach");
		csvPractitioners.add(csvdata);

		Practitioner practitioner = new Practitioner();
		practitioner.setIdentifier("a30f1e8b-ed9c-4e0c-a2fd-218c603fe5ec");
		//
		//		Organization organization = new Organization();
		//		organization.setName("test-org-A");
		//		organization.setIdentifier("a30f1e8b-ed9c-4e0c-a2fd-218c603fe523");

		when(practitionerService.getPractionerByUsername(anyString())).thenReturn(practitioner);
		when(organizationService.getOrganization(anyLong())).thenReturn(null);
		when(practitionerService.addOrUpdatePractitioner(any(Practitioner.class))).thenReturn(practitioner);
		doNothing().when(practitionerRoleService).assignPractitionerRole(anyLong(), anyString(), anyString(), any(
				PractitionerRole.class));

		CsvBulkImportDataSummary csvBulkImportDataSummary = importBulkDataService
				.convertandPersistPractitionerdata(csvPractitioners);

		assertEquals(new Integer(1), csvBulkImportDataSummary.getNumberOfCsvRows());
		assertEquals(new Integer(0), csvBulkImportDataSummary.getNumberOfRowsProcessed());
		assertEquals(1, csvBulkImportDataSummary.getFailedRecordSummaryList().size());
		assertEquals(new Integer(1), csvBulkImportDataSummary.getFailedRecordSummaryList().get(0).getRowNumber());
		assertEquals(ORGANIZATION_NOT_FOUND,
				csvBulkImportDataSummary.getFailedRecordSummaryList().get(0).getReasonOfFailure());
	}

	@Test
	public void testConvertandPersistPractitionerdataWithInvalidOrganizationName() {
		List<Map<String, String>> csvPractitioners = new ArrayList<>();
		Map<String, String> csvdata = new HashMap<>();
		csvdata.put(ORGANIZATION_ID_KEY, "1");
		csvdata.put(ORGANIZATION_NAME_KEY, "test-org-A");
		csvdata.put(USER_NAME_KEY, "John");
		csvdata.put(NAME_KEY, "Jack");
		csvdata.put(USER_ID_KEY, "test-123");
		csvdata.put(ROLE_KEY, "Health coach");
		csvPractitioners.add(csvdata);

		Practitioner practitioner = new Practitioner();
		practitioner.setIdentifier("a30f1e8b-ed9c-4e0c-a2fd-218c603fe5ec");

		Organization organization = new Organization();
		organization.setName("test-org-B");
		organization.setIdentifier("a30f1e8b-ed9c-4e0c-a2fd-218c603fe523");

		when(practitionerService.getPractionerByUsername(anyString())).thenReturn(practitioner);
		when(organizationService.getOrganization(anyLong())).thenReturn(organization);
		when(practitionerService.addOrUpdatePractitioner(any(Practitioner.class))).thenReturn(practitioner);
		doNothing().when(practitionerRoleService).assignPractitionerRole(anyLong(), anyString(), anyString(), any(
				PractitionerRole.class));

		CsvBulkImportDataSummary csvBulkImportDataSummary = importBulkDataService
				.convertandPersistPractitionerdata(csvPractitioners);

		assertEquals(new Integer(1), csvBulkImportDataSummary.getNumberOfCsvRows());
		assertEquals(new Integer(0), csvBulkImportDataSummary.getNumberOfRowsProcessed());
		assertEquals(1, csvBulkImportDataSummary.getFailedRecordSummaryList().size());
		assertEquals(new Integer(1), csvBulkImportDataSummary.getFailedRecordSummaryList().get(0).getRowNumber());
		assertEquals(ORGANIZATION_NAME_MISMATCH_ERROR,
				csvBulkImportDataSummary.getFailedRecordSummaryList().get(0).getReasonOfFailure());
	}

}
