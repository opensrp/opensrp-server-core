package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensrp.search.BaseSearchBean;
import org.smartregister.domain.Practitioner;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.search.PractitionerSearchBean;
import org.springframework.beans.factory.annotation.Autowired;

public class PractitionerRepositoryTest extends BaseRepositoryTest{

    @Autowired
    private PractitionerRepository practitionerRepository;

    @BeforeClass
    public static void bootStrap() {
        tableNames= Collections.singletonList("team.practitioner");
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
        assertEquals("practitioner-1-identifier",practitioners.get(0).getIdentifier());
        assertEquals(true,practitioners.get(0).getActive());
        assertEquals("Practitioner",practitioners.get(0).getName());
        assertEquals("Practitioner1",practitioners.get(0).getUsername());
        assertEquals("user1",practitioners.get(0).getUserId());
        assertNotNull(practitioners.get(0).getDateEdited());
        assertNotNull(practitioners.get(0).getDateCreated());
        assertTrue(practitioners.get(0).getServerVersion() > 0);
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

        Practitioner practitioner = practitionerRepository.get("practitioner-2-identifier");
        assertNotNull(practitioner);
        assertEquals("practitioner-2-identifier", practitioner.getIdentifier());
        assertEquals(false, practitioner.getActive());
        assertEquals("Second Practitioner", practitioner.getName());
        assertEquals("Practitioner2", practitioner.getUsername());
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
        practitionerRepository.add(practitioner2);

        practitionerRepository.safeRemove(practitioner2.getIdentifier());

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(1,practitioners.size());
        assertEquals("practitioner-1-identifier",practitioners.get(0).getIdentifier());
        assertEquals(true,practitioners.get(0).getActive());
        assertEquals("Practitioner",practitioners.get(0).getName());
        assertEquals("Practitioner1",practitioners.get(0).getUsername());
        assertEquals("user1",practitioners.get(0).getUserId());
    }

    @Test
    public void testUpdateShouldUpdateExistingPractitioner() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        Practitioner addedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitioner-1-identifier", addedPractitioner.getIdentifier());
        assertEquals(true, addedPractitioner.getActive());
        assertEquals("Practitioner", addedPractitioner.getName());

        addedPractitioner.setActive(false);
        addedPractitioner.setName("First Practitioner");
        practitionerRepository.update(addedPractitioner);

