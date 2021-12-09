package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    @JsonProperty("goal")
    @SerializedName("goal")
    private List<GoalTemplate> goals;
    @JsonProperty("action")
    @SerializedName("action")
    private List<ActionTemplate> actions;
    @JsonProperty
    private boolean experimental;

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UseContext implements Serializable {
        private String code;
        private String valueCodableConcept;

    }
}
