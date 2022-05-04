package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.smartregister.domain.Condition;
import org.smartregister.domain.DynamicValue;
import org.smartregister.domain.Trigger;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
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

    public static class SubjectConcept implements Serializable {
        private String text;

        public String getText() {
            return text;
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
