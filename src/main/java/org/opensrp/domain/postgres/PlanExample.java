package org.opensrp.domain.postgres;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanExample {
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	protected String orderByClause;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	protected boolean distinct;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	protected List<Criteria> oredCriteria;
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public PlanExample() {
		oredCriteria = new ArrayList<>();
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public String getOrderByClause() {
		return orderByClause;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public boolean isDistinct() {
		return distinct;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public Criteria createCriteria() {
		Criteria criteria = createCriteriaInternal();
		if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		}
		return criteria;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}
	
	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
	 */
	protected abstract static class GeneratedCriteria {
		
		protected List<Criterion> jsonCriteria;
		
		protected List<Criterion> allCriteria;
		
		protected List<Criterion> criteria;
		
		protected GeneratedCriteria() {
			super();
			criteria = new ArrayList<>();
			jsonCriteria = new ArrayList<>();
		}
		
		public List<Criterion> getJsonCriteria() {
			return jsonCriteria;
		}
		
		protected void addJsonCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new IllegalArgumentException("Value for " + property + " cannot be null");
			}
			jsonCriteria.add(new Criterion(condition, value, "org.opensrp.repository.postgres.handler.PlanTypeHandler"));
			allCriteria = null;
		}
		
		protected void addJsonCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new IllegalArgumentException("Between values for " + property + " cannot be null");
			}
			jsonCriteria.add(
			    new Criterion(condition, value1, value2, "org.opensrp.repository.postgres.handler.PlanTypeHandler"));
			allCriteria = null;
		}
		
		public boolean isValid() {
			return criteria.size() > 0 || jsonCriteria.size() > 0;
		}
		
		public List<Criterion> getAllCriteria() {
			if (allCriteria == null) {
				allCriteria = new ArrayList<>();
				allCriteria.addAll(criteria);
				allCriteria.addAll(jsonCriteria);
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
		}
		
		protected void addCriterion(String condition, Object value, String property) {
			if (value == null) {
				throw new IllegalArgumentException("Value for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value));
		}
		
		protected void addCriterion(String condition, Object value1, Object value2, String property) {
			if (value1 == null || value2 == null) {
				throw new IllegalArgumentException("Between values for " + property + " cannot be null");
			}
			criteria.add(new Criterion(condition, value1, value2));
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
		
		public Criteria andJsonIsNull() {
			addCriterion("json is null");
			return (Criteria) this;
		}
		
		public Criteria andJsonIsNotNull() {
			addCriterion("json is not null");
			return (Criteria) this;
		}
		
		public Criteria andJsonEqualTo(Object value) {
			addCriterion("json =", value, "json");
			return (Criteria) this;
		}
		
		public Criteria andJsonNotEqualTo(Object value) {
			addCriterion("json <>", value, "json");
			return (Criteria) this;
		}
		
		public Criteria andJsonGreaterThan(Object value) {
			addCriterion("json >", value, "json");
			return (Criteria) this;
		}
		
		public Criteria andJsonGreaterThanOrEqualTo(Object value) {
			addCriterion("json >=", value, "json");
			return (Criteria) this;
		}
		
		public Criteria andJsonLessThan(Object value) {
			addCriterion("json <", value, "json");
			return (Criteria) this;
		}
		
		public Criteria andJsonLessThanOrEqualTo(Object value) {
			addCriterion("json <=", value, "json");
			return (Criteria) this;
		}
		
		public Criteria andJsonIn(List<Object> values) {
			addCriterion("json in", values, "json");
			return (Criteria) this;
		}
		
		public Criteria andJsonNotIn(List<Object> values) {
			addCriterion("json not in", values, "json");
			return (Criteria) this;
		}
		
		public Criteria andJsonBetween(Object value1, Object value2) {
			addCriterion("json between", value1, value2, "json");
			return (Criteria) this;
		}
		
		public Criteria andJsonNotBetween(Object value1, Object value2) {
			addCriterion("json not between", value1, value2, "json");
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
		
		public Criteria andExperimentalIsNull() {
			addCriterion("experimental is null");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalIsNotNull() {
			addCriterion("experimental is not null");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalEqualTo(Boolean value) {
			addCriterion("experimental =", value, "experimental");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalNotEqualTo(Boolean value) {
			addCriterion("experimental <>", value, "experimental");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalGreaterThan(Boolean value) {
			addCriterion("experimental >", value, "experimental");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalGreaterThanOrEqualTo(Boolean value) {
			addCriterion("experimental >=", value, "experimental");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalLessThan(Boolean value) {
			addCriterion("experimental <", value, "experimental");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalLessThanOrEqualTo(Boolean value) {
			addCriterion("experimental <=", value, "experimental");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalIn(List<Boolean> values) {
			addCriterion("experimental in", values, "experimental");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalNotIn(List<Boolean> values) {
			addCriterion("experimental not in", values, "experimental");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalBetween(Boolean value1, Boolean value2) {
			addCriterion("experimental between", value1, value2, "experimental");
			return (Criteria) this;
		}
		
		public Criteria andExperimentalNotBetween(Boolean value1, Boolean value2) {
			addCriterion("experimental not between", value1, value2, "experimental");
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
	}
	
	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated do_not_delete_during_merge Wed Sep 30 15:22:34 EAT 2020
	 */
	public static class Criteria extends GeneratedCriteria {
		
		protected Criteria() {
			super();
		}
	}
	
	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table
	 * core.plan
	 *
	 * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
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
