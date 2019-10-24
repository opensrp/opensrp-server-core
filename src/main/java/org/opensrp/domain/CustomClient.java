package org.opensrp.domain;

import java.util.Date;

public class CustomClient extends Client {
	
	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	
	private Date lastContactDate;
	
	private Date edd;
	
	private String gestationalAge;
	
	private String riskCategory;
	
	private String immunizationStatus;
	
	public Date getLastContactDate() {
		return lastContactDate;
	}
	
	public void setLastContactDate(Date lastContactDate) {
		this.lastContactDate = lastContactDate;
	}
	
	public Date getEdd() {
		return edd;
	}
	
	public void setEdd(Date edd) {
		this.edd = edd;
	}
	
	public String getGestationalAge() {
		return gestationalAge;
	}
	
	public void setGestationalAge(String gestationalAge) {
		this.gestationalAge = gestationalAge;
	}
	
	public String getRiskCategory() {
		return riskCategory;
	}
	
	public void setRiskCategory(String riskCategory) {
		this.riskCategory = riskCategory;
	}
	
	public String getImmunizationStatus() {
		return immunizationStatus;
	}
	
	public void setImmunizationStatus(String immunizationStatus) {
		this.immunizationStatus = immunizationStatus;
	}
	
}
