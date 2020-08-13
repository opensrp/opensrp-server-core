package org.opensrp.queue;

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

    String resource;
    QuestionnaireResponse questionnaireResponse;
    Action action;
    String planIdentifier;
    String jurisdictionCode;
    TriggerType triggerType;

}
