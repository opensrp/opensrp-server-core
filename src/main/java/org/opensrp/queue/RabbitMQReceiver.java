package org.opensrp.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQReceiver implements MessageListener {

	@Autowired
	private AmqpTemplate rabbitTemplate;

	private PlanEvaluator planEvaluator;

	@Autowired
	private Queue queue;

	@Autowired
	AmqpAdmin amqpAdmin;

	@Value("${rabbitmq.queue}")
	String queueName;

	private static Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class.toString());

	@RabbitListener(queues = "rabbitmq.task.queue")
	public void onMessage(Message message) {
		logger.info("Consuming Message - " + new String(message.getBody()));
		int count = (Integer) amqpAdmin.getQueueProperties(queue.getName()).get("QUEUE_MESSAGE_COUNT");
		CustomPlanEvaluatorMessage customPlanEvaluatorMessage = null;

		if (count >= 1) {
			logger.info("Messages in queue present");
			customPlanEvaluatorMessage = (CustomPlanEvaluatorMessage) rabbitTemplate
					.receiveAndConvert(queue.getName());
		}
		logger.info("CustomPlanEvaluatorMessage received : ", customPlanEvaluatorMessage);
		if (customPlanEvaluatorMessage != null) {
			planEvaluator.evaluatePlan(customPlanEvaluatorMessage.getPlanDefinition(),
					customPlanEvaluatorMessage.getTriggerType(),
					customPlanEvaluatorMessage.getJurisdiction(), null);
		}
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

	public void setPlanEvaluator(PlanEvaluator planEvaluator) {
		this.planEvaluator = planEvaluator;
	}
}
