package org.opensrp.queue;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.parser.exception.FHIRParserException;
import com.ibm.fhir.model.resource.DomainResource;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.TaskRepository;
import org.opensrp.repository.StocksRepository;
import org.opensrp.service.PlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Profile("rabbitmq")
@Component
@RabbitListener(queues = "rabbitmq.task.queue", id = "listener")
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

	@Autowired
	private StocksRepository stocksRepository;

	@Autowired
	private PlanService planService;
	
	@Autowired
	private QueueHelper queueHelper;

	private FHIRParser fhirParser;

	private static Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class.toString());

	@PostConstruct
	public void init() {
		PathEvaluatorLibrary.init(locationRepository, clientsRepository, taskRepository, eventsRepository, stocksRepository);
		planEvaluator = new PlanEvaluator("",queueHelper);
		fhirParser = FHIRParser.parser(Format.JSON);
	}

	@RabbitHandler
	public void receiver(PlanEvaluatorMessage planEvaluatorMessage) {
		logger.info("PlanEvaluatorMessage listener invoked - Consuming Message with Plan Definition Identifier : " + planEvaluatorMessage.getPlanIdentifier());

		if (planEvaluatorMessage != null) {
			PlanDefinition planDefinition = planService.getPlan(planEvaluatorMessage.getPlanIdentifier());
			if (planDefinition != null && planDefinition.getActions() != null && planEvaluatorMessage.getJurisdiction() != null) {
				planEvaluator.evaluatePlan(planDefinition,
						planEvaluatorMessage.getTriggerType(),
						planEvaluatorMessage.getJurisdiction(), null);
			}
		}
	}

	@RabbitHandler
	public void receiver(ResourceEvaluatorMessage resourceEvaluatorMessage) {
		logger.info("ResourceEvaluatorMessage invoked - Consuming Message");
		if (resourceEvaluatorMessage != null) {
			InputStream stream = resourceEvaluatorMessage.getResource() != null ? new ByteArrayInputStream(
					resourceEvaluatorMessage.getResource().getBytes(StandardCharsets.UTF_8)) : null;
			try {
				if (stream != null) {
					DomainResource resource = fhirParser.parse(stream);
					logger.info("Resource id is : " + resource.getId());
					if (resource != null && resourceEvaluatorMessage != null
							&& resourceEvaluatorMessage.getAction() != null
							&& resourceEvaluatorMessage.getAction().getCondition() != null) {
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
