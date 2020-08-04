package org.opensrp.queue;

import org.opensrp.service.PlanService;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.TriggerType;
import org.smartregister.pathevaluator.dao.QueuingHelper;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class QueueHelper implements QueuingHelper {

	PlanService planService;

	RabbitMQSender rabbitMQSender;

	@Override
	public void addToQueue(String planIdentifier, TriggerType triggerType, String locationId) {
		PlanDefinition planDefinition = planService.getPlan(planIdentifier);
		Jurisdiction jurisdiction = new Jurisdiction(locationId);
		CustomPlanEvaluatorMessage customPlanEvaluatorMessage = new CustomPlanEvaluatorMessage(planDefinition,triggerType,jurisdiction); //todo : rename
		rabbitMQSender.send(customPlanEvaluatorMessage);
	}

	public void setPlanService(PlanService planService) {
		this.planService = planService;
	}

	public void setRabbitMQSender(RabbitMQSender rabbitMQSender) {
		this.rabbitMQSender = rabbitMQSender;
	}
}
