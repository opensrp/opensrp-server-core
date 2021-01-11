package org.opensrp.domain.postgres;

import java.util.Date;

public class Client {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.client.id
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	private Long id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.client.json
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	private Object json;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.client.date_deleted
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	private Date dateDeleted;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.client.server_version
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	private Long serverVersion;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.client.id
	 * @return  the value of core.client.id
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	public Long getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.client.id
	 * @param id  the value for core.client.id
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.client.json
	 * @return  the value of core.client.json
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	public Object getJson() {
		return json;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.client.json
	 * @param json  the value for core.client.json
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	public void setJson(Object json) {
		this.json = json;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.client.date_deleted
	 * @return  the value of core.client.date_deleted
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	public Date getDateDeleted() {
		return dateDeleted;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.client.date_deleted
	 * @param dateDeleted  the value for core.client.date_deleted
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	public void setDateDeleted(Date dateDeleted) {
		this.dateDeleted = dateDeleted;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.client.server_version
	 * @return  the value of core.client.server_version
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	public Long getServerVersion() {
		return serverVersion;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.client.server_version
	 * @param serverVersion  the value for core.client.server_version
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}
}
