package org.opensrp.search;

import java.util.List;

public class OrganizationSearchBean {
	
	public enum OrderByType {
		ASC, DESC
	};
	
	public enum FieldName {
		id, identifier, name, parent_id, member_count
	};
	
	private String name;
	
	private int pageNumber = 0;
	
	private int pageSize = 0;
	
	private FieldName orderByFieldName;
	
	private OrderByType orderByType;
	
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
	
	public FieldName getOrderByFieldName() {
		return orderByFieldName;
	}
	
	public void setOrderByFieldName(FieldName orderByFieldName) {
		this.orderByFieldName = orderByFieldName;
	}
	
	public OrderByType getOrderByType() {
		return orderByType;
	}
	
	public void setOrderByType(OrderByType orderByType) {
		this.orderByType = orderByType;
	}
	
	public List<Integer> getLocations() {
		return locations;
	}
	
	public void setLocations(List<Integer> locations) {
		this.locations = locations;
	}
	
}
