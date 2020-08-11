package org.opensrp.queue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.pathevaluator.PathEvaluatorLibrary;
import org.smartregister.pathevaluator.plan.PlanEvaluator;
import org.smartregister.utils.DateTimeTypeConverter;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RabbitMQReceiver implements MessageListener {

    private PlanEvaluator planEvaluator;

    @Autowired
    private Queue queue;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ClientsRepository clientsRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EventsRepository eventsRepository;

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();


    private static Logger logger = LoggerFactory.getLogger(RabbitMQReceiver.class.toString());

    @PostConstruct
    private void postConstruct() {
        PathEvaluatorLibrary.init(locationRepository, clientsRepository, taskRepository, eventsRepository);
        planEvaluator = new PlanEvaluator("");
    }

    @RabbitListener(queues = "rabbitmq.task.queue")
    public void onMessage(Message message) {
        logger.info("Consuming Message - " + new String(message.getBody()));
        int count = (Integer) amqpAdmin.getQueueProperties(queue.getName()).get("QUEUE_MESSAGE_COUNT");
        PlanEvaluatorMessage planEvaluatorMessage = null;
        if (count >= 1) {
            planEvaluatorMessage = gson.fromJson(new String(message.getBody()), PlanEvaluatorMessage.class);
            logger.info("CustomPlanEvaluatorMessage received : ", planEvaluatorMessage);
            if (planEvaluatorMessage != null) {
                planEvaluator.evaluatePlan(planEvaluatorMessage.getPlanDefinition(),
                        planEvaluatorMessage.getTriggerType(),
                        planEvaluatorMessage.getJurisdiction(), null);
            }
        }
    }
}
