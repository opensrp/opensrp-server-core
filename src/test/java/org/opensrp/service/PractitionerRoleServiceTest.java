package org.opensrp.service;

import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.Organization;
import org.smartregister.domain.PractitionerRole;
import org.smartregister.domain.PractitionerRoleCode;
import org.opensrp.repository.PractitionerRoleRepository;
import org.opensrp.search.PractitionerRoleSearchBean;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class PractitionerRoleServiceTest {

    private PractitionerRoleService practitionerRoleService;

    private PractitionerRoleRepository practitionerRoleRepository;

    private OrganizationService organizationService;

    private PractitionerService practitionerService;

    @Before
    public void setUp() {
        practitionerRoleRepository = mock(PractitionerRoleRepository.class);
        practitionerRoleService = new PractitionerRoleService();
        practitionerRoleService.setPractitionerRoleRepository(practitionerRoleRepository);
        organizationService = mock(OrganizationService.class);
        practitionerRoleService.setOrganizationService(organizationService);
        practitionerService = mock(PractitionerService.class);
        practitionerRoleService.setPractitionerService(practitionerService);
    }

    @Test
    public void testGetAllPractitionerRoles() {
        List<PractitionerRole> expectedPractitionerRoles = new ArrayList<>();
        expectedPractitionerRoles.add(initTestPractitionerRole());
        when(practitionerRoleRepository.getAllPractitionerRoles(any(PractitionerRoleSearchBean.class))).thenReturn(expectedPractitionerRoles);

        PractitionerRoleSearchBean practitionerRoleSearchBean = new PractitionerRoleSearchBean();
        List<PractitionerRole> actutalPractitionerRoles = practitionerRoleService.getAllPractitionerRoles(practitionerRoleSearchBean);
        verify(practitionerRoleRepository).getAllPractitionerRoles(practitionerRoleSearchBean);
        assertEquals(1, actutalPractitionerRoles.size());
        assertEquals("pr1-identifier", actutalPractitionerRoles.get(0).getIdentifier());
    }

    @Test
    public void testGetPractitionerRoleByIdentifier() {
        PractitionerRole expectedPractitionerRole = initTestPractitionerRole();
        when(practitionerRoleRepository.get(anyString())).thenReturn(expectedPractitionerRole);

        PractitionerRole actutalPractitionerRole = practitionerRoleService.getPractitionerRole(expectedPractitionerRole.getIdentifier());
        verify(practitionerRoleRepository).get(anyString());
        assertNotNull(actutalPractitionerRole);
        assertEquals("pr1-identifier", actutalPractitionerRole.getIdentifier());
    }

    @Test
    public void testAddOrUpdateShouldCallRepostoryAddMethod() {
        when(practitionerRoleRepository.get(anyString())).thenReturn(null);
        PractitionerRole practitionerRole = initTestPractitionerRole();
        practitionerRoleService.addOrUpdatePractitionerRole(practitionerRole);
        verify(practitionerRoleRepository).add(eq(practitionerRole));
    }

    @Test
    public void testAddOrUpdateShouldCallRepostoryUpdateMethod() {
        when(practitionerRoleRepository.get(anyString())).thenReturn(initTestPractitionerRole());
        PractitionerRole practitionerRole = initTestPractitionerRole();
        practitionerRoleService.addOrUpdatePractitionerRole(practitionerRole);
        verify(practitionerRoleRepository).update(eq(practitionerRole));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddOrUpdateWithNullIdentifier() {
        when(practitionerRoleRepository.get(anyString())).thenReturn(initTestPractitionerRole());
        PractitionerRole practitionerRole = initTestPractitionerRole();
        practitionerRole.setIdentifier(null);
        practitionerRoleService.addOrUpdatePractitionerRole(practitionerRole);
        verify(practitionerRoleRepository, never()).update(eq(practitionerRole));
    }

    @Test
    public void testDeleteShouldCallRepostorySafeRemoveMethod() {
        when(practitionerRoleRepository.get(anyString())).thenReturn(initTestPractitionerRole());
        PractitionerRole practitionerRole = initTestPractitionerRole();
        practitionerRoleService.deletePractitionerRole(practitionerRole);
        verify(practitionerRoleRepository).safeRemove(eq(practitionerRole));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteWithNullIdentifier() {
        PractitionerRole practitionerRole = initTestPractitionerRole();
        practitionerRole.setIdentifier(null);
        practitionerRoleService.deletePractitionerRole(practitionerRole);
        verify(practitionerRoleRepository, never()).safeRemove(eq(practitionerRole));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByIdentifierWithEmptyIdentifier() {
        practitionerRoleService.deletePractitionerRole("");
        verify(practitionerRoleRepository, never()).safeRemove(eq(any(String.class)));
    }

    @Test
    public void testDeleteByIdentifierShouldCallRepostorySafeRemoveMethod() {
        when(practitionerRoleRepository.get(anyString())).thenReturn(initTestPractitionerRole());
        PractitionerRole practitionerRole = initTestPractitionerRole();
        practitionerRoleService.deletePractitionerRole(practitionerRole.getIdentifier());
        verify(practitionerRoleRepository).safeRemove(eq(practitionerRole.getIdentifier()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeleteByOrganizationAndPractitionerWithNullValues() {
        practitionerRoleService.deletePractitionerRole(null, null);
        verify(practitionerRoleRepository, never()).safeRemove(any(Long.class), any(Long.class));
    }

    @Test
    public void testDeleteByOrganizationAndPractitionerShouldCallRepostorySafeRemoveMethod() {

        when(practitionerRoleRepository.getPractitionerRole(anyLong(), anyLong() ))
                .thenReturn(Collections.singletonList(new org.opensrp.domain.postgres.PractitionerRole()));

        Organization organization = new Organization();
        organization.setId(1l);
        when(organizationService.getOrganization(anyString())).thenReturn(organization);

        org.opensrp.domain.postgres.Practitioner pgPractitioner = new org.opensrp.domain.postgres.Practitioner();
        pgPractitioner.setId(2l);

        when(practitionerService.getPgPractitioner(anyString())).thenReturn(pgPractitioner);

        PractitionerRole practitionerRole = initTestPractitionerRole();
        practitionerRoleService.deletePractitionerRole(practitionerRole.getIdentifier(), practitionerRole.getOrganizationIdentifier());
        verify(practitionerRoleRepository).safeRemove(anyLong(), anyLong());
    }


    @Test
    public void testGetRolesForPractitionerShouldCallGetRolesForPractitionerMethod() {
        List<PractitionerRole> expectedPractitionerRoles = new ArrayList<>();
        expectedPractitionerRoles.add(initTestPractitionerRole());
        when(practitionerRoleRepository.getRolesForPractitioner(anyString())).thenReturn(expectedPractitionerRoles);

        List<PractitionerRole> actutalPractitionerRoles = practitionerRoleService.getRolesForPractitioner("identifier");
        verify(practitionerRoleRepository).getRolesForPractitioner(anyString());
        assertEquals(1, actutalPractitionerRoles.size());
        assertEquals("pr1-identifier", actutalPractitionerRoles.get(0).getIdentifier());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRolesForPractitionerWithNullIdentifier() {

        List<PractitionerRole> actutalPractitionerRoles = practitionerRoleService.getRolesForPractitioner(null);
        assertTrue(actutalPractitionerRoles.isEmpty());
        verify(practitionerRoleRepository, never()).getRolesForPractitioner(anyString());
    }

    @Test
    public void testGetPgRolesForPractitionerShouldCallGetPgRolesForPractitionerMethod() {
        List<org.opensrp.domain.postgres.PractitionerRole> expectedPractitionerRoles = new ArrayList<>();
        org.opensrp.domain.postgres.PractitionerRole practitionerRole = new org.opensrp.domain.postgres.PractitionerRole();
        practitionerRole.setIdentifier("pr1-identifier");
        expectedPractitionerRoles.add(practitionerRole);
        when(practitionerRoleRepository.getPgRolesForPractitioner(anyString())).thenReturn(expectedPractitionerRoles);

        List<org.opensrp.domain.postgres.PractitionerRole> actutalPractitionerRoles = practitionerRoleService.getPgRolesForPractitioner("identifier");
        verify(practitionerRoleRepository).getPgRolesForPractitioner(anyString());
        assertEquals(1, actutalPractitionerRoles.size());
        assertEquals("pr1-identifier", actutalPractitionerRoles.get(0).getIdentifier());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPgRolesForPractitionerWithNullIdentifier() {

        List<org.opensrp.domain.postgres.PractitionerRole> actutalPractitionerRoles = practitionerRoleService.getPgRolesForPractitioner(null);
        assertTrue(actutalPractitionerRoles.isEmpty());
        verify(practitionerRoleRepository, never()).getPgRolesForPractitioner(anyString());
    }

    @Test
    public void testGetPractitionerRolesByOrgIdAndCode() {
        PractitionerRole practitionerRole1 = initTestPractitionerRole();
        practitionerRoleRepository.add(practitionerRole1);

        PractitionerRole practitionerRole2 = initTestPractitionerRole2();
        practitionerRoleRepository.add(practitionerRole2);

        List<PractitionerRole> practitionerRoles = new ArrayList<>();
        practitionerRoles.add(practitionerRole1);
        practitionerRoles.add(practitionerRole2);

        when(practitionerRoleRepository.getPractitionerRolesByOrgIdAndCode(anyLong(), anyString())).thenReturn(practitionerRoles);
        assertNotNull(practitionerRoles);
        assertEquals(2l, practitionerRoles.size());
        assertEquals("pr1-identifier", practitionerRoles.get(0).getIdentifier());
        assertEquals("p1-identifier", practitionerRoles.get(0).getPractitionerIdentifier());
        assertEquals("pr2-identifier", practitionerRoles.get(1).getIdentifier());
        assertEquals("p2-identifier", practitionerRoles.get(1).getPractitionerIdentifier());

    }

    private static PractitionerRole initTestPractitionerRole(){
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.setIdentifier("pr1-identifier");
        practitionerRole.setActive(true);
        practitionerRole.setOrganizationIdentifier("org-identifier");
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
