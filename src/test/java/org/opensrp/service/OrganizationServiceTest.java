/**
 * 
 */
package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.Organization;
import org.smartregister.domain.PhysicalLocation;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.OrganizationRepository;
import org.opensrp.repository.PlanRepository;
import org.opensrp.search.OrganizationSearchBean;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Samuel Githengi created on 09/17/19
 */
@RunWith(PowerMockRunner.class)
public class OrganizationServiceTest {

	private OrganizationService organizationService;

	@Mock
	private OrganizationRepository organizationRepository;

	@Mock
	private PlanRepository planRepository;

	 @Mock
	private LocationRepository locationRepository;

	private Organization organization;

	private String identifier = UUID.randomUUID().toString();

	private ArgumentCaptor<Date> dateCaptor = ArgumentCaptor.forClass(Date.class);

	@Before
	public void setUp() {
		organizationService = new OrganizationService(organizationRepository, locationRepository, planRepository);
		organization = new Organization();
		organization.setIdentifier(identifier);

	}

	@Test
	public void testGetAllOrganizations() {
		List<Organization> expected = Collections.singletonList(organization);
		when(organizationRepository.getAll()).thenReturn(expected);
		List<Organization> organizations = organizationService.getAllOrganizations();
		verify(organizationRepository).getAll();
		assertEquals(expected, organizations);
	}

	@Test
	public void testSelectOrganizationsEncompassLocations(){
		String locationID = "12345_location";
		organizationService.selectOrganizationsEncompassLocations(locationID);
		verify(organizationRepository).selectOrganizationsEncompassLocations(Mockito.eq(locationID), Mockito.any(Date.class));
	}

	@Test
	public void testGetOrganization() {
		when(organizationRepository.get(identifier)).thenReturn(organization);
		Organization actual = organizationService.getOrganization(identifier);
		verify(organizationRepository).get(identifier);
		assertEquals(organization, actual);
	}

	@Test
	public void testGetOrganizationById() {
		when(organizationRepository.getByPrimaryKey(1l)).thenReturn(organization);
		Organization actual = organizationService.getOrganization(1l);
		verify(organizationRepository).getByPrimaryKey(1l);
		assertEquals(organization, actual);
	}

	@Test
	public void testAddOrUpdateOrganizationShouldAdd() {
		organizationService.addOrUpdateOrganization(organization);
		verify(organizationRepository).add(organization);
	}

	@Test
	public void testAddOrUpdateOrganizationShouldUpdate() {
		when(organizationRepository.get(identifier)).thenReturn(organization);
		organizationService.addOrUpdateOrganization(organization);
		verify(organizationRepository).update(organization);
	}

	@Test
	public void testAddOrganization() {
		organizationService.addOrganization(organization);
		verify(organizationRepository).add(organization);
	}

