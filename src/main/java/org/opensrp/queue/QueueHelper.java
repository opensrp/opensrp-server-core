package org.opensrp.queue;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.parser.exception.FHIRParserException;
import com.ibm.fhir.model.resource.DomainResource;
import com.ibm.fhir.model.resource.QuestionnaireResponse;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.TaskRepository;
import org.opensrp.service.PlanService;
import org.smartregister.domain.Action;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.pathevaluator.TriggerType;
import org.smartregister.pathevaluator.dao.QueuingHelper;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QueueHelper implements QueuingHelper {

	@Autowired
	private PlanService planService;

	@Autowired
	private RabbitMQSender rabbitMQSender;

	@Autowired
	private LocationRepository locationRepository;

	@Autowired
	private ClientsRepository clientsRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private EventsRepository eventsRepository;

	private PlanEvaluator planEvaluator;

	private FHIRParser fhirParser;

	@Value("#{opensrp['rabbitmq.queuing.enabled']  ?: false}")
	private boolean isQueuingEnabled;

	public QueueHelper(PlanEvaluator planEvaluator, FHIRParser fhirParser) {
		this.planEvaluator = planEvaluator;
		this.fhirParser = fhirParser;
	}

	public QueueHelper() {

	}

	@PostConstruct
	public void init() {
		PathEvaluatorLibrary.init(locationRepository, clientsRepository, taskRepository, eventsRepository);
		planEvaluator = new PlanEvaluator("");
		fhirParser = FHIRParser.parser(Format.JSON);
	}

	@Override
	public void addToQueue(String planIdentifier, TriggerType triggerType, String locationId) {
		PlanDefinition planDefinition = planService.getPlan(planIdentifier);
		Jurisdiction jurisdiction = new Jurisdiction(locationId);
		PlanEvaluatorMessage planEvaluatorMessage = new PlanEvaluatorMessage(planIdentifier, triggerType, jurisdiction);
		if (isQueuingEnabled) {
			rabbitMQSender.send(planEvaluatorMessage);
		} else {
			planEvaluator.evaluatePlan(planDefinition, triggerType, jurisdiction, null);
		}

	}

	@Override
	public void addToQueue(String resource, QuestionnaireResponse questionnaireResponse, Action action,
			String planIdentifier, String jurisdictionCode,
			TriggerType triggerType) {
		ResourceEvaluatorMessage resourceEvaluatorMessage = new ResourceEvaluatorMessage(resource, questionnaireResponse,
				action, planIdentifier, jurisdictionCode, triggerType);
		if (isQueuingEnabled) {
			rabbitMQSender.send(resourceEvaluatorMessage);
		} else {
			InputStream stream = new ByteArrayInputStream(
					resourceEvaluatorMessage.getResource().getBytes(StandardCharsets.UTF_8));
			try {
				DomainResource domainResource = fhirParser.parse(stream);
				if (domainResource != null && resourceEvaluatorMessage!=null && resourceEvaluatorMessage.getAction() != null) {
					planEvaluator.evaluateResource(domainResource, resourceEvaluatorMessage.getQuestionnaireResponse(),
							resourceEvaluatorMessage.getAction(), resourceEvaluatorMessage.getPlanIdentifier(),
							resourceEvaluatorMessage.getJurisdictionCode(), resourceEvaluatorMessage.getTriggerType());
				}
			}
			catch (FHIRParserException e) {
				e.printStackTrace();
			}
		}
	}

	public void setPlanService(PlanService planService) {
		this.planService = planService;
	}

	public void setRabbitMQSender(RabbitMQSender rabbitMQSender) {
		this.rabbitMQSender = rabbitMQSender;
	}
}
