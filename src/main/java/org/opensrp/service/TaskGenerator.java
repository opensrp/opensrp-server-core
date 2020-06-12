/**
 * 
 */
package org.opensrp.service;

import org.opensrp.domain.PlanDefinition;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


/**
 * @author Samuel Githengi created on 06/12/20
 */
@Service
public class TaskGenerator {
	
	@Async
	public void processPlanEvaluation(PlanDefinition planDefinition, PlanDefinition existingPlanDefinition) {
		//TODO consider mooving PlanEvaluator to container managed bean
		PlanEvaluator planEvaluator = new PlanEvaluator();
		//TODO uncomment after domains are moved to plan eveluator lib
		//planEvaluator.evaluatePlan(planDefinition, existingPlanDefinition);
	}
}
