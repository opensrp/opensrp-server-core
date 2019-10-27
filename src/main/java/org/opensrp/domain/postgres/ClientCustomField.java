package org.opensrp.domain.postgres;

public class ClientCustomField {
	
	private int memebrCount;
	
	private String providerId;
	
	private String householdHead;
	
	private String relationalId;
	
	private int totalCount;
	
	public int getTotalCount() {
		return totalCount;
	}
	
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	public int getMemebrCount() {
		return memebrCount;
	}
	
	public void setMemebrCount(int memebrCount) {
		this.memebrCount = memebrCount;
	}
	
	public String getProviderId() {
		return providerId;
	}
	
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	public String getHouseholdHead() {
		return householdHead;
	}
	
	public void setHouseholdHead(String householdHead) {
		this.householdHead = householdHead;
	}
	
	public String getRelationalId() {
		return relationalId;
	}
	
	public void setRelationalId(String relationalId) {
		this.relationalId = relationalId;
	}
	
}
