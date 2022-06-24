/**
 *
 */
package org.opensrp.queue.sender;

import com.ibm.fhir.model.format.Format;
import com.ibm.fhir.model.parser.FHIRParser;
import com.ibm.fhir.model.parser.exception.FHIRParserException;
import com.ibm.fhir.model.resource.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.queue.PlanEvaluatorMessage;
import org.opensrp.queue.QueueHelper;
import org.opensrp.queue.ResourceEvaluatorMessage;
import org.opensrp.service.PlanService;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Samuel Githengi created on 08/28/20
 */
@Profile("!rabbitmq")
@Component
public class InternalSenderImpl implements MessageSender {

    private static Logger logger = LogManager.getLogger(InternalSenderImpl.class.toString());

    @Autowired
    private PlanService planservice;

    @Autowired
    @Lazy
    private QueueHelper queueHelper;


    @Override
    public void send(PlanEvaluatorMessage planMessage) {
        PlanEvaluator planEvaluator = new PlanEvaluator(planMessage.getUsername(), queueHelper);
        planEvaluator.evaluatePlan(planservice.getPlan(planMessage.getPlanIdentifier()), planMessage.getTriggerType(),
                planMessage.getJurisdiction(), null);

    }

    @Override
    public void send(ResourceEvaluatorMessage resourceMessage) {
        InputStream stream = new ByteArrayInputStream(resourceMessage.getResource().getBytes(StandardCharsets.UTF_8));
        PlanEvaluator planEvaluator = new PlanEvaluator(resourceMessage.getUsername(), queueHelper);
        try {
            Resource domainResource = FHIRParser.parser(Format.JSON).parse(stream);
            if (domainResource != null && resourceMessage != null && resourceMessage.getAction() != null) {
                planEvaluator.evaluateResource(domainResource, resourceMessage.getQuestionnaireResponse(),
                        resourceMessage.getAction(), resourceMessage.getPlanIdentifier(), resourceMessage.getJurisdictionCode(),
                        resourceMessage.getTriggerType());
            }
        } catch (FHIRParserException e) {
            logger.error("FHIRParserException occurred " + e.getMessage());
        }

    }

}
