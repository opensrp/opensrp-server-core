package org.opensrp.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

import com.google.gson.annotations.SerializedName;

public class LocationProperty {

	enum PropertyStatus {
		@SerializedName("Active")
		ACTIVE,
		@SerializedName("Inactive")
		INACTIVE,
		@SerializedName("Pending Review")
		PENDING_REVIEW;

	};

	@JsonProperty
	private String uid;

	private String code;

	private String type;

	private PropertyStatus status;

	private String parentId;

	private String name;

	private int geographicLevel;

	private DateTime effectiveStartDate;

	private DateTime effectiveStartEnd;

	private int version;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public PropertyStatus getStatus() {
		return status;
	}

	public void setStatus(PropertyStatus status) {
		this.status = status;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGeographicLevel() {
		return geographicLevel;
	}

	public void setGeographicLevel(int geographicLevel) {
		this.geographicLevel = geographicLevel;
	}

	public DateTime getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(DateTime effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public DateTime getEffectiveStartEnd() {
		return effectiveStartEnd;
	}

	public void setEffectiveStartEnd(DateTime effectiveStartEnd) {
		this.effectiveStartEnd = effectiveStartEnd;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

}
