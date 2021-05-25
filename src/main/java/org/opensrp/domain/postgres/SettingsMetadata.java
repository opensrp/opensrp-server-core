package org.opensrp.domain.postgres;

public class SettingsMetadata {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private Long id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.settings_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private Long settingsId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.document_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String documentId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.identifier
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String identifier;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.server_version
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private Long serverVersion;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.team
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String team;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.team_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String teamId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.provider_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String providerId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.location_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String locationId;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.uuid
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String uuid;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.json
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private Object json;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.setting_type
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String settingType;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.setting_value
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String settingValue;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.setting_key
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String settingKey;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.setting_description
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String settingDescription;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.setting_label
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String settingLabel;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.inherited_from
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private String inheritedFrom;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.settings_metadata.metadata_version
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	private Long metadataVersion;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.id
	 * @return  the value of core.settings_metadata.id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public Long getId() {
		return id;
	}

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column core.settings_metadata.id
     *
     * @param id the value for core.settings_metadata.id
     *
     * @mbg.generated Fri Apr 24 10:25:50 EAT 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column core.settings_metadata.settings_id
     *
     * @return the value of core.settings_metadata.settings_id
     *
     * @mbg.generated Fri Apr 24 10:25:50 EAT 2020
     */
    public Long getSettingsId() {
        return settingsId;
    }

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.settings_id
	 * @param settingsId  the value for core.settings_metadata.settings_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setSettingsId(Long settingsId) {
		this.settingsId = settingsId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.document_id
	 * @return  the value of core.settings_metadata.document_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.document_id
	 * @param documentId  the value for core.settings_metadata.document_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.identifier
	 * @return  the value of core.settings_metadata.identifier
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.identifier
	 * @param identifier  the value for core.settings_metadata.identifier
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.server_version
	 * @return  the value of core.settings_metadata.server_version
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public Long getServerVersion() {
		return serverVersion;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.server_version
	 * @param serverVersion  the value for core.settings_metadata.server_version
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setServerVersion(Long serverVersion) {
		this.serverVersion = serverVersion;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.team
	 * @return  the value of core.settings_metadata.team
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.team
	 * @param team  the value for core.settings_metadata.team
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setTeam(String team) {
		this.team = team;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.team_id
	 * @return  the value of core.settings_metadata.team_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getTeamId() {
		return teamId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.team_id
	 * @param teamId  the value for core.settings_metadata.team_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.provider_id
	 * @return  the value of core.settings_metadata.provider_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getProviderId() {
		return providerId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.provider_id
	 * @param providerId  the value for core.settings_metadata.provider_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.location_id
	 * @return  the value of core.settings_metadata.location_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getLocationId() {
		return locationId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.location_id
	 * @param locationId  the value for core.settings_metadata.location_id
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.uuid
	 * @return  the value of core.settings_metadata.uuid
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.uuid
	 * @param uuid  the value for core.settings_metadata.uuid
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.json
	 * @return  the value of core.settings_metadata.json
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public Object getJson() {
		return json;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.json
	 * @param json  the value for core.settings_metadata.json
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setJson(Object json) {
		this.json = json;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.setting_type
	 * @return  the value of core.settings_metadata.setting_type
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getSettingType() {
		return settingType;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.setting_type
	 * @param settingType  the value for core.settings_metadata.setting_type
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setSettingType(String settingType) {
		this.settingType = settingType;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.setting_value
	 * @return  the value of core.settings_metadata.setting_value
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getSettingValue() {
		return settingValue;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.setting_value
	 * @param settingValue  the value for core.settings_metadata.setting_value
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setSettingValue(String settingValue) {
		this.settingValue = settingValue;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.setting_key
	 * @return  the value of core.settings_metadata.setting_key
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getSettingKey() {
		return settingKey;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.setting_key
	 * @param settingKey  the value for core.settings_metadata.setting_key
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setSettingKey(String settingKey) {
		this.settingKey = settingKey;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.setting_description
	 * @return  the value of core.settings_metadata.setting_description
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getSettingDescription() {
		return settingDescription;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.setting_description
	 * @param settingDescription  the value for core.settings_metadata.setting_description
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setSettingDescription(String settingDescription) {
		this.settingDescription = settingDescription;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.setting_label
	 * @return  the value of core.settings_metadata.setting_label
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getSettingLabel() {
		return settingLabel;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.setting_label
	 * @param settingLabel  the value for core.settings_metadata.setting_label
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setSettingLabel(String settingLabel) {
		this.settingLabel = settingLabel;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.inherited_from
	 * @return  the value of core.settings_metadata.inherited_from
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public String getInheritedFrom() {
		return inheritedFrom;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.inherited_from
	 * @param inheritedFrom  the value for core.settings_metadata.inherited_from
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setInheritedFrom(String inheritedFrom) {
		this.inheritedFrom = inheritedFrom;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.settings_metadata.metadata_version
	 * @return  the value of core.settings_metadata.metadata_version
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public Long getMetadataVersion() {
		return metadataVersion;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.settings_metadata.metadata_version
	 * @param metadataVersion  the value for core.settings_metadata.metadata_version
	 * @mbg.generated  Fri May 21 17:49:57 PKT 2021
	 */
	public void setMetadataVersion(Long metadataVersion) {
		this.metadataVersion = metadataVersion;
	}
}