package org.opensrp.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
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
	private AmqpAdmin amqpAdmin;

	@Value("${rabbitmq.queue}")
	private String queueName;

	private static Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class.toString());

	@RabbitListener(queues = "rabbitmq.task.queue")
	public void onMessage(Message message) {
		logger.info("Consuming Message - " + new String(message.getBody()));
		int count = (Integer) amqpAdmin.getQueueProperties(queue.getName()).get("QUEUE_MESSAGE_COUNT");
		PlanEvaluatorMessage planEvaluatorMessage = null;

		if (count >= 1) {
			logger.info("Messages in queue present");
			planEvaluatorMessage = (PlanEvaluatorMessage) rabbitTemplate
					.receiveAndConvert(queue.getName());
		}
		logger.info("CustomPlanEvaluatorMessage received : ", planEvaluatorMessage);
		if (planEvaluatorMessage != null) {
			planEvaluator.evaluatePlan(planEvaluatorMessage.getPlanDefinition(),
					planEvaluatorMessage.getTriggerType(),
					planEvaluatorMessage.getJurisdiction(), null);
		}
	}

	public PlanEvaluatorMessage receiveMessage() {
		PlanEvaluatorMessage planEvaluatorMessage = (PlanEvaluatorMessage) rabbitTemplate
				.receiveAndConvert(queue.getName());
		return planEvaluatorMessage;
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
