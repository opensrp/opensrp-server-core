package org.opensrp.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class PractitionerRole implements Serializable {

    private static final long serialVersionUID = -2472589757270251270L;
    @JsonProperty
    private String identifier;

    @JsonProperty
    private Boolean active;

    @JsonProperty
    private Long organizationId;

    @JsonProperty
    private String practitionerIdentifier;

    @JsonProperty
    private PractitionerRoleCode code;

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

    public PractitionerRoleCode getCode() {
        return code;
    }

    public void setCode(PractitionerRoleCode code) {
        this.code = code;
    }
}
