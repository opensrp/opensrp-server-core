/**
 * 
 */
package org.opensrp.service;

import javax.annotation.PostConstruct;

import org.opensrp.queue.QueueHelper;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.TaskRepository;
import org.opensrp.repository.EventsRepository;
import org.smartregister.converters.EventConverter;
import org.smartregister.domain.Event;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Samuel Githengi created on 06/12/20
 */
@Service
public class TaskGenerator {
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private ClientsRepository clientsRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	private EventsRepository eventsRepository;

	@Autowired
	@Lazy
	private QueueHelper queueHelper;
	
	@PostConstruct
	private void postConstruct() {
		PathEvaluatorLibrary.init(locationRepository, clientsRepository, taskRepository, eventsRepository);
	}
	
	@Async
	public void processPlanEvaluation(PlanDefinition planDefinition, PlanDefinition existingPlanDefinition, String username) {
		PlanEvaluator planEvaluator = new PlanEvaluator(username,queueHelper);
		planEvaluator.evaluatePlan(planDefinition, existingPlanDefinition);
	}

	@Async
	public void processPlanEvaluation(PlanDefinition planDefinition, String username, Event event) {
		PlanEvaluator planEvaluator = new PlanEvaluator(username);
		planEvaluator.evaluatePlan(planDefinition, EventConverter.convertEventToEncounterResource(event));
	}
}
