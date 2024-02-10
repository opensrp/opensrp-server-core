package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensrp.domain.rapidpro.RapidProStateToken;
import org.opensrp.service.ClientService;
import org.opensrp.service.ConfigService;
import org.opensrp.service.EventService;
import org.opensrp.service.IdentifierSourceService;
import org.opensrp.service.OrganizationService;
import org.opensrp.service.PhysicalLocationService;
import org.opensrp.service.UniqueIdentifierService;
import org.opensrp.service.callback.RapidProOnTaskComplete;
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base class for RapidProService, extend this class to provide project specific integration with RapidPro
 */
public abstract class BaseRapidProService {

	protected final Logger logger = LogManager.getLogger(getClass());

	protected final ObjectMapper objectMapper = new ObjectMapper();

	protected EventService eventService;

	protected ClientService clientService;

	protected PhysicalLocationService locationService;

	protected CloseableHttpClient closeableHttpClient;

	protected ConfigService configService;

	protected IdentifierSourceService identifierSourceService;

	protected UniqueIdentifierService uniqueIdentifierService;

	protected OrganizationService organizationService;

	protected ZeirRapidProStateService rapidProStateService;

	@Value("#{opensrp['rapidpro.url']}")
	protected String rapidProUrl;

	@Value("#{opensrp['rapidpro.token']}")
	protected String rapidProToken;

	public BaseRapidProService() {
		this.closeableHttpClient = HttpClients.createDefault();
	}

	@Autowired
	public void setLocationService(PhysicalLocationService locationService) {
		this.locationService = locationService;
	}

	@Autowired
	public void setConfigService(ConfigService configService) {
		this.configService = configService;
		this.configService.registerAppStateToken(RapidProStateToken.RAPIDPRO_STATE_TOKEN, "#",
				"Token to keep track of the date of the last processed rapidpro contacts", true);
	}

	@Autowired
	public void setUniqueIdentifierService(UniqueIdentifierService uniqueIdentifierService) {
		this.uniqueIdentifierService = uniqueIdentifierService;
	}

	@Autowired
	public void setIdentifierSourceService(IdentifierSourceService identifierSourceService) {
		this.identifierSourceService = identifierSourceService;
	}

	@Autowired
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	@Autowired
	public void setRapidProStateService(ZeirRapidProStateService zeirRapidProStateService) {
		this.rapidProStateService = zeirRapidProStateService;
	}

	public void setCloseableHttpClient(CloseableHttpClient closeableHttpClient) {
		this.closeableHttpClient = closeableHttpClient;
	}

	public boolean locationTagExists(Set<LocationTag> locationTags, String tag) {
		return locationTags.stream().anyMatch(it -> it.getName().equalsIgnoreCase(tag));
	}

	public List<PhysicalLocation> findLocationsWithNameAndTag(List<PhysicalLocation> locations, String locationName,
			String tag) {
		return locations.stream()
				.filter(it -> it.getProperties().getName().equalsIgnoreCase(locationName) && locationTagExists(
						it.getLocationTags(), tag))
				.collect(Collectors.toList());
	}

	public EventService getEventService() {
		return eventService;
	}

	@Autowired
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public ClientService getClientService() {
		return clientService;
	}

	@Autowired
	public void setClientService(ClientService clientService) {
		this.clientService = clientService;
	}

	public abstract void queryContacts(RapidProOnTaskComplete onTaskComplete);

}
