package org.opensrp.service.rapidpro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
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
import org.smartregister.domain.LocationTag;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Base class for RapidProService, extend this class to provide project specific integration with RapidPro
 */
public abstract class BaseRapidProService {

	private static final String API_URL = "/api/v2";

	protected final Logger logger = LogManager.getLogger(getClass());

	protected final ReentrantLock reentrantLock = new ReentrantLock();

	protected final ObjectMapper objectMapper = new ObjectMapper();

	protected EventService eventService;

	protected ClientService clientService;

	protected PhysicalLocationService locationService;

	protected HttpClient httpClient;

	protected ConfigService configService;

	protected IdentifierSourceService identifierSourceService;

	protected UniqueIdentifierService uniqueIdentifierService;

	protected OrganizationService organizationService;

	@Value("#{opensrp['rapidpro.url']}")
	private String rapidProUrl;

	@Value("#{opensrp['rapidpro.token']}")
	private String rapidProToken;

	public BaseRapidProService() {
		this.httpClient = HttpClientBuilder.create().build();
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

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
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

}
