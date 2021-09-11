package org.opensrp.domain.postgres;

import java.util.Date;

public class PlanProcessingStatus {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column core.plan_processing_status.id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column core.plan_processing_status.plan_id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    private Long planId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column core.plan_processing_status.event_id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    private Long eventId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column core.plan_processing_status.template_id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    private Long templateId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column core.plan_processing_status.status
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    private Integer status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column core.plan_processing_status.date_created
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    private Date dateCreated;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column core.plan_processing_status.date_edited
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    private Date dateEdited;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column core.plan_processing_status.id
     *
     * @return the value of core.plan_processing_status.id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column core.plan_processing_status.id
     *
     * @param id the value for core.plan_processing_status.id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column core.plan_processing_status.plan_id
     *
     * @return the value of core.plan_processing_status.plan_id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public Long getPlanId() {
        return planId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column core.plan_processing_status.plan_id
     *
     * @param planId the value for core.plan_processing_status.plan_id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column core.plan_processing_status.event_id
     *
     * @return the value of core.plan_processing_status.event_id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public Long getEventId() {
        return eventId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column core.plan_processing_status.event_id
     *
     * @param eventId the value for core.plan_processing_status.event_id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column core.plan_processing_status.template_id
     *
     * @return the value of core.plan_processing_status.template_id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public Long getTemplateId() {
        return templateId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column core.plan_processing_status.template_id
     *
     * @param templateId the value for core.plan_processing_status.template_id
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column core.plan_processing_status.status
     *
     * @return the value of core.plan_processing_status.status
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column core.plan_processing_status.status
     *
     * @param status the value for core.plan_processing_status.status
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column core.plan_processing_status.date_created
     *
     * @return the value of core.plan_processing_status.date_created
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column core.plan_processing_status.date_created
     *
     * @param dateCreated the value for core.plan_processing_status.date_created
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column core.plan_processing_status.date_edited
     *
     * @return the value of core.plan_processing_status.date_edited
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public Date getDateEdited() {
        return dateEdited;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column core.plan_processing_status.date_edited
     *
     * @param dateEdited the value for core.plan_processing_status.date_edited
     *
     * @mbg.generated Sat Sep 11 11:04:03 EAT 2021
     */
    public void setDateEdited(Date dateEdited) {
        this.dateEdited = dateEdited;
    }
}