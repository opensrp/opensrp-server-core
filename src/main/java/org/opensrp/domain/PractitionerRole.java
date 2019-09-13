package org.opensrp.domain;

import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonProperty;

public class PractitionerRole {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private Boolean active;

    @JsonProperty
    @SerializedName("organization_id")
    private Long organizationId;

    @JsonProperty
    @SerializedName("practitioner_identifier")
    private String practitionerIdentifier;

    @JsonProperty
    private String code;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getPractitionerIdentifier() {
        return practitionerIdentifier;
    }

    public void setPractitionerIdentifier(String practitionerIdentifier) {
        this.practitionerIdentifier = practitionerIdentifier;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
