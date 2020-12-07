package org.opensrp.queue.sender;

import org.opensrp.queue.PlanEvaluatorMessage;
import org.opensrp.queue.ResourceEvaluatorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("rabbitmq")
@Component
public class RabbitMQSenderImpl implements MessageSender{

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private Queue queue;

	private static Logger logger = LoggerFactory.getLogger(RabbitMQSenderImpl.class.toString());

	public RabbitMQSenderImpl(AmqpTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void send(PlanEvaluatorMessage planEvaluatorMessage) {
		rabbitTemplate.convertAndSend(queue.getName(), planEvaluatorMessage);
		logger.info("Send Message : " + planEvaluatorMessage.toString());
	}

	@Override
	public void send(ResourceEvaluatorMessage resourceEvaluatorMessage) {
		rabbitTemplate.convertAndSend(queue.getName(), resourceEvaluatorMessage);
		logger.info("Send Message : " + resourceEvaluatorMessage.toString());
	}

	public void setRabbitTemplate(AmqpTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}
}
