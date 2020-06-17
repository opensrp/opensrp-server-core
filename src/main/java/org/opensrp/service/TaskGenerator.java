/**
 * 
 */
package org.opensrp.service;

import org.opensrp.repository.ClientsRepository;
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
	
	private PlanEvaluator planEvaluator;
	
	private PathEvaluatorLibrary pathEvaluatorLibrary;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private ClientsRepository clientsRepository;
	
	@Autowired
	private TaskRepository taskRepository;
	
	public TaskGenerator() {
		PathEvaluatorLibrary.init();
		pathEvaluatorLibrary = PathEvaluatorLibrary.getInstance();
		pathEvaluatorLibrary.setLocationDao(locationRepository);
		pathEvaluatorLibrary.setClientDao(clientsRepository);
		pathEvaluatorLibrary.setTaskDao(taskRepository);
		planEvaluator = new PlanEvaluator();
	}
	
	@Async
	public void processPlanEvaluation(PlanDefinition planDefinition, PlanDefinition existingPlanDefinition) {
		planEvaluator.evaluatePlan(planDefinition, existingPlanDefinition);
	}
}
