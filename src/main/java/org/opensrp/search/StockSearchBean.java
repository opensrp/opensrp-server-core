package org.opensrp.search;

import java.util.List;

public class StockSearchBean {

	public enum OrderByType {
		ASC, DESC
	};

	public enum FieldName {
		id, identifier, serverVersion
	};

	private String identifier;
	
	private String stockTypeId;
	
	private String transactionType;
	
	private String providerId;
	
	private String value;
	
	private String dateCreated;
	
	private String toFrom;
	
	private String dateUpdated;
	
	private Long serverVersion;

	private List<String> locations;

	private Integer pageNumber = 0;

	private Integer pageSize = 0;

	private Integer offset;

	private Integer limit;

	private FieldName orderByFieldName;

	private OrderByType orderByType;

	private boolean returnProduct;
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getStockTypeId() {
		return stockTypeId;
	}
	
	public void setStockTypeId(String stockTypeId) {
		this.stockTypeId = stockTypeId;
	}
	
	public String getTransactionType() {
		return transactionType;
	}
	
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
	public String getProviderId() {
		return providerId;
	}
	
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDateCreated() {
		return dateCreated;
	}
	
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public String getToFrom() {
		return toFrom;
	}
	
	public void setToFrom(String toFrom) {
		this.toFrom = toFrom;
	}
	
	public String getDateUpdated() {
		return dateUpdated;
	}
	
	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
	
	public Long getServerVersion() {
		return serverVersion;
	}
	
	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
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

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public boolean isReturnProduct() {
		return returnProduct;
	}

	public void setReturnProduct(boolean returnProduct) {
		this.returnProduct = returnProduct;
	}
}
