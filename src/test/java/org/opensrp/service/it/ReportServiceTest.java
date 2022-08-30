package org.opensrp.service.it;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.opensrp.util.SampleFullDomainObject.BASE_ENTITY_ID;
import static org.opensrp.util.SampleFullDomainObject.DIFFERENT_BASE_ENTITY_ID;
import static org.opensrp.util.SampleFullDomainObject.FORM_SUBMISSION_ID;
import static org.opensrp.util.SampleFullDomainObject.IDENTIFIER_TYPE;
import static org.opensrp.util.SampleFullDomainObject.IDENTIFIER_VALUE;
import static org.opensrp.util.SampleFullDomainObject.getReport;
import static org.opensrp.util.SampleFullDomainObject.identifier;
import static org.utils.AssertionUtil.assertNewObjectCreation;
import static org.utils.AssertionUtil.assertObjectUpdate;
import static org.utils.AssertionUtil.assertTwoListAreSameIgnoringOrder;
import static org.utils.DbAccessUtils.addObjectToRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.BaseIntegrationTest;
import org.opensrp.domain.Report;
import org.opensrp.repository.postgres.ReportsRepositoryImpl;
import org.opensrp.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportServiceTest extends BaseIntegrationTest {

    @Autowired
    private ReportsRepositoryImpl allReports;

    @Autowired
    private ReportService reportService;

    @Before
    public void setUp() {
        allReports.removeAll();
    }

    @After
    public void cleanUp() {
        allReports.removeAll();
    }

    @Test
    public void shouldFindById() {
        addObjectToRepository(Collections.singletonList(getReport()), allReports);
        Report expectedReport = allReports.getAll().get(0);

        Report actualReport = reportService.getById(expectedReport.getId());

        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void shouldFindByBaseEntityIdAndFormSubmissionId() {
        Report expectedReport = getReport();
        Report invalidReport = getReport();
        invalidReport.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        addObjectToRepository(asList(expectedReport, invalidReport), allReports);

        Report actualReport = reportService.getByBaseEntityAndFormSubmissionId(BASE_ENTITY_ID, FORM_SUBMISSION_ID);

        assertEquals(expectedReport, actualReport);
    }

    @Test
    public void shouldFindByBaseEntityId() {
        Report report = getReport();
        Report report1 = getReport();
        List<Report> expectedReports = asList(report, report1);
        Report invalidReport = getReport();
        invalidReport.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);
        addObjectToRepository(asList(report, report1, invalidReport), allReports);

        List<Report> actualReports = reportService.findByBaseEntityId(BASE_ENTITY_ID);

        assertTwoListAreSameIgnoringOrder(expectedReports, actualReports);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfMultipleFound() {
        Report report = getReport();
        Report report1 = getReport();
        addObjectToRepository(asList(report, report1), allReports);

        reportService.getByBaseEntityAndFormSubmissionId(BASE_ENTITY_ID, FORM_SUBMISSION_ID);
    }

    @Test
    public void shouldReturnNullIfNoneFound() {
        assertNull(reportService.getByBaseEntityAndFormSubmissionId(BASE_ENTITY_ID, FORM_SUBMISSION_ID));
    }

    @Test
    public void shouldFindByUniqueIdentifier() {
        Report expectedReport = getReport();
        Report invalidReport = getReport();
        Map<String, String> differentIdentifiers = new HashMap<>(identifier);
        differentIdentifiers.put(IDENTIFIER_TYPE, DIFFERENT_BASE_ENTITY_ID);
        invalidReport.setIdentifiers(differentIdentifiers);
        addObjectToRepository(asList(expectedReport, invalidReport), allReports);

        Report actualReport = reportService.find(IDENTIFIER_VALUE);

        assertEquals(expectedReport, actualReport);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMultipleFoundWhileFindByUniqueId() {
        Report report = getReport();
        Report report1 = getReport();
        addObjectToRepository(asList(report, report1), allReports);

        reportService.find(IDENTIFIER_VALUE);
    }

    @Test
    public void shouldReturnNullIfNoneFoundWhileFindByIdentifier() {
        assertNull(reportService.find(IDENTIFIER_VALUE));
    }

    @Test
    public void shouldFindByReportObject() {
        Report expectedReport = getReport();
        Report invalidReport = getReport();
        Map<String, String> differentIdentifiers = new HashMap<>(identifier);
        differentIdentifiers.put(IDENTIFIER_TYPE, DIFFERENT_BASE_ENTITY_ID);
        invalidReport.setIdentifiers(differentIdentifiers);
        addObjectToRepository(asList(expectedReport, invalidReport), allReports);

        Report actualReport = reportService.find(expectedReport);

        assertEquals(expectedReport, actualReport);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfMultipleFoundWhileFindByReportObject() {
        Report report = getReport();
        Report report1 = getReport();
        addObjectToRepository(asList(report, report1), allReports);

        reportService.find(report);
    }

    @Test
    public void shouldReturnNullIfNoneFoundWhileFindByReportObject() {
        assertNull(reportService.find(getReport()));
    }

    @Test
    public void shouldFindByReportId() {
        addObjectToRepository(Collections.singletonList(getReport()), allReports);
        Report expectedReport = allReports.getAll().get(0);

        Report actualReport = reportService.findById(expectedReport.getId());

        assertEquals(expectedReport, actualReport);
    }

    @Test(expected = Exception.class)
    public void shouldThrowErrorIfMultipleRecordFoundWhileFindById() {
        addObjectToRepository(Collections.singletonList(getReport()), allReports);
        Report expectedReport = allReports.getAll().get(0);
        addObjectToRepository(Collections.singletonList(expectedReport), allReports);

        reportService.findById(expectedReport.getId());

    }

    @Test
    public void shouldReturnNullIfNoneFoundUsingDocumentId() {
        addObjectToRepository(Collections.singletonList(getReport()), allReports);
        assertNull(reportService.findById(DIFFERENT_BASE_ENTITY_ID));
    }

    @Test
    public void shouldReturnNullForNullOrEmptyId() {
        assertNull(reportService.findById(null));
        assertNull(reportService.findById(""));
    }

    @Test
    public void shouldGetAllReport() {
        Report report = getReport();
        Report report1 = getReport();
        List<Report> expectedReports = asList(report, report1);
        addObjectToRepository(expectedReports, allReports);

        List<Report> actualReports = reportService.getAll();

        assertTwoListAreSameIgnoringOrder(expectedReports, actualReports);
    }

    @Test
    public void shouldFindByServerVersion() {
        Report report = getReport();
        Report report1 = getReport();
        List<Report> expectedReports = asList(report, report1);
        addObjectToRepository(expectedReports, allReports);

        List<Report> actualReports = reportService.findByServerVersion(0);

        assertTwoListAreSameIgnoringOrder(expectedReports, actualReports);
    }

    @Test
    public void shouldFindByIdentifier() {
        Report report1 = getReport();
        Report report = getReport();
        Report invalidReport = getReport();
        Map<String, String> differentIdentifiers = new HashMap<>(identifier);
        differentIdentifiers.put(IDENTIFIER_TYPE, DIFFERENT_BASE_ENTITY_ID);
        invalidReport.setIdentifiers(differentIdentifiers);
        List<Report> expectedReports = asList(report1, report);
        addObjectToRepository(asList(report1, report, invalidReport), allReports);

        List<Report> actualReports = reportService.findAllByIdentifier(IDENTIFIER_VALUE);

        assertTwoListAreSameIgnoringOrder(expectedReports, actualReports);
    }

    @Test
    public void shouldAddReport() {
        Report expectedReport = getReport();

        Report actualReport = reportService.addReport(expectedReport);
        List<Report> dbReports = allReports.getAll();

        assertEquals(1, dbReports.size());
        assertEquals(expectedReport, actualReport);
        assertNewObjectCreation(expectedReport, dbReports.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhileAddingExistingReport() {
        Report report = getReport();

        addObjectToRepository(Collections.singletonList(report), allReports);

        reportService.addReport(report);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfExistingReportFoundByBaseEntityIdAndFormSubmissionId() {
        Report report = getReport();
        addObjectToRepository(Collections.singletonList(report), allReports);
        report.setIdentifiers(null);
        reportService.addReport(report);
    }

    @Test
    public void shouldUpdateExistingReport() {
        addObjectToRepository(Collections.singletonList(getReport()), allReports);
        Report updatedReport = allReports.getAll().get(0);
        updatedReport.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);

        reportService.updateReport(updatedReport);

        List<Report> actualReports = allReports.getAll();

        assertEquals(1, actualReports.size());
        assertObjectUpdate(updatedReport, actualReports.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNewObjectWhileUpdate() {
        reportService.updateReport(getReport());
    }

    @Test
    public void shouldAddIfNewInAddOrUpdate() {
        Report expectedReport = getReport();

        Report actualReport = reportService.addorUpdateReport(expectedReport);
        List<Report> dbReports = allReports.getAll();

        assertEquals(1, dbReports.size());
        assertEquals(expectedReport, actualReport);
        assertNewObjectCreation(expectedReport, dbReports.get(0));
    }

    @Test
    public void shouldUpdateReportIfExistInAddOrUpdate() {
        addObjectToRepository(Collections.singletonList(getReport()), allReports);
        Report updatedReport = allReports.getAll().get(0);
        updatedReport.setBaseEntityId(DIFFERENT_BASE_ENTITY_ID);

        Report actualReport = reportService.addorUpdateReport(updatedReport);
        List<Report> dbReports = allReports.getAll();

        assertEquals(1, dbReports.size());
        assertEquals(updatedReport, actualReport);

        assertNotNull(dbReports.get(0).getServerVersion());
        updatedReport.setServerVersion(0l);
        dbReports.get(0).setServerVersion(0l);

        assertObjectUpdate(updatedReport, dbReports.get(0));
    }
}
