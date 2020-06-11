package org.opensrp.domain;

import java.util.Set;

/**
 * Created by samuelgithengi on 4/29/19.
 */
public class Action {

    private String identifier;

    private int prefix;

    private String title;

    private String description;

    private String code;

    private ExecutionPeriod timingPeriod;

    private String reason;

    private String goalId;

    private SubjectConcept subjectCodableConcept;

    private String taskTemplate;

    private Set<Trigger> triggers;

    private Set<Condition> conditions;

    private String definitionUri;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getPrefix() {
        return prefix;
    }

    public void setPrefix(int prefix) {
        this.prefix = prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ExecutionPeriod getTimingPeriod() {
        return timingPeriod;
    }

    public void setTimingPeriod(ExecutionPeriod timingPeriod) {
        this.timingPeriod = timingPeriod;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getGoalId() {
        return goalId;
    }

    public void setGoalId(String goalId) {
        this.goalId = goalId;
    }

    public SubjectConcept getSubjectCodableConcept() {
        return subjectCodableConcept;
    }

    public void setSubjectCodableConcept(SubjectConcept subjectCodableConcept) {
        this.subjectCodableConcept = subjectCodableConcept;
    }

    public String getTaskTemplate() {
        return taskTemplate;
    }

    public void setTaskTemplate(String taskTemplate) {
        this.taskTemplate = taskTemplate;
    }

    public Set<Trigger> getTriggers() {
        return triggers;
    }

    public void setTriggers(Set<Trigger> triggers) {
        this.triggers = triggers;
    }

    public Set<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(Set<Condition> conditions) {
        this.conditions = conditions;
    }

    public String getDefinitionUri() {
        return definitionUri;
    }

    public void setDefinitionUri(String definitionUri) {
        this.definitionUri = definitionUri;
    }

    static class SubjectConcept {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
