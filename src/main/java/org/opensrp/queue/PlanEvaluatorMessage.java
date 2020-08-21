package org.opensrp.queue;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.pathevaluator.TriggerType;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanEvaluatorMessage implements Serializable {

	private static final long serialVersionUID = -1138446817700416884L;

	@JsonProperty
	private String planIdentifier;

	@JsonProperty
	private TriggerType triggerType;

	@JsonProperty
	private Jurisdiction jurisdiction;
}
