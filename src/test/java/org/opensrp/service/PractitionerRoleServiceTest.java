package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.PractitionerRole;
import org.opensrp.domain.PractitionerRoleCode;
import org.opensrp.repository.PractitionerRoleRepository;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class PractitionerRoleServiceTest {

    private PractitionerRoleService practitionerRoleService;

    private PractitionerRoleRepository practitionerRoleRepository;

    @Before
    public void setUp() {
        practitionerRoleRepository = mock(PractitionerRoleRepository.class);
        practitionerRoleService = new PractitionerRoleService();
        practitionerRoleService.setPractitionerRoleRepository(practitionerRoleRepository);
    }

    @Test
    public void testGetAllPractitionerRoles() {
        List<PractitionerRole> expectedPractitionerRoles = new ArrayList<>();
        expectedPractitionerRoles.add(initTestPractitionerRole());
        when(practitionerRoleRepository.getAll()).thenReturn(expectedPractitionerRoles);

        List<PractitionerRole> actutalPractitionerRoles = practitionerRoleService.getAllPractitionerRoles();
        verify(practitionerRoleRepository).getAll();
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

    @Test
    public void testDeleteShouldCallRepostorySafeRemoveMethod() {
        when(practitionerRoleRepository.get(anyString())).thenReturn(initTestPractitionerRole());
        PractitionerRole practitionerRole = initTestPractitionerRole();
        practitionerRoleService.deletePractitionerRole(practitionerRole);
        verify(practitionerRoleRepository).safeRemove(eq(practitionerRole));
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
}
