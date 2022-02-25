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
	
	private Integer pageNumber = 0;
	
	private Integer pageSize = 0;
	
	private FieldName orderByFieldName;
	
	private OrderByType orderByType;
	
	private List<Integer> locations;

	private Long serverVersion;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(Integer pageSize) {
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

	public Long getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}
}
