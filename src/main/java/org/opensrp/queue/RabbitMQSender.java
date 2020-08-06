package org.opensrp.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {

	private AmqpTemplate rabbitTemplate;

	private Queue queue;

	private static Logger logger = LoggerFactory.getLogger(RabbitMQSender.class.toString());

	public RabbitMQSender(AmqpTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void send(PlanEvaluatorMessage planEvaluatorMessage) {
		rabbitTemplate.convertAndSend(queue.getName(), planEvaluatorMessage);
		logger.info("Send Message : " + planEvaluatorMessage.toString());
	}

	public void setRabbitTemplate(AmqpTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}
}
