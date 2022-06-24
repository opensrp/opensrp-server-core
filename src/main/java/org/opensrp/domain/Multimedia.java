package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.smartregister.domain.BaseDataEntity;

import java.util.Date;

public class Multimedia extends BaseDataEntity {

    @JsonProperty
    private String caseId;

    @JsonProperty
    private String providerId;

    @JsonProperty
    private String contentType;

    @JsonProperty
    private String filePath;

    @JsonProperty
    private String fileCategory;

    @JsonProperty
    private Date dateUploaded = new Date();

    @JsonProperty
    private String summary;

    @JsonProperty
    private String originalFileName;

    public Multimedia() {

    }

    public Multimedia(String caseId, String providerId, String contentType, String filePath, String fileCategory) {
        this.caseId = caseId;
        this.providerId = providerId;
        this.contentType = contentType;
        this.filePath = filePath;
        this.fileCategory = fileCategory;
    }

    public Multimedia withCaseId(String caseId) {
        this.caseId = caseId;
        return this;
    }

    public Multimedia withProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public Multimedia withContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Multimedia withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public Multimedia withFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
        return this;
    }

    public Multimedia withDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
        return this;
    }

    public Multimedia withSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public Multimedia withOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
        return this;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(String fileCategory) {
        this.fileCategory = fileCategory;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    @Override
    public final boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "id", "revision");
    }

    @Override
    public final int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id", "revision");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
