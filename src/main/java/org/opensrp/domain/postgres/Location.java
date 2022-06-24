package org.opensrp.domain.postgres;

public class Location {

    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column core.location.id
     *
     * @mbg.generated Tue Dec 08 19:20:02 EAT 2020
     */
    private Long id;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column core.location.json
     *
     * @mbg.generated Tue Dec 08 19:20:02 EAT 2020
     */
    private Object json;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column core.location.server_version
     *
     * @mbg.generated Tue Dec 08 19:20:02 EAT 2020
     */
    private Long serverVersion;

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column core.location.id
     *
     * @return the value of core.location.id
     * @mbg.generated Tue Dec 08 19:20:02 EAT 2020
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column core.location.id
     *
     * @param id the value for core.location.id
     * @mbg.generated Tue Dec 08 19:20:02 EAT 2020
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column core.location.json
     *
     * @return the value of core.location.json
     * @mbg.generated Tue Dec 08 19:20:02 EAT 2020
     */
    public Object getJson() {
        return json;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column core.location.json
     *
     * @param json the value for core.location.json
     * @mbg.generated Tue Dec 08 19:20:02 EAT 2020
     */
    public void setJson(Object json) {
        this.json = json;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column core.location.server_version
     *
     * @return the value of core.location.server_version
     * @mbg.generated Tue Dec 08 19:20:02 EAT 2020
     */
    public Long getServerVersion() {
        return serverVersion;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column core.location.server_version
     *
     * @param serverVersion the value for core.location.server_version
     * @mbg.generated Tue Dec 08 19:20:02 EAT 2020
     */
    public void setServerVersion(Long serverVersion) {
        this.serverVersion = serverVersion;
    }
}