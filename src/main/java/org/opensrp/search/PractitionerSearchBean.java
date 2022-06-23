package org.opensrp.search;


public class PractitionerSearchBean {


	private Integer pageNumber = 0;

	private Integer pageSize = 0;

	private FieldName orderByFieldName;

	private OrderByType orderByType;

	public enum OrderByType {
		ASC, DESC
	}


	public enum FieldName {
		id, identifier, server_version
	}

	private Long serverVersion;

	public Long getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
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
}
