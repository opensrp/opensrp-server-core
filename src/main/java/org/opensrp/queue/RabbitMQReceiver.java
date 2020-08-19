package org.opensrp.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.parser.exception.FHIRParserException;
import com.ibm.fhir.model.resource.DomainResource;
import org.joda.time.DateTime;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.smartregister.utils.DateTimeTypeConverter;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class RabbitMQReceiver {

	private PlanEvaluator planEvaluator;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private ClientsRepository clientsRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private EventsRepository eventsRepository;

	private FHIRParser fhirParser;

	private static Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class.toString());

	@PostConstruct
	public void init() {
		PathEvaluatorLibrary.init(locationRepository, clientsRepository, taskRepository, eventsRepository);
		planEvaluator = new PlanEvaluator("");
		fhirParser = FHIRParser.parser(Format.JSON);
	}

	@RabbitHandler
	@RabbitListener(queues = "rabbitmq.task.queue")
	public void receiver(PlanEvaluatorMessage planEvaluatorMessage) {
		logger.info("PlanEvaluatorMessage listener invoked - Consuming Message - " + planEvaluatorMessage);
		if (planEvaluatorMessage != null) {
			logger.info("CustomPlanEvaluatorMessage received : ", planEvaluatorMessage);
			if (planEvaluatorMessage != null) {
				planEvaluator.evaluatePlan(planEvaluatorMessage.getPlanDefinition(),
						planEvaluatorMessage.getTriggerType(),
						planEvaluatorMessage.getJurisdiction(), null);
			}
		}
	}

	@RabbitHandler
	@RabbitListener(queues = "rabbitmq.task.queue")
	public void receiver(ResourceEvaluatorMessage resourceEvaluatorMessage) {
		System.out.println("ResourceEvaluatorMessage invoked - Consuming Message: " + resourceEvaluatorMessage);
		InputStream stream = new ByteArrayInputStream(
				resourceEvaluatorMessage.getResource().getBytes(StandardCharsets.UTF_8));
		try {
			DomainResource resource = fhirParser.parse(stream);
			planEvaluator.evaluateResource(resource, resourceEvaluatorMessage.getQuestionnaireResponse(),
					resourceEvaluatorMessage.action, resourceEvaluatorMessage.planIdentifier,
					resourceEvaluatorMessage.jurisdictionCode, resourceEvaluatorMessage.getTriggerType());
		}
		catch (FHIRParserException e) {
			logger.error("FHIRParserException occurred " + e.getMessage());
		}
	}

}
