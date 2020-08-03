package org.opensrp.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQSender {

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Value("${rabbitmq.exchange}")
	private String exchange;

	@Value("${rabbitmq.routingkey}")
	private String routingkey;

	public void send(CustomPlanEvaluatorMessage customPlanEvaluatorMessage) {
		rabbitTemplate.convertAndSend(exchange, routingkey, customPlanEvaluatorMessage);
		System.out.println("Send msg = " + customPlanEvaluatorMessage);
	}

	public void setRabbitTemplate(AmqpTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}
}
