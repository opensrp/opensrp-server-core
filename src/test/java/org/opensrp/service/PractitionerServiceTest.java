package org.opensrp.service;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.search.PractitionerSearchBean;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.domain.Practitioner;
import org.smartregister.domain.PractitionerRole;
import org.smartregister.domain.PractitionerRoleCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class PractitionerServiceTest {

    private PractitionerService practitionerService;

    @Mock
    private PractitionerRepository practitionerRepository;

    @Mock
    private PractitionerRoleService practitionerRoleService;

    @Mock
    private OrganizationService organizationService;

    @Before
    public void setUp() {
        practitionerService = new PractitionerService(); //new PractitionerService(practitionerRepository,practitionerRoleService, organizationService);
        practitionerService.setOrganizationService(organizationService);
        practitionerService.setPractitionerRoleService(practitionerRoleService);
        practitionerService.setPractitionerRepository(practitionerRepository);
    }

    @Test
    public void testgetAllPractitioners() {
        List<Practitioner> expectedPractitioners = new ArrayList<>();
        expectedPractitioners.add(initTestPractitioner());
        when(practitionerRepository.getAllPractitioners(any(PractitionerSearchBean.class))).thenReturn(expectedPractitioners);

        PractitionerSearchBean practitionerSearchBean = new PractitionerSearchBean();
        List<Practitioner> actutalPractitioners = practitionerService.getAllPractitioners(practitionerSearchBean);
        verify(practitionerRepository).getAllPractitioners(practitionerSearchBean);
        assertEquals(1, actutalPractitioners.size());
        assertEquals("practitoner-1-identifier", actutalPractitioners.get(0).getIdentifier());
    }

    @Test
    public void testGetPractitionerByIdentifier() {
        Practitioner expectedPractitioner = initTestPractitioner();
        when(practitionerRepository.get(anyString())).thenReturn(expectedPractitioner);

        Practitioner actutalPractitioner = practitionerService.getPractitioner(expectedPractitioner.getIdentifier());
        verify(practitionerRepository).get(anyString());
        assertNotNull(actutalPractitioner);
        assertEquals("practitoner-1-identifier", actutalPractitioner.getIdentifier());
    }

    @Test
    public void testGetPractitionerByUserId() {
        Practitioner expectedPractitioner = initTestPractitioner();
        when(practitionerRepository.getPractitionerByUserId(anyString())).thenReturn(expectedPractitioner);

        Practitioner practitioner = practitionerService.getPractitionerByUserId(expectedPractitioner.getUserId());
        verify(practitionerRepository).getPractitionerByUserId(anyString());
        assertNotNull(practitioner);
        assertEquals("user1", practitioner.getUserId());
    }

    @Test
    public void testAddOrUpdateShouldCallRepostoryAddMethod() {
        when(practitionerRepository.get(anyString())).thenReturn(null);
        Practitioner practitioner = initTestPractitioner();
        practitionerService.addOrUpdatePractitioner(practitioner);
        verify(practitionerRepository).add(eq(practitioner));
    }

    @Test
    public void testAddOrUpdateShouldCallRepostoryUpdateMethod() {
        when(practitionerRepository.get(anyString())).thenReturn(initTestPractitioner());
        Practitioner practitioner = initTestPractitioner();
        practitionerService.addOrUpdatePractitioner(practitioner);
        verify(practitionerRepository).update(eq(practitioner));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateWithoutPractitionerIdentifier() {
        Practitioner practitioner = initTestPractitioner();
        practitioner.setIdentifier(null);
        practitionerService.addOrUpdatePractitioner(practitioner);
        verify(practitionerRepository, never()).update(eq(practitioner));
    }


    @Test
    public void testDeleteShouldCallRepostorySafeRemoveMethod() {
        when(practitionerRepository.get(anyString())).thenReturn(initTestPractitioner());
        Practitioner practitioner = initTestPractitioner();
        practitionerService.deletePractitioner(practitioner);
        verify(practitionerRepository).safeRemove(eq(practitioner));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithoutPlanIdentifier() {
        Practitioner practitioner = initTestPractitioner();
        practitioner.setIdentifier(null);
        practitionerService.deletePractitioner(practitioner);
        verify(practitionerRepository, never()).safeRemove(eq(practitioner));
    }

    @Test
    public void testGetOrganizationByUserIdShouldCallGetPgRolesForPractitionerMethod() {
        Practitioner practitioner = initTestPractitioner();
        when(practitionerRepository.getPractitionerByUserId(anyString())).thenReturn(practitioner);

        practitionerService.getOrganizationsByUserId("user-id");
        verify(practitionerRepository).getPractitionerByUserId(eq("user-id"));
        verify(practitionerRoleService).getPgRolesForPractitioner(eq(practitioner.getIdentifier()));
    }

    @Test
    public void testGetPractitionersByOrgId() {

        Practitioner practitioner = initTestPractitioner();
        when(practitionerRepository.getPractitionersByOrgId(anyLong()))
                .thenReturn(Collections.singletonList(practitioner));

        practitionerService.getPractitionersByOrgId(1l);

        verify(practitionerRepository).getPractitionersByOrgId(anyLong());

    }

	@Test
	public void testGetPractionerByUsername() {
		String username = "janedoe";
		Practitioner practitioner = initTestPractitioner();
		when(practitionerRepository.getPractitionerByUsername(username)).thenReturn(practitioner);

		Practitioner actual = practitionerService.getPractionerByUsername("janedoe");

		verify(practitionerRepository).getPractitionerByUsername(username);
		assertEquals(practitioner, actual);

	}

	@Test
	public void testGetPractitionersByIdentifiers() {
		Practitioner practitioner1 = initTestPractitioner();
		Practitioner practitioner2 = initTestPractitioner2();
		List<Practitioner> practitioners = new ArrayList<>();
		practitioners.add(practitioner1);
		practitioners.add(practitioner2);

		when(practitionerRepository.getAllPractitionersByIdentifiers(any(List.class))).thenReturn(practitioners);

		List<String> identifiers = new ArrayList<>();
		identifiers.add("practitoner-1-identifier");
		identifiers.add("practitoner-2-identifier");
		List<Practitioner> actual = practitionerService.getPractitionersByIdentifiers(identifiers);

		verify(practitionerRepository).getAllPractitionersByIdentifiers(identifiers);
		assertEquals(practitioners.size(), actual.size());
		assertEquals(practitioners.get(0).getName(), actual.get(0).getName());
		assertEquals(practitioners.get(1).getName(), actual.get(1).getName());
	}

	@Test
	public void testGetAssignedPractitionersByIdentifierAndCode() {
		PractitionerRole practitionerRole = initTestPractitionerRole1();
		PractitionerRole practitionerRole2 = initTestPractitionerRole2();

		List<PractitionerRole> practitionerRoles = new ArrayList<>();
		practitionerRoles.add(practitionerRole);
		practitionerRoles.add(practitionerRole2);

		List<org.opensrp.domain.postgres.PractitionerRole> roles = new ArrayList<>();
		List<Long> organizationIds = Arrays.asList(11l, 40l,7667l);
		for(long id:organizationIds) {
			org.opensrp.domain.postgres.PractitionerRole role = new org.opensrp.domain.postgres.PractitionerRole();
			role.setOrganizationId(id);
			roles.add(role);
		}

		Practitioner practitioner1 = initTestPractitioner();
		Practitioner practitioner2 = initTestPractitioner2();
		List<Practitioner> practitioners = new ArrayList<>();
		practitioners.add(practitioner1);
		practitioners.add(practitioner2);
    	when(practitionerRoleService.getPgRolesForPractitioner(anyString())).thenReturn(roles);
    	when(practitionerRoleService.getPractitionerRolesByOrgIdAndCode(anyLong(),anyString())).thenReturn(practitionerRoles);
    	when(practitionerRepository.getAllPractitionersByIdentifiers(any(List.class))).thenReturn(practitioners);

		List<Practitioner> actual = practitionerService.getAssignedPractitionersByIdentifierAndCode("test-identifier","pr1Code");

		assertEquals(practitioners.size(), actual.size());
		assertEquals(practitioners.get(0).getName(), actual.get(0).getName());
		assertEquals(practitioners.get(1).getName(), actual.get(1).getName());
	}

	@Test
	public void testCountAllPractitioners() {
		Practitioner practitioner1 = initTestPractitioner();
		Practitioner practitioner2 = initTestPractitioner2();
		List<Practitioner> practitioners = new ArrayList<>();
		practitioners.add(practitioner1);
		practitioners.add(practitioner2);
		doReturn((long)practitioners.size()).when(practitionerRepository).countAllPractitioners();
		assertEquals(2, practitionerService.countAllPractitioners());
	}

	@Test
	public void testGetOrganizationsByPractitionerIdentifier() {
		Practitioner practitioner = initTestPractitioner();
		List<org.opensrp.domain.postgres.PractitionerRole> roles = new ArrayList<>();
		List<Long> organizationIds = Arrays.asList(11l, 40l,7667l);
		for(long id:organizationIds) {
			org.opensrp.domain.postgres.PractitionerRole role = new org.opensrp.domain.postgres.PractitionerRole();
			role.setOrganizationId(id);
			roles.add(role);
		}
		when(practitionerRepository.getPractitionerByIdentifier(anyString())).thenReturn(practitioner);
		when(practitionerRoleService.getPgRolesForPractitioner(anyString())).thenReturn(roles);
		ImmutablePair<Practitioner, List<Long>> practitioneroOrganizationIds = practitionerService.getOrganizationsByPractitionerIdentifier("test-practitioner");
		assertNotNull(practitioneroOrganizationIds);
		assertEquals(practitioner, practitioneroOrganizationIds.getLeft());
		assertEquals("practitoner-1-identifier", practitioneroOrganizationIds.getLeft().getIdentifier());
		assertEquals(3, practitioneroOrganizationIds.getRight().size());
	}

    private Practitioner initTestPractitioner(){
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier("practitoner-1-identifier");
        practitioner.setActive(true);
        practitioner.setName("Practitioner");
        practitioner.setUsername("Practioner1");
        practitioner.setUserId("user1");
        return practitioner;
    }

	private Practitioner initTestPractitioner2(){
		Practitioner practitioner = new Practitioner();
		practitioner.setIdentifier("practitoner-2-identifier");
		practitioner.setActive(true);
		practitioner.setName("Practitioner 2");
		practitioner.setUsername("Practioner2");
		practitioner.setUserId("user2");
		return practitioner;
	}

	private static PractitionerRole initTestPractitionerRole1(){
		PractitionerRole practitionerRole = new PractitionerRole();
		practitionerRole.setIdentifier("pr1-identifier");
		practitionerRole.setActive(true);
		practitionerRole.setOrganizationIdentifier("org1");
		practitionerRole.setPractitionerIdentifier("p1-identifier");
		PractitionerRoleCode code = new PractitionerRoleCode();
		code.setText("pr1Code");
		practitionerRole.setCode(code);
		return practitionerRole;
	}

	private static PractitionerRole initTestPractitionerRole2(){
		PractitionerRole practitionerRole = new PractitionerRole();
		practitionerRole.setIdentifier("pr2-identifier");
		practitionerRole.setActive(true);
		practitionerRole.setOrganizationIdentifier("org1");
		practitionerRole.setPractitionerIdentifier("p2-identifier");
		PractitionerRoleCode code = new PractitionerRoleCode();
		code.setText("pr2Code");
		practitionerRole.setCode(code);
		return practitionerRole;
	}
}
