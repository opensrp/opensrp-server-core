package org.opensrp.queue;

import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQReceiver implements MessageListener {

	@Autowired
	private AmqpTemplate rabbitTemplate;

	private PlanEvaluator planEvaluator;

	public void onMessage(Message message) {
		System.out.println("Consuming Message - " + new String(message.getBody()));
		CustomPlanEvaluatorMessage customPlanEvaluatorMessage = (CustomPlanEvaluatorMessage) rabbitTemplate.receiveAndConvert();
		planEvaluator.evaluatePlan(customPlanEvaluatorMessage.getPlanDefinition(), customPlanEvaluatorMessage.getTriggerType(), customPlanEvaluatorMessage.getJurisdiction(), null);
	}
}
