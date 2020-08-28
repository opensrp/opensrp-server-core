package org.opensrp.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.fhir.model.resource.DomainResource;
import com.ibm.fhir.model.resource.Location;
import com.ibm.fhir.model.resource.QuestionnaireResponse;
import com.ibm.fhir.model.type.code.LocationStatus;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.ibm.fhir.model.type.String.of;
import static org.junit.Assert.assertEquals;

import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.opensrp.queue.sender.RabbitMQSenderImpl;
import org.opensrp.repository.PlanRepository;
import org.opensrp.service.PlanService;
import org.smartregister.domain.Action;
import org.smartregister.domain.Jurisdiction;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Condition;
import org.smartregister.domain.Expression;
import org.smartregister.pathevaluator.TriggerType;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.smartregister.utils.DateTypeConverter;
import org.smartregister.utils.TaskDateTimeTypeConverter;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mockito.Mockito;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.nullable;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
@ActiveProfiles(profiles = { "rabbitmq" })
public class QueueHelperTest {

	@Mock
	private PlanService planService;

	@Mock
	private PlanEvaluator planEvaluator;

	@Autowired
	private RabbitMQSenderImpl rabbitMQSender;

	@Autowired
	private AmqpTemplate rabbitTemplate;

	@Autowired
	private Queue queue;

	@InjectMocks
	private QueueHelper queueHelper;

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	private PlanRepository planRepository;

	@Captor
	private ArgumentCaptor<Jurisdiction> argumentCaptor = ArgumentCaptor.forClass(Jurisdiction.class);

	private ArgumentCaptor<DomainResource> domainResourceArgumentCaptor = ArgumentCaptor.forClass(DomainResource.class);

