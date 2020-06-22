package org.opensrp.domain.postgres;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class ClientFormMetadataExampleTest {

	@Test
	public void testClear() {
		ClientFormMetadataExample clientFormMetadataExample = new ClientFormMetadataExample();
		clientFormMetadataExample.or();
		clientFormMetadataExample.setOrderByClause("date_created DESC");
		clientFormMetadataExample.setDistinct(true);

		assertEquals(1, clientFormMetadataExample.getOredCriteria().size());
		assertTrue(clientFormMetadataExample.isDistinct());
		assertNotNull(clientFormMetadataExample.getOrderByClause());

		clientFormMetadataExample.clear();

		assertEquals(0, clientFormMetadataExample.getOredCriteria().size());
		assertFalse(clientFormMetadataExample.isDistinct());
		assertNull(clientFormMetadataExample.getOrderByClause());
	}

	@Test
	public void testCreateCriteria() {
		ClientFormMetadataExample ClientFormMetadataExample = new ClientFormMetadataExample();
		ClientFormMetadataExample.createCriteria();

		assertEquals(1, ClientFormMetadataExample.getOredCriteria().size());
	}

	@Test
	public void testCriteriaAddIdIsNull() {
		ClientFormMetadataExample.Criteria criteria = new ClientFormMetadataExample.Criteria()
				.andIdIsNull();

		assertEquals("id is null", criteria.getAllCriteria().get(0).getCondition());
	}

	@Test
	public void testCriteriaIsValid() {
		ClientFormMetadataExample.Criteria criteria = new ClientFormMetadataExample.Criteria()
				.andIdIsNotNull();

		assertTrue(criteria.isValid());
	}

	@Test
	public void testCriterionGetterSetter() {
		ClientFormMetadataExample.Criterion criterion = new ClientFormMetadataExample.Criterion("id =", 1);

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
		ClientFormMetadataExample.Criterion criterion = new ClientFormMetadataExample.Criterion("id in", 1, 2);

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
		ClientFormMetadataExample.Criteria criteria = new ClientFormMetadataExample.Criteria();
		criteria.addCriterion(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratedCriteriaAddCriterion2ThrowsException() {
		ClientFormMetadataExample.Criteria criteria = new ClientFormMetadataExample.Criteria();
		criteria.addCriterion("id is null", null, "id");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGeneratedCriteriaAddCriterion3ThrowsException() {
		ClientFormMetadataExample.Criteria criteria = new ClientFormMetadataExample.Criteria();
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

		ClientFormMetadataExample.Criteria criteria = new ClientFormMetadataExample.Criteria()
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

	@Test
	public void testGeneratedCriteriaBuilder2() {
		// If the builder changes/fails, there should be an NPE

		ClientFormMetadataExample.Criteria criteria = new ClientFormMetadataExample.Criteria()
				.andIdentifierIsNull()
				.andIdentifierIsNotNull()
				.andIdentifierEqualTo("anc/json")
				.andIdentifierNotEqualTo("maternity/json")
				.andIdentifierGreaterThan("anc/json")
				.andIdentifierGreaterThanOrEqualTo("child/json")
				.andIdentifierLessThan("child/json")
				.andIdentifierLessThanOrEqualTo("child/json")
				.andIdentifierLike("child/json")
				.andIdentifierNotLike("child/json")
				.andIdentifierIn(new ArrayList<>())
				.andIdentifierNotIn(new ArrayList<>())
				.andIdentifierBetween("1", "10")
				.andIdentifierNotBetween("4", "7")
				.andJurisdictionIsNotNull()
				.andJurisdictionIsNull()
				.andJurisdictionEqualTo("kenya")
				.andJurisdictionNotEqualTo("kenya")
				.andJurisdictionNotBetween("rwanda", "uganda")
				.andJurisdictionLessThan("sa")
				.andJurisdictionLessThanOrEqualTo("sa")
				.andJurisdictionGreaterThan("sa")
				.andJurisdictionGreaterThanOrEqualTo("sa")
				.andJurisdictionLike("%a%")
				.andJurisdictionNotLike("%a%")
				.andJurisdictionIn(new ArrayList<>())
				.andJurisdictionNotIn(new ArrayList<>())
				.andJurisdictionBetween("a", "z")
				.andJurisdictionNotBetween("ba", "chw")
				.andVersionIsNull()
				.andVersionIsNotNull()
				.andVersionEqualTo("0.0.1")
				.andVersionNotEqualTo("0.0.2")
				.andVersionGreaterThan("0.0.3")
				.andVersionGreaterThanOrEqualTo("0.0.3")
				.andIsDraftIsNull()
				.andIsDraftIsNotNull()
				.andIsDraftEqualTo(true)
				.andIsDraftGreaterThan(false)
				.andIsDraftGreaterThanOrEqualTo(false)
				.andIsDraftLessThan(true)
				.andIsDraftLessThan(true)
				.andIsDraftLessThanOrEqualTo(true)
				.andIsDraftIn(new ArrayList<>())
				.andIsDraftBetween(true, false)
				.andIsDraftNotBetween(true, false)
				.andIsJsonValidatorIsNull()
				.andIsJsonValidatorIsNotNull()
				.andIsJsonValidatorEqualTo(true)
				.andIsJsonValidatorNotEqualTo(true)
				.andIsJsonValidatorGreaterThan(false)
				.andIsJsonValidatorGreaterThanOrEqualTo(false)
				.andIsJsonValidatorLessThan(true)
				.andIsJsonValidatorLessThanOrEqualTo(true)
				.andIsJsonValidatorIn(new ArrayList<>())
				.andIsJsonValidatorNotIn(new ArrayList<>())
				.andRelationIsNull()
				.andRelationIsNotNull()
				.andRelationEqualTo("anc/child.json")
				.andRelationLike("%a%")
				.andRelationNotLike("%a%")
				.andRelationIn(new ArrayList<>())
				.andRelationNotIn(new ArrayList<>())
				.andRelationBetween("child", "registration")
				.andRelationNotBetween("child", "registration")
				.andRelationLessThan("child")
				.andRelationLessThanOrEqualTo("child")
				.andRelationGreaterThan("child")
				.andRelationGreaterThanOrEqualTo("child");

		assertEquals(69, criteria.getAllCriteria().size());
	}

}
