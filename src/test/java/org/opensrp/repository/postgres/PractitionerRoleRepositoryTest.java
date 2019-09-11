package org.opensrp.repository.postgres;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensrp.domain.PractitionerRole;
import org.opensrp.repository.PractitionerRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        scripts.add("organization.sql");
        return scripts;
    }

    @Test
    public void testAddShouldAddNewPractitioner() {
        PractitionerRole practitionerRole1 = initTestPractitionerRole1();
        practitionerRoleRepository.add(practitionerRole1);

        List<PractitionerRole> practitionerRoles = practitionerRoleRepository.getAll();
        assertNotNull(practitionerRoles);
        assertEquals(1, practitionerRoles.size());
        assertEquals("pr1-identifier", practitionerRoles.get(0).getIdentifier());
        assertEquals(true, practitionerRoles.get(0).getActive());
        assertEquals(1, practitionerRoles.get(0).getOrganizationId().longValue());
        assertEquals(1, practitionerRoles.get(0).getPractitionerId().longValue());
        assertEquals("pr1Code", practitionerRoles.get(0).getCode());
    }


    private static PractitionerRole initTestPractitionerRole1(){
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.setIdentifier("pr1-identifier");
        practitionerRole.setActive(true);
        practitionerRole.setOrganizationId(1l);
        practitionerRole.setPractitionerId(1l);
        practitionerRole.setCode("pr1Code");
        return practitionerRole;
    }

    private static PractitionerRole initTestPractitionerRole2(){
        PractitionerRole practitionerRole = new PractitionerRole();
        practitionerRole.setIdentifier("pr2-identifier");
        practitionerRole.setActive(true);
        practitionerRole.setOrganizationId(1l);
        practitionerRole.setPractitionerId(2l);
        practitionerRole.setCode("pr2Code");
        return practitionerRole;
    }

    private boolean testIfAllIdsExists(List<PractitionerRole> practitionerRoles, Set<String> ids) {
        for (PractitionerRole practitionerRole : practitionerRoles) {
            ids.remove(practitionerRole.getIdentifier());
        }
        return ids.size() == 0;
    }
}
