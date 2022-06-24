package org.opensrp.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.pathevaluator.TriggerType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanEvaluatorMessage implements Serializable {

    private static final long serialVersionUID = -1138446817700416884L;

    @JsonProperty
    private String planIdentifier;

    @JsonProperty
    private TriggerType triggerType;

    @JsonProperty
    private Jurisdiction jurisdiction;

    @JsonProperty
    private String username;

    @Override
    public String toString() {
        String result = "PlanIdentifier : " + getPlanIdentifier() + ", TriggerType : " + getTriggerType() + ", Jurisdiction Code : " + getJurisdiction();
        return result;
    }
}
