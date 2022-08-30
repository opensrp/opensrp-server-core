package org.opensrp.service;

import java.util.Collections;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.smartregister.domain.Period;
import org.smartregister.domain.PlanDefinition;

public class TaskGeneratorTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private TaskGenerator taskGenerator;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        taskGenerator = new TaskGenerator();
    }

    @Test
    public void testIsInternalTaskGenerationPlanWithInternalTaskGenerationStatusReturnsTrue() {

        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("plan-id-1");
        plan.setStatus(PlanDefinition.PlanStatus.ACTIVE);
        Period executionPeriod = new Period();
        executionPeriod.setEnd(new LocalDate().plusYears(2).toDateTimeAtStartOfDay());
        plan.setEffectivePeriod(executionPeriod);
        PlanDefinition.UseContext useContext = new PlanDefinition.UseContext();
        useContext.setCode("taskGenerationStatus");
        useContext.setValueCodableConcept("internal");
        plan.setUseContext(Collections.singletonList(useContext));

        boolean isInternalTaskGeneration = taskGenerator.isInternalTaskGeneration(plan);

        Assert.assertTrue(isInternalTaskGeneration);

    }

    @Test
    public void testIsInternalTaskGenerationPlanWithoutInternalTaskGenerationStatusReturnsTrue() {

        PlanDefinition plan = new PlanDefinition();
        plan.setIdentifier("plan-id-1");
        plan.setStatus(PlanDefinition.PlanStatus.ACTIVE);
        Period executionPeriod = new Period();
        executionPeriod.setEnd(new LocalDate().plusYears(2).toDateTimeAtStartOfDay());
        plan.setEffectivePeriod(executionPeriod);
        PlanDefinition.UseContext useContext = new PlanDefinition.UseContext();
        useContext.setCode("taskGenerationStatus");
        useContext.setValueCodableConcept("True");
        plan.setUseContext(Collections.singletonList(useContext));

        boolean isInternalTaskGeneration = taskGenerator.isInternalTaskGeneration(plan);

        Assert.assertFalse(isInternalTaskGeneration);

    }

}
