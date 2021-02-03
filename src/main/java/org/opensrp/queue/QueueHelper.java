package org.opensrp.queue;

import org.opensrp.queue.sender.MessageSender;
import org.smartregister.domain.Action;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.pathevaluator.TriggerType;
import org.smartregister.pathevaluator.dao.QueuingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ibm.fhir.model.resource.QuestionnaireResponse;

@Component
public class QueueHelper implements QueuingHelper {
	
	@Autowired
	private MessageSender messageSender;
		
	private String username;
	
	@Override
	public void addToQueue(String planIdentifier, TriggerType triggerType, String locationId) {
		Jurisdiction jurisdiction = new Jurisdiction(locationId);
		PlanEvaluatorMessage planEvaluatorMessage = new PlanEvaluatorMessage(planIdentifier, triggerType, jurisdiction,
		        username);
		messageSender.send(planEvaluatorMessage);
	}
	
	@Override
	public void addToQueue(String resource, QuestionnaireResponse questionnaireResponse, Action action,
	        String planIdentifier, String jurisdictionCode, TriggerType triggerType) {
		ResourceEvaluatorMessage resourceEvaluatorMessage = new ResourceEvaluatorMessage(resource, questionnaireResponse,
		        action, planIdentifier, jurisdictionCode, triggerType, username);
		messageSender.send(resourceEvaluatorMessage);
	}
	
	public void setRabbitMQSender(MessageSender messageSender) {
		this.messageSender = messageSender;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
}
