package org.opensrp.service.rapidpro;

import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RapidProService {

	private ZeirRapidProService zeirRapidProService;

	@Autowired
	public void setZeirRapidProService(ZeirRapidProService zeirRapidProService) {
		this.zeirRapidProService = zeirRapidProService;
	}

	/**
	 * This method will query RapidPro contacts filtering by the date/time they were last modified.
	 * The default placeholder string is the hash sign ('#'). Query all contacts modified before today's date/time when the
	 * parameter passed is a '#' otherwise get only the contacts that were updated after the last modified date.
	 *
	 * @param dateModified the last date the contact was updated. Default placeholder '#'
	 * @return A list of RapidPro contacts
	 */
	public void queryContacts(String dateModified, RapidProOnTaskComplete onTaskComplete) {
		zeirRapidProService.queryContacts(dateModified, onTaskComplete);
	}
}
