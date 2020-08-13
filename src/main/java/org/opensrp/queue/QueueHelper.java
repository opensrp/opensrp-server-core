package org.opensrp.queue;

import com.ibm.fhir.model.resource.QuestionnaireResponse;
import org.opensrp.service.PlanService;
import org.smartregister.domain.Action;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.TriggerType;
import org.smartregister.pathevaluator.dao.QueuingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueHelper implements QueuingHelper {

	@Autowired
	private PlanService planService;

	@Autowired
	private RabbitMQSender rabbitMQSender;

	@Override
	public void addToQueue(String planIdentifier, TriggerType triggerType, String locationId) {
		PlanDefinition planDefinition = planService.getPlan(planIdentifier);
		Jurisdiction jurisdiction = new Jurisdiction(locationId);
		PlanEvaluatorMessage planEvaluatorMessage = new PlanEvaluatorMessage(planDefinition,triggerType,jurisdiction);
		rabbitMQSender.send(planEvaluatorMessage);
	}

	@Override
	public void addToQueue(String resource, QuestionnaireResponse questionnaireResponse, Action action, String planIdentifier, String jurisdictionCode,
			TriggerType triggerType) {
		ResourceEvaluatorMessage resourceEvaluatorMessage = new ResourceEvaluatorMessage(resource, questionnaireResponse,
				action, planIdentifier, jurisdictionCode, triggerType);
		rabbitMQSender.send(resourceEvaluatorMessage);
	}

	public void setPlanService(PlanService planService) {
		this.planService = planService;
	}

	public void setRabbitMQSender(RabbitMQSender rabbitMQSender) {
		this.rabbitMQSender = rabbitMQSender;
	}
}
