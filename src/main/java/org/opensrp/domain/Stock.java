package org.opensrp.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.smartregister.domain.BaseDataObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock extends BaseDataObject {
	
	@JsonProperty
	private Long identifier;
	
	@JsonProperty
	private String vaccine_type_id;
	
	@JsonProperty
	private String transaction_type;
	
	@JsonProperty
	private String providerid;
	
	@JsonProperty
	private int value;
	
	@JsonProperty
	private Long date_created;
	
	@JsonProperty
	private String to_from;
	
	@JsonProperty
	private Long date_updated;
	
	@JsonProperty
	private long version;

	@JsonProperty
	private Date deliveryDate;

	@JsonProperty
	private Date accountabilityEndDate;

	@JsonProperty
	private String donor;

	@JsonProperty
	private String serialNumber;

	@JsonProperty
	private String locationId;

	@JsonProperty
	private Map<String, String> customProperties = new HashMap();

	public Stock() {
		this.version = System.currentTimeMillis();
	}
	
	public Stock(Long identifier, String vaccine_type_id, String transaction_type, String providerid, int value,
	    Long date_created, String to_from, Long date_updated, long version) {
		this.identifier = identifier;
		this.vaccine_type_id = vaccine_type_id;
		this.transaction_type = transaction_type;
		this.providerid = providerid;
		this.value = value;
		this.date_created = date_created;
		this.to_from = to_from;
		this.date_updated = date_updated;
		this.version = version;
	}

	public Stock(Long identifier, String vaccine_type_id, String transaction_type, String providerid, int value,
			StockObjectMetadata stockObjectMetadata, Inventory inventory) {
		this.identifier = identifier;
		this.vaccine_type_id = vaccine_type_id;
		this.transaction_type = transaction_type;
		this.providerid = providerid;
		this.value = value;
		this.date_created = stockObjectMetadata.getDate_created();
		this.to_from = stockObjectMetadata.getTo_from();
		this.date_updated = stockObjectMetadata.getDate_updated();
		this.version = stockObjectMetadata.getVersion();
		this.deliveryDate = inventory.getDeliveryDate();
		this.donor = inventory.getDonor();
		this.serialNumber = inventory.getSerialNumber();
		this.locationId = inventory.getServicePointId();
		// TODO : accountabilityDate to be computed later and also set customProperties
	}

	public Long getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}
	
	public String getVaccine_type_id() {
		return vaccine_type_id;
	}
	
	public void setVaccine_type_id(String vaccine_type_id) {
		this.vaccine_type_id = vaccine_type_id;
	}
	
	public String getTransaction_type() {
		return transaction_type;
	}
	
	public void setTransaction_type(String transaction_type) {
		this.transaction_type = transaction_type;
	}
	
	public String getProviderid() {
		return providerid;
	}
	
	public void setProviderid(String providerid) {
		this.providerid = providerid;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public Long getDate_created() {
		return date_created;
	}
	
	public void setDate_created(Long date_created) {
		this.date_created = date_created;
	}
	
	public String getTo_from() {
		return to_from;
	}
	
	public void setTo_from(String to_from) {
		this.to_from = to_from;
	}
	
	public Long getDate_updated() {
		return date_updated;
	}
	
	public void setDate_updated(Long date_updated) {
		this.date_updated = date_updated;
	}
	
	public long getVersion() {
		return version;
	}
	
	public void setVersion(long version) {
		this.version = version;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Date getAccountabilityEndDate() {
		return accountabilityEndDate;
	}

	public void setAccountabilityEndDate(Date accountabilityEndDate) {
		this.accountabilityEndDate = accountabilityEndDate;
	}

	public String getDonor() {
		return donor;
	}

	public void setDonor(String donor) {
		this.donor = donor;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public Map<String, String> getCustomProperties() {
		return customProperties;
	}

	public void setCustomProperties(Map<String, String> customProperties) {
		this.customProperties = customProperties;
	}

	@Override
	public final boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, "id", "revision");
	}
	
	@Override
	public final int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, "id", "revision");
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
