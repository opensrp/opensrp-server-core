/**
 * 
 */
package org.opensrp.service;

import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author Samuel Githengi created on 06/12/20
 */
@Service
public class TaskGenerator {
	
	private PlanEvaluator planEvaluator;
	
	public TaskGenerator() {
		planEvaluator = new PlanEvaluator();
	}
	
	@Async
	public void processPlanEvaluation(PlanDefinition planDefinition, PlanDefinition existingPlanDefinition) {
		planEvaluator.evaluatePlan(planDefinition, existingPlanDefinition);
	}
}
