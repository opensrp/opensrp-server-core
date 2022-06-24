package org.opensrp.queue.sender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.queue.PlanEvaluatorMessage;
import org.opensrp.queue.ResourceEvaluatorMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("rabbitmq")
@Component
public class RabbitMQSenderImpl implements MessageSender {

    private static Logger logger = LogManager.getLogger(RabbitMQSenderImpl.class.toString());
    @Autowired
    private AmqpTemplate rabbitTemplate;
    @Autowired
    private Queue queue;

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
