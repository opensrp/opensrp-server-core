package org.opensrp.domain;

import org.joda.time.DateTime;
import org.smartregister.domain.Client;

public class RapidProContact implements Comparable<Client> {
	
	private String motherFirstName;
	
	private String motherSecondName;
	
	private String motherZeirID;
	
	private String motherTel;
	
	private String homeFacility;
	
	private String residentialArea;
	
	private String homeAddress;
	
	private String landMark;
	
	private String birthDate;
	
	private String dateJoined;
	
	private Long serverVersion;
	
	private String childName;
	
	private String zeirID;
	
	private String mvaccUuid;
	
	private String baseEntityId;
	
	private String c2dob;
	
	private String c2zeir;
	
	private String c2name;
	
	private String c3dob;
	
	private String c3zeir;
	
	private String c3name;
	
	private DateTime dateCreated;
	
	public String getMotherFirstName() {
		return motherFirstName;
	}
	
	public void setMotherFirstName(String motherFirstName) {
		this.motherFirstName = motherFirstName;
	}
	
	public String getMotherSecondName() {
		return motherSecondName;
	}
	
	public void setMotherSecondName(String motherSecondName) {
		this.motherSecondName = motherSecondName;
	}
	
	public String getMotherZeirID() {
		return motherZeirID;
	}
	
	public void setMotherZeirID(String motherZeirID) {
		this.motherZeirID = motherZeirID;
	}
	
	public String getMotherTel() {
		return motherTel;
	}
	
	public void setMotherTel(String motherTel) {
		this.motherTel = motherTel;
	}
	
	public String getHomeFacility() {
		return homeFacility;
	}
	
	public void setHomeFacility(String homeFacility) {
		this.homeFacility = homeFacility;
	}
	
	public String getResidentialArea() {
		return residentialArea;
	}
	
	public void setResidentialArea(String residentialArea) {
		this.residentialArea = residentialArea;
	}
	
	public String getHomeAddress() {
		return homeAddress;
	}
	
	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}
	
	public String getLandMark() {
		return landMark;
	}
	
	public void setLandMark(String landMark) {
		this.landMark = landMark;
	}
	
	public String getBirthDate() {
		return birthDate;
	}
	
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	
	public String getDateJoined() {
		return dateJoined;
	}
	
	public void setDateJoined(String dateJoined) {
		this.dateJoined = dateJoined;
	}
	
	public Long getServerVersion() {
		return serverVersion;
	}
	
	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}
	
	public String getChildName() {
		return childName;
	}
	
	public void setChildName(String childName) {
		this.childName = childName;
	}
	
	public String getMvaccUuid() {
		return mvaccUuid;
	}
	
	public void setMvaccUuid(String mvaccUuid) {
		this.mvaccUuid = mvaccUuid;
	}
	
	public String getBaseEntityId() {
		return baseEntityId;
	}
	
	public void setBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
	}
	
	public String getC2dob() {
		return c2dob;
	}
	
	public void setC2dob(String c2dob) {
		this.c2dob = c2dob;
	}
	
	public String getC2name() {
		return c2name;
	}
	
	public void setC2name(String c2name) {
		this.c2name = c2name;
	}
	
	public String getC3dob() {
		return c3dob;
	}
	
	public void setC3dob(String c3dob) {
		this.c3dob = c3dob;
	}
	
	public String getC3name() {
		return c3name;
	}
	
	public void setC3name(String c3name) {
		this.c3name = c3name;
	}
	
	public DateTime getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(DateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public String getZeirID() {
		return zeirID;
	}
	
	public void setZeirID(String zeirID) {
		this.zeirID = zeirID;
	}
	
	public String getC2zeir() {
		return c2zeir;
	}
	
	public void setC2zeir(String c2zeir) {
		this.c2zeir = c2zeir;
	}
	
	public String getC3zeir() {
		return c3zeir;
	}
	
	public void setC3zeir(String c3zeir) {
		this.c3zeir = c3zeir;
	}
	
	@Override
	public int compareTo(Client client) {
		return getDateCreated().compareTo(client.getDateCreated());
	}
}
