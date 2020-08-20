package org.opensrp.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.parser.exception.FHIRParserException;
import com.ibm.fhir.model.resource.DomainResource;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@RabbitListener(queues = "rabbitmq.task.queue")
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
	public void receiver(PlanEvaluatorMessage planEvaluatorMessage) {
		logger.info("PlanEvaluatorMessage listener invoked - Consuming Message - " + planEvaluatorMessage);
		if (planEvaluatorMessage != null) {
			planEvaluator.evaluatePlan(planEvaluatorMessage.getPlanDefinition(),
					planEvaluatorMessage.getTriggerType(),
					planEvaluatorMessage.getJurisdiction(), null);
		}
	}

	@RabbitHandler
	public void receiver(ResourceEvaluatorMessage resourceEvaluatorMessage) {
		logger.info("ResourceEvaluatorMessage invoked - Consuming Message: " + resourceEvaluatorMessage);
		if (resourceEvaluatorMessage != null) {
			InputStream stream = resourceEvaluatorMessage.getResource() != null ? new ByteArrayInputStream(
					resourceEvaluatorMessage.getResource().getBytes(StandardCharsets.UTF_8)) : null;
			try {
				if (stream != null) {
					DomainResource resource = fhirParser.parse(stream);
					if (resource != null && resourceEvaluatorMessage != null) {
						planEvaluator.evaluateResource(resource, resourceEvaluatorMessage.getQuestionnaireResponse(),
								resourceEvaluatorMessage.getAction(), resourceEvaluatorMessage.getPlanIdentifier(),
								resourceEvaluatorMessage.getJurisdictionCode(), resourceEvaluatorMessage.getTriggerType());
					}
				}
			}
			catch (FHIRParserException e) {
				logger.error("FHIRParserException occurred " + e.getMessage());
			}
		}
	}

}
