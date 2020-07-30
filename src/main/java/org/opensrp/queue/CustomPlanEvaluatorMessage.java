package org.opensrp.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.TriggerType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomPlanEvaluatorMessage {

	@JsonProperty
	private PlanDefinition planDefinition;

	@JsonProperty
	private TriggerType triggerType;

	@JsonProperty
	private Jurisdiction jurisdiction;
}
