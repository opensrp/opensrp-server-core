package org.opensrp.queue;

import org.opensrp.service.PlanService;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.TriggerType;
import org.springframework.beans.factory.annotation.Autowired;

public class QueueHelper implements org.smartregister.pathevaluator.dao.QueuingHelper {

	@Autowired
	private PlanService planService;

	@Autowired
	RabbitMQSender rabbitMQSender;

	@Override
	public void addToQueue(String planIdentifier, TriggerType triggerType, String locationId) {
		PlanDefinition planDefinition = planService.getPlan(planIdentifier);
		Jurisdiction jurisdiction = new Jurisdiction(locationId);
		CustomPlanEvaluatorMessage customPlanEvaluatorMessage = new CustomPlanEvaluatorMessage(planDefinition,triggerType,jurisdiction);
        rabbitMQSender.send(customPlanEvaluatorMessage);
	}
}
