package org.opensrp.domain.postgres;

import java.util.Date;

public class CustomClient extends Client {
	
	private Date lastContactDate;
	
	private Date edd;
	
	private String gestationalAge;
	
	private String riskCategory;
	
	private String immunizationStatus;
	
	private String registrationStatus;
	
	private int ageYearPart;
	
	private int ageMonthPart;
	
	public String getRegistrationStatus() {
		return registrationStatus;
	}
	
	public void setRegistrationStatus(String registrationStatus) {
		this.registrationStatus = registrationStatus;
	}
	
	public int getAgeYearPart() {
		return ageYearPart;
	}
	
	public void setAgeYearPart(int ageYearPart) {
		this.ageYearPart = ageYearPart;
	}
	
	public int getAgeMonthPart() {
		return ageMonthPart;
	}
	
	public void setAgeMonthPart(int ageMonthPart) {
		this.ageMonthPart = ageMonthPart;
	}
	
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
	
	@Override
	public String toString() {
		return "CustomClient [lastContactDate=" + lastContactDate + ", edd=" + edd + ", gestationalAge=" + gestationalAge
		        + ", riskCategory=" + riskCategory + ", immunizationStatus=" + immunizationStatus + "]";
	}
	
}
