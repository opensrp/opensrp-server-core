package org.opensrp.domain.rapidpro.contact.zeir;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RapidProFields implements Serializable {

	@JsonProperty("supervisor_phone")
	private String supervisorPhone;

	@JsonProperty("temp")
	private String temp;

	@JsonProperty("bcg")
	private String bcg;

	@JsonProperty("birth")
	private String birth;

	@JsonProperty("children_count")
	private String childrenCount;

	@JsonProperty("date_joined")
	private String dateJoined;

	@JsonProperty("district")
	private String district;

	@JsonProperty("dob")
	private String dob;

	@JsonProperty("dpt1")
	private String dpt1;

	@JsonProperty("dpt2")
	private String dpt2;

	@JsonProperty("dpt3")
	private String dpt3;

	@JsonProperty("facility")
	private String facility;

	@JsonProperty("location")
	private String location;

	@JsonProperty("measles")
	private String measles;

	@JsonProperty("measles2")
	private String measles2;

	@JsonProperty("monthly_children_count")
	private String monthlyChildrenCount;

	@JsonProperty("mother_name")
	private String motherName;

	@JsonProperty("mother_phone")
	private String motherPhone;

	@JsonProperty("mvacc_id")
	private String mvaccId;

	@JsonProperty("no_vaccine")
	private String noVaccine;

	@JsonProperty("opv0")
	private String opv0;

	@JsonProperty("opv1")
	private String opv1;

	@JsonProperty("opv2")
	private String opv2;

	@JsonProperty("opv3")
	private String opv3;

	@JsonProperty("opv4")
	private String opv4;

	@JsonProperty("outreach")
	private String outreach;

	@JsonProperty("outreach_session")
	private String outreachSession;

	@JsonProperty("pcv1")
	private String pcv1;

	@JsonProperty("pcv2")
	private String pcv2;

	@JsonProperty("pcv3")
	private String pcv3;

	@JsonProperty("ipv")
	private String ipv;

	@JsonProperty("position")
	private String position;

	@JsonProperty("province")
	private String province;

	@JsonProperty("rota1")
	private String rota1;

	@JsonProperty("rota2")
	private String rota2;

	@JsonProperty("sex")
	private String sex;

	@JsonProperty("supervisor")
	private String supervisor;

	@JsonProperty("under5_id")
	private String under5Id;

	@JsonProperty("yearly_children_count")
	private String yearlyChildrenCount;

	@JsonProperty("zone")
	private String zone;

	@JsonProperty("facility_location_id")
	@JsonAlias({ "health_facility_location_id" })
	private String facilityLocationId;

	@JsonProperty("facility_code")
	@JsonAlias({ "health_facility_code" })
	private String facilityCode;

	@JsonProperty("height")
	@JsonAlias("growth_monitoring_height")
	private String height;

	@JsonProperty("growth_monitoring_height_modified_on")
	@JsonAlias({ "gmh_date_modified", "growth_monitoring_height_date_modified" })
	private String gmHeightDateModified;

	@JsonProperty("weight")
	@JsonAlias("growth_monitoring_weight")
	private String weight;

	@JsonProperty("growth_monitoring_weight_modified_on")
	@JsonAlias({ "gmw_date_modified", "growth_monitoring_weight_date_modified" })
	private String gmWeightDateModified;

	@JsonProperty("registration_processed")
	@JsonAlias({ "reg_processed", "is_registration_processed", "processed_registration" })
	private String registrationProcessed;

	@JsonProperty("opensrp_id")
	@JsonAlias({ "zeir_id", "ZEIR_ID", "OPENSRP_ID" })
	private String opensrpId;

	public String getSupervisorPhone() {
		return this.supervisorPhone;
	}

	public void setSupervisorPhone(String supervisorPhone) {
		this.supervisorPhone = supervisorPhone;
	}

	public String getTemp() {
		return this.temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getBcg() {
		return this.bcg;
	}

	public void setBcg(String bcg) {
		this.bcg = bcg;
	}

	public String getBirth() {
		return this.birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getChildrenCount() {
		return this.childrenCount;
	}

	public void setChildrenCount(String childrenCount) {
		this.childrenCount = childrenCount;
	}

	public String getDateJoined() {
		return this.dateJoined;
	}

	public void setDateJoined(String dateJoined) {
		this.dateJoined = dateJoined;
	}

	public String getDistrict() {
		return this.district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getDob() {
		return this.dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getDpt1() {
		return this.dpt1;
	}

	public void setDpt1(String dpt1) {
		if (dpt1 != null)
			this.dpt1 = dpt1;
	}

	public String getDpt2() {
		return this.dpt2;
	}

	public void setDpt2(String dpt2) {
		if (dpt2 != null)
			this.dpt2 = dpt2;
	}

	public String getDpt3() {
		return this.dpt3;
	}

	public void setDpt3(String dpt3) {
		if (dpt3 != null)
			this.dpt3 = dpt3;
	}

	public String getFacility() {
		return this.facility;
	}

	public void setFacility(String facility) {
		this.facility = facility;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMeasles() {
		return this.measles;
	}

	public void setMeasles(String measles) {
		if (measles != null)
			this.measles = measles;
	}

	public String getMeasles2() {
		return this.measles2;
	}

	public void setMeasles2(String measles2) {
		if (measles2 != null)
			this.measles2 = measles2;
	}

	public String getMonthlyChildrenCount() {
		return this.monthlyChildrenCount;
	}

	public void setMonthlyChildrenCount(String monthlyChildrenCount) {
		this.monthlyChildrenCount = monthlyChildrenCount;
	}

	public String getMotherName() {
		return this.motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public String getMotherPhone() {
		return this.motherPhone;
	}

	public void setMotherPhone(String motherPhone) {
		this.motherPhone = motherPhone;
	}

	public String getMvaccId() {
		return this.mvaccId;
	}

	public void setMvaccId(String mvaccId) {
		this.mvaccId = mvaccId;
	}

	public String getNoVaccine() {
		return this.noVaccine;
	}

	public void setNoVaccine(String noVaccine) {
		this.noVaccine = noVaccine;
	}

	public String getOpv0() {
		return this.opv0;
	}

	public void setOpv0(String opv0) {
		if (opv0 != null)
			this.opv0 = opv0;
	}

	public String getOpv1() {
		return this.opv1;
	}

	public void setOpv1(String opv1) {
		if (opv1 != null)
			this.opv1 = opv1;
	}

	public String getOpv2() {
		return this.opv2;
	}

	public void setOpv2(String opv2) {
		if (opv2 != null)
			this.opv2 = opv2;
	}

	public String getOpv3() {
		return this.opv3;
	}

	public void setOpv3(String opv3) {
		if (opv3 != null)
			this.opv3 = opv3;
	}

	public String getOpv4() {
		return this.opv4;
	}

	public void setOpv4(String opv4) {
		if (opv4 != null)
			this.opv4 = opv4;
	}

	public String getOutreach() {
		return this.outreach;
	}

	public void setOutreach(String outreach) {
		this.outreach = outreach;
	}

	public String getOutreachSession() {
		return this.outreachSession;
	}

	public void setOutreachSession(String outreachSession) {
		this.outreachSession = outreachSession;
	}

	public String getPcv1() {
		return this.pcv1;
	}

	public void setPcv1(String pcv1) {
		if (pcv1 != null)
			this.pcv1 = pcv1;
	}

	public String getPcv2() {
		return this.pcv2;
	}

	public void setPcv2(String pcv2) {
		if (pcv2 != null)
			this.pcv2 = pcv2;
	}

	public String getPcv3() {
		return this.pcv3;
	}

	public void setPcv3(String pcv3) {
		if (pcv3 != null)
			this.pcv3 = pcv3;
	}

	public String getIpv() {
		return this.ipv;
	}

	public void setIpv(String ipv) {
		if (ipv != null)
			this.ipv = ipv;
	}

	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getProvince() {
		return this.province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getRota1() {
		return this.rota1;
	}

	public void setRota1(String rota1) {
		if (rota1 != null)
			this.rota1 = rota1;
	}

	public String getRota2() {
		return this.rota2;
	}

	public void setRota2(String rota2) {
		if (rota2 != null)
			this.rota2 = rota2;
	}

	public String getSex() {
		return this.sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSupervisor() {
		return this.supervisor;
	}

	public void setSupervisor(String supervisor) {
		this.supervisor = supervisor;
	}

	public String getUnder5Id() {
		return this.under5Id;
	}

	public void setUnder5Id(String under5Id) {
		this.under5Id = under5Id;
	}

	public String getYearlyChildrenCount() {
		return this.yearlyChildrenCount;
	}

	public void setYearlyChildrenCount(String yearlyChildrenCount) {
		this.yearlyChildrenCount = yearlyChildrenCount;
	}

	public String getZone() {
		return this.zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getFacilityLocationId() {
		return facilityLocationId;
	}

	public void setFacilityLocationId(String facilityLocationId) {
		this.facilityLocationId = facilityLocationId;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		if (height != null)
			this.height = height;
	}

	public String getGmHeightDateModified() {
		return gmHeightDateModified;
	}

	public void setGmHeightDateModified(String gmHeightDateModified) {
		this.gmHeightDateModified = gmHeightDateModified;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		if (weight != null)
			this.weight = weight;
	}

	public String getGmWeightDateModified() {
		return gmWeightDateModified;
	}

	public void setGmWeightDateModified(String gmWeightDateModified) {
		this.gmWeightDateModified = gmWeightDateModified;
	}

	public String isRegistrationProcessed() {
		return registrationProcessed;
	}

	public void setRegistrationProcessed(String registrationProcessed) {
		this.registrationProcessed = registrationProcessed;
	}

	public String getFacilityCode() {
		return facilityCode;
	}

	public void setFacilityCode(String facilityCode) {
		this.facilityCode = facilityCode;
	}

	public String getOpensrpId() {
		return opensrpId;
	}

	public void setOpensrpId(String opensrpId) {
		this.opensrpId = opensrpId;
	}
}
