/**
 *
 */
package org.opensrp.repository.postgres;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.junit.Test;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.Code;
import org.opensrp.domain.Organization;
import org.opensrp.repository.OrganizationRepository;
import org.opensrp.repository.PlanRepository;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.repository.PractitionerRoleRepository;
import org.opensrp.search.AssignedLocationAndPlanSearchBean;
import org.opensrp.search.OrganizationSearchBean;
import org.opensrp.search.OrganizationSearchBean.FieldName;
import org.opensrp.search.OrganizationSearchBean.OrderByType;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Practitioner;
import org.smartregister.domain.PractitionerRole;
import org.smartregister.domain.PractitionerRoleCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

/**
 * @author Samuel Githengi created on 09/16/19
 */
public class OrganizationRepositoryTest extends BaseRepositoryTest {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private PractitionerRoleRepository practitionerRoleRepository;

    @Autowired
    private PlanRepository planRepository;

    private static PractitionerRole initTestPractitionerRole() {
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.setIdentifier("pr3-identifier");
        practitionerRole.setActive(true);
        practitionerRole.setOrganizationIdentifier("fcc19470-d599-11e9-bb65-2a2ae2dbcce4");
        practitionerRole.setPractitionerIdentifier("practitoner-3-identifier");
        PractitionerRoleCode code = new PractitionerRoleCode();
        code.setText("pr3Code");
        practitionerRole.setCode(code);
        return practitionerRole;
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<String>();
        scripts.add("organization.sql");
        scripts.add("plan.sql");
        return scripts;
    }

    @Test
    public void testGetOrganization() throws JsonGenerationException, JsonMappingException, IOException {
        Organization organization = organizationRepository.get("fcc19470-d599-11e9-bb65-2a2ae2dbcce4");
        assertNotNull(organization);
        assertEquals(1, organization.getId(), 0);
        assertEquals("fcc19470-d599-11e9-bb65-2a2ae2dbcce4", organization.getIdentifier());
        assertEquals("The Luang", organization.getName());
        assertEquals(1, organization.getType().getCoding().size());
        Code code = organization.getType().getCoding().get(0);
        assertEquals("http://terminology.hl7.org/CodeSystem/organization-type", code.getSystem());
        assertEquals("team", code.getCode());
        assertEquals("Team", code.getDisplay());
        assertNull(organization.getPartOf());

        assertNull(organizationRepository.get("121sd"));

    }

    @Test
    public void testAddOrganization() {
        String identifier = UUID.randomUUID().toString();
        assertNull(organizationRepository.get(identifier));

        Organization organization = new Organization();
        organization.setIdentifier(identifier);
        organization.setName("Ateam");
        organization.setPartOf(1l);
        organization.setActive(true);
        organizationRepository.add(organization);

        Organization savedOrganization = organizationRepository.get(identifier);

        assertNotNull(savedOrganization);
        assertNotNull(savedOrganization.getId());
        assertEquals(identifier, savedOrganization.getIdentifier());
        assertEquals("Ateam", savedOrganization.getName());
        assertNull(savedOrganization.getType());
        assertEquals(1, savedOrganization.getPartOf(), 0);
        assertNotNull(savedOrganization.getDateEdited());
        assertNotNull(savedOrganization.getDateCreated());
        assertEquals(3, savedOrganization.getServerVersion());

    }

    @Test
    public void testUpdateOrganization() {

        Organization organization = organizationRepository.getByPrimaryKey(3l);
        organization.setName("Ateam");
        organization.setPartOf(1l);
        organization.setActive(false);
        organizationRepository.update(organization);

        Organization updatedOrganization = organizationRepository.getByPrimaryKey(3l);

        assertNotNull(updatedOrganization);
        assertEquals(3l, updatedOrganization.getId(), 0);
        assertEquals("4c506c98-d3a9-11e9-bb65-2a2ae2dbcce4", updatedOrganization.getIdentifier());
        assertEquals("Ateam", updatedOrganization.getName());
        assertNull(updatedOrganization.getType());
        assertEquals(1l, updatedOrganization.getPartOf(), 0);
        assertNotNull(updatedOrganization.getDateEdited());
        assertNotNull(updatedOrganization.getDateCreated());
        assertEquals(3, updatedOrganization.getServerVersion());

    }

