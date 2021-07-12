package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.domain.rapidpro.contact.zeir.RapidProContact;
import org.opensrp.domain.rapidpro.converter.BaseRapidProClientConverter;
import org.opensrp.domain.rapidpro.converter.BaseRapidProEventConverter;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.service.PhysicalLocationService;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public abstract class BaseRapidProService {

	private static final String API_URL = "/api/v2";

	protected final Logger logger = LogManager.getLogger(getClass());

	protected final ReentrantLock reentrantLock = new ReentrantLock();

	protected final ObjectMapper objectMapper = new ObjectMapper();

	protected EventService eventService;

	protected ClientService clientService;

	@Value("#{opensrp['rapidpro.url']}")
	private String rapidProUrl;

	@Value("#{opensrp['rapidpro.token']}")
	private String rapidProToken;

	protected PhysicalLocationService locationService;

	@Autowired
	public void setLocationService(PhysicalLocationService locationService) {
		this.locationService = locationService;
	}

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

	public String getBaseUrl() {
		return StringUtils.isBlank(rapidProUrl) || StringUtils.isEmpty(rapidProUrl) ? "" :
				rapidProUrl.endsWith(API_URL) ? rapidProUrl : rapidProUrl + API_URL;
	}

	public HttpRequestBase setupRapidproRequest(String url, HttpRequestBase httpRequestBase) {
		httpRequestBase.setURI(URI.create(url));
		httpRequestBase.setHeader("Authorization", " Token " + rapidProToken);
		httpRequestBase.addHeader("content-type", "application/json");
		httpRequestBase.addHeader("Accept", "application/json");
		return httpRequestBase;
	}

	public boolean locationTagExists(Set<LocationTag> locationTags, String tag) {
		return locationTags.stream().filter(it -> it.getName().equalsIgnoreCase(tag)).count() > 1;
	}

	public List<PhysicalLocation> findLocationsWithNameAndTag(List<PhysicalLocation> locations, String locationName,
			String tag) {
		return locations.stream()
				.filter(it -> it.getProperties().getName().equalsIgnoreCase(locationName) && locationTagExists(
						it.getLocationTags(), tag))
				.collect(Collectors.toList());
	}
}
