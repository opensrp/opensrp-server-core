package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.converter.BaseRapidProClientConverter;
import org.opensrp.domain.rapidpro.converter.BaseRapidProEventConverter;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.locks.ReentrantLock;

public abstract class BaseRapidProService {

	protected final Logger logger = LogManager.getLogger(getClass());

	protected final ReentrantLock reentrantLock = new ReentrantLock();

	protected final ObjectMapper objectMapper = new ObjectMapper();

	protected EventService eventService;

	protected ClientService clientService;

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	@Autowired
	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	protected void saveEvent(RapidProContact rapidProContact, BaseRapidProEventConverter eventConverter) {
		Event event = eventConverter.convertContactToEvent(rapidProContact);
		eventService.addorUpdateEvent(event, rapidProContact.getFields().getSupervisor());
	}

	protected void saveClient(RapidProContact rapidProContact, BaseRapidProClientConverter clientConverter) {
		Client client = clientConverter.convertContactToClient(rapidProContact);
		clientService.addorUpdate(client);
	}
}
