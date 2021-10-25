package org.opensrp.repository.postgres;

import static org.opensrp.common.AllConstants.BaseEntity.BASE_ENTITY_ID;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.postgres.ClientExample;
import org.opensrp.domain.postgres.ClientMetadata;
import org.opensrp.domain.postgres.ClientMetadataExample;
import org.opensrp.domain.postgres.ClientMetadataExample.Criteria;
import org.opensrp.domain.postgres.CustomClient;
import org.opensrp.domain.postgres.HouseholdClient;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.EventsRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomClientMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomClientMetadataMapper;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;
import org.smartregister.converters.ClientConverter;
import org.smartregister.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.fhir.model.resource.Patient;

@Repository("clientsRepositoryPostgres")
public class ClientsRepositoryImpl extends BaseRepositoryImpl<Client> implements ClientsRepository {
	
	private static Logger logger = LogManager.getLogger(ClientsRepository.class.toString());
	
	public static String RESIDENCE = "residence";
	
	@Autowired
	private CustomClientMetadataMapper clientMetadataMapper;
	
	@Autowired
	private CustomClientMapper clientMapper;
	
	@Autowired
	private EventsRepository eventsRepository;
	
	@Override
	public Client get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		
		org.opensrp.domain.postgres.Client pgClient = clientMetadataMapper.selectByDocumentId(id);
		if (pgClient == null) {
			return null;
		}
		return convert(pgClient);
	}
	
	@Override
	@Transactional
	public void add(Client entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { // Client already added
			return;
		}
		
		if (entity.getId() == null || entity.getId().isEmpty())
			entity.setId(UUID.randomUUID().toString());
		
		setRevision(entity);
		
		org.opensrp.domain.postgres.Client pgClient = convert(entity, null);
		if (pgClient == null) {
			throw new IllegalStateException();
		}
		
		int rowsAffected = clientMapper.insertSelectiveAndSetId(pgClient);
		if (rowsAffected < 1 || pgClient.getId() == null) {
			throw new IllegalStateException();
		}
		
		updateServerVersion(pgClient, entity);
		
		ClientMetadata clientMetadata = createMetadata(entity, pgClient.getId());
		if (clientMetadata != null) {
			clientMetadataMapper.insertSelective(clientMetadata);
		}
	}
	
	private void updateServerVersion(org.opensrp.domain.postgres.Client pgClient, Client entity) {
		long serverVersion = clientMapper.selectServerVersionByPrimaryKey(pgClient.getId());
		entity.setServerVersion(serverVersion);
		pgClient.setJson(entity);
		pgClient.setServerVersion(null);
		int rowsAffected = clientMapper.updateByPrimaryKeySelective(pgClient);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
	}
	
	@Override
	public void update(Client entity) {
		update(entity, false);
	}
	
	@Transactional
	@Override
	public void update(Client entity, boolean allowArchived) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity, allowArchived);
		if (id == null) { // Client not added
			throw new IllegalStateException();
		}
		
		setRevision(entity);
		
		org.opensrp.domain.postgres.Client pgClient = convert(entity, id);
		if (pgClient == null) {
			throw new IllegalStateException();
		}
		
		int rowsAffected = clientMapper.updateByPrimaryKeyAndGenerateServerVersion(pgClient);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
		
		updateServerVersion(pgClient, entity);
		
		ClientMetadata clientMetadata = createMetadata(entity, id);
		if (clientMetadata == null) {
			throw new IllegalStateException();
		}
		
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		Criteria criteria = clientMetadataExample.createCriteria();
		criteria.andClientIdEqualTo(id);
		if (!allowArchived) {
			criteria.andDateDeletedIsNull();
		}
		ClientMetadata metadata = clientMetadataMapper.selectByExample(clientMetadataExample).get(0);
		clientMetadata.setId(metadata.getId());
		clientMetadata.setDateCreated(metadata.getDateCreated());
		clientMetadataMapper.updateByPrimaryKey(clientMetadata);
	}
	
	@Override
	public List<Client> getAll() {
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andDateDeletedIsNull();
		List<org.opensrp.domain.postgres.Client> clients = clientMetadataMapper.selectMany(clientMetadataExample, 0,
		    DEFAULT_FETCH_SIZE);
		return convert(clients);
	}
	
	@Override
	public void safeRemove(Client entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		Date dateDeleted = entity.getDateVoided() == null ? new Date() : entity.getDateVoided().toDate();
		ClientMetadata clientMetadata = new ClientMetadata();
		clientMetadata.setDateDeleted(dateDeleted);
		
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andClientIdEqualTo(id).andDateDeletedIsNull();
		
		int rowsAffected = clientMetadataMapper.updateByExampleSelective(clientMetadata, clientMetadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		org.opensrp.domain.postgres.Client pgClient = new org.opensrp.domain.postgres.Client();
		pgClient.setId(id);
		pgClient.setDateDeleted(dateDeleted);
		clientMapper.updateByPrimaryKeySelective(pgClient);
	}
	
	@Override
	public Client findByBaseEntityId(String baseEntityId) {
		if (StringUtils.isBlank(baseEntityId)) {
			return null;
		}
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andBaseEntityIdEqualTo(baseEntityId).andDateDeletedIsNull();
		org.opensrp.domain.postgres.Client pgClient = clientMetadataMapper.selectOne(clientMetadataExample);
		return convert(pgClient);
	}
	
	@Override
	public Client findById(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andDocumentIdEqualTo(id).andDateDeletedIsNull();
		org.opensrp.domain.postgres.Client pgClient = clientMetadataMapper.selectOne(clientMetadataExample);
		return convert(pgClient);
	}
	
	@Override
	public List<Client> findAllClients() {
		return getAll();
	}
	
	@Override
	public List<Client> findAllByIdentifier(String identifier) {
		List<org.opensrp.domain.postgres.Client> clients = clientMapper.selectByIdentifier(identifier);
		return convert(clients);
	}
	
	@Override
	public List<Client> findAllByIdentifier(String identifierType, String identifier) {
		List<org.opensrp.domain.postgres.Client> clients = clientMapper.selectByIdentifierOfType(identifierType, identifier);
		return convert(clients);
	}

	@Override
	public List<Client> findAllByAttribute(String attributeType, String attribute) {
		List<org.opensrp.domain.postgres.Client> clients = clientMapper.selectByAttributeOfType(attributeType, attribute);
		return convert(clients);
	}

	@Override
	public List<Client> findAllByAttributes(String attributeType, List<String> attributes) {
		List<org.opensrp.domain.postgres.Client> clients = clientMapper.selectByAttributesOfType(attributeType, attributes);
		return convert(clients);
	}
	
	@Override
	public List<Client> findAllByMatchingName(String nameMatches) {
		List<org.opensrp.domain.postgres.Client> clients = clientMetadataMapper.selectByName(nameMatches, 0,
		    DEFAULT_FETCH_SIZE);
		return convert(clients);
	}
	
	@Override
	public List<Client> findByRelationshipIdAndDateCreated(String relationalId, String dateFrom, String dateTo) {
		List<org.opensrp.domain.postgres.Client> clients = clientMapper.selectByRelationshipIdAndDateCreated(relationalId,
		    new DateTime(dateFrom).toDate(), new DateTime(dateTo).toDate());
		return convert(clients);
	}
	
	public List<Client> findByRelationshipId(String relationshipType, String entityId) {
		List<org.opensrp.domain.postgres.Client> clients = clientMapper.selectByRelationshipIdOfType(relationshipType,
		    entityId);
		return convert(clients);
	}
	
	@Override
	public List<Client> findByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean) {
		
		int pageSize = searchBean.getPageSize();
		if (pageSize == 0) {
			pageSize = DEFAULT_FETCH_SIZE;
		}
		
		int offset = searchBean.getPageNumber() * pageSize;
		return convert(clientMetadataMapper.selectBySearchBean(searchBean, addressSearchBean, offset, pageSize));
	}
	
	@Override
	public List<Client> findByDynamicQuery(String query) {
		throw new IllegalArgumentException("Method not supported");
	}
	
	@Override
	public List<Client> findByCriteria(ClientSearchBean searchBean) {
		return findByCriteria(searchBean, new AddressSearchBean());
	}
	
	@Override
	public List<Client> findByCriteria(AddressSearchBean addressSearchBean, DateTime lastEditFrom, DateTime lastEditTo) {
		ClientSearchBean clientSearchBean = new ClientSearchBean();
		clientSearchBean.setLastEditFrom(lastEditFrom);
		clientSearchBean.setLastEditTo(lastEditTo);
		return findByCriteria(clientSearchBean, addressSearchBean);
	}
	
	@Override
	public List<Client> findByRelationShip(String relationIndentier) {
		List<org.opensrp.domain.postgres.Client> clients = clientMapper.selectByRelationShip(relationIndentier);
		return convert(clients);
	}
	
	@Override
	public List<Client> findByEmptyServerVersion() {
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andServerVersionIsNull();
		clientMetadataExample.or(clientMetadataExample.createCriteria().andServerVersionEqualTo(0l));
		clientMetadataExample.setOrderByClause("client_id ASC");
		List<org.opensrp.domain.postgres.Client> clients = clientMetadataMapper.selectMany(clientMetadataExample, 0,
		    DEFAULT_FETCH_SIZE);
		return convert(clients);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Client> findByServerVersion(long serverVersion, Integer limit) {
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion).andDateDeletedIsNull();
		clientMetadataExample.setOrderByClause(this.getOrderByClause(SERVER_VERSION, ASCENDING));
		Integer pageLimit = limit == null ? DEFAULT_FETCH_SIZE : limit;
		List<org.opensrp.domain.postgres.Client> clients = clientMetadataMapper.selectMany(clientMetadataExample, 0,
		    pageLimit);
		return convert(clients);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long countAll(long serverVersion) {
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(serverVersion).andDateDeletedIsNull();
		return clientMetadataMapper.countMany(clientMetadataExample);
	}
	
	@Override
	public List<Client> findByFieldValue(String field, List<String> ids) {
		if (field.equals(BASE_ENTITY_ID) && ids != null && !ids.isEmpty()) {
			ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
			clientMetadataExample.createCriteria().andBaseEntityIdIn(ids).andDateDeletedIsNull();
			List<org.opensrp.domain.postgres.Client> clients = clientMetadataMapper.selectMany(clientMetadataExample, 0,
			    DEFAULT_FETCH_SIZE);
			return convert(clients);
		}
		return new ArrayList<>();
	}
	
	@Override
	public List<Client> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar) {
		long serverStartKey = serverVersion + 1;
		long serverEndKey = calendar.getTimeInMillis();
		if (serverStartKey < serverEndKey) {
			ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
			clientMetadataExample.createCriteria().andOpenmrsUuidIsNull()
			        .andServerVersionBetween(serverStartKey, serverEndKey).andDateDeletedIsNull();
			
			List<org.opensrp.domain.postgres.Client> clients = clientMetadataMapper.selectMany(clientMetadataExample, 0,
			    DEFAULT_FETCH_SIZE);
			return convert(clients);
		}
		return new ArrayList<>();
	}
	
	// Private Methods
	@Override
	public List<Client> convert(List<org.opensrp.domain.postgres.Client> clients) {
		if (clients == null || clients.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Client> convertedClients = new ArrayList<>();
		for (org.opensrp.domain.postgres.Client client : clients) {
			Client convertedClient = convert(client);
			if (convertedClient != null) {
				convertedClients.add(convertedClient);
			}
		}
		
		return convertedClients;
	}

	private Client convert(org.opensrp.domain.postgres.Client client) {
		if (client == null || client.getJson() == null || !(client.getJson() instanceof Client)) {
			return null;
		}
		return (Client) client.getJson();
	}
	
	private org.opensrp.domain.postgres.Client convert(Client client, Long primaryKey) {
		if (client == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Client pgClient = new org.opensrp.domain.postgres.Client();
		pgClient.setId(primaryKey);
		pgClient.setJson(client);
		
		return pgClient;
	}
	
	private ClientMetadata createMetadata(Client client, Long clientId) {
		try {
			ClientMetadata clientMetadata = new ClientMetadata();
			clientMetadata.setDocumentId(client.getId());
			clientMetadata.setBaseEntityId(client.getBaseEntityId());
			if (client.getBirthdate() != null) {
				clientMetadata.setBirthDate(client.getBirthdate().toDate());
			}
			clientMetadata.setClientId(clientId);
			clientMetadata.setFirstName(client.getFirstName());
			clientMetadata.setMiddleName(client.getMiddleName());
			clientMetadata.setLastName(client.getLastName());

			if(clientId != null){
				clientMetadata.setDateEdited(new Date());
			}

			String relationalId = null;
			Map<String, List<String>> relationShips = client.getRelationships();
			if (relationShips != null && !relationShips.isEmpty()) {
				for (Map.Entry<String, List<String>> maEntry : relationShips.entrySet()) {
					List<String> values = maEntry.getValue();
					if (values != null && !values.isEmpty()) {
						relationalId = values.get(0);
						break;
					}
				}
			}
			clientMetadata.setRelationalId(relationalId);
			
			String uniqueId = null;
			String openmrsUUID = null;
			Map<String, String> identifiers = client.getIdentifiers();
			if (identifiers != null && !identifiers.isEmpty()) {
				for (Map.Entry<String, String> entry : identifiers.entrySet()) {
					String value = entry.getValue();
					if (StringUtils.isNotBlank(value)) {
						if (AllConstants.Client.OPENMRS_UUID_IDENTIFIER_TYPE.equalsIgnoreCase(entry.getKey())) {
							openmrsUUID = value;
						} else {
							uniqueId = value;
						}
					}
				}
			}
			
			clientMetadata.setUniqueId(uniqueId);
			clientMetadata.setOpenmrsUuid(openmrsUUID);
			clientMetadata.setServerVersion(client.getServerVersion());
			if (client.getDateVoided() != null)
				clientMetadata.setDateDeleted(client.getDateVoided().toDate());
			Object residence = client.getAttribute(RESIDENCE);
			if (residence != null)
				clientMetadata.setResidence(residence.toString());
			clientMetadata.setLocationId(client.getLocationId());
			clientMetadata.setClientType(client.getClientType());
			return clientMetadata;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	@Override
	protected Long retrievePrimaryKey(Client entity) {
		return retrievePrimaryKey(entity, false);
	}
	
	/**
	 * @param entity
	 * @param allowArchived
	 * @return
	 */
	private Long retrievePrimaryKey(Client entity, boolean allowArchived) {
		Object uniqueId = getUniqueField(entity);
		if (uniqueId == null) {
			return null;
		}
		
		String baseEntityId = uniqueId.toString();
		
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		Criteria criteria = clientMetadataExample.createCriteria();
		criteria.andBaseEntityIdEqualTo(baseEntityId);
		if (!allowArchived) {
			criteria.andDateDeletedIsNull();
		}
		
		org.opensrp.domain.postgres.Client pgClient = clientMetadataMapper.selectOne(clientMetadataExample);
		if (pgClient == null) {
			return null;
		}
		return pgClient.getId();
	}
	
	@Override
	protected Object getUniqueField(Client t) {
		if (t == null) {
			return null;
		}
		return t.getBaseEntityId();
	}
	
	@Override
	public List<HouseholdClient> selectMemberCountHouseholdHeadProviderByClients(String field, List<String> ids,
	        String clientType) {
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andRelationalIdIn(ids);
		return clientMetadataMapper.selectMemberCountHouseholdHeadProviderByClients(clientMetadataExample, clientType);
	}
	
	@Override
	public HouseholdClient findTotalCountHouseholdByCriteria(ClientSearchBean searchBean,
	        AddressSearchBean addressSearchBean) {
		
		return clientMetadataMapper.selectHouseholdCountBySearchBean(searchBean, addressSearchBean);
	}
	
	@Override
	public List<Client> findMembersByRelationshipId(String baseEntityId) {
		
		List<CustomClient> members = new ArrayList<CustomClient>();
		if (!StringUtils.isBlank(baseEntityId)) {
			members = clientMetadataMapper.selectMembersByRelationshipId(baseEntityId);
		}
		return customClientConvert(members);
	}
	
	@Override
	public List<Client> findAllClientsByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean) {
		int pageSize = searchBean.getPageSize();
		if (pageSize == 0) {
			pageSize = DEFAULT_FETCH_SIZE;
		}
		
		int offset = searchBean.getPageNumber() * pageSize;
		
		List<CustomClient> clients = clientMetadataMapper.selectAllClientsBySearchBean(searchBean, addressSearchBean, offset,
		    pageSize);
		return customClientConvert(clients);
	}
	
	private Client customClientConvert(CustomClient customClient) {
		
		if (customClient == null || customClient.getJson() == null || !(customClient.getJson() instanceof Client)) {
			return null;
		}
		
		Client cl = (Client) customClient.getJson();
		cl.addAttribute("dynamicProperties", customClient.getDynamicProperties());
		return cl;
	}
	
	protected List<Client> customClientConvert(List<CustomClient> clients) {
		if (clients == null || clients.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Client> convertedClients = new ArrayList<>();
		for (CustomClient client : clients) {
			Client convertedClient = customClientConvert(client);
			if (convertedClient != null) {
				convertedClients.add(convertedClient);
			}
		}
		
		return convertedClients;
	}
	
	@Override
	public HouseholdClient findCountAllClientsByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean) {
		
		return clientMetadataMapper.selectCountAllClientsBySearchBean(searchBean, addressSearchBean);
	}
	
	@Override
	public List<Client> findHouseholdByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean) {
		Map<String, Integer> pageSizeAndOffset = getPageSizeAndOffset(searchBean);
		return customClientConvert(clientMetadataMapper.selectHouseholdBySearchBean(searchBean, addressSearchBean,
		    pageSizeAndOffset.get("offset"), pageSizeAndOffset.get("pageSize")));
	}
	
	@Override
	public List<Client> findANCByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean) {
		Map<String, Integer> pageSizeAndOffset = getPageSizeAndOffset(searchBean);
		List<CustomClient> clients = clientMetadataMapper.selectANCBySearchBean(searchBean, addressSearchBean,
		    pageSizeAndOffset.get("offset"), pageSizeAndOffset.get("pageSize"));
		return customClientConvert(clients);
	}
	
	@Override
	public int findCountANCByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean) {
		return clientMetadataMapper.selectCountANCBySearchBean(searchBean, addressSearchBean);
	}
	
	@Override
	public List<Client> findChildByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean) {
		Map<String, Integer> pageSizeAndOffset = getPageSizeAndOffset(searchBean);
		List<CustomClient> clients = clientMetadataMapper.selectChildBySearchBean(searchBean, addressSearchBean,
		    pageSizeAndOffset.get("offset"), pageSizeAndOffset.get("pageSize"));
		return customClientConvert(clients);
	}
	
	@Override
	public int findCountChildByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean) {
		return clientMetadataMapper.selectCountChildBySearchBean(searchBean, addressSearchBean);
	}
	
	private Map<String, Integer> getPageSizeAndOffset(ClientSearchBean searchBean) {
		Map<String, Integer> pageSizeAndOffset = new HashMap<>();
		int pageSize = searchBean.getPageSize();
		if (pageSize == 0) {
			pageSize = DEFAULT_FETCH_SIZE;
		}
		
		int offset = searchBean.getPageNumber() * pageSize;
		pageSizeAndOffset.put("pageSize", pageSize);
		pageSizeAndOffset.put("offset", offset);
		return pageSizeAndOffset;
		
	}
	
	/**
	 * Method should be used only during Unit testing Deletes all existing records
	 */
	public void removeAll() {
		clientMetadataMapper.deleteByExample(new ClientMetadataExample());
		clientMapper.deleteByExample(new ClientExample());
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair<List<String>, Long> findAllIds(long serverVersion, int limit, boolean isArchived) {
		Long lastServerVersion = null;
		ClientMetadataExample example = new ClientMetadataExample();
		Criteria criteria = example.createCriteria();
		criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
		
		if (isArchived) {
			criteria.andDateDeletedIsNotNull();
		} else {
			criteria.andDateDeletedIsNull();
		}
		
		return getClientListLongPair(limit, lastServerVersion, example);
	}
	
	@Override
	public Pair<List<String>, Long> findAllIds(long serverVersion, int limit, boolean isArchived, Date fromDate,
	        Date toDate) {
		if (toDate == null && fromDate == null) {
			return findAllIds(serverVersion, limit, isArchived);
		} else {
			Long lastServerVersion = null;
			ClientMetadataExample example = new ClientMetadataExample();
			Criteria criteria = example.createCriteria();
			criteria.andServerVersionGreaterThanOrEqualTo(serverVersion);
			if (isArchived) {
				criteria.andDateDeletedIsNotNull();
			} else {
				criteria.andDateDeletedIsNull();
			}
			
			if (toDate != null && fromDate != null) {
				criteria.andDateCreatedBetween(fromDate, toDate);
			} else if (fromDate != null) {
				criteria.andDateCreatedGreaterThanOrEqualTo(fromDate);
			} else {
				criteria.andDateCreatedLessThanOrEqualTo(toDate);
			}
			
			return getClientListLongPair(limit, lastServerVersion, example);
		}
	}
	
	private Pair<List<String>, Long> getClientListLongPair(int limit, Long lastServerVersion,
	        ClientMetadataExample example) {
		Long serverVersion = lastServerVersion;
		ClientMetadataExample clientMetadataExample = example;
		int fetchLimit = limit > 0 ? limit : DEFAULT_FETCH_SIZE;
		
		List<String> clientIdentifiers = clientMetadataMapper.selectManyIds(clientMetadataExample, 0, fetchLimit);
		
		if (clientIdentifiers != null && !clientIdentifiers.isEmpty()) {
			clientMetadataExample = new ClientMetadataExample();
			clientMetadataExample.createCriteria().andDocumentIdEqualTo(clientIdentifiers.get(clientIdentifiers.size() - 1));
			List<ClientMetadata> clientMetaDataList = clientMetadataMapper.selectByExample(clientMetadataExample);

			serverVersion = clientMetaDataList != null && !clientMetaDataList.isEmpty()
			        ? clientMetaDataList.get(0).getServerVersion()
			        : 0;
		}
		return Pair.of(clientIdentifiers, serverVersion);
	}
	
	@Override
	public List<Patient> findClientById(String id) {
		Client client = get(id);
		return client == null ? Collections.emptyList() : convertToFHIR(Collections.singletonList(client));
	}
	
	@Override
	public List<Patient> findFamilyByJurisdiction(String jurisdiction) {
		
		List<String> baseEntityIds = eventsRepository.findBaseEntityIdsByLocation(jurisdiction);
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		if (baseEntityIds.isEmpty())
			return Collections.emptyList();
		clientMetadataExample.createCriteria().andBaseEntityIdIn(baseEntityIds).andDateDeletedIsNull()
		        .andLastNameEqualTo("Family");
		return convertToFHIR(convert(clientMetadataMapper.selectMany(clientMetadataExample, 0, 20000)));
	}
	
	@Override
	public List<Patient> findFamilyByResidence(String structureId) {
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andResidenceEqualTo(structureId).andDateDeletedIsNull()
		        .andLastNameEqualTo("Family");
		return convertToFHIR(convert(clientMetadataMapper.selectMany(clientMetadataExample, 0, DEFAULT_FETCH_SIZE)));
	}
	
	@Override
	public List<Patient> findFamilyMemberyByJurisdiction(String jurisdiction) {
		List<Client> clients = findByLocationIdExclusiveOfType(jurisdiction, "Family");
		return convertToFHIR(clients);
	}
	
	@Override
	public List<Patient> findFamilyMemberByResidence(String structureId) {
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andResidenceEqualTo(structureId).andDateDeletedIsNull()
		        .andClientTypeNotEqualTo("Family");
		return convertToFHIR(convert(clientMetadataMapper.selectMany(clientMetadataExample, 0, DEFAULT_FETCH_SIZE)));
	}
	
	@Override
	public List<Patient> findClientByRelationship(String relationship, String id) {
		return convertToFHIR(findByRelationshipId(relationship, id));
	}
	
	@Override
	public List<Client> findByClientTypeAndLocationId(String clientType, String locationId) {
		List<org.opensrp.domain.postgres.Client> clients = clientMetadataMapper.selectByLocationIdOfType(clientType,
		    locationId);
		return convert(clients);
	}

	@Override
	public List<Client> findByLocationIdExclusiveOfType(String locationId, String clientType) {
		List<org.opensrp.domain.postgres.Client> clients = clientMetadataMapper.selectByLocationIdAndNotOfType(locationId,
				clientType);
		return convert(clients);
	}

	@Override
	public Long countFamilyMembersByLocation(List<String> locationIds, Integer ageLowerBound) {
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		Criteria criteria = clientMetadataExample.createCriteria();
		criteria.andLocationIdIn(locationIds).andClientTypeNotEqualTo("Family");
		if (ageLowerBound != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, -5);
			Date date = calendar.getTime();
			criteria.andBirthDateLessThan(date);
		}
		return clientMetadataMapper.countMany(clientMetadataExample);
	}

	private List<Patient> convertToFHIR(List<Client> clients) {
		return clients.stream().map(client -> ClientConverter.convertClientToPatientResource(client))
		        .collect(Collectors.toList());
	}
	
}
