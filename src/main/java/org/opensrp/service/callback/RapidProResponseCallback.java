package org.opensrp.service.callback;

/**
 * Callback methods for RapidPro called after fetching data from the server.
 */
public interface RapidProResponseCallback {

	void handleContactResponse(String response, RapidProOnTaskComplete onTaskComplete);
}