	@Test
	public void testUpdateOrganization() {
		organizationService.updateOrganization(organization);
		verify(organizationRepository).update(organization);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testUpdateOrganizationWithoutIdnetifier() {
		organizationService.updateOrganization(new Organization());
		verify(organizationRepository, never()).update(organization);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignLocationAndPlanWithoutOrgId() {
		organizationService.assignLocationAndPlan(null, "jurisdictionId", "planId", null, null);
		verify(organizationRepository, never()).assignLocationAndPlan(anyLong(), anyString(), anyLong(), anyString(),
		    anyLong(), any(Date.class), any(Date.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignLocationAndPlanWithoutJurisdictionAndPlan() {
		organizationService.assignLocationAndPlan(identifier, null, null, null, null);
		verify(organizationRepository, never()).assignLocationAndPlan(anyLong(), anyString(), anyLong(), anyString(),
		    anyLong(), any(Date.class), any(Date.class));
	}

	@Test
	public void testAssignLocationAndPlan() {
		when(organizationRepository.get(identifier)).thenReturn(organization);
		organization.setId(1233l);
		String planIdentifier = UUID.randomUUID().toString();
		String jurisdictionIdentifier = UUID.randomUUID().toString();
		Long planId = 54311l;
		Long locationId = 123l;
		PhysicalLocation location = new PhysicalLocation();
		location.setId(jurisdictionIdentifier);
		when(locationRepository.retrievePrimaryKey(jurisdictionIdentifier, true,0)).thenReturn(locationId);
		when(planRepository.retrievePrimaryKey(planIdentifier)).thenReturn(planId);
		organizationService.assignLocationAndPlan(identifier, jurisdictionIdentifier, planIdentifier, null, null);
		Date date = new Date();
		verify(organizationRepository).assignLocationAndPlan(eq(1233l), eq(jurisdictionIdentifier), eq(locationId),
		    eq(planIdentifier), eq(planId), dateCaptor.capture(), dateCaptor.capture());

		assertEquals(date.toInstant().getEpochSecond(), dateCaptor.getAllValues().get(0).toInstant().getEpochSecond());

		assertNull(dateCaptor.getAllValues().get(1));

		Date dateFrom = new Date();
		Date dateTo = new Date();
		organizationService.assignLocationAndPlan(identifier, jurisdictionIdentifier, planIdentifier, dateFrom, dateTo);
		verify(organizationRepository).assignLocationAndPlan(eq(1233l), eq(jurisdictionIdentifier), eq(locationId),
		    eq(planIdentifier), eq(planId), eq(dateFrom), eq(dateTo));

	}

	@Test
	public void testAssignLocationAndPlanWithoutPlan() {
		when(organizationRepository.get(identifier)).thenReturn(organization);
		organization.setId(1233l);
		String planIdentifier = null;
		String jurisdictionIdentifier = UUID.randomUUID().toString();
		Long locationId = 123l;
		Long planId = null;
		PhysicalLocation location = new PhysicalLocation();
		location.setId(jurisdictionIdentifier);
		when(locationRepository.retrievePrimaryKey(jurisdictionIdentifier, true,0)).thenReturn(locationId);
		when(planRepository.retrievePrimaryKey(planIdentifier)).thenReturn(planId);
		Date dateFrom = new Date();
		Date dateTo = null;
		organizationService.assignLocationAndPlan(identifier, jurisdictionIdentifier, planIdentifier, dateFrom, dateTo);
		verify(organizationRepository).assignLocationAndPlan(eq(1233l), eq(jurisdictionIdentifier), eq(locationId),
		    eq(planIdentifier), eq(planId), eq(dateFrom), eq(dateTo));
	}

	@Test
	public void testAssignLocationAndPlanWithoutJurisdiction() {
		when(organizationRepository.get(identifier)).thenReturn(organization);
		organization.setId(1233l);
		String planIdentifier = UUID.randomUUID().toString();
		String jurisdictionIdentifier = null;
		Long locationId = null;
		Long planId = 19871l;
		PhysicalLocation location = new PhysicalLocation();
		location.setId(jurisdictionIdentifier);
		when(locationRepository.retrievePrimaryKey(jurisdictionIdentifier, true,0)).thenReturn(locationId);
		when(planRepository.retrievePrimaryKey(planIdentifier)).thenReturn(planId);
		Date dateFrom = new Date();
		Date dateTo = null;
		organizationService.assignLocationAndPlan(identifier, jurisdictionIdentifier, planIdentifier, dateFrom, dateTo);
		verify(organizationRepository).assignLocationAndPlan(eq(1233l), eq(jurisdictionIdentifier), eq(locationId),
		    eq(planIdentifier), eq(planId), eq(dateFrom), eq(dateTo));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignLocationAndPlanWithoutIdentifier() {
		String planIdentifier = UUID.randomUUID().toString();
		String jurisdictionIdentifier = null;
		organizationService.assignLocationAndPlan(null, jurisdictionIdentifier, planIdentifier, null, null);
		verify(organizationRepository, never()).assignLocationAndPlan(anyLong(), eq(jurisdictionIdentifier), anyLong(),
		    eq(planIdentifier), anyLong(), any(Date.class), any(Date.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAssignLocationAndMissingOrganization() {
		String planIdentifier = UUID.randomUUID().toString();
		String jurisdictionIdentifier = UUID.randomUUID().toString();
		when(organizationRepository.get(identifier)).thenReturn(null);
		organizationService.assignLocationAndPlan(identifier, jurisdictionIdentifier, planIdentifier, null, null);
		verify(organizationRepository, never()).assignLocationAndPlan(anyLong(), eq(jurisdictionIdentifier), anyLong(),
		    eq(planIdentifier), anyLong(), any(Date.class), any(Date.class));
	}

	@Test
	public void testFindAssignedLocationsAndPlans() {
		organization.setId(12l);
		AssignedLocations assigment = new AssignedLocations("loc1", "plan1");
		List<AssignedLocations> expected = Collections.singletonList(assigment);
		when(organizationRepository.findAssignedLocations(12l)).thenReturn(expected);
		when(organizationRepository.get(identifier)).thenReturn(organization);
		List<AssignedLocations> assigned = organizationService.findAssignedLocationsAndPlans(identifier);
		assertEquals(expected, assigned);
		verify(organizationRepository).findAssignedLocations(12l);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindAssignedLocationsAndPlansOrganitionMissing() {
		organization.setId(12l);
		organizationService.findAssignedLocationsAndPlans(identifier);
		verify(organizationRepository, never()).findAssignedLocations(anyLong());

	}

	@Test
	public void testFindAssignedLocationsAndPlansWithMultipleIds() {
		List<Long> organizationIds = Arrays.asList(12l, 34l, 45l);
		AssignedLocations assigment = new AssignedLocations("loc1", "plan1");
		List<AssignedLocations> expected = Collections.singletonList(assigment);
		when(organizationRepository.findAssignedLocations(organizationIds)).thenReturn(expected);
		List<AssignedLocations> assigned = organizationService.findAssignedLocationsAndPlans(organizationIds);
		assertEquals(expected, assigned);
		verify(organizationRepository).findAssignedLocations(organizationIds);
	}

	@Test
	public void testFindAssignedLocationsAndPlansByPlanIdentifier() {
		AssignedLocations assigment = new AssignedLocations("loc1", "plan1");
		List<AssignedLocations> expected = Collections.singletonList(assigment);
		when(planRepository.retrievePrimaryKey(anyString())).thenReturn(1l);
		when(organizationRepository.findAssignedLocationsByPlanId(1l)).thenReturn(expected);
		List<AssignedLocations> assigned = organizationService.findAssignedLocationsAndPlansByPlanIdentifier(identifier);
		assertEquals(expected, assigned);
		verify(organizationRepository).findAssignedLocationsByPlanId(1l);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindAssignedLocationsAndPlansByPlanIdentifierWithNullParam() {
		organizationService.findAssignedLocationsAndPlansByPlanIdentifier(null);
		verify(organizationRepository, never()).findAssignedLocations(anyLong());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindAssignedLocationsAndPlansByPlanIdentifierMissingPlan() {
		when(planRepository.retrievePrimaryKey(anyString())).thenReturn(null);

		organizationService.findAssignedLocationsAndPlansByPlanIdentifier("plan-id-1");
		verify(organizationRepository, never()).findAssignedLocations(anyLong());
	}

	@Test
	public void testSearchOrganizationsBySearchParam() {
		organization.setActive(true);
		organization.setMemberCount(1);
		List<Organization> expected = Collections.singletonList(organization);
		OrganizationSearchBean organizationSearchBean = new OrganizationSearchBean();
		organizationSearchBean.setPageNumber(0);
		organizationSearchBean.setPageSize(10);
		when(organizationRepository.findSearchOrganizations(organizationSearchBean)).thenReturn(expected);
		List<Organization> organizations = organizationService.getSearchOrganizations(organizationSearchBean);
		verify(organizationRepository).findSearchOrganizations(organizationSearchBean);
		assertEquals(expected, organizations);
	}

}
