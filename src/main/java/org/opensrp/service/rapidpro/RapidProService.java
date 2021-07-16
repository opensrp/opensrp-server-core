package org.opensrp.service.rapidpro;

import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.opensrp.util.constants.RapidProConstants.RapidProProjects.ZEIR_RAPIDPRO;

@Service
public class RapidProService {

	@Value("#{opensrp['rapidpro.project']}")
	private String rapidProProject;

	private ZeirRapidProService zeirRapidProService;

	@Autowired
	public void setZeirRapidProService(ZeirRapidProService zeirRapidProService) {
		this.zeirRapidProService = zeirRapidProService;
	}

	/**
	 * This method will query RapidPro contacts filtering by the date/time they were last modified.
	 * The default placeholder string is the hash sign ('#'). Query all contacts modified before today's date/time when the
	 * parameter passed is a '#' otherwise get only the contacts that were updated after the last modified date.
	 */
	public void queryContacts(RapidProOnTaskComplete onTaskComplete) {
		if (ZEIR_RAPIDPRO.equalsIgnoreCase(rapidProProject)) {
			zeirRapidProService.queryContacts(onTaskComplete);
		}
	}

	public void syncOpenSRPEventClientsToRapidPro() {
		if (ZEIR_RAPIDPRO.equalsIgnoreCase(rapidProProject)) {
			zeirRapidProService.syncOpenSRPEventClientsToRapidPro();
		}
	}
}