	public static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new TaskDateTimeTypeConverter())
			.registerTypeAdapter(LocalDate.class, new DateTypeConverter()).create();

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

	public static final String plan = "{\n"
			+ "  \"identifier\": \"test-plan-identifier-\",\n"
			+ "  \"version\": \"1\",\n"
			+ "  \"name\": \"FI-Routine-Dynamic-Task-Test-Plan-2020-06-26\",\n"
			+ "  \"title\": \"FI Routine Dynamic Task-Test Plan 2020-06-26\",\n"
			+ "  \"status\": \"active\",\n"
			+ "  \"date\": \"2020-06-26\",\n"
			+ "  \"effectivePeriod\": {\n"
			+ "    \"start\": \"2020-06-26\",\n"
			+ "    \"end\": \"2020-07-13\"\n"
			+ "  },\n"
			+ "  \"useContext\": [\n"
			+ "    {\n"
			+ "      \"code\": \"fiStatus\",\n"
			+ "      \"valueCodableConcept\": \"A1\"\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"code\": \"fiReason\",\n"
			+ "      \"valueCodableConcept\": \"Routine\"\n"
			+ "    }\n"
			+ "  ],\n"
			+ "  \"jurisdiction\": [\n"
			+ "    {\n"
			+ "      \"code\": \"1435f854-e818-45bb-8de1-ee27c5ec4e1c\"\n"
			+ "    }\n"
			+ "  ],\n"
			+ "  \"goal\": [\n"
			+ "    {\n"
			+ "      \"id\": \"RACD_register_families\",\n"
			+ "      \"description\": \"Register all families &amp; family members in all residential structures enumerated (100%) within the operational area\",\n"
			+ "      \"priority\": \"medium-priority\",\n"
			+ "      \"target\": [\n"
			+ "        {\n"
			+ "          \"measure\": \"Percent of residential structures with full family registration\",\n"
			+ "          \"detail\": {\n"
			+ "            \"detailQuantity\": {\n"
			+ "              \"value\": 100,\n"
			+ "              \"comparator\": \">=\",\n"
			+ "              \"unit\": \"Percent\"\n"
			+ "            }\n"
			+ "          },\n"
			+ "          \"due\": \"2020-07-06\"\n"
			+ "        }\n"
			+ "      ]\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"id\": \"RACD_Blood_Screening\",\n"
			+ "      \"description\": \"Visit all residential structures (100%) within a 1 km radius of a confirmed index case and test each registered person\",\n"
			+ "      \"priority\": \"medium-priority\",\n"
			+ "      \"target\": [\n"
			+ "        {\n"
			+ "          \"measure\": \"Number of registered people tested\",\n"
			+ "          \"detail\": {\n"
			+ "            \"detailQuantity\": {\n"
			+ "              \"value\": 100,\n"
			+ "              \"comparator\": \">=\",\n"
			+ "              \"unit\": \"Person(s)\"\n"
			+ "            }\n"
			+ "          },\n"
			+ "          \"due\": \"2020-07-06\"\n"
			+ "        }\n"
			+ "      ]\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"id\": \"RACD_bednet_distribution\",\n"
			+ "      \"description\": \"Visit 100% of residential structures in the operational area and provide nets\",\n"
			+ "      \"priority\": \"medium-priority\",\n"
			+ "      \"target\": [\n"
			+ "        {\n"
			+ "          \"measure\": \"Percent of residential structures received nets\",\n"
			+ "          \"detail\": {\n"
			+ "            \"detailQuantity\": {\n"
			+ "              \"value\": 100,\n"
			+ "              \"comparator\": \">=\",\n"
			+ "              \"unit\": \"Percent\"\n"
			+ "            }\n"
			+ "          },\n"
			+ "          \"due\": \"2020-07-06\"\n"
			+ "        }\n"
			+ "      ]\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"id\": \"Larval_Dipping\",\n"
			+ "      \"description\": \"Perform a minimum of three larval dipping activities in the operational area\",\n"
			+ "      \"priority\": \"medium-priority\",\n"
			+ "      \"target\": [\n"
			+ "        {\n"
			+ "          \"measure\": \"Number of larval dipping activities completed\",\n"
			+ "          \"detail\": {\n"
			+ "            \"detailQuantity\": {\n"
			+ "              \"value\": 3,\n"
			+ "              \"comparator\": \">=\",\n"
			+ "              \"unit\": \"activit(y|ies)\"\n"
			+ "            }\n"
			+ "          },\n"
			+ "          \"due\": \"2020-07-06\"\n"
			+ "        }\n"
			+ "      ]\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"id\": \"Mosquito_Collection\",\n"
			+ "      \"description\": \"Set a minimum of three mosquito collection traps and complete the mosquito collection process\",\n"
			+ "      \"priority\": \"medium-priority\",\n"
			+ "      \"target\": [\n"
			+ "        {\n"
			+ "          \"measure\": \"Number of mosquito collection activities completed\",\n"
			+ "          \"detail\": {\n"
			+ "            \"detailQuantity\": {\n"
			+ "              \"value\": 3,\n"
			+ "              \"comparator\": \">=\",\n"
			+ "              \"unit\": \"activit(y|ies)\"\n"
			+ "            }\n"
			+ "          },\n"
			+ "          \"due\": \"2020-07-06\"\n"
			+ "        }\n"
			+ "      ]\n"
			+ "    },\n"
			+ "    {\n"
			+ "      \"id\": \"BCC_Focus\",\n"
			+ "      \"description\": \"Complete at least 1 BCC activity for the operational area\",\n"
			+ "      \"priority\": \"medium-priority\",\n"
			+ "      \"target\": [\n"
			+ "        {\n"
			+ "          \"measure\": \"BCC Activities Completed\",\n"
			+ "          \"detail\": {\n"
			+ "            \"detailQuantity\": {\n"
			+ "              \"value\": 1,\n"
			+ "              \"comparator\": \">=\",\n"
			+ "              \"unit\": \"activit(y|ies)\"\n"
			+ "            }\n"
			+ "          },\n"
			+ "          \"due\": \"2020-07-06\"\n"
			+ "        }\n"
			+ "      ]\n"
			+ "    }\n"
			+ "  ],\n"
			+ "  \"action\": [\n"
			+ "    {\n"
			+ "      \"identifier\": \"d1c6dac7-1ce8-4723-b28d-ac20621f171e\",\n"
			+ "      \"prefix\": 1,\n"
			+ "      \"title\": \"Family Registration\",\n"
			+ "      \"description\": \"Register all families &amp; family members in all residential structures enumerated (100%) within the operational area\",\n"
			+ "      \"code\": \"RACD Register Family\",\n"
			+ "      \"trigger\": [\n"
			+ "        {\n"
			+ "          \"type\": \"named-event\",\n"
			+ "          \"name\": \"plan-activation\"\n"
			+ "        },\n"
			+ "        {\n"
			+ "          \"type\": \"named-event\",\n"
			+ "          \"name\": \"event-submission\",\n"
			+ "          \"expression\": {\n"
			+ "            \"expression\": \"questionnaire = 'Register_Structure'\"\n"
			+ "          }\n"
			+ "        }\n"
			+ "      ],\n"
			+ "      \"condition\": [\n"
			+ "        {\n"
			+ "          \"kind\": \"applicability\",\n"
			+ "          \"expression\": {\n"
			+ "            \"description\": \"Structure is residential\",\n"
			+ "            \"reference\": \"plan-activation\",\n"
			+ "            \"expression\": \"$this.type.where(id='locationType').text = 'Residential Structure'\"\n"
			+ "          }\n"
			+ "        },\n"
			+ "        {\n"
			+ "          \"kind\": \"applicability\",\n"
			+ "          \"expression\": {\n"
			+ "            \"description\": \"Family does not exist for structure\",\n"
			+ "            \"reference\": \"plan-activation\",\n"
			+ "            \"expression\": \"$this.contained.exists().not()\",\n"
			+ "            \"subjectCodableConcept\": {\n"
			+ "              \"text\": \"Family\"\n"
			+ "            }\n"
			+ "          }\n"
			+ "        },\n"
			+ "        {\n"
			+ "          \"kind\": \"applicability\",\n"
			+ "          \"expression\": {\n"
			+ "            \"description\": \"Register stucture Event submitted for a residential structure\",\n"
			+ "            \"reference\": \"event-submission\",\n"
			+ "            \"expression\": \"questionnaire = 'Register_Structure' AND item.where(linkId='structureType').answer.value ='Residential Structure'\"\n"
			+ "          }\n"
			+ "        }\n"
			+ "      ],\n"
			+ "      \"timingPeriod\": {\n"
			+ "        \"start\": \"2020-06-26\",\n"
			+ "        \"end\": \"2020-07-06\"\n"
			+ "      },\n"
			+ "      \"reason\": \"Routine\",\n"
			+ "      \"goalId\": \"RACD_register_families\",\n"
			+ "      \"subjectCodableConcept\": {\n"
			+ "        \"text\": \"Location\"\n"
			+ "      },\n"
			+ "      \"definitionUri\": \"thailand_family_register.json\"\n"
			+ "    }\n"
			+ "  ],\n"
			+ "  \"experimental\": false\n"
			+ "}";

	@Before
	public void setup() {
		initMocks(this);
		queueHelper.init();
		amqpAdmin.purgeQueue(queue.getName());
		rabbitMQSender.setRabbitTemplate(rabbitTemplate);
		rabbitMQSender.setQueue(queue);
		queueHelper.setRabbitMQSender(rabbitMQSender);
	}

	@Test
	public void testAddToQueue() {
		PlanDefinition planDefinition = createPlan();
		when(planService.getPlan(anyString())).thenReturn(planDefinition);
		addNewPlanToRepo();
		Mockito.doNothing().when(planEvaluator)
				.evaluatePlan(any(PlanDefinition.class), any(TriggerType.class), any(Jurisdiction.class), any(
						QuestionnaireResponse.class));
		queueHelper.addToQueue("test-plan-identifier-", TriggerType.PLAN_ACTIVATION, "loc-1");
		int count = (Integer) amqpAdmin.getQueueProperties(queue.getName()).get("QUEUE_MESSAGE_COUNT");
		assertEquals(0, count); // This shows message has been consumed
	}

	@Test
	public void testAddToQueueV2() {
		Action action = new Action();
		Mockito.doNothing().when(planEvaluator)
				.evaluateResource(any(DomainResource.class), nullable(QuestionnaireResponse.class), any(Action.class),
						anyString(), anyString(), any(TriggerType.class));
		queueHelper.addToQueue(location, null, action, "plan-id", "jur-id", TriggerType.PLAN_ACTIVATION);
		int count = (Integer) amqpAdmin.getQueueProperties(queue.getName()).get("QUEUE_MESSAGE_COUNT");
		assertEquals(0, count); // This shows message has been consumed
	}

	@Test
	public void testAddToQueueV2WithQueuingDisabledAndInvalidResource() {
		Action action = createAction();
		queueHelper.addToQueue(plan, null, action, "plan-id", "jur-id", TriggerType.PLAN_ACTIVATION);
		verify(planEvaluator, never())
				.evaluateResource(eq(domainResourceArgumentCaptor.capture()), null, eq(action), eq("plan-id"), eq("jur-id"),
						eq(TriggerType.PLAN_ACTIVATION));
	}

	public static PlanDefinition createPlan() {
		return gson.fromJson(plan, PlanDefinition.class);
	}

	public static DomainResource createFHIRLocation() {
		return gson.fromJson(location, DomainResource.class);
	}

	public static Location createLocation() {
		return Location.builder().id(UUID.randomUUID().toString()).name(of("Nairobi")).status(LocationStatus.ACTIVE).build();
	}

	public void addNewPlanToRepo() {
		PlanDefinition planDefinition = createPlan();
		planRepository.add(planDefinition);
	}

	public static Action createAction() {
		Action action = new Action();
		Set<Condition> conditions = new HashSet<>();
		Condition condition = new Condition();
		Expression expression = new Expression();
		expression.setExpression("'Cancelled'");
		condition.setExpression(expression);
		conditions.add(condition);

		condition = new Condition();
		expression = new Expression();
		expression.setExpression("'Family Already Registered'");
		condition.setExpression(expression);
		conditions.add(condition);
		action.setCondition(conditions);
		return action;
	}

}
