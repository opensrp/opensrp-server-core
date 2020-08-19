package org.opensrp.config;

import org.opensrp.queue.PlanEvaluatorMessage;
import org.opensrp.queue.ResourceEvaluatorMessage;
import org.slf4j.Logger;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

@EnableRabbit
@Configuration
@ComponentScan("org.opensrp")
public class RabbitMQConfig {

	@Value("${rabbitmq.queue}")
	private String queueName;

	@Value("${rabbitmq.exchange}")
	private String exchange;

	@Value("${rabbitmq.routingkey}")
	private String routingkey;

	@Value("${rabbitmq.username}")
	private String username;

	@Value("${rabbitmq.password}")
	private String password;

	@Value("${rabbitmq.host}")
	private String host;

	@Value("${rabbitmq.virtualhost}")
	private String virtualHost;

	@Bean
	public Queue queue() {
		return new Queue(queueName, false);
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(exchange);
	}

	@Bean
	public Binding binding(Queue queue, DirectExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingkey);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
		DefaultClassMapper classMapper = new DefaultClassMapper();
		classMapper.setTrustedPackages("*");
		Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
		idClassMapping.put(
				"org.opensrp.queue.PlanEvaluatorMessage", PlanEvaluatorMessage.class);
		idClassMapping.put(
				"org.opensrp.queue.ResourceEvaluatorMessage", ResourceEvaluatorMessage.class);
		classMapper.setIdClassMapping(idClassMapping);
		converter.setClassMapper(classMapper);

		return converter;

	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setVirtualHost(virtualHost);
		connectionFactory.setHost(host);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		return connectionFactory;
	}

	@Bean
	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setDefaultReceiveQueue(queueName);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		rabbitTemplate.setReplyAddress(queue().getName());
		rabbitTemplate.setReplyTimeout(60000);
		rabbitTemplate.setUseDirectReplyToContainer(false);
		return rabbitTemplate;
	}

	@Bean
	public AmqpAdmin amqpAdmin() {
		return new RabbitAdmin(connectionFactory());
	}

	@Bean
	public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
		final SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory());
		factory.setMessageConverter(jsonMessageConverter());
		factory.setConcurrentConsumers(1);
		factory.setMaxConcurrentConsumers(1);
		factory.setErrorHandler(errorHandler());
		return factory;
	}

	@Bean
	public ErrorHandler errorHandler() {
		return new ConditionalRejectingErrorHandler(new MyFatalExceptionStrategy());
	}

	public static class MyFatalExceptionStrategy extends ConditionalRejectingErrorHandler.DefaultExceptionStrategy {

		private final Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

		@Override
		public boolean isFatal(Throwable t) {
			if (t instanceof ListenerExecutionFailedException) {
				ListenerExecutionFailedException lefe = (ListenerExecutionFailedException) t;
				logger.error("Failed to process inbound message from queue "
						+ lefe.getFailedMessage().getMessageProperties().getConsumerQueue()
						+ "; failed message: " + lefe.getFailedMessage(), t);
			}
			return super.isFatal(t);
		}

	}
}
