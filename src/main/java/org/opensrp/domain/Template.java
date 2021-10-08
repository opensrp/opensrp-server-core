package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Template implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty
    private Integer templateId;
    @JsonProperty
    private PlanTemplate template;
    @JsonProperty
    private String type;
    @JsonProperty
    private int version;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public PlanTemplate getTemplate() {
        return template;
    }

    public void setTemplate(PlanTemplate template) {
        this.template = template;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        type = type;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
