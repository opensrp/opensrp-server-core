/**
 * 
 */
package org.opensrp.service;

import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.TaskRepository;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	public TaskGenerator() {
		PathEvaluatorLibrary.init(locationRepository,clientsRepository,taskRepository,eventsRepository);
		PathEvaluatorLibrary.getInstance();
		
	}
	
	@Async
	public void processPlanEvaluation(PlanDefinition planDefinition, PlanDefinition existingPlanDefinition) {
		PlanEvaluator planEvaluator = new PlanEvaluator();
		planEvaluator.evaluatePlan(planDefinition, existingPlanDefinition);
	}
}
