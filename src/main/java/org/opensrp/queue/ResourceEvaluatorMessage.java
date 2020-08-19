package org.opensrp.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.fhir.model.resource.QuestionnaireResponse;
import lombok.*;
import org.smartregister.domain.Action;
import org.smartregister.pathevaluator.TriggerType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResourceEvaluatorMessage implements Serializable {

	@JsonProperty
	String resource;

	@JsonProperty
	QuestionnaireResponse questionnaireResponse;

	@JsonProperty
	Action action;

	@JsonProperty
	String planIdentifier;

	@JsonProperty
	String jurisdictionCode;

	@JsonProperty
	TriggerType triggerType;

}
