package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GoalTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String description;
    private String priority;
    @JsonProperty("target")
    @SerializedName("target")
    private List<TargetTemplate> targets;

    public GoalTemplate() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public List<TargetTemplate> getTargets() {
        return this.targets;
    }

    public void setTargets(List<TargetTemplate> targets) {
        this.targets = targets;
    }
}
