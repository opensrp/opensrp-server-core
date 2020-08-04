package org.opensrp.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQReceiver implements MessageListener {

	private AmqpTemplate rabbitTemplate;

	private PlanEvaluator planEvaluator;

	@Autowired
	private Queue queue;

	private static Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class.toString());

	public void onMessage(Message message) {
		logger.info("Consuming Message - " + new String(message.getBody()));
		CustomPlanEvaluatorMessage customPlanEvaluatorMessage = (CustomPlanEvaluatorMessage) rabbitTemplate
				.receiveAndConvert();
		planEvaluator.evaluatePlan(customPlanEvaluatorMessage.getPlanDefinition(), customPlanEvaluatorMessage.getTriggerType(),
						customPlanEvaluatorMessage.getJurisdiction(), null);
	}

	public CustomPlanEvaluatorMessage receiveMessage() {
		CustomPlanEvaluatorMessage customPlanEvaluatorMessage = (CustomPlanEvaluatorMessage) rabbitTemplate
				.receiveAndConvert(queue.getName());
		return customPlanEvaluatorMessage;
	}

	public void setRabbitTemplate(AmqpTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}
}
