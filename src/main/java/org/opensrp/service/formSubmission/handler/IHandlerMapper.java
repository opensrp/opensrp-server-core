package org.opensrp.service.formSubmission.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class IHandlerMapper {
	
	public Map<String, EventsHandler> handlerMap() {
		return new HashMap<>();
	}
	
	public Map<String, EventsHandler> addHandler(String name, EventsHandler handler) {
		return new HashMap<>();
	}
}
