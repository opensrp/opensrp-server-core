package org.opensrp.queue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.service.PlanService;
import org.opensrp.service.TaskGenerator;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.parser.exception.FHIRParserException;
import com.ibm.fhir.model.resource.Resource;

@Profile("rabbitmq")
@Component
@RabbitListener(queues = "rabbitmq.task.queue", id = "listener")
public class RabbitMQReceiver {

    private static Logger logger = LogManager.getLogger(RabbitMQReceiver.class.toString());
    private PlanEvaluator planEvaluator;
    @Autowired
    private PlanService planService;
    @Autowired
    private QueueHelper queueHelper;
    //import task generator to guarantee PathEvaluatorLibrary is instantiated first
    @SuppressWarnings("unused")
    @Autowired
    private TaskGenerator taskGenerator;
    private FHIRParser fhirParser;

    @PostConstruct
    public void init() {
        fhirParser = FHIRParser.parser(Format.JSON);
    }

    @RabbitHandler
    public void receiver(PlanEvaluatorMessage planEvaluatorMessage) {
        logger.info("PlanEvaluatorMessage listener invoked - Consuming Message with Plan Definition Identifier : " + planEvaluatorMessage.getPlanIdentifier());
        initializePathEvaluator(planEvaluatorMessage.getUsername());

        if (planEvaluatorMessage != null && planEvaluator != null) {
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
                    Resource resource = fhirParser.parse(stream);
                    logger.info("Resource id is : " + resource.getId());
                    initializePathEvaluator(resourceEvaluatorMessage.getUsername());
                    if (resource != null && resourceEvaluatorMessage != null
                            && resourceEvaluatorMessage.getAction() != null
                            && resourceEvaluatorMessage.getAction().getCondition() != null
                            && planEvaluator != null) {
                        planEvaluator.evaluateResource(resource, resourceEvaluatorMessage.getQuestionnaireResponse(),
                                resourceEvaluatorMessage.getAction(), resourceEvaluatorMessage.getPlanIdentifier(),
                                resourceEvaluatorMessage.getJurisdictionCode(), resourceEvaluatorMessage.getTriggerType());
                    }
                }
            } catch (FHIRParserException e) {
                logger.error("FHIRParserException occurred " + e.getMessage());
            }
        }
    }

    private void initializePathEvaluator(String username) {
        if (planEvaluator == null)
            planEvaluator = new PlanEvaluator(username, queueHelper);
    }

}
