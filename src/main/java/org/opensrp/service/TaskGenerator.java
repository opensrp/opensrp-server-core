/**
 *
 */
package org.opensrp.service;

import org.opensrp.queue.QueueHelper;
import org.opensrp.repository.*;
import org.smartregister.converters.EventConverter;
import org.smartregister.domain.Event;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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
    private StocksRepository stocksRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    @Lazy
    private QueueHelper queueHelper;

    @PostConstruct
    private void postConstruct() {
        PathEvaluatorLibrary.init(locationRepository, clientsRepository, taskRepository, eventsRepository, stocksRepository);
        PathEvaluatorLibrary.getInstance().setPlanDao(planRepository);
    }

    @Async
    public void processPlanEvaluation(PlanDefinition planDefinition, PlanDefinition existingPlanDefinition, String username) {
        if (!isInternalTaskGeneration(planDefinition)) {
            return;
        }
        PlanEvaluator planEvaluator = new PlanEvaluator(username, queueHelper);
        planEvaluator.evaluatePlan(planDefinition, existingPlanDefinition);
    }

    @Async
    public void processPlanEvaluation(PlanDefinition planDefinition, String username, Event event) {
        PlanEvaluator planEvaluator = new PlanEvaluator(username, queueHelper);
        planEvaluator.evaluatePlan(planDefinition, EventConverter.convertEventToEncounterResource(event));
    }

    public boolean isInternalTaskGeneration(PlanDefinition plan) {
        boolean internalTaskGeneration = false;
        for (PlanDefinition.UseContext useContext : plan.getUseContext()) {
            if (useContext.getCode().equalsIgnoreCase("taskGenerationStatus")
                    && useContext.getValueCodableConcept().equalsIgnoreCase("Internal")) {

                internalTaskGeneration = true;
                break;
            }
        }
        return internalTaskGeneration;
    }

}
