package org.opensrp.domain.postgres;

import java.util.ArrayList;
import java.util.List;

public class PlanMetadataExample {
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	protected String orderByClause;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	protected boolean distinct;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	protected List<Criteria> oredCriteria;
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	public PlanMetadataExample() {
		oredCriteria = new ArrayList<Criteria>();
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 *
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	public String getOrderByClause() {
		return orderByClause;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 *
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 *
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	public boolean isDistinct() {
		return distinct;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 *
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	public void or(Criteria criteria) {
		oredCriteria.add(criteria);
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	public Criteria or() {
		Criteria criteria = createCriteriaInternal();
		oredCriteria.add(criteria);
		return criteria;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
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
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	protected Criteria createCriteriaInternal() {
		Criteria criteria = new Criteria();
		return criteria;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}
	
	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
	 */
	protected abstract static class GeneratedCriteria {
		
		protected List<Criterion> criteria;
		
		protected GeneratedCriteria() {
			super();
			criteria = new ArrayList<Criterion>();
		}
		
		public boolean isValid() {
			return criteria.size() > 0;
		}
		
		public List<Criterion> getAllCriteria() {
			return criteria;
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
		
		public Criteria andOperationalAreaIdIsNull() {
			addCriterion("operational_area_id is null");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdIsNotNull() {
			addCriterion("operational_area_id is not null");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdEqualTo(String value) {
			addCriterion("operational_area_id =", value, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdNotEqualTo(String value) {
			addCriterion("operational_area_id <>", value, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdGreaterThan(String value) {
			addCriterion("operational_area_id >", value, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdGreaterThanOrEqualTo(String value) {
			addCriterion("operational_area_id >=", value, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdLessThan(String value) {
			addCriterion("operational_area_id <", value, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdLessThanOrEqualTo(String value) {
			addCriterion("operational_area_id <=", value, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdLike(String value) {
			addCriterion("operational_area_id like", value, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdNotLike(String value) {
			addCriterion("operational_area_id not like", value, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdIn(List<String> values) {
			addCriterion("operational_area_id in", values, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdNotIn(List<String> values) {
			addCriterion("operational_area_id not in", values, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdBetween(String value1, String value2) {
			addCriterion("operational_area_id between", value1, value2, "operationalAreaId");
			return (Criteria) this;
		}
		
		public Criteria andOperationalAreaIdNotBetween(String value1, String value2) {
			addCriterion("operational_area_id not between", value1, value2, "operationalAreaId");
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
		
		public Criteria andPlanIdIsNull() {
			addCriterion("plan_id is null");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdIsNotNull() {
			addCriterion("plan_id is not null");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdEqualTo(Long value) {
			addCriterion("plan_id =", value, "planId");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdNotEqualTo(Long value) {
			addCriterion("plan_id <>", value, "planId");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdGreaterThan(Long value) {
			addCriterion("plan_id >", value, "planId");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdGreaterThanOrEqualTo(Long value) {
			addCriterion("plan_id >=", value, "planId");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdLessThan(Long value) {
			addCriterion("plan_id <", value, "planId");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdLessThanOrEqualTo(Long value) {
			addCriterion("plan_id <=", value, "planId");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdIn(List<Long> values) {
			addCriterion("plan_id in", values, "planId");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdNotIn(List<Long> values) {
			addCriterion("plan_id not in", values, "planId");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdBetween(Long value1, Long value2) {
			addCriterion("plan_id between", value1, value2, "planId");
			return (Criteria) this;
		}
		
		public Criteria andPlanIdNotBetween(Long value1, Long value2) {
			addCriterion("plan_id not between", value1, value2, "planId");
			return (Criteria) this;
		}
	}
	
	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table
	 * core.plan_metadata
	 * 
	 * @mbg.generated Fri Aug 30 12:59:38 EAT 2019
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
	}
	
	/**
	 * This class was generated by MyBatis Generator. This class corresponds to the database table
	 * core.plan_metadata
	 *
	 * @mbg.generated do_not_delete_during_merge Fri May 10 11:11:46 EAT 2019
	 */
	public static class Criteria extends GeneratedCriteria {
		
		protected Criteria() {
			super();
		}
	}
}
