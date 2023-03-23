package org.opensrp.service.callback;

/**
 * Callback method called when any rapidpro task is completed e.g. after fetching and processing of contacts is done.
 */
public interface RapidProOnTaskComplete {

	void completeTask();
}
