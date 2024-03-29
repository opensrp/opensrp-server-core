package org.opensrp.domain.postgres;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrganizationExample {

	/**
	 * This field was generated by MyBatis Generator.
	 * This field corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	protected String orderByClause;

	/**
	 * This field was generated by MyBatis Generator.
	 * This field corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	protected boolean distinct;

	/**
	 * This field was generated by MyBatis Generator.
	 * This field corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	protected List<Criteria> oredCriteria;

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public OrganizationExample() {
		oredCriteria = new ArrayList<Criteria>();
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public String getOrderByClause() {
		return orderByClause;
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public boolean isDistinct() {
		return distinct;
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}

	/**
	 * This method was generated by MyBatis Generator.
	 * This method corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

	/**
	 * This class was generated by MyBatis Generator.
	 * This class corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	protected abstract static class GeneratedCriteria {
		protected List<Criterion> typeCriteria;
		protected List<Criterion> allCriteria;
		protected List<Criterion> criteria;

		protected GeneratedCriteria() {
			super();
			criteria = new ArrayList<Criterion>();
			typeCriteria = new ArrayList<Criterion>();
		}

		public List<Criterion> getTypeCriteria() {
			return typeCriteria;
		}

		protected void addTypeCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new IllegalArgumentException("Value for " + property + " cannot be null");
			}
			typeCriteria.add(
					new Criterion(condition, value, "org.opensrp.repository.postgres.handler.CodeSystemTypeHandler"));
			allCriteria = null;
		}

		protected void addTypeCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new IllegalArgumentException("Between values for " + property + " cannot be null");
			}
			typeCriteria.add(new Criterion(condition, value1, value2,
					"org.opensrp.repository.postgres.handler.CodeSystemTypeHandler"));
			allCriteria = null;
		}

		public boolean isValid() {
			return criteria.size() > 0 || typeCriteria.size() > 0;
		}

		public List<Criterion> getAllCriteria() {
			if (allCriteria == null) {
				allCriteria = new ArrayList<Criterion>();
				allCriteria.addAll(criteria);
				allCriteria.addAll(typeCriteria);
			}
			return allCriteria;
		}

		public List<Criterion> getCriteria() {
			return criteria;
		}

		protected void addCriterion(String condition) {
			if (condition == null) {
				throw new IllegalArgumentException("Value for condition cannot be null");
			}
			criteria.add(new Criterion(condition));
			allCriteria = null;
		}

		protected void addCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new IllegalArgumentException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value));
			allCriteria = null;
		}

		protected void addCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new IllegalArgumentException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2));
			allCriteria = null;
		}

		public Criteria andIdIsNull() {
			addCriterion("id is null");
			return (Criteria) this;
		}

		public Criteria andIdIsNotNull() {
			addCriterion("id is not null");
			return (Criteria) this;
		}

		public Criteria andIdEqualTo(Long value) {
			addCriterion("id =", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdNotEqualTo(Long value) {
			addCriterion("id <>", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdGreaterThan(Long value) {
			addCriterion("id >", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdGreaterThanOrEqualTo(Long value) {
			addCriterion("id >=", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdLessThan(Long value) {
			addCriterion("id <", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdLessThanOrEqualTo(Long value) {
			addCriterion("id <=", value, "id");
			return (Criteria) this;
		}

		public Criteria andIdIn(List<Long> values) {
			addCriterion("id in", values, "id");
			return (Criteria) this;
		}

		public Criteria andIdNotIn(List<Long> values) {
			addCriterion("id not in", values, "id");
			return (Criteria) this;
		}

		public Criteria andIdBetween(Long value1, Long value2) {
			addCriterion("id between", value1, value2, "id");
			return (Criteria) this;
		}

		public Criteria andIdNotBetween(Long value1, Long value2) {
			addCriterion("id not between", value1, value2, "id");
			return (Criteria) this;
		}

		public Criteria andIdentifierIsNull() {
			addCriterion("identifier is null");
			return (Criteria) this;
		}

		public Criteria andIdentifierIsNotNull() {
			addCriterion("identifier is not null");
			return (Criteria) this;
		}

		public Criteria andIdentifierEqualTo(String value) {
			addCriterion("identifier =", value, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierNotEqualTo(String value) {
			addCriterion("identifier <>", value, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierGreaterThan(String value) {
			addCriterion("identifier >", value, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierGreaterThanOrEqualTo(String value) {
			addCriterion("identifier >=", value, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierLessThan(String value) {
			addCriterion("identifier <", value, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierLessThanOrEqualTo(String value) {
			addCriterion("identifier <=", value, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierLike(String value) {
			addCriterion("identifier like", value, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierNotLike(String value) {
			addCriterion("identifier not like", value, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierIn(List<String> values) {
			addCriterion("identifier in", values, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierNotIn(List<String> values) {
			addCriterion("identifier not in", values, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierBetween(String value1, String value2) {
			addCriterion("identifier between", value1, value2, "identifier");
			return (Criteria) this;
		}

		public Criteria andIdentifierNotBetween(String value1, String value2) {
			addCriterion("identifier not between", value1, value2, "identifier");
			return (Criteria) this;
		}

		public Criteria andActiveIsNull() {
			addCriterion("active is null");
			return (Criteria) this;
		}

		public Criteria andActiveIsNotNull() {
			addCriterion("active is not null");
			return (Criteria) this;
		}

		public Criteria andActiveEqualTo(Boolean value) {
			addCriterion("active =", value, "active");
			return (Criteria) this;
		}

		public Criteria andActiveNotEqualTo(Boolean value) {
			addCriterion("active <>", value, "active");
			return (Criteria) this;
		}

		public Criteria andActiveGreaterThan(Boolean value) {
			addCriterion("active >", value, "active");
			return (Criteria) this;
		}

		public Criteria andActiveGreaterThanOrEqualTo(Boolean value) {
			addCriterion("active >=", value, "active");
			return (Criteria) this;
		}

		public Criteria andActiveLessThan(Boolean value) {
			addCriterion("active <", value, "active");
			return (Criteria) this;
		}

		public Criteria andActiveLessThanOrEqualTo(Boolean value) {
			addCriterion("active <=", value, "active");
			return (Criteria) this;
		}

		public Criteria andActiveIn(List<Boolean> values) {
			addCriterion("active in", values, "active");
			return (Criteria) this;
		}

		public Criteria andActiveNotIn(List<Boolean> values) {
			addCriterion("active not in", values, "active");
			return (Criteria) this;
		}

		public Criteria andActiveBetween(Boolean value1, Boolean value2) {
			addCriterion("active between", value1, value2, "active");
			return (Criteria) this;
		}

		public Criteria andActiveNotBetween(Boolean value1, Boolean value2) {
			addCriterion("active not between", value1, value2, "active");
			return (Criteria) this;
		}

		public Criteria andNameIsNull() {
			addCriterion("name is null");
			return (Criteria) this;
		}

		public Criteria andNameIsNotNull() {
			addCriterion("name is not null");
			return (Criteria) this;
		}

		public Criteria andNameEqualTo(String value) {
			addCriterion("name =", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameNotEqualTo(String value) {
			addCriterion("name <>", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameGreaterThan(String value) {
			addCriterion("name >", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameGreaterThanOrEqualTo(String value) {
			addCriterion("name >=", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameLessThan(String value) {
			addCriterion("name <", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameLessThanOrEqualTo(String value) {
			addCriterion("name <=", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameLike(String value) {
			addCriterion("name like", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameNotLike(String value) {
			addCriterion("name not like", value, "name");
			return (Criteria) this;
		}

		public Criteria andNameIn(List<String> values) {
			addCriterion("name in", values, "name");
			return (Criteria) this;
		}

		public Criteria andNameNotIn(List<String> values) {
			addCriterion("name not in", values, "name");
			return (Criteria) this;
		}

		public Criteria andNameBetween(String value1, String value2) {
			addCriterion("name between", value1, value2, "name");
			return (Criteria) this;
		}

		public Criteria andNameNotBetween(String value1, String value2) {
			addCriterion("name not between", value1, value2, "name");
			return (Criteria) this;
		}

		public Criteria andTypeIsNull() {
			addCriterion("type is null");
			return (Criteria) this;
		}

		public Criteria andTypeIsNotNull() {
			addCriterion("type is not null");
			return (Criteria) this;
		}

		public Criteria andTypeEqualTo(Object value) {
			addTypeCriterion("type =", value, "type");
			return (Criteria) this;
		}

		public Criteria andTypeNotEqualTo(Object value) {
			addTypeCriterion("type <>", value, "type");
			return (Criteria) this;
		}

		public Criteria andTypeGreaterThan(Object value) {
			addTypeCriterion("type >", value, "type");
			return (Criteria) this;
		}

		public Criteria andTypeGreaterThanOrEqualTo(Object value) {
			addTypeCriterion("type >=", value, "type");
			return (Criteria) this;
		}

		public Criteria andTypeLessThan(Object value) {
			addTypeCriterion("type <", value, "type");
			return (Criteria) this;
		}

		public Criteria andTypeLessThanOrEqualTo(Object value) {
			addTypeCriterion("type <=", value, "type");
			return (Criteria) this;
		}

		public Criteria andTypeIn(List<Object> values) {
			addTypeCriterion("type in", values, "type");
			return (Criteria) this;
		}

		public Criteria andTypeNotIn(List<Object> values) {
			addTypeCriterion("type not in", values, "type");
			return (Criteria) this;
		}

		public Criteria andTypeBetween(Object value1, Object value2) {
			addTypeCriterion("type between", value1, value2, "type");
			return (Criteria) this;
		}

		public Criteria andTypeNotBetween(Object value1, Object value2) {
			addTypeCriterion("type not between", value1, value2, "type");
			return (Criteria) this;
		}

		public Criteria andDateDeletedIsNull() {
			addCriterion("date_deleted is null");
			return (Criteria) this;
		}

		public Criteria andDateDeletedIsNotNull() {
			addCriterion("date_deleted is not null");
			return (Criteria) this;
		}

		public Criteria andDateDeletedEqualTo(Date value) {
			addCriterion("date_deleted =", value, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andDateDeletedNotEqualTo(Date value) {
			addCriterion("date_deleted <>", value, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andDateDeletedGreaterThan(Date value) {
			addCriterion("date_deleted >", value, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andDateDeletedGreaterThanOrEqualTo(Date value) {
			addCriterion("date_deleted >=", value, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andDateDeletedLessThan(Date value) {
			addCriterion("date_deleted <", value, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andDateDeletedLessThanOrEqualTo(Date value) {
			addCriterion("date_deleted <=", value, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andDateDeletedIn(List<Date> values) {
			addCriterion("date_deleted in", values, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andDateDeletedNotIn(List<Date> values) {
			addCriterion("date_deleted not in", values, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andDateDeletedBetween(Date value1, Date value2) {
			addCriterion("date_deleted between", value1, value2, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andDateDeletedNotBetween(Date value1, Date value2) {
			addCriterion("date_deleted not between", value1, value2, "dateDeleted");
			return (Criteria) this;
		}

		public Criteria andParentIdIsNull() {
			addCriterion("parent_id is null");
			return (Criteria) this;
		}

		public Criteria andParentIdIsNotNull() {
			addCriterion("parent_id is not null");
			return (Criteria) this;
		}

		public Criteria andParentIdEqualTo(Long value) {
			addCriterion("parent_id =", value, "parentId");
			return (Criteria) this;
		}

		public Criteria andParentIdNotEqualTo(Long value) {
			addCriterion("parent_id <>", value, "parentId");
			return (Criteria) this;
		}

		public Criteria andParentIdGreaterThan(Long value) {
			addCriterion("parent_id >", value, "parentId");
			return (Criteria) this;
		}

		public Criteria andParentIdGreaterThanOrEqualTo(Long value) {
			addCriterion("parent_id >=", value, "parentId");
			return (Criteria) this;
		}

		public Criteria andParentIdLessThan(Long value) {
			addCriterion("parent_id <", value, "parentId");
			return (Criteria) this;
		}

		public Criteria andParentIdLessThanOrEqualTo(Long value) {
			addCriterion("parent_id <=", value, "parentId");
			return (Criteria) this;
		}

		public Criteria andParentIdIn(List<Long> values) {
			addCriterion("parent_id in", values, "parentId");
			return (Criteria) this;
		}

		public Criteria andParentIdNotIn(List<Long> values) {
			addCriterion("parent_id not in", values, "parentId");
			return (Criteria) this;
		}

		public Criteria andParentIdBetween(Long value1, Long value2) {
			addCriterion("parent_id between", value1, value2, "parentId");
			return (Criteria) this;
		}

		public Criteria andParentIdNotBetween(Long value1, Long value2) {
			addCriterion("parent_id not between", value1, value2, "parentId");
			return (Criteria) this;
		}

		public Criteria andDateCreatedIsNull() {
			addCriterion("date_created is null");
			return (Criteria) this;
		}

		public Criteria andDateCreatedIsNotNull() {
			addCriterion("date_created is not null");
			return (Criteria) this;
		}

		public Criteria andDateCreatedEqualTo(Date value) {
			addCriterion("date_created =", value, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateCreatedNotEqualTo(Date value) {
			addCriterion("date_created <>", value, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateCreatedGreaterThan(Date value) {
			addCriterion("date_created >", value, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateCreatedGreaterThanOrEqualTo(Date value) {
			addCriterion("date_created >=", value, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateCreatedLessThan(Date value) {
			addCriterion("date_created <", value, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateCreatedLessThanOrEqualTo(Date value) {
			addCriterion("date_created <=", value, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateCreatedIn(List<Date> values) {
			addCriterion("date_created in", values, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateCreatedNotIn(List<Date> values) {
			addCriterion("date_created not in", values, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateCreatedBetween(Date value1, Date value2) {
			addCriterion("date_created between", value1, value2, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateCreatedNotBetween(Date value1, Date value2) {
			addCriterion("date_created not between", value1, value2, "dateCreated");
			return (Criteria) this;
		}

		public Criteria andDateEditedIsNull() {
			addCriterion("date_edited is null");
			return (Criteria) this;
		}

		public Criteria andDateEditedIsNotNull() {
			addCriterion("date_edited is not null");
			return (Criteria) this;
		}

		public Criteria andDateEditedEqualTo(Date value) {
			addCriterion("date_edited =", value, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andDateEditedNotEqualTo(Date value) {
			addCriterion("date_edited <>", value, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andDateEditedGreaterThan(Date value) {
			addCriterion("date_edited >", value, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andDateEditedGreaterThanOrEqualTo(Date value) {
			addCriterion("date_edited >=", value, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andDateEditedLessThan(Date value) {
			addCriterion("date_edited <", value, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andDateEditedLessThanOrEqualTo(Date value) {
			addCriterion("date_edited <=", value, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andDateEditedIn(List<Date> values) {
			addCriterion("date_edited in", values, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andDateEditedNotIn(List<Date> values) {
			addCriterion("date_edited not in", values, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andDateEditedBetween(Date value1, Date value2) {
			addCriterion("date_edited between", value1, value2, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andDateEditedNotBetween(Date value1, Date value2) {
			addCriterion("date_edited not between", value1, value2, "dateEdited");
			return (Criteria) this;
		}

		public Criteria andServerVersionIsNull() {
			addCriterion("server_version is null");
			return (Criteria) this;
		}

		public Criteria andServerVersionIsNotNull() {
			addCriterion("server_version is not null");
			return (Criteria) this;
		}

		public Criteria andServerVersionEqualTo(Long value) {
			addCriterion("server_version =", value, "serverVersion");
			return (Criteria) this;
		}

		public Criteria andServerVersionNotEqualTo(Long value) {
			addCriterion("server_version <>", value, "serverVersion");
			return (Criteria) this;
		}

		public Criteria andServerVersionGreaterThan(Long value) {
			addCriterion("server_version >", value, "serverVersion");
			return (Criteria) this;
		}

		public Criteria andServerVersionGreaterThanOrEqualTo(Long value) {
			addCriterion("server_version >=", value, "serverVersion");
			return (Criteria) this;
		}

		public Criteria andServerVersionLessThan(Long value) {
			addCriterion("server_version <", value, "serverVersion");
			return (Criteria) this;
		}

		public Criteria andServerVersionLessThanOrEqualTo(Long value) {
			addCriterion("server_version <=", value, "serverVersion");
			return (Criteria) this;
		}

		public Criteria andServerVersionIn(List<Long> values) {
			addCriterion("server_version in", values, "serverVersion");
			return (Criteria) this;
		}

		public Criteria andServerVersionNotIn(List<Long> values) {
			addCriterion("server_version not in", values, "serverVersion");
			return (Criteria) this;
		}

		public Criteria andServerVersionBetween(Long value1, Long value2) {
			addCriterion("server_version between", value1, value2, "serverVersion");
			return (Criteria) this;
		}

		public Criteria andServerVersionNotBetween(Long value1, Long value2) {
			addCriterion("server_version not between", value1, value2, "serverVersion");
			return (Criteria) this;
		}
	}

	/**
	 * This class was generated by MyBatis Generator.
	 * This class corresponds to the database table team.organization
	 *
	 * @mbg.generated do_not_delete_during_merge Mon Dec 06 14:53:20 EAT 2021
	 */
	public static class Criteria extends GeneratedCriteria {

		protected Criteria() {
			super();
		}
	}

	/**
	 * This class was generated by MyBatis Generator.
	 * This class corresponds to the database table team.organization
	 *
	 * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
	 */
	public static class Criterion {

		private String condition;

		private Object value;

		private Object secondValue;

		private boolean noValue;

		private boolean singleValue;

		private boolean betweenValue;

		private boolean listValue;

		private String typeHandler;

		public String getCondition() {
			return condition;
		}

		public Object getValue() {
			return value;
		}

		public Object getSecondValue() {
			return secondValue;
		}

		public boolean isNoValue() {
			return noValue;
		}

		public boolean isSingleValue() {
			return singleValue;
		}

		public boolean isBetweenValue() {
			return betweenValue;
		}

		public boolean isListValue() {
			return listValue;
		}

		public String getTypeHandler() {
			return typeHandler;
		}

		protected Criterion(String condition) {
			super();
			this.condition = condition;
			this.typeHandler = null;
			this.noValue = true;
		}

		protected Criterion(String condition, Object value, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.typeHandler = typeHandler;
			if (value instanceof List<?>) {
				this.listValue = true;
			} else {
				this.singleValue = true;
			}
		}

		protected Criterion(String condition, Object value) {
			this(condition, value, null);
		}

		protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
			super();
			this.condition = condition;
			this.value = value;
			this.secondValue = secondValue;
			this.typeHandler = typeHandler;
			this.betweenValue = true;
		}

		protected Criterion(String condition, Object value, Object secondValue) {
			this(condition, value, secondValue, null);
		}
	}
}