    @Test
    public void testGetAll() {
        assertEquals(3, organizationRepository.getAll().size());

        Organization organization = new Organization();
        organization.setIdentifier(UUID.randomUUID().toString());
        organization.setName("Ateam");
        organization.setPartOf(1l);
        organization.setActive(true);
        organizationRepository.add(organization);

        assertEquals(4, organizationRepository.getAll().size());

    }

    @Test
    public void testSafeRemove() {

        Organization organization = organizationRepository.getByPrimaryKey(2l);
        organizationRepository.safeRemove(organization);

        assertNull(organizationRepository.getByPrimaryKey(2l));

        assertEquals(2, organizationRepository.getAll().size());

    }

    @Test
    public void testSelectOrganizationsEncompassLocations() throws ParseException {
        String jurisdiction = "304cbcd4-0850-404a-a8b1-486b02f7b84d";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = format.parse("2019-12-10 20:20:20");
        List<Organization> organizations = organizationRepository.selectOrganizationsEncompassLocations(jurisdiction, date);
        assertEquals(organizations.size(), 2);
    }

    @Test
    public void testAssignLocation() {
        String identifier = UUID.randomUUID().toString();
        Organization organization = new Organization();
        organization.setIdentifier(identifier);
        organization.setName("ATeam");
        organizationRepository.add(organization);

        organization = organizationRepository.get(identifier);
        Calendar calendar = Calendar.getInstance();
        Date fromDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 2);
        Date toDate = calendar.getTime();
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, fromDate, toDate);

        AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(organization.getId())
                .returnFutureAssignments(true).build();
        List<AssignedLocations> assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(1, assignedLocations.size());
        assertEquals("304cbcd4-0850-404a-a8b1-486b02f7b84d", assignedLocations.get(0).getJurisdictionId());
        assertEquals("9d1403a5-756d-517b-91d6-5b19059a69f0", assignedLocations.get(0).getPlanId());
        assertEquals(dateFormat.format(fromDate), dateFormat.format(assignedLocations.get(0).getFromDate()));
        assertEquals(dateFormat.format(toDate), dateFormat.format(assignedLocations.get(0).getToDate()));

        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, fromDate, null);

        assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(organization.getId())
                .returnFutureAssignments(true).build();
        assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(1, assignedLocations.size());
        assertNull(assignedLocations.get(0).getToDate());

    }

    @Test(expected = DataIntegrityViolationException.class)
    public void testAssignLocationAndPlanShouldErrorWithOverlappingData() {
        String identifier = UUID.randomUUID().toString();
        Organization organization = new Organization();
        organization.setIdentifier(identifier);
        organization.setName("ATeam");
        organizationRepository.add(organization);

        organization = organizationRepository.get(identifier);
        Calendar calendar = Calendar.getInstance();
        Date fromDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 2);
        Date toDate = calendar.getTime();
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, fromDate, toDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.MONTH, 2);
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, calendar2.getTime(), null);

    }

    @Test
    public void testAssignLocationAndPlanExpired() {
        String identifier = UUID.randomUUID().toString();
        Organization organization = new Organization();
        organization.setIdentifier(identifier);
        organization.setName("ATeam");
        organizationRepository.add(organization);

        organization = organizationRepository.get(identifier);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date fromDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 2);
        Date toDate = calendar.getTime();
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, fromDate, toDate);

        AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(organization.getId())
                .returnFutureAssignments(true).build();
        List<AssignedLocations> assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(0, assignedLocations.size());
    }

    @Test
    public void testAssignLocationAndPlanExpiringTodayShouldNotReturned() {
        String identifier = UUID.randomUUID().toString();
        Organization organization = new Organization();
        organization.setIdentifier(identifier);
        organization.setName("ATeam");
        organizationRepository.add(organization);

        organization = organizationRepository.get(identifier);
        Calendar calendar = Calendar.getInstance();
        Date fromDate = calendar.getTime();
        Date toDate = calendar.getTime();
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, fromDate, toDate);

        AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(organization.getId())
                .returnFutureAssignments(true).build();
        List<AssignedLocations> assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(0, assignedLocations.size());
    }

    @Test
    public void testAssignLocationAndPlanUpdatesExistingIfFromDateMatches() {
        String identifier = UUID.randomUUID().toString();
        Organization organization = new Organization();
        organization.setIdentifier(identifier);
        organization.setName("ATeam");
        organizationRepository.add(organization);

        organization = organizationRepository.get(identifier);
        Calendar calendar = Calendar.getInstance();
        Date fromDate = calendar.getTime();
        Date toDate = calendar.getTime();
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, fromDate, toDate);
        AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(organization.getId())
                .returnFutureAssignments(true).build();
        List<AssignedLocations> assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(0, assignedLocations.size());

        calendar.add(Calendar.YEAR, 1);
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, fromDate, calendar.getTime());
        assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(organization.getId())
                .returnFutureAssignments(true).build();
        assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(1, assignedLocations.size());
    }

    @Test
    public void testAssignLocationAndPlanAcceptsMultipleAssigmentIfTheyDontOverlap() {
        String identifier = UUID.randomUUID().toString();
        Organization organization = new Organization();
        organization.setIdentifier(identifier);
        organization.setName("ATeam");
        organizationRepository.add(organization);

        organization = organizationRepository.get(identifier);
        Calendar calendar = Calendar.getInstance();
        Date fromDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        Date toDate = calendar.getTime();
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, fromDate, toDate);
        AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(organization.getId())
                .returnFutureAssignments(true).build();
        List<AssignedLocations> assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(1, assignedLocations.size());

        calendar.add(Calendar.DATE, 1);
        fromDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 1);
        toDate = calendar.getTime();
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                "7f2ae03f-9569-5535-918c-9d976b3ae5f8", 11l, fromDate, toDate);
        assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(organization.getId())
                .returnFutureAssignments(true).build();
        assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(2, assignedLocations.size());
    }

    @Test
    public void testFindAssignedLocations() {
        AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(1l)
                .returnFutureAssignments(true).build();
        List<AssignedLocations> assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(2, assignedLocations.size());

        assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .organizationId(2l)
                .returnFutureAssignments(true).build();
        assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(1, assignedLocations.size());
        assertEquals("304cbcd4-0850-404a-a8b1-486b02f7b84d", assignedLocations.get(0).getJurisdictionId());
        assertEquals("7f2ae03f-9569-5535-918c-9d976b3ae5f8", assignedLocations.get(0).getPlanId());
        assertEquals("2019-09-10", dateFormat.format(assignedLocations.get(0).getFromDate()));
        assertEquals("2022-09-10", dateFormat.format(assignedLocations.get(0).getToDate()));

    }

    @Test
    public void testFindAssignedLocationsMutipleIds() {
        List<AssignedLocations> assignedLocations = organizationRepository
                .findAssignedLocations(Collections.singletonList(1l), true);
        assertEquals(2, assignedLocations.size());

        assignedLocations = organizationRepository.findAssignedLocations(Collections.singletonList(2l), true);
        assertEquals(1, assignedLocations.size());
        assertEquals("304cbcd4-0850-404a-a8b1-486b02f7b84d", assignedLocations.get(0).getJurisdictionId());
        assertEquals("7f2ae03f-9569-5535-918c-9d976b3ae5f8", assignedLocations.get(0).getPlanId());
        assertEquals("2019-09-10", dateFormat.format(assignedLocations.get(0).getFromDate()));
        assertEquals("2022-09-10", dateFormat.format(assignedLocations.get(0).getToDate()));

        assignedLocations = organizationRepository.findAssignedLocations(Arrays.asList(1l, 2l), true);
        assertEquals(3, assignedLocations.size());
    }

    @Test
    public void testFindOrganizationsByNameAndLcations() {
        practitionerRepository.add(initTestPractitioner());
        practitionerRoleRepository.add(initTestPractitionerRole());
        OrganizationSearchBean organizationSearchBean = new OrganizationSearchBean();
        organizationSearchBean.setPageNumber(0);
        organizationSearchBean.setPageSize(10);
        organizationSearchBean.setName("The Luang");
        List<Integer> locations = new ArrayList<>();
        locations.add(2243);
        locations.add(1);
        organizationSearchBean.setLocations(locations);
        List<Organization> organizations = organizationRepository.findSearchOrganizations(organizationSearchBean);
        assertEquals(1, organizations.size());
    }

    @Test
    public void testFindOrganizationsWithoutSearchParam() {
        practitionerRepository.add(initTestPractitioner());
        practitionerRoleRepository.add(initTestPractitionerRole());
        OrganizationSearchBean organizationSearchBean = new OrganizationSearchBean();
        organizationSearchBean.setPageNumber(0);
        organizationSearchBean.setPageSize(10);
        organizationSearchBean.setOrderByFieldName(FieldName.valueOf("name"));
        organizationSearchBean.setOrderByType(OrderByType.valueOf("ASC"));
        List<Organization> organizations = organizationRepository.findSearchOrganizations(organizationSearchBean);
        assertEquals(3, organizations.size());
    }

    @Test
    public void testFindEmptyOrganizationsByNameAndLcations() {
        practitionerRepository.add(initTestPractitioner());
        practitionerRoleRepository.add(initTestPractitionerRole());
        OrganizationSearchBean organizationSearchBean = new OrganizationSearchBean();
        organizationSearchBean.setPageNumber(0);
        organizationSearchBean.setPageSize(10);
        organizationSearchBean.setName("The Luang Bell");
        List<Integer> locations = new ArrayList<>();
        locations.add(22435);
        locations.add(1);
        organizationSearchBean.setLocations(locations);
        List<Organization> organizations = organizationRepository.findSearchOrganizations(organizationSearchBean);
        assertTrue(organizations.isEmpty());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindOrganizationsByOrderByFieldNameNotExists() {
        OrganizationSearchBean organizationSearchBean = new OrganizationSearchBean();
        organizationSearchBean.setPageNumber(0);
        organizationSearchBean.setPageSize(10);
        organizationSearchBean.setOrderByFieldName(FieldName.valueOf("names"));
        organizationSearchBean.setOrderByType(OrderByType.valueOf("ASC"));
        organizationRepository.findSearchOrganizations(organizationSearchBean);

    }

    @Test
    public void testFindTotalSearchOrganizationsByNameAndLcations() {
        practitionerRepository.add(initTestPractitioner());
        practitionerRoleRepository.add(initTestPractitionerRole());
        OrganizationSearchBean organizationSearchBean = new OrganizationSearchBean();
        organizationSearchBean.setPageNumber(0);
        organizationSearchBean.setPageSize(10);
        organizationSearchBean.setName("The Luang");
        List<Integer> locations = new ArrayList<>();
        locations.add(2243);
        locations.add(1);
        organizationSearchBean.setLocations(locations);
        int totalCount = organizationRepository.findOrganizationCount(organizationSearchBean);
        assertEquals(1, totalCount);

    }

    @Test
    public void testFindTotalSearchOrganizationsWithoutSearchParam() {
        practitionerRepository.add(initTestPractitioner());
        practitionerRoleRepository.add(initTestPractitionerRole());
        OrganizationSearchBean organizationSearchBean = new OrganizationSearchBean();

        int totalCount = organizationRepository.findOrganizationCount(organizationSearchBean);
        assertEquals(3, totalCount);

    }

    @Test
    public void testGetAllOrganizations() {
        OrganizationSearchBean organizationSearchBean = new OrganizationSearchBean();
        organizationSearchBean.setOrderByType(OrderByType.ASC);
        organizationSearchBean.setOrderByFieldName(FieldName.id);
        organizationSearchBean.setServerVersion(1l);
        assertEquals(3, organizationRepository.getAllOrganizations(organizationSearchBean).size());

        Organization organization = new Organization();
        organization.setIdentifier(UUID.randomUUID().toString());
        organization.setName("Ateam");
        organization.setPartOf(1l);
        organization.setActive(true);
        organizationRepository.add(organization);

        List<Organization> organizations = organizationRepository.getAllOrganizations(organizationSearchBean);
        assertEquals(4, organizations.size());
        assertEquals(new Long(1l), organizations.get(0).getId());
        assertEquals(new Long(4l), organizations.get(3).getId());

    }

    @Test
    public void testUnassignLocationAndPlan() {
        String identifier = UUID.randomUUID().toString();
        Organization organization = new Organization();
        organization.setIdentifier(identifier);
        organization.setName("ATeam");
        organizationRepository.add(organization);

        String planIdentifier = UUID.randomUUID().toString();
        PlanDefinition planDefinition = new PlanDefinition();
        planDefinition.setIdentifier(planIdentifier);
        planDefinition.setName("Plan A");
        planRepository.add(planDefinition);

        organization = organizationRepository.get(identifier);
        Long planId = planRepository.retrievePrimaryKey(planIdentifier);
        Calendar calendar = Calendar.getInstance();
        Date fromDate = calendar.getTime();
        calendar.add(Calendar.YEAR, 2);
        Date toDate = calendar.getTime();
        organizationRepository.assignLocationAndPlan(organization.getId(), "04cbcd4-0850-404a-a8b1-486b02f7b84d", 2243l,
                planIdentifier, planId, fromDate, toDate);

        AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .planId(planId)
                .returnFutureAssignments(true).build();
        List<AssignedLocations> assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(1, assignedLocations.size());
        assertEquals("304cbcd4-0850-404a-a8b1-486b02f7b84d", assignedLocations.get(0).getJurisdictionId());
        assertEquals(planIdentifier, assignedLocations.get(0).getPlanId());
        assertEquals(dateFormat.format(fromDate), dateFormat.format(assignedLocations.get(0).getFromDate()));
        assertEquals(dateFormat.format(toDate), dateFormat.format(assignedLocations.get(0).getToDate()));

        organizationRepository.unassignLocationAndPlan(planId);
        assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .planId(planId)
                .build();
        assignedLocations = organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);
        assertEquals(0, assignedLocations.size()); // verified - removes all the assigned teams
    }

    @Test
    public void testCountAllOrganizations() {
        Organization organization1 = new Organization();
        Organization organization2 = new Organization();
        organizationRepository.add(organization1);
        organizationRepository.add(organization2);
        assertEquals(3, organizationRepository.countAllOrganizations());
    }

    @Test
    public void testGetOrganizationsByIds() {
        List<Long> organizationIds = new ArrayList<>();
        organizationIds.add(1l);
        organizationIds.add(2l);
        organizationIds.add(3l);
        List<Organization> organizations = organizationRepository.getOrganizationsByIds(organizationIds);
        assertNotNull(organizations);
        assertEquals(3, organizations.size());
        assertEquals(new Long(1), organizations.get(0).getId());
        assertEquals("fcc19470-d599-11e9-bb65-2a2ae2dbcce4", organizations.get(0).getIdentifier());
        assertEquals("The Luang", organizations.get(0).getName());
        assertEquals(1, organizations.get(0).getType().getCoding().size());
        Code code = organizations.get(0).getType().getCoding().get(0);
        assertEquals("http://terminology.hl7.org/CodeSystem/organization-type", code.getSystem());
        assertEquals("team", code.getCode());
        assertEquals("Team", code.getDisplay());
        assertNull(organizations.get(0).getPartOf());

        assertEquals(new Long(3), organizations.get(1).getId());
        assertEquals("4c506c98-d3a9-11e9-bb65-2a2ae2dbcce4", organizations.get(1).getIdentifier());

        assertEquals(new Long(2), organizations.get(2).getId());
        assertEquals("d23f7350-d406-11e9-bb65-2a2ae2dbcce4", organizations.get(2).getIdentifier());
    }

    private Practitioner initTestPractitioner() {
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier("practitoner-3-identifier");
        practitioner.setActive(false);
        practitioner.setName("Third Practitioner");
        practitioner.setUsername("Practioner3");
        practitioner.setUserId("user3");
        return practitioner;
    }
}
