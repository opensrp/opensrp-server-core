/**
 * 
 */
package org.opensrp.service;

import org.opensrp.domain.PlanDefinition;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


/**
 * @author Samuel Githengi created on 06/12/20
 */
@Service
public class TaskGenerator {
	
	@Async
	public void processPlanEvaluation(PlanDefinition planDefinition, PlanDefinition existingPlanDefinition) {
		//TODO consider moving PlanEvaluator to container managed bean
		//TODO uncomment after domains are moved to plan evaluator lib
		/*PlanEvaluator planEvaluator = new PlanEvaluator();
		planEvaluator.evaluatePlan(planDefinition, existingPlanDefinition);*/
	}
}
