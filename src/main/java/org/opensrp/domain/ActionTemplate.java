package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import org.smartregister.domain.Condition;
import org.smartregister.domain.DynamicValue;
import org.smartregister.domain.Trigger;

import java.io.Serializable;
import java.util.Set;

public class ActionTemplate  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String identifier;
    private int prefix;
    private String title;
    private String description;
    private String code;
    private PeriodTemplate timingPeriod;
    private String reason;
    private String goalId;
    private ActionTemplate.SubjectConcept subjectCodableConcept;
    private String taskTemplate;
    private Set<Trigger> trigger;
    private Set<Condition> condition;
    private String definitionUri;
    private Set<DynamicValue> dynamicValue;
    private ActionTemplate.ActionType type;

    public ActionTemplate() {
        this.type = ActionTemplate.ActionType.CREATE;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPrefix(int prefix) {
        this.prefix = prefix;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTimingPeriod(PeriodTemplate timingPeriod) {
        this.timingPeriod = timingPeriod;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public void setSubjectCodableConcept(ActionTemplate.SubjectConcept subjectCodableConcept) {
        this.subjectCodableConcept = subjectCodableConcept;
    }

    public void setTaskTemplate(String taskTemplate) {
        this.taskTemplate = taskTemplate;
    }

    public void setTrigger(Set<Trigger> trigger) {
        this.trigger = trigger;
    }

    public void setCondition(Set<Condition> condition) {
        this.condition = condition;
    }

    public void setDefinitionUri(String definitionUri) {
        this.definitionUri = definitionUri;
    }

    public void setDynamicValue(Set<DynamicValue> dynamicValue) {
        this.dynamicValue = dynamicValue;
    }

    public void setType(ActionTemplate.ActionType type) {
        this.type = type;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public int getPrefix() {
        return this.prefix;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCode() {
        return this.code;
    }

    public PeriodTemplate getTimingPeriod() {
        return this.timingPeriod;
    }

    public String getReason() {
        return this.reason;
    }

    public String getGoalId() {
        return this.goalId;
    }

    public ActionTemplate.SubjectConcept getSubjectCodableConcept() {
        return this.subjectCodableConcept;
    }

    public String getTaskTemplate() {
        return this.taskTemplate;
    }

    public Set<Trigger> getTrigger() {
        return this.trigger;
    }

    public Set<Condition> getCondition() {
        return this.condition;
    }

    public String getDefinitionUri() {
        return this.definitionUri;
    }

    public Set<DynamicValue> getDynamicValue() {
        return this.dynamicValue;
    }

    public ActionTemplate.ActionType getType() {
        return this.type;
    }

    public static class SubjectConcept implements Serializable {
        private String text;

        public SubjectConcept(String text) {
            this.text = text;
        }

        public SubjectConcept() {
        }

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static enum ActionType {
        @JsonProperty("create")
        @SerializedName("create")
        CREATE,
        @JsonProperty("update")
        @SerializedName("update")
        UPDATE,
        @JsonProperty("remove")
        @SerializedName("remove")
        REMOVE,
        @JsonProperty("fire-event")
        @SerializedName("fire-event")
        FIRE_EVENT;

    }
}
