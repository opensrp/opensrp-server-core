/**
 * 
 */
package org.opensrp.domain;

import java.util.Date;

/**
 * @author Samuel Githengi created on 09/10/19
 */
public class AssignedLocations {

	private String jurisdictionId;

	private String planId;

	private Date fromDate;

	private Date toDate;

	public AssignedLocations(String jurisdictionId, String planId) {
		setJurisdictionId(jurisdictionId);
		setPlanId(planId);
	}
	
	public AssignedLocations() {//Default constructor, needed by mybatis
	}

	public String getJurisdictionId() {
		return jurisdictionId;
	}

	public void setJurisdictionId(String jurisdictionId) {
		this.jurisdictionId = jurisdictionId;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

}
