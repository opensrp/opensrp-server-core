package org.opensrp.domain.postgres;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ClientFormExampleTest {

    @Test
    public void testClear() {
        ClientFormExample clientFormExample = new ClientFormExample();
        clientFormExample.or();
        clientFormExample.setOrderByClause("date_created DESC");
        clientFormExample.setDistinct(true);

        assertEquals(1, clientFormExample.getOredCriteria().size());
        assertTrue(clientFormExample.isDistinct());
        assertNotNull(clientFormExample.getOrderByClause());

        clientFormExample.clear();


        assertEquals(0, clientFormExample.getOredCriteria().size());
        assertFalse(clientFormExample.isDistinct());
        assertNull(clientFormExample.getOrderByClause());
    }

    @Test
    public void testCreateCriteria() {
        ClientFormExample clientFormExample = new ClientFormExample();
        clientFormExample.createCriteria();

        assertEquals(1, clientFormExample.getOredCriteria().size());
    }

    @Test
    public void testCriteriaAddIdIsNull() {
        ClientFormExample.Criteria criteria = new ClientFormExample.Criteria()
                .andIdIsNull();

        assertEquals("id is null", criteria.getAllCriteria().get(0).getCondition());
    }

    @Test
    public void testCriteriaIsValid() {
        ClientFormExample.Criteria criteria = new ClientFormExample.Criteria()
                .andIdIsNotNull();

        assertTrue(criteria.isValid());
    }

    @Test
    public void testCriterionGetterSetter() {
        ClientFormExample.Criterion criterion = new ClientFormExample.Criterion("id =", 1);

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
        ClientFormExample.Criterion criterion = new ClientFormExample.Criterion("id in", 1, 2);

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
        ClientFormExample.Criteria criteria = new ClientFormExample.Criteria();
        criteria.addCriterion(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeneratedCriteriaAddCriterion2ThrowsException() {
        ClientFormExample.Criteria criteria = new ClientFormExample.Criteria();
        criteria.addCriterion("id is null", null, "id");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGeneratedCriteriaAddCriterion3ThrowsException() {
        ClientFormExample.Criteria criteria = new ClientFormExample.Criteria();
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

        ClientFormExample.Criteria criteria = new ClientFormExample.Criteria()
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
                .andJsonIsNotNull()
                .andCreatedAtIsNotNull();

        assertEquals(13, criteria.getAllCriteria().size());
    }

    @Test
    public void testGeneratedCriteriaBuilder2() {
        // If the builder changes/fails, there should be an NPE

        ClientFormExample.Criteria criteria = new ClientFormExample.Criteria()
                .andJsonIsNull()
                .andJsonEqualTo("null")
                .andJsonNotEqualTo("{}")
                .andJsonGreaterThan("")
                .andJsonGreaterThanOrEqualTo("{'property': 'empty'}");

        assertEquals(5, criteria.getAllCriteria().size());
    }
}
