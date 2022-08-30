package org.opensrp.service.formSubmission.handler;

import org.json.JSONObject;
import org.smartregister.domain.Event;

public interface EventsHandler {

    public void handle(Event event, JSONObject scheduleConfigEvent, String scheduleName);
}