        Practitioner updatedPractitioner = practitionerRepository.get(addedPractitioner.getIdentifier());
        assertNotNull(updatedPractitioner);
        assertEquals("practitioner-1-identifier", updatedPractitioner.getIdentifier());
        assertEquals(false, updatedPractitioner.getActive());
        assertEquals("First Practitioner", updatedPractitioner.getName());
        assertEquals(addedPractitioner.getDateCreated(), updatedPractitioner.getDateCreated());
        assertEquals(addedPractitioner.getServerVersion() + 1, updatedPractitioner.getServerVersion());
    }

    @Test
    public void testUpdateWithNullParamDoesNotUpdateExistingRecord() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        Practitioner addedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitioner-1-identifier", addedPractitioner.getIdentifier());
        assertEquals(true, addedPractitioner.getActive());
        assertEquals("Practitioner", addedPractitioner.getName());

        practitionerRepository.update(null);

        Practitioner updatedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitioner-1-identifier", updatedPractitioner.getIdentifier());
        assertEquals(true, updatedPractitioner.getActive());
        assertEquals("Practitioner", updatedPractitioner.getName());
    }

    @Test
    public void testUpdateWithNullIdentifierDoesNotUpdateExistingRecord() {
        Practitioner practitioner1 = initTestPractitioner1();
        String practitioner1Identifier = practitioner1.getIdentifier();
        practitionerRepository.add(practitioner1);

        Practitioner addedPractitioner = practitionerRepository.get(practitioner1.getIdentifier());
        assertNotNull(addedPractitioner);
        assertEquals("practitioner-1-identifier", addedPractitioner.getIdentifier());
        assertEquals(true, addedPractitioner.getActive());
        assertEquals("Practitioner", addedPractitioner.getName());

        practitioner1.setIdentifier(null);
        practitioner1.setActive(false);
        practitioner1.setName("Practitioner edit");
        practitionerRepository.update(practitioner1);

        Practitioner updatedPractitioner = practitionerRepository.get(practitioner1Identifier);
        assertNotNull(updatedPractitioner);
        assertEquals("practitioner-1-identifier", updatedPractitioner.getIdentifier());
        assertEquals(true, updatedPractitioner.getActive());
        assertEquals("Practitioner", updatedPractitioner.getName());
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
    public void testSafeRemoveWithEmptyParamDoesNotAffectExistingRecord() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);

        List<Practitioner> practitioners = practitionerRepository.getAll();
        assertNotNull(practitioners);
        assertEquals(1,practitioners.size());

        practitionerRepository.safeRemove(new Practitioner());

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
        assertEquals("practitioner-2-identifier", actualPractitioner.getIdentifier());
        assertEquals(false, actualPractitioner.getActive());
        assertEquals("Second Practitioner", actualPractitioner.getName());
        assertEquals("Practitioner2", actualPractitioner.getUsername());
        assertEquals("user2", actualPractitioner.getUserId());
    }

    @Test
    public void testGetPractitionerByUserIdWithNullUserIdReturnsNull() {
        Practitioner expectedPractitioner = initTestPractitioner2();
        expectedPractitioner.setUserId(null);

        Practitioner actualPractitioner = practitionerRepository.getPractitionerByUserId(expectedPractitioner.getUserId());
        assertNull(actualPractitioner);
    }

    @Test
    public void testGetPractitionerByUsername() {
        Practitioner expectedPractitioner = initTestPractitioner2();
        practitionerRepository.add(expectedPractitioner);

        Practitioner actualPractitioner = practitionerRepository.getPractitionerByUsername(expectedPractitioner.getUsername());
        assertNotNull(actualPractitioner);
        assertEquals(expectedPractitioner.getIdentifier(),actualPractitioner.getIdentifier());
        assertEquals(expectedPractitioner.getName(),actualPractitioner.getName());
        assertEquals(expectedPractitioner.getUserId(),actualPractitioner.getUserId());
        assertEquals(expectedPractitioner.getUsername(),actualPractitioner.getUsername());
        
        assertNull( practitionerRepository.getPractitionerByUsername("janeDoe"));
    }

    private Practitioner initTestPractitioner1(){
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier("practitioner-1-identifier");
        practitioner.setActive(true);
        practitioner.setName("Practitioner");
        practitioner.setUsername("Practitioner1");
        practitioner.setUserId("user1");
        return practitioner;
    }

    private Practitioner initTestPractitioner2(){
        Practitioner practitioner = new Practitioner();
        practitioner.setIdentifier("practitioner-2-identifier");
        practitioner.setActive(false);
        practitioner.setName("Second Practitioner");
        practitioner.setUsername("Practitioner2");
        practitioner.setUserId("user2");
        return practitioner;
    }

    private boolean testIfAllIdsExists(List<Practitioner> practitioners, Set<String> ids) {
        for (Practitioner practitioner : practitioners) {
            ids.remove(practitioner.getIdentifier());
        }
        return ids.size() == 0;
    }

    @Test
    public void testGetAllPractitioners() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);
        Practitioner practitioner2 = initTestPractitioner2();
        practitionerRepository.add(practitioner2);
        PractitionerSearchBean practitionerSearchBean = PractitionerSearchBean.builder().
                orderByType(BaseSearchBean.OrderByType.DESC).
                orderByFieldName(BaseSearchBean.FieldName.id).build();
        practitionerSearchBean.setServerVersion(1l);
        List<Practitioner> practitioners = practitionerRepository.getAllPractitioners(practitionerSearchBean);
        assertNotNull(practitioners);
        assertEquals(2,practitioners.size());
        assertEquals("practitioner-2-identifier",practitioners.get(0).getIdentifier());
        assertEquals("practitioner-1-identifier",practitioners.get(1).getIdentifier());

        practitionerSearchBean = PractitionerSearchBean.builder().orderByType(BaseSearchBean.OrderByType.ASC).
                orderByFieldName(BaseSearchBean.FieldName.id).build();
        practitionerSearchBean.setServerVersion(1l);
        practitioners = practitionerRepository.getAllPractitioners(practitionerSearchBean);
        assertNotNull(practitioners);
        assertEquals(2,practitioners.size());
        assertEquals("practitioner-1-identifier",practitioners.get(0).getIdentifier());
        assertEquals("practitioner-2-identifier",practitioners.get(1).getIdentifier());
    }

    @Test
	public void testGetAllPractitionersByIdentifiers() {
	    Practitioner practitioner1 = initTestPractitioner1();
	    practitionerRepository.add(practitioner1);
	    Practitioner practitioner2 = initTestPractitioner2();
	    practitionerRepository.add(practitioner2);

	    List<String> practitionerIdentifiers = new ArrayList<>();
	    practitionerIdentifiers.add("practitioner-1-identifier");
	    practitionerIdentifiers.add("practitioner-2-identifier");
	    List<Practitioner> practitioners = practitionerRepository.getAllPractitionersByIdentifiers(practitionerIdentifiers);
	    assertNotNull(practitioners);
	    assertEquals(2,practitioners.size());
	    assertEquals("Practitioner",practitioners.get(0).getName());
	    assertEquals("Second Practitioner",practitioners.get(1).getName());
    }

    @Test
    public void testCountAllPractitioners() {
        Practitioner practitioner1 = initTestPractitioner1();
        practitionerRepository.add(practitioner1);
        assertEquals(1, practitionerRepository.countAllPractitioners());
    }

    @Test
    public void testGetPractitionersByIdentifier() {
        Practitioner expectedPractitioner = initTestPractitioner1();
        practitionerRepository.add(expectedPractitioner);
        Practitioner actualPractitioner = practitionerRepository.getPractitionerByIdentifier(expectedPractitioner.getIdentifier());
        assertNotNull(actualPractitioner);
        assertEquals(expectedPractitioner.getIdentifier(),actualPractitioner.getIdentifier());
        assertEquals(expectedPractitioner.getName(),actualPractitioner.getName());
        assertEquals(expectedPractitioner.getUserId(),actualPractitioner.getUserId());
        assertEquals(expectedPractitioner.getUsername(),actualPractitioner.getUsername());
    }

}
