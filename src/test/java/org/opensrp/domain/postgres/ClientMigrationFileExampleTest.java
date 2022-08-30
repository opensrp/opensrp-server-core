package org.opensrp.domain.postgres;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ClientMigrationFileExampleTest {


    @Test
    public void testClear() {
        ClientMigrationFileExample clientMigrationFileExample = new ClientMigrationFileExample();
        clientMigrationFileExample.or();
        clientMigrationFileExample.setOrderByClause("date_created DESC");
        clientMigrationFileExample.setDistinct(true);

        assertEquals(1, clientMigrationFileExample.getOredCriteria().size());
        assertTrue(clientMigrationFileExample.isDistinct());
        assertNotNull(clientMigrationFileExample.getOrderByClause());

        clientMigrationFileExample.clear();


        assertEquals(0, clientMigrationFileExample.getOredCriteria().size());
        assertFalse(clientMigrationFileExample.isDistinct());
        assertNull(clientMigrationFileExample.getOrderByClause());
    }

    @Test
    public void testCreateCriteria() {
        ClientMigrationFileExample clientMigrationFileExample = new ClientMigrationFileExample();
        clientMigrationFileExample.createCriteria();

        assertEquals(1, clientMigrationFileExample.getOredCriteria().size());
    }

    @Test
    public void testCriteriaAddIdIsNull() {
        ClientMigrationFileExample.Criteria criteria = new ClientMigrationFileExample.Criteria()
                .andIdIsNull();

        assertEquals("id is null", criteria.getAllCriteria().get(0).getCondition());
    }

    @Test
    public void testCriteriaIsValid() {
        ClientMigrationFileExample.Criteria criteria = new ClientMigrationFileExample.Criteria()
                .andIdIsNotNull();

        assertTrue(criteria.isValid());
    }

    @Test
    public void testCriterionGetterSetter() {
        ClientMigrationFileExample.Criterion criterion = new ClientMigrationFileExample.Criterion("id =", 1);

        assertTrue(criterion.isSingleValue());
        assertEquals("id =", criterion.getCondition());
        assertNull(criterion.getSecondValue());
        assertFalse(criterion.isNoValue());
        assertFalse(criterion.isBetweenValue());
        assertFalse(criterion.isListValue());
        assertNull(criterion.getTypeHandler());
    }

    @Test
    public void testCriterionSecondValueConstructor() {
        ClientMigrationFileExample.Criterion criterion = new ClientMigrationFileExample.Criterion("id in", 1, 2);

        assertFalse(criterion.isSingleValue());
        assertEquals("id in", criterion.getCondition());
        assertEquals(2, criterion.getSecondValue());
        assertFalse(criterion.isNoValue());
        assertTrue(criterion.isBetweenValue());
        assertFalse(criterion.isListValue());
        assertNull(criterion.getTypeHandler());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeneratedCriteriaAddCriterionThrowsException() {
        ClientMigrationFileExample.Criteria criteria = new ClientMigrationFileExample.Criteria();
        criteria.addCriterion(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeneratedCriteriaAddCriterion2ThrowsException() {
        ClientMigrationFileExample.Criteria criteria = new ClientMigrationFileExample.Criteria();
        criteria.addCriterion("id is null", null, "id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeneratedCriteriaAddCriterion3ThrowsException() {
        ClientMigrationFileExample.Criteria criteria = new ClientMigrationFileExample.Criteria();
        criteria.addCriterion("id is null", 1, null, "id");
    }

    @Test
    public void testGeneratedCriteriaBuilder() {
        // If the builder changes/fails, there should be an NPE
        List<Long> idsList = new ArrayList<>();
        idsList.add(22L);
        idsList.add(24L);
        idsList.add(26L);

        List<Long> idsExcludeList = new ArrayList<>();
        idsExcludeList.add(17L);
        idsExcludeList.add(18L);

        ClientMigrationFileExample.Criteria criteria = new ClientMigrationFileExample.Criteria()
                .andIdEqualTo(1L)
                .andIdEqualTo(2L)
                .andIdEqualTo(4L)
                .andIdNotEqualTo(3L)
                .andIdGreaterThan(8L)
                .andIdLessThan(11L)
                .andIdGreaterThanOrEqualTo(15L)
                .andIdLessThanOrEqualTo(20L)
                .andIdIn(idsList)
                .andIdNotIn(idsExcludeList)
                .andIdBetween(50L, 53L)
                .andCreatedAtIsNotNull();

        assertEquals(12, criteria.getAllCriteria().size());
    }
}
