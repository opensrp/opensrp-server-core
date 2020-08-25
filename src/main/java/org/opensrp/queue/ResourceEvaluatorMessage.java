package org.opensrp.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ibm.fhir.model.resource.QuestionnaireResponse;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.smartregister.domain.Action;
import org.smartregister.pathevaluator.TriggerType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResourceEvaluatorMessage implements Serializable {

	private static final long serialVersionUID = -1607322794388098644L;

	@JsonProperty
	private String resource;

	@JsonProperty
	private QuestionnaireResponse questionnaireResponse;

	@JsonProperty
	private Action action;

	@JsonProperty
	private String planIdentifier;

	@JsonProperty
	private String jurisdictionCode;

	@JsonProperty
	private TriggerType triggerType;

	@Override
	public String toString() {
		String result = "Action Identifier is : " + getAction() != null ? getAction().getIdentifier() : "null" + "PlanIdentifier : " + getPlanIdentifier() + ", Jurisdiction Code : " + getJurisdictionCode() + ", TriggerType : " + getTriggerType();
		return result;
	}

}
