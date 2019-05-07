package org.opensrp.domain;

import com.google.gson.annotations.SerializedName;

import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.LocalDate;
import org.opensrp.domain.postgres.Jurisdiction;

import java.util.List;

/**
 * Created by samuelgithengi on 4/29/19.
 */
public class PlanDefinition {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private String version;

    @JsonProperty
    private String name;

    @JsonProperty
    private String title;

    @JsonProperty
    private String status;

    @JsonProperty
    private LocalDate date;

    @JsonProperty
    private ExecutionPeriod effectivePeriod;

    @JsonProperty
    private List<UseContext> useContext;

    @JsonProperty
    private List<Jurisdiction> jurisdiction;

    private Long serverVersion;

    @JsonProperty
    @SerializedName("goal")
    private List<Goal> goals;

    @JsonProperty
    @SerializedName("action")
    private List<Action> actions;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ExecutionPeriod getEffectivePeriod() {
        return effectivePeriod;
    }

    public void setEffectivePeriod(ExecutionPeriod effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
    }

    public List<UseContext> getUseContext() {
        return useContext;
    }

    public void setUseContext(List<UseContext> useContext) {
        this.useContext = useContext;
    }

    public List<Jurisdiction> getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(List<Jurisdiction> jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Long getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(Long serverVersion) {
        this.serverVersion = serverVersion;
    }

    static class UseContext {
        private String code;

        private String valueCodableConcept;

        public UseContext() {}

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValueCodableConcept() {
            return valueCodableConcept;
        }

        public void setValueCodableConcept(String valueCodableConcept) {
            this.valueCodableConcept = valueCodableConcept;
        }
    }
}




