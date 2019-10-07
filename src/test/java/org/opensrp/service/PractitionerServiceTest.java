package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.Organization;
import org.opensrp.domain.Practitioner;
import org.opensrp.repository.PractitionerRepository;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class PractitionerServiceTest {

    private PractitionerService practitionerService;

    private PractitionerRepository practitionerRepository;

    private PractitionerRoleService practitionerRoleService;

    private OrganizationService organizationService;

    @Before
    public void setUp() {
        practitionerRepository = mock(PractitionerRepository.class);
        practitionerService = new PractitionerService();
        practitionerService.setPractitionerRepository(practitionerRepository);
        practitionerRoleService = mock(PractitionerRoleService.class);
        practitionerService.setPractitionerRoleService(practitionerRoleService);
        organizationService = mock(OrganizationService.class);
        practitionerService.setOrganizationService(organizationService);
    }

    @Test
    public void testgetAllPractitioners() {
        List<Practitioner> expectedPractitioners = new ArrayList<>();
        expectedPractitioners.add(initTestPractitioner());
        when(practitionerRepository.getAll()).thenReturn(expectedPractitioners);

        List<Practitioner> actutalPractitioners = practitionerService.getAllPractitioners();
        verify(practitionerRepository).getAll();
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
    public void testGetPractitionersByOrgIdentifier() {

        Practitioner practitioner = initTestPractitioner();
        when(practitionerRepository.getPractitionersByOrgId(anyLong()))
                .thenReturn(Collections.singletonList(practitioner));

        Organization organization = new Organization();
        organization.setId(1l);
        organization.setIdentifier("org1");
        when(organizationService.getOrganization("org1")).thenReturn(organization);

        practitionerService.getPractitionersByOrgIdentifier("org1");

        verify(organizationService).validateIdentifier(anyString());
        verify(organizationService).getOrganization(anyString());
        verify(practitionerRepository).getPractitionersByOrgId(anyLong());

    }

    private Practitioner initTestPractitioner(){
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier("practitoner-1-identifier");
        practitioner.setActive(true);
        practitioner.setName("Practitioner");
        practitioner.setUserName("Practioner1");
        practitioner.setUserId("user1");
        return practitioner;
    }
}
