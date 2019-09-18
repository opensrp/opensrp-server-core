package org.opensrp.repository.postgres;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensrp.domain.Practitioner;
import org.opensrp.repository.PractitionerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PractitionerRepositoryTest extends BaseRepositoryTest{

    @Autowired
    private PractitionerRepository practitionerRepository;

    @BeforeClass
    public static void bootStrap() {
        tableNames.add("team.practitioner");
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<>();
        return scripts;
    }

    @Test
    public void testAddShouldAddNewPractitioner() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(1,practitioners.size());
        assertEquals("practitoner-1-identifier",practitioners.get(0).getIdentifier());
        assertEquals(true,practitioners.get(0).getActive());
        assertEquals("Practitioner",practitioners.get(0).getName());
        assertEquals("Practioner1",practitioners.get(0).getUserName());
        assertEquals("user1",practitioners.get(0).getUserId());
    }

    @Test
    public void testAddShouldNotInsertRecordIfPractitionerIsNull() {
        practitionerRepository.add(null);

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertTrue(practitioners.isEmpty());
    }

    @Test
    public void testAddShouldNotInsertRecordIfPractitionerIdentifierIsNull() {
        Practitioner expectedPractitioner = initTestPractitioner1();
        expectedPractitioner.setIdentifier(null);
        practitionerRepository.add(expectedPractitioner);

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertTrue(practitioners.isEmpty());
    }

    @Test
    public void testGetShouldGetPractitionerByIdentifier() {

        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        Practitioner practitioner2 = initTestPractitioner2();
        practitionerRepository.add(practitioner2);

        Practitioner practitioner = practitionerRepository.get("practitoner-2-identifier");
        assertNotNull(practitioner);
        assertEquals("practitoner-2-identifier", practitioner.getIdentifier());
        assertEquals(false, practitioner.getActive());
        assertEquals("Second Practitioner", practitioner.getName());
        assertEquals("Practioner2", practitioner.getUserName());
        assertEquals("user2", practitioner.getUserId());

    }

    @Test
    public void testGetShouldGetReturnsNullIfIdentifierIsNullOrEmpty() {
        Practitioner practitioner = practitionerRepository.get(null);
        assertNull(practitioner);

        practitioner = practitionerRepository.get("");
        assertNull(practitioner);
    }

    @Test
    public void testGetShouldnotReturnDeletedPractitioners() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        Practitioner practitioner2 = initTestPractitioner2();
        practitioner2.setDateDeleted(new Date());
        practitionerRepository.add(practitioner2);

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(1,practitioners.size());
        assertEquals("practitoner-1-identifier",practitioners.get(0).getIdentifier());
        assertEquals(true,practitioners.get(0).getActive());
        assertEquals("Practitioner",practitioners.get(0).getName());
        assertEquals("Practioner1",practitioners.get(0).getUserName());
        assertEquals("user1",practitioners.get(0).getUserId());
    }

    @Test
    public void testUpdateShouldUpdateExistingPractitioner() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        Practitioner addedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitoner-1-identifier", addedPractitioner.getIdentifier());
        assertEquals(true, addedPractitioner.getActive());
        assertEquals("Practitioner", addedPractitioner.getName());

        practitioner1.setActive(false);
        practitioner1.setName("First Practitioner");
        practitionerRepository.update(practitioner1);

        Practitioner updatedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitoner-1-identifier", updatedPractitioner.getIdentifier());
        assertEquals(false, updatedPractitioner.getActive());
        assertEquals("First Practitioner", updatedPractitioner.getName());
    }

    @Test
    public void testUpdateWithNullParamDoesNotUpdateExistingRecord() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        Practitioner addedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitoner-1-identifier", addedPractitioner.getIdentifier());
        assertEquals(true, addedPractitioner.getActive());
        assertEquals("Practitioner", addedPractitioner.getName());

        practitionerRepository.update(null);

        Practitioner updatedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitoner-1-identifier", addedPractitioner.getIdentifier());
        assertEquals(true, addedPractitioner.getActive());
        assertEquals("Practitioner", addedPractitioner.getName());
    }

    @Test
    public void testUpdateWithNullIdentifierDoesNotUpdateExistingRecord() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        Practitioner addedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitoner-1-identifier", addedPractitioner.getIdentifier());
        assertEquals(true, addedPractitioner.getActive());
        assertEquals("Practitioner", addedPractitioner.getName());

        practitioner1.setIdentifier(null);
        practitioner1.setActive(false);
        practitioner1.setName("Practitioner edit");
        practitionerRepository.update(practitioner1);

        Practitioner updatedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitoner-1-identifier", addedPractitioner.getIdentifier());
        assertEquals(true, addedPractitioner.getActive());
        assertEquals("Practitioner", addedPractitioner.getName());
    }

    @Test
    public void testUpdateWithNonExistingRecordNotUpdate() {
        Practitioner practitioner1 = initTestPractitioner1();

        practitionerRepository.update(practitioner1);

        Practitioner updatedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNull(updatedPractitioner);
    }

    @Test
    public void testGetAllShouldGetAllPractitioners() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        Practitioner practitioner2 = initTestPractitioner2();
        practitionerRepository.add(practitioner2);

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(2,practitioners.size());

        Set<String> ids = new HashSet<>();
        ids.add(practitioner1.getIdentifier());
        ids.add(practitioner2.getIdentifier());
        assertTrue(testIfAllIdsExists(practitioners, ids));
    }

    @Test
    public void testSafeRemoveShouldMarkPractitionerAsDeleted() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        Practitioner practitioner2 = initTestPractitioner2();
        practitionerRepository.add(practitioner2);

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(2,practitioners.size());

        practitionerRepository.safeRemove(practitioner2);

        practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(1, practitioners.size());
        assertEquals(practitioner1.getIdentifier(), practitioners.get(0).getIdentifier());
    }

    @Test
    public void testSafeRemoveWithEmptyParamShouldDoesNotAffectExistingRecord() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(1,practitioners.size());

        practitionerRepository.safeRemove(null);

        practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(1, practitioners.size());
        assertEquals(practitioner1.getIdentifier(), practitioners.get(0).getIdentifier());
    }

    @Test
    public void testSafeRemoveWithNonExistingRecordShouldDoesNotAffectExistingRecord() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(1,practitioners.size());

        Practitioner practitioner2 = initTestPractitioner2();
        practitionerRepository.safeRemove(practitioner2);

        practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(1, practitioners.size());
        assertEquals(practitioner1.getIdentifier(), practitioners.get(0).getIdentifier());
    }

    @Test
    public void testGetPractitionerByUserId() {
        Practitioner expectedPractitioner = initTestPractitioner2();
        practitionerRepository.add(expectedPractitioner);

        Practitioner actualPractitioner = practitionerRepository.getPractitionerByUserId(expectedPractitioner.getUserId());
        assertNotNull(actualPractitioner);
        assertEquals("practitoner-2-identifier", actualPractitioner.getIdentifier());
        assertEquals(false, actualPractitioner.getActive());
        assertEquals("Second Practitioner", actualPractitioner.getName());
        assertEquals("Practioner2", actualPractitioner.getUserName());
        assertEquals("user2", actualPractitioner.getUserId());
    }

    @Test
    public void testGetPractitionerByUserIdWithNullUserIdReturnsNull() {
        Practitioner expectedPractitioner = initTestPractitioner2();
        expectedPractitioner.setUserId(null);

        Practitioner actualPractitioner = practitionerRepository.getPractitionerByUserId(expectedPractitioner.getUserId());
        assertNull(actualPractitioner);
    }

    private Practitioner initTestPractitioner1(){
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier("practitoner-1-identifier");
        practitioner.setActive(true);
        practitioner.setName("Practitioner");
        practitioner.setUserName("Practioner1");
        practitioner.setUserId("user1");
        return practitioner;
    }

    private Practitioner initTestPractitioner2(){
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier("practitoner-2-identifier");
        practitioner.setActive(false);
        practitioner.setName("Second Practitioner");
        practitioner.setUserName("Practioner2");
        practitioner.setUserId("user2");
        return practitioner;
    }

    private boolean testIfAllIdsExists(List<Practitioner> practitioners, Set<String> ids) {
        for (Practitioner practitioner : practitioners) {
            ids.remove(practitioner.getIdentifier());
        }
        return ids.size() == 0;
    }

}
