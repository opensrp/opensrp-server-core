package org.opensrp.service.formSubmission;

import static java.text.MessageFormat.format;
import static java.util.Collections.sort;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.joda.time.DateTime;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.AppStateToken;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.service.ConfigService;
import org.opensrp.service.ErrorTraceService;
import org.opensrp.service.EventService;
import org.opensrp.service.formSubmission.handler.EventsRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventsListener {
	
	private static Logger logger = LoggerFactory.getLogger(EventsListener.class.toString());
	
	private static final ReentrantLock lock = new ReentrantLock();
	
	private ConfigService configService;
	
	@Autowired
	private EventService eventService;
	
	private EventsRouter eventsRouter;
	
	private ErrorTraceService errorTraceService;

	@Autowired
	public EventsListener(EventsRouter eventsRouter, ConfigService configService, EventService eventService,
	    ErrorTraceService errorTraceService) {
		this.configService = configService;
		this.errorTraceService = errorTraceService;
		this.eventsRouter = eventsRouter;
		this.eventService = eventService;
		this.configService.registerAppStateToken(AllConstants.Config.EVENTS_PARSER_LAST_PROCESSED_EVENT, 0,
		    "Token to keep track of events processed for client n event parsing and schedule handling", true);
	}
	
	public void processEvent() {
		if (!lock.tryLock()) {
			logger.warn("Not fetching events from Message Queue. It is already in progress.");
			return;
		}
		try {
			logger.info("Fetching Events");
			long version = getVersion();
			
			List<Event> events = eventService.findByServerVersion(version);
			
			if (events.isEmpty()) {
				logger.info("No new events found. Export token: " + version);
				return;
			}
			
			logger.info(format("Fetched {0} new events found. Export token: {1}", events.size(), version));
			
			sort(events, serverVersionComparator());
			
			for (Event event : events) {
				try {
					event = eventService.processOutOfArea(event, event.getProviderId());
					eventsRouter.route(event);
					configService.updateAppStateToken(AllConstants.Config.EVENTS_PARSER_LAST_PROCESSED_EVENT,
					    event.getServerVersion());
				}
				catch (Exception e) {
					e.printStackTrace();
					errorTraceService
					        .addError(new ErrorTrace(new DateTime(), "FormSubmissionProcessor", this.getClass().getName(),
					                e.getStackTrace().toString(), "unsolved ", "FormSubmission"));
				}
			}
		}
		catch (Exception e) {
			logger.error(format("{0} occurred while trying to fetch events. Message: {1} with stack trace {2}", e.toString(),
			    e.getMessage(), getFullStackTrace(e)));
		}
		finally {
			lock.unlock();
		}
	}
	
	public long getCurrentMilliseconds() {
		return System.currentTimeMillis();
	}
	
	private long getVersion() {
		AppStateToken token = configService.getAppStateTokenByName(AllConstants.Config.EVENTS_PARSER_LAST_PROCESSED_EVENT);
		return token == null ? 0L : token.longValue();
	}
	
	private Comparator<Event> serverVersionComparator() {
		return new Comparator<Event>() {
			
			public int compare(Event firstEvent, Event secondEvent) {
				long firstTimestamp = firstEvent.getVersion();
				long secondTimestamp = secondEvent.getVersion();
				return firstTimestamp == secondTimestamp ? 0 : firstTimestamp < secondTimestamp ? -1 : 1;
			}
		};
	}
}
