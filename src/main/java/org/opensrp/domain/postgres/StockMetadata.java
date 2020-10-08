package org.opensrp.domain.postgres;

import java.util.Date;

public class StockMetadata {
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.stock_metadata.id
	 * 
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	private Long id;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.stock_metadata.stock_id
	 * 
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	private Long stockId;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.stock_metadata.document_id
	 * 
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	private String documentId;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.stock_metadata.server_version
	 * 
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	private Long serverVersion;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.stock_metadata.provider_id
	 * 
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	private String providerId;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.stock_metadata.location_id
	 * 
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	private String locationId;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.stock_metadata.team
	 * 
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	private String team;
	
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column
	 * core.stock_metadata.team_id
	 * 
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	private String teamId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.stock_metadata.date_deleted
	 * @mbg.generated  Wed Oct 07 14:13:33 PKT 2020
	 */
	private Date dateDeleted;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.stock_metadata.id
	 * 
	 * @return the value of core.stock_metadata.id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public Long getId() {
		return id;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.stock_metadata.id
	 * 
	 * @param id the value for core.stock_metadata.id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.stock_metadata.stock_id
	 * 
	 * @return the value of core.stock_metadata.stock_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public Long getStockId() {
		return stockId;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.stock_metadata.stock_id
	 * 
	 * @param stockId the value for core.stock_metadata.stock_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public void setStockId(Long stockId) {
		this.stockId = stockId;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.stock_metadata.document_id
	 * 
	 * @return the value of core.stock_metadata.document_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public String getDocumentId() {
		return documentId;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.stock_metadata.document_id
	 * 
	 * @param documentId the value for core.stock_metadata.document_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.stock_metadata.server_version
	 * 
	 * @return the value of core.stock_metadata.server_version
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public Long getServerVersion() {
		return serverVersion;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.stock_metadata.server_version
	 * 
	 * @param serverVersion the value for core.stock_metadata.server_version
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.stock_metadata.provider_id
	 * 
	 * @return the value of core.stock_metadata.provider_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public String getProviderId() {
		return providerId;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.stock_metadata.provider_id
	 * 
	 * @param providerId the value for core.stock_metadata.provider_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.stock_metadata.location_id
	 * 
	 * @return the value of core.stock_metadata.location_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public String getLocationId() {
		return locationId;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.stock_metadata.location_id
	 * 
	 * @param locationId the value for core.stock_metadata.location_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.stock_metadata.team
	 * 
	 * @return the value of core.stock_metadata.team
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public String getTeam() {
		return team;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.stock_metadata.team
	 * 
	 * @param team the value for core.stock_metadata.team
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public void setTeam(String team) {
		this.team = team;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database
	 * column core.stock_metadata.team_id
	 * 
	 * @return the value of core.stock_metadata.team_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public String getTeamId() {
		return teamId;
	}
	
	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database
	 * column core.stock_metadata.team_id
	 * 
	 * @param teamId the value for core.stock_metadata.team_id
	 * @mbg.generated Fri Mar 23 15:56:38 EAT 2018
	 */
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.stock_metadata.date_deleted
	 * @return  the value of core.stock_metadata.date_deleted
	 * @mbg.generated  Wed Oct 07 14:13:33 PKT 2020
	 */
	public Date getDateDeleted() {
		return dateDeleted;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.stock_metadata.date_deleted
	 * @param dateDeleted  the value for core.stock_metadata.date_deleted
	 * @mbg.generated  Wed Oct 07 14:13:33 PKT 2020
	 */
	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}
}
