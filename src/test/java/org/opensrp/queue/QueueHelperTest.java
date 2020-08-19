package org.opensrp.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.fhir.model.resource.DomainResource;
import com.ibm.fhir.model.resource.QuestionnaireResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

import org.mockito.Mockito;
import org.opensrp.repository.LocationRepository;
import org.opensrp.service.PlanService;
import org.smartregister.domain.Action;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.pathevaluator.TriggerType;
import org.smartregister.pathevaluator.condition.ConditionHelper;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.smartregister.utils.DateTypeConverter;
import org.smartregister.utils.TaskDateTimeTypeConverter;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class QueueHelperTest {

	@Mock
	private PlanService planService;

	@Mock
	private PlanEvaluator planEvaluator;

	@Autowired
	private RabbitMQSender rabbitMQSender;

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private Queue queue;

	@InjectMocks
	private QueueHelper queueHelper;

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	LocationRepository locationRepository;

	@Mock
	private ConditionHelper conditionHelper;

	public static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new TaskDateTimeTypeConverter())
			.registerTypeAdapter(LocalDate.class, new DateTypeConverter()).create();

	public static String plan = "{\"identifier\":\"d18f15ec-afaf-42f3-ba96-d207c456645b\",\"version\":\"2\",\"name\":\"A1-59ad4fa0-1945-4b50-a6e3-a056a7cdceb2-2019-09-09\",\"title\":\"A1 - Ban Khane Chu OA - 2019-09-09(deprecated)\",\"status\":\"retired\",\"date\":\"2019-09-09\",\"effectivePeriod\":{\"start\":\"2019-09-09\",\"end\":\"2019-09-29\"},\"useContext\":[{\"code\":\"interventionType\",\"valueCodableConcept\":\"FI\"},{\"code\":\"fiStatus\",\"valueCodableConcept\":\"A1\"},{\"code\":\"fiReason\",\"valueCodableConcept\":\"Routine\"},{\"code\":\"caseNum\",\"valueCodableConcept\":\"3336\"},{\"code\":\"opensrpEventId\",\"valueCodableConcept\":\"75049cd6-d77b-4239-9092-7bd1aa0d438c\"},{\"code\":\"taskGenerationStatus\",\"valueCodableConcept\":\"True\"}],\"jurisdiction\":[{\"code\":\"59ad4fa0-1945-4b50-a6e3-a056a7cdceb2\"}],\"serverVersion\":1568110931837,\"goal\":[{\"id\":\"Case_Confirmation\",\"description\":\"Confirm the index case\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of cases confirmed\",\"detail\":{\"detailQuantity\":{\"value\":1,\"comparator\":\">=\",\"unit\":\"case(s)\"}},\"due\":\"2019-09-19\"}]},{\"id\":\"RACD_register_families\",\"description\":\"Register all families and family members in all residential structures enumerated or added (100%) within operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of residential structures with full family registration\",\"detail\":{\"detailQuantity\":{\"value\":100,\"comparator\":\">=\",\"unit\":\"Percent\"}},\"due\":\"2019-09-29\"}]},{\"id\":\"RACD_Blood_Screening\",\"description\":\"Visit all residential structures (100%) within a 1 km radius of a confirmed index case and test each registered person\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of registered people tested\",\"detail\":{\"detailQuantity\":{\"value\":50,\"comparator\":\">=\",\"unit\":\"Person(s)\"}},\"due\":\"2019-09-29\"}]},{\"id\":\"RACD_bednet_distribution\",\"description\":\"Visit 100% of residential structures in the operational area and provide nets\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Percent of residential structures received nets\",\"detail\":{\"detailQuantity\":{\"value\":90,\"comparator\":\">=\",\"unit\":\"Percent\"}},\"due\":\"2019-09-29\"}]},{\"id\":\"Larval_Dipping\",\"description\":\"Perform a minimum of three larval dipping activities in the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of larval dipping activities completed\",\"detail\":{\"detailQuantity\":{\"value\":3,\"comparator\":\">=\",\"unit\":\"activit(y|ies)\"}},\"due\":\"2019-09-29\"}]},{\"id\":\"Mosquito_Collection\",\"description\":\"Set a minimum of three mosquito collection traps and complete the mosquito collection process\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of mosquito collection activities completed\",\"detail\":{\"detailQuantity\":{\"value\":3,\"comparator\":\">=\",\"unit\":\"activit(y|ies)\"}},\"due\":\"2019-09-29\"}]},{\"id\":\"BCC_Focus\",\"description\":\"Complete at least 1 BCC activity for the operational area\",\"priority\":\"medium-priority\",\"target\":[{\"measure\":\"Number of BCC Activities Completed\",\"detail\":{\"detailQuantity\":{\"value\":1,\"comparator\":\">=\",\"unit\":\"activit(y|ies)\"}},\"due\":\"2019-09-29\"}]}],\"action\":[{\"identifier\":\"baebfd98-2f1f-4a5e-9478-680f13b45697\",\"prefix\":1,\"title\":\"Case Confirmation\",\"description\":\"Confirm the index case\",\"code\":\"Case Confirmation\",\"timingPeriod\":{\"start\":\"2019-09-09\",\"end\":\"2019-09-19\"},\"reason\":\"Investigation\",\"goalId\":\"Case_Confirmation\",\"subjectCodableConcept\":{\"text\":\"Case_Confirmation\"},\"taskTemplate\":\"Case_Confirmation\"},{\"identifier\":\"9f554b5a-0d00-4f18-996f-30dce50e57d8\",\"prefix\":2,\"title\":\"Family Registration\",\"description\":\"Register all families & family members in all residential structures enumerated (100%) within the operational area\",\"code\":\"RACD Register Family\",\"timingPeriod\":{\"start\":\"2019-09-09\",\"end\":\"2019-09-29\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_register_families\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"RACD_register_families\"},{\"identifier\":\"28164940-e2cb-4622-8153-b6ac8ff6e940\",\"prefix\":3,\"title\":\"Blood screening\",\"description\":\"Visit all residential structures (100%) within a 1 km radius of a confirmed index case and test each registered person\",\"code\":\"Blood Screening\",\"timingPeriod\":{\"start\":\"2019-09-09\",\"end\":\"2019-09-29\"},\"reason\":\"Investigation\",\"goalId\":\"RACD_Blood_Screening\",\"subjectCodableConcept\":{\"text\":\"Person\"},\"taskTemplate\":\"RACD_Blood_Screening\"},{\"identifier\":\"b9f68512-376f-49c9-bb53-80d9e81a73d2\",\"prefix\":4,\"title\":\"Bednet Distribution\",\"description\":\"Visit 100% of residential structures in the operational area and provide nets\",\"code\":\"Bednet Distribution\",\"timingPeriod\":{\"start\":\"2019-09-09\",\"end\":\"2019-09-29\"},\"reason\":\"Routine\",\"goalId\":\"RACD_bednet_distribution\",\"subjectCodableConcept\":{\"text\":\"Residential_Structure\"},\"taskTemplate\":\"Bednet_Distribution\"},{\"identifier\":\"3b3f265d-05fe-4c16-a5a1-141bac82f128\",\"prefix\":5,\"title\":\"Larval Dipping\",\"description\":\"Perform a minimum of three larval dipping activities in the operational area\",\"code\":\"Larval Dipping\",\"timingPeriod\":{\"start\":\"2019-09-09\",\"end\":\"2019-09-29\"},\"reason\":\"Investigation\",\"goalId\":\"Larval_Dipping\",\"subjectCodableConcept\":{\"text\":\"Breeding_Site\"},\"taskTemplate\":\"Larval_Dipping\"},{\"identifier\":\"c295655a-8630-4bc1-a5fd-79b22a9b1125\",\"prefix\":6,\"title\":\"Mosquito Collection\",\"description\":\"Set a minimum of three mosquito collection traps and complete the mosquito collection process\",\"code\":\"Mosquito Collection\",\"timingPeriod\":{\"start\":\"2019-09-09\",\"end\":\"2019-09-29\"},\"reason\":\"Investigation\",\"goalId\":\"Mosquito_Collection\",\"subjectCodableConcept\":{\"text\":\"Mosquito_Collection_Point\"},\"taskTemplate\":\"Mosquito_Collection_Point\"},{\"identifier\":\"8b899c73-56c8-40a2-a738-6a62b459e8f8\",\"prefix\":7,\"title\":\"Behaviour Change Communication\",\"description\":\"Conduct BCC activity\",\"code\":\"BCC\",\"timingPeriod\":{\"start\":\"2019-09-09\",\"end\":\"2019-09-29\"},\"reason\":\"Investigation\",\"goalId\":\"BCC_Focus\",\"subjectCodableConcept\":{\"text\":\"Operational_Area\"},\"taskTemplate\":\"BCC_Focus\"}]}";

	public static String location = "{\n"
			+ "    \"resourceType\": \"Location\",\n"
			+ "    \"id\": \"304cbcd4-0850-404a-a8b1-486b02f7b84d\",\n"
			+ "    \"meta\": {\n"
			+ "        \"versionId\": \"0\",\n"
			+ "        \"lastUpdated\": \"2019-09-13T04:41:49.467+05:00\"\n"
			+ "    },\n"
			+ "    \"identifier\": [\n"
			+ "        {\n"
			+ "            \"system\": \"OpenMRS_Id\",\n"
			+ "            \"value\": \"de28c78d-3111-4266-957b-c731a3330c1d\"\n"
			+ "        }\n"
			+ "    ],\n"
			+ "    \"status\": \"active\",\n"
			+ "    \"name\": \"TLv1_02\",\n"
			+ "    \"mode\": \"instance\",\n"
			+ "    \"_mode\": {\n"
			+ "        \"id\": \"mode\"\n"
			+ "    },\n"
			+ "    \"physicalType\": {\n"
			+ "        \"coding\": [\n"
			+ "            {\n"
			+ "                \"code\": \"jdn\"\n"
			+ "            }\n"
			+ "        ]\n"
			+ "    },\n"
			+ "    \"partOf\": {\n"
			+ "        \"reference\": \"dad42fa6-b9b8-4658-bf25-bfa7ab5b16ae\"\n"
			+ "    }\n"
			+ "}";

	@Before
	public void setup() {
		initMocks(this);
		queueHelper.init();
		amqpAdmin.purgeQueue(queue.getName());
		rabbitMQSender.setRabbitTemplate(rabbitTemplate);
		rabbitMQSender.setQueue(queue);
		queueHelper.setRabbitMQSender(rabbitMQSender);
		queueHelper.setPlanService(planService);
	}

	@Test
	public void testAddToQueue() {
		PlanDefinition planDefinition = createPlan();
		when(planService.getPlan(anyString())).thenReturn(planDefinition);
		Mockito.doNothing().when(planEvaluator)
				.evaluatePlan(any(PlanDefinition.class), any(TriggerType.class), any(Jurisdiction.class), any(
						QuestionnaireResponse.class));
		queueHelper.addToQueue("planid", TriggerType.PLAN_ACTIVATION, "loc-1");
		int count = (Integer) amqpAdmin.getQueueProperties(queue.getName()).get("QUEUE_MESSAGE_COUNT");
		assertEquals(0, count); // This shows message has been consumed
	}

	@Test
	public void testAddToQueueV2() {
		Action action = new Action();
		//		when(conditionHelper.evaluateActionConditions(any(DomainResource.class),any(Action.class),anyString(),any(TriggerType.class))).thenReturn(true);
		Mockito.doNothing().when(planEvaluator)
				.evaluateResource(any(DomainResource.class), nullable(QuestionnaireResponse.class), any(Action.class),
						anyString(), anyString(), any(TriggerType.class));
		queueHelper.addToQueue(location, null, action, "plan-id", "jur-id", TriggerType.PLAN_ACTIVATION);
		int count = (Integer) amqpAdmin.getQueueProperties(queue.getName()).get("QUEUE_MESSAGE_COUNT");
		assertEquals(0, count); // This shows message has been consumed
	}

	public static PlanDefinition createPlan() {
		return gson.fromJson(plan, PlanDefinition.class);
	}

	public static DomainResource createFHIRLocation() {
		return gson.fromJson(location, DomainResource.class);
	}
}
