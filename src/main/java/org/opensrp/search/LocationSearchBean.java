package org.opensrp.search;

import com.google.gson.annotations.SerializedName;

public class LocationSearchBean {
	
	public enum OrderByType {
		@SerializedName("asc")
		ASC, @SerializedName("desc")
		DESC
		
	};
	
	private String name;
	
	private Long locationTagId;
	
	private Long parentId;
	
	private String status;
	
	private Integer pageSize;
	
	private Integer pageNumber;
	
	private String orderByFieldName;
	
	private OrderByType orderByType;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getLocationTagId() {
		return locationTagId;
	}
	
	public void setLocationTagId(Long locationTagId) {
		this.locationTagId = locationTagId;
	}
	
	public Long getParentId() {
		return parentId;
	}
	
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getPageSize() {
		return pageSize;
	}
	
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}
	
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	
	public String getOrderByFieldName() {
		return orderByFieldName;
	}
	
	public void setOrderByFieldName(String orderByFieldName) {
		this.orderByFieldName = orderByFieldName;
	}
	
	public OrderByType getOrderByType() {
		return orderByType;
	}
	
	public void setOrderByType(OrderByType orderByType) {
		this.orderByType = orderByType;
	}
	
}
