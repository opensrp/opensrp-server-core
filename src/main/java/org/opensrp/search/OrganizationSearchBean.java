package org.opensrp.search;

import java.util.List;

public class OrganizationSearchBean {
	
	private String name;
	
	private int pageNumber = 0;
	
	private int pageSize = 0;
	
	private String orderByFieldName;
	
	private String orderByType;
	
	private List<Integer> locations;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	public String getOrderByFieldName() {
		return orderByFieldName;
	}
	
	public void setOrderByFieldName(String orderByFieldName) {
		this.orderByFieldName = orderByFieldName;
	}
	
	public String getOrderByType() {
		return orderByType;
	}
	
	public void setOrderByType(String orderByType) {
		this.orderByType = orderByType;
	}
	
	public List<Integer> getLocations() {
		return locations;
	}
	
	public void setLocations(List<Integer> locations) {
		this.locations = locations;
	}
	
}
