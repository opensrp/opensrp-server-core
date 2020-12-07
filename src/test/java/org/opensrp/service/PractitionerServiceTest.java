package org.opensrp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.opensrp.domain.Organization;
import org.opensrp.domain.Practitioner;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.search.PractitionerSearchBean;
import org.powermock.modules.junit4.PowerMockRunner;

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
        practitionerService = new PractitionerService(practitionerRepository,practitionerRoleService);
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

    private Practitioner initTestPractitioner(){
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier("practitoner-1-identifier");
        practitioner.setActive(true);
        practitioner.setName("Practitioner");
        practitioner.setUsername("Practioner1");
        practitioner.setUserId("user1");
        return practitioner;
    }
}
