package org.opensrp.repository.postgres;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensrp.domain.PractitionerRole;
import org.opensrp.domain.PractitionerRoleCode;
import org.opensrp.repository.PractitionerRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PractitionerRoleRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private PractitionerRoleRepository practitionerRoleRepository;

    @BeforeClass
    public static void bootStrap() {
        tableNames.add("team.organization");
        tableNames.add("team.practitioner");
        tableNames.add("team.practitioner_role");
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<>();
        scripts.add("practitioner.sql");
        return scripts;
    }

    @Test
    public void testAddShouldAddNewPractitionerRole() {
        PractitionerRole practitionerRole1 = initTestPractitionerRole1();
        practitionerRoleRepository.add(practitionerRole1);

        List<PractitionerRole> practitionerRoles = practitionerRoleRepository.getAll();
        assertNotNull(practitionerRoles);
        assertEquals(1, practitionerRoles.size());
        assertEquals("pr1-identifier", practitionerRoles.get(0).getIdentifier());
        assertEquals(true, practitionerRoles.get(0).getActive());
        assertEquals("org1", practitionerRoles.get(0).getOrganizationIdentifier());
        assertEquals("p1-identifier", practitionerRoles.get(0).getPractitionerIdentifier());
        assertEquals("pr1Code", practitionerRoles.get(0).getCode().getText());
    }

    @Test
    public void testGetShouldGetPractitionerRoleById() {

        PractitionerRole practitionerRole1 = initTestPractitionerRole1();
        practitionerRoleRepository.add(practitionerRole1);

        PractitionerRole practitionerRole2 = initTestPractitionerRole2();
        practitionerRoleRepository.add(practitionerRole2);

        PractitionerRole practitionerRole = practitionerRoleRepository.get("pr2-identifier");
        assertNotNull(practitionerRole);
        assertEquals("pr2-identifier", practitionerRole.getIdentifier());
        assertEquals(true, practitionerRole.getActive());
        assertEquals("org1", practitionerRole.getOrganizationIdentifier());
        assertEquals("p2-identifier", practitionerRole.getPractitionerIdentifier());
        assertEquals("pr2Code", practitionerRole.getCode().getText());

    }

    @Test
    public void testUpdateShouldUpdateExistingPractitioner() {
        PractitionerRole practitionerRole2 = initTestPractitionerRole2();
        practitionerRoleRepository.add(practitionerRole2);

        PractitionerRole addedPractitionerRole = practitionerRoleRepository.get(practitionerRole2.getIdentifier());
        assertNotNull(addedPractitionerRole);
        assertEquals("pr2-identifier", addedPractitionerRole.getIdentifier());
        assertEquals(true, addedPractitionerRole.getActive());
        assertEquals("pr2Code", addedPractitionerRole.getCode().getText());

        practitionerRole2.setActive(false);
        practitionerRole2.getCode().setText("updatedCode");
        practitionerRoleRepository.update(practitionerRole2);

        PractitionerRole updatedPractitionerRole = practitionerRoleRepository.get(practitionerRole2.getIdentifier());
        assertNotNull(updatedPractitionerRole);
        assertEquals("pr2-identifier", updatedPractitionerRole.getIdentifier());
        assertEquals(false, updatedPractitionerRole.getActive());
        assertEquals("updatedCode", updatedPractitionerRole.getCode().getText());
    }

    @Test
    public void testGetAllShouldGetAllPractitionerRoles() {
        PractitionerRole practitionerRole1 = initTestPractitionerRole1();
        practitionerRoleRepository.add(practitionerRole1);

        PractitionerRole practitionerRole2 = initTestPractitionerRole2();
        practitionerRoleRepository.add(practitionerRole2);

        List<PractitionerRole> practitionerRoles = practitionerRoleRepository.getAll();
        assertNotNull(practitionerRoles);
        assertEquals(2,practitionerRoles.size());

        Set<String> ids = new HashSet<>();
        ids.add(practitionerRole1.getIdentifier());
        ids.add(practitionerRole2.getIdentifier());
        assertTrue(testIfAllIdsExists(practitionerRoles, ids));
    }

    @Test
    public void testSafeRemoveShouldDeletePractitionerRole() {
        PractitionerRole practitionerRole1 = initTestPractitionerRole1();
        practitionerRoleRepository.add(practitionerRole1);

        PractitionerRole practitionerRole2 = initTestPractitionerRole2();
        practitionerRoleRepository.add(practitionerRole2);

        List<PractitionerRole> practitionerRoles = practitionerRoleRepository.getAll();
        assertNotNull(practitionerRoles);
        assertEquals(2,practitionerRoles.size());

        practitionerRoleRepository.safeRemove(practitionerRole2);

        practitionerRoles = practitionerRoleRepository.getAll();
        assertNotNull(practitionerRoles);
        assertEquals(1, practitionerRoles.size());
        assertEquals(practitionerRole1.getIdentifier(), practitionerRoles.get(0).getIdentifier());
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

    private boolean testIfAllIdsExists(List<PractitionerRole> practitionerRoles, Set<String> ids) {
        for (PractitionerRole practitionerRole : practitionerRoles) {
            ids.remove(practitionerRole.getIdentifier());
        }
        return ids.size() == 0;
    }
}
