package org.opensrp.queue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.pathevaluator.TriggerType;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class RabbitMQTest {

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private RabbitMQSender rabbitMQSender;

	@Autowired
	private RabbitMQReceiver rabbitMQReceiver;

	@Autowired
	private Queue queue;

	private static Logger logger = LoggerFactory.getLogger(RabbitMQTest.class.toString());

	@Configuration
	@Import(RabbitMQConfig.class) // the actual configuration
	public static class TestConfig {

		@Bean
		MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
			SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
			simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
			return simpleMessageListenerContainer;

		}
	}

	@Before
	public void setup() {
		amqpAdmin.purgeQueue(queue.getName());
		amqpAdmin.declareQueue(new Queue(queue.getName(), false));
	}

	@Test
	public void senderReceiverTestWithRabbitTemplate() {
		try {
			CustomPlanEvaluatorMessage customPlanEvaluatorMessage = new CustomPlanEvaluatorMessage();
			Jurisdiction jurisdiction = new Jurisdiction();
			jurisdiction.setCode("test-loc1");
			customPlanEvaluatorMessage.setJurisdiction(jurisdiction);
			customPlanEvaluatorMessage.setTriggerType(TriggerType.PLAN_ACTIVATION);

			rabbitTemplate.convertAndSend("javainuse.exchange", "javainuse.routingkey", customPlanEvaluatorMessage);
			CustomPlanEvaluatorMessage received = (CustomPlanEvaluatorMessage) rabbitTemplate.receiveAndConvert();
			assertEquals(customPlanEvaluatorMessage.getJurisdiction().getCode(), received.getJurisdiction().getCode());
			assertEquals(customPlanEvaluatorMessage.getTriggerType().name(), received.getTriggerType().name());
		}
		catch (AmqpException e) {
			Assert.fail("Test failed: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void senderReceiverTestWithComponents() {
		try {
			CustomPlanEvaluatorMessage customPlanEvaluatorMessage = new CustomPlanEvaluatorMessage();
			Jurisdiction jurisdiction = new Jurisdiction();
			jurisdiction.setCode("test-loc1");
			customPlanEvaluatorMessage.setJurisdiction(jurisdiction);
			rabbitMQSender.setRabbitTemplate(rabbitTemplate);
			rabbitMQSender.setQueue(queue);
			rabbitMQSender.send(customPlanEvaluatorMessage);
			rabbitMQReceiver.setRabbitTemplate(rabbitTemplate);
			rabbitMQReceiver.setQueue(queue);
			CustomPlanEvaluatorMessage received = rabbitMQReceiver.receiveMessage();

			logger.info("Message received : " + received);
			Assert.assertEquals(customPlanEvaluatorMessage.getJurisdiction().getCode(),
					received.getJurisdiction().getCode());

		}
		catch (AmqpException e) {
			Assert.fail("Test failed: " + e.getLocalizedMessage());
		}
	}

}
