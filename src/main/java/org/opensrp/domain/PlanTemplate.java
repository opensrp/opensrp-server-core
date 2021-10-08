package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import org.smartregister.domain.*;

import java.io.Serializable;
import java.util.List;

public class PlanTemplate  implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonProperty
    private String identifier;
    @JsonProperty
    private String description;
    @JsonProperty
    private String version;
    @JsonProperty
    private String name;
    @JsonProperty
    private String title;
    @JsonProperty
    private String status;
    @JsonProperty
    private String date;
    @JsonProperty
    private PeriodTemplate effectivePeriod;
    @JsonProperty
    private List<PlanDefinition.UseContext> useContext;
    @JsonProperty
    private List<Jurisdiction> jurisdiction;
    private Long serverVersion;
    @JsonProperty
    @SerializedName("goal")
    private List<GoalTemplate> goals;
    @JsonProperty
    @SerializedName("action")
    private List<ActionTemplate> actions;
    @JsonProperty
    private boolean experimental;

    public PlanTemplate() {
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public PeriodTemplate getEffectivePeriod() {
        return this.effectivePeriod;
    }

    public void setEffectivePeriod(PeriodTemplate effectivePeriod) {
        this.effectivePeriod = effectivePeriod;
    }

    public List<PlanDefinition.UseContext> getUseContext() {
        return this.useContext;
    }

    public void setUseContext(List<PlanDefinition.UseContext> useContext) {
        this.useContext = useContext;
    }

    public List<Jurisdiction> getJurisdiction() {
        return this.jurisdiction;
    }

    public void setJurisdiction(List<Jurisdiction> jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public List<GoalTemplate> getGoals() {
        return this.goals;
    }

    public void setGoals(List<GoalTemplate> goals) {
        this.goals = goals;
    }

    public List<ActionTemplate> getActions() {
        return this.actions;
    }

    public void setActions(List<ActionTemplate> actions) {
        this.actions = actions;
    }

    public Long getServerVersion() {
        return this.serverVersion;
    }

    public void setServerVersion(Long serverVersion) {
        this.serverVersion = serverVersion;
    }

    public boolean isExperimental() {
        return this.experimental;
    }

    public void setExperimental(boolean experimental) {
        this.experimental = experimental;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int compareTo(PlanDefinition o) {
        return this.getName().equals(o.getName()) ? this.getName().compareTo(o.getIdentifier()) : this.getName().compareTo(o.getName());
    }

    public static enum PlanStatus {
        @SerializedName("draft")
        DRAFT("draft"),
        @SerializedName("active")
        ACTIVE("active"),
        @SerializedName("retired")
        RETIRED("retired"),
        @SerializedName("complete")
        COMPLETED("complete"),
        @SerializedName("unknown")
        UNKNOWN("unknown");

        private final String value;

        private PlanStatus(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }

        public static PlanTemplate.PlanStatus from(String value) {
            PlanTemplate.PlanStatus[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                PlanTemplate.PlanStatus c = var1[var3];
                if (c.value.equals(value)) {
                    return c;
                }
            }

            throw new IllegalArgumentException(value);
        }
    }

    public static class UseContext implements Serializable {
        private String code;
        private String valueCodableConcept;

        public UseContext() {
        }

        public String getCode() {
            return this.code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getValueCodableConcept() {
            return this.valueCodableConcept;
        }

        public void setValueCodableConcept(String valueCodableConcept) {
            this.valueCodableConcept = valueCodableConcept;
        }
    }
}
