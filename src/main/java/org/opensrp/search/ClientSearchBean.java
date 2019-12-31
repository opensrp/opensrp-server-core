package org.opensrp.search;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

public class ClientSearchBean {
	
	private String nameLike;
	
	private String gender;
	
	private DateTime birthdateFrom;
	
	private DateTime birthdateTo;
	
	private DateTime deathdateFrom;
	
	private DateTime deathdateTo;
	
	private String attributeType;
	
	private String attributeValue;
	
	private DateTime lastEditFrom;
	
	private DateTime lastEditTo;
	
	private Map<String, String> identifiers;
	
	private Map<String, String> attributes;
	
	private String firstName;
	
	private String middleName;
	
	private String lastName;
	
	private String clientType;
	
	private String providerId;
	
	private int pageNumber = 0;
	
	private int pageSize = 0;
	
	private String orderByField; // The field name which used to order result 
	
	private String orderByType; // type of order ASC or DESC
	
	private List<String> locations;
	
	private Date startDate;
	
	private Date endDate;
	
	public List<String> getLocations() {
		return locations;
	}
	
	public void setLocations(List<String> locations) {
		this.locations = locations;
	}
	
	public String getOrderByField() {
		return orderByField;
	}
	
	public void setOrderByField(String orderByField) {
		this.orderByField = orderByField;
	}
	
	public String getOrderByType() {
		return orderByType;
	}
	
	public void setOrderByType(String orderByType) {
		this.orderByType = orderByType;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public String getNameLike() {
		return nameLike;
	}
	
	public void setNameLike(String nameLike) {
		this.nameLike = nameLike;
	}
	
	public String getGender() {
		return gender;
	}
	
	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public DateTime getBirthdateFrom() {
		return birthdateFrom;
	}
	
	public void setBirthdateFrom(DateTime birthdateFrom) {
		this.birthdateFrom = birthdateFrom;
	}
	
	public DateTime getBirthdateTo() {
		return birthdateTo;
	}
	
	public void setBirthdateTo(DateTime birthdateTo) {
		this.birthdateTo = birthdateTo;
	}
	
	public DateTime getDeathdateFrom() {
		return deathdateFrom;
	}
	
	public void setDeathdateFrom(DateTime deathdateFrom) {
		this.deathdateFrom = deathdateFrom;
	}
	
	public DateTime getDeathdateTo() {
		return deathdateTo;
	}
	
	public void setDeathdateTo(DateTime deathdateTo) {
		this.deathdateTo = deathdateTo;
	}
	
	public String getAttributeType() {
		return attributeType;
	}
	
	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}
	
	public String getAttributeValue() {
		return attributeValue;
	}
	
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	
	public DateTime getLastEditFrom() {
		return lastEditFrom;
	}
	
	public void setLastEditFrom(DateTime lastEditFrom) {
		this.lastEditFrom = lastEditFrom;
	}
	
	public DateTime getLastEditTo() {
		return lastEditTo;
	}
	
	public void setLastEditTo(DateTime lastEditTo) {
		this.lastEditTo = lastEditTo;
	}
	
	public Map<String, String> getIdentifiers() {
		return identifiers;
	}
	
	public void setIdentifiers(Map<String, String> identifiers) {
		this.identifiers = identifiers;
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getMiddleName() {
		return middleName;
	}
	
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getClientType() {
		return clientType;
	}
	
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	
	public String getProviderId() {
		return providerId;
	}
	
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
