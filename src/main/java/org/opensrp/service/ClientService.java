package org.opensrp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.smartregister.domain.Address;
import org.smartregister.domain.Client;
import org.opensrp.domain.postgres.HouseholdClient;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;
import org.opensrp.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class ClientService {
	
	private final ClientsRepository allClients;
	
	@Autowired
	public ClientService(ClientsRepository allClients) {
		this.allClients = allClients;
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostAuthorize("hasPermission(returnObject,'Client', 'CLIENT_VIEW')")
	public Client getByBaseEntityId(String baseEntityId) {
		return allClients.findByBaseEntityId(baseEntityId);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findAllClients() {
		return allClients.findAllClients();
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findAllByIdentifier(String identifier) {
		return allClients.findAllByIdentifier(identifier);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findAllByIdentifier(String identifierType, String identifier) {
		return allClients.findAllByIdentifier(identifierType, identifier);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByRelationshipIdAndDateCreated(String relationalId, String dateFrom, String dateTo) {
		return allClients.findByRelationshipIdAndDateCreated(relationalId, dateFrom, dateTo);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByRelationship(String relationalId) {
		return allClients.findByRelationShip(relationalId);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByRelationshipIdAndType(String relationshipType, String entityId) {
		return allClients.findByRelationshipId(relationshipType, entityId);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findAllByAttribute(String attributeType, String attribute) {
		return allClients.findAllByAttribute(attributeType, attribute);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findAllByMatchingName(String nameMatches) {
		return allClients.findAllByMatchingName(nameMatches);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByCriteria(ClientSearchBean clientSearchBean, AddressSearchBean addressSearchBean,
	                                   DateTime lastEditFrom, DateTime lastEditTo) {
		clientSearchBean.setLastEditFrom(lastEditFrom);
		clientSearchBean.setLastEditTo(lastEditTo);
		
		return allClients.findByCriteria(clientSearchBean, addressSearchBean);//db.queryView(q.includeDocs(true), Client.class);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByCriteria(ClientSearchBean clientSearchBean, Long serverVersion) {
		return allClients.findByCriteria(clientSearchBean, new AddressSearchBean());
	}
	
	/*	public List<Client> findByCriteria(String addressType, String country, String stateProvince, String cityVillage, String countyDistrict, 
				String  subDistrict, String town, String subTown, DateTime lastEditFrom, DateTime lastEditTo) {
			return allClients.findByCriteria(null, null, null, null, null, null, null, null, addressType, country, stateProvince, cityVillage, countyDistrict, subDistrict, town, subTown, lastEditFrom, lastEditTo);
		}*/

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByDynamicQuery(String query) {
		return allClients.findByDynamicQuery(query);
	}

	@PreAuthorize("hasPermission(#client,'CLIENT', 'CLIENT_CREATE')")
	public Client addClient(Client client) {
		if (client.getBaseEntityId() == null) {
			throw new RuntimeException("No baseEntityId");
		}
		Client c = findClient(client);
		if (c != null) {
			try {
				client.setId(c.getId());
				client.setRevision(c.getRevision());
				updateClient(client);
			}
			catch (JSONException e) {
				throw new IllegalArgumentException(
				        "A client already exists with given list of identifiers. Consider updating data.[" + c + "]");
			}
		}
		
		client.setDateCreated(DateTime.now());
		allClients.add(client);
		return client;
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostAuthorize("hasPermission(returnObject,'Client', 'CLIENT_VIEW')")
	public Client findClient(Client client) {
		// find by auto assigned entity id
		Client c = allClients.findByBaseEntityId(client.getBaseEntityId());
		if (c != null) {
			return c;
		}
		
		//still not found!! search by generic identifiers
		
		for (String idt : client.getIdentifiers().keySet()) {
			List<Client> cl = allClients.findAllByIdentifier(client.getIdentifier(idt));
			if (cl.size() > 1) {
				throw new IllegalArgumentException("Multiple clients with identifier type " + idt + " and ID "
				        + client.getIdentifier(idt) + " exist.");
			} else if (cl.size() != 0) {
				return cl.get(0);
			}
		}
		return c;
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostAuthorize("hasPermission(filterObject, 'Client' , 'CLIENT_VIEW')")
	public Client find(String uniqueId) {
		// find by document id
		Client c = allClients.findByBaseEntityId(uniqueId);
		if (c != null) {
			return c;
		}
		
		// if not found find if it is in any identifiers TODO refactor it later
		List<Client> cl = allClients.findAllByIdentifier(uniqueId);
		if (cl.size() > 1) {
			throw new IllegalArgumentException("Multiple clients with identifier " + uniqueId + " exist.");
		} else if (cl.size() != 0) {
			return cl.get(0);
		}
		
		return c;
	}

	@PreAuthorize("hasPermission(#updatedClient,'Client', 'CLIENT_UPDATE')")
	public void updateClient(Client updatedClient) throws JSONException {
		// If update is on original entity
		if (updatedClient.isNew()) {
			throw new IllegalArgumentException(
			        "Client to be updated is not an existing and persisting domain object. Update database object instead of new pojo");
		}
		
		if (findClient(updatedClient) == null) {
			throw new IllegalArgumentException("No client found with given list of identifiers. Consider adding new!");
		}
		
		updatedClient.setDateEdited(DateTime.now());
		allClients.update(updatedClient);
	}

	@PreAuthorize("hasPermission(#updatedClient,'Client', 'CLIENT_UPDATE')")
	public Client mergeClient(Client updatedClient) {
		try {
			Client original = findClient(updatedClient);
			if (original == null) {
				throw new IllegalArgumentException("No client found with given list of identifiers. Consider adding new!");
			}
			
			original = (Client) Utils.getMergedJSON(original, updatedClient,
			    Arrays.asList(Client.class.getDeclaredFields()), Client.class);
			
			for (Address a : updatedClient.getAddresses()) {
				if (original.getAddress(a.getAddressType()) == null) {
					original.addAddress(a);
				} else {
					original.removeAddress(a.getAddressType());
					original.addAddress(a);
				}
			}
			for (String k : updatedClient.getIdentifiers().keySet()) {
				original.addIdentifier(k, updatedClient.getIdentifier(k));
			}
			for (String k : updatedClient.getAttributes().keySet()) {
				original.addAttribute(k, updatedClient.getAttribute(k));
			}
			
			original.setDateEdited(DateTime.now());
			allClients.update(original);
			return original;
		}
		catch (JSONException | JsonProcessingException | SecurityException e) {
			throw new RuntimeException(e);
		}
	
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByServerVersion(long serverVersion, Integer limit) {
		return allClients.findByServerVersion(serverVersion, limit);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar) {
		return allClients.notInOpenMRSByServerVersion(serverVersion, calendar);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByFieldValue(String field, List<String> ids) {
		return allClients.findByFieldValue(field, ids);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByFieldValue(String id) {
		return allClients.findByRelationShip(id);
	}

	@PreAuthorize("(hasRole('CLIENT_CREATE') or (hasRole('CLIENT_UPDATE'))) and"
			+ " (hasPermission(#client,'Client','CLIENT_CREATE') or hasPermission(#client,'Client','CLIENT_UPDATE'))")
	public Client addorUpdate(Client client) {
		if (client.getBaseEntityId() == null) {
			throw new RuntimeException("No baseEntityId");
		}
		Client c = findClient(client);
		if (c != null) {
			client.setRevision(c.getRevision());
			client.setId(c.getId());
			client.setDateEdited(DateTime.now());
			client.setServerVersion(0l);
			client.addIdentifier("OPENMRS_UUID", c.getIdentifier("OPENMRS_UUID"));
			allClients.update(client);
			
		} else {
			
			client.setDateCreated(DateTime.now());
			allClients.add(client);
		}
		return client;
	}

	@PreAuthorize("hasPermission(#client,'Client','CLIENT_CREATE') and hasPermission(#client,'Client','CLIENT_UPDATE')")
	public Client addorUpdate(Client client, boolean resetServerVersion) {
		if (client.getBaseEntityId() == null) {
			throw new RuntimeException("No baseEntityId");
		}
		Client c = findClient(client);
		if (c != null) {
			client.setRevision(c.getRevision());
			client.setId(c.getId());
			client.setDateEdited(DateTime.now());
			if (resetServerVersion) {
				client.setServerVersion(0l);
			}
			allClients.update(client);
			
		} else {
			client.setDateCreated(DateTime.now());
			allClients.add(client);
		}
		return client;
	}
	
	public Map<String, HouseholdClient> getMemberCountHouseholdHeadProviderByClients(List<String> ids, String clientType) {
		List<HouseholdClient> householdClients = allClients.selectMemberCountHouseholdHeadProviderByClients("", ids,
		    clientType);
		Map<String, HouseholdClient> households = new HashMap<String, HouseholdClient>();
		if (householdClients != null) {
			for (HouseholdClient householdClient : householdClients) {
				households.put(householdClient.getRelationalId(), householdClient);
			}
		}
		return households;
	}
	
	public HouseholdClient findTotalCountHouseholdByCriteria(ClientSearchBean clientSearchBean,
	                                                         AddressSearchBean addressSearchBean) {
		return allClients.findTotalCountHouseholdByCriteria(clientSearchBean, addressSearchBean);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> getHouseholdList(List<String> ids, String clientType, AddressSearchBean addressSearchBean,
	                                     ClientSearchBean searchBean, List<Client> clients) {
		Map<String, HouseholdClient> householdClients = getMemberCountHouseholdHeadProviderByClients(ids, clientType);
		
		List<Client> clientList = new ArrayList<Client>();
		
		for (Client client : clients) {
			if (householdClients != null) {
				HouseholdClient householdClient = householdClients.get(client.getBaseEntityId());
				if (householdClient != null) {
					client.addAttribute("memberCount", householdClient.getMemebrCount());
					client.addAttribute("HouseholdHead", householdClient.getHouseholdHead());
					client.addAttribute("ProvierId", householdClient.getProviderId());
				} else {
					client.addAttribute("memberCount", 0);
					client.addAttribute("HouseholdHead", "");
					client.addAttribute("ProvierId", "");
				}
				clientList.add(client);
			}
		}
		return clientList;
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findMembersByRelationshipId(String relationshipId) {
		return allClients.findMembersByRelationshipId(relationshipId);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findAllClientsByCriteria(ClientSearchBean clientSearchBean, AddressSearchBean addressSearchBean) {
		return allClients.findAllClientsByCriteria(clientSearchBean, addressSearchBean);
	}
	
	public HouseholdClient findTotalCountAllClientsByCriteria(ClientSearchBean clientSearchBean,
	                                                          AddressSearchBean addressSearchBean) {
		return allClients.findCountAllClientsByCriteria(clientSearchBean, addressSearchBean);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findHouseholdByCriteria(ClientSearchBean clientSearchBean, AddressSearchBean addressSearchBean,
	                                            DateTime lastEditFrom, DateTime lastEditTo) {
		clientSearchBean.setLastEditFrom(lastEditFrom);
		clientSearchBean.setLastEditTo(lastEditTo);
		return allClients.findHouseholdByCriteria(clientSearchBean, addressSearchBean);
		
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findAllANCByCriteria(ClientSearchBean clientSearchBean, AddressSearchBean addressSearchBean) {
		return allClients.findANCByCriteria(clientSearchBean, addressSearchBean);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	public int findCountANCByCriteria(ClientSearchBean clientSearchBean, AddressSearchBean addressSearchBean) {
		return allClients.findCountANCByCriteria(clientSearchBean, addressSearchBean);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findAllChildByCriteria(ClientSearchBean clientSearchBean, AddressSearchBean addressSearchBean) {
		return allClients.findChildByCriteria(clientSearchBean, addressSearchBean);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	public int findCountChildByCriteria(ClientSearchBean clientSearchBean, AddressSearchBean addressSearchBean) {
		return allClients.findCountChildByCriteria(clientSearchBean, addressSearchBean);
	}

	/**
	 * This method searches for client ids paginated by server version
	 *
	 * @param serverVersion server version for last client that was fetched
	 * @param limit upper limit on number of client ids to fetch
	 * @param isArchived whether to return archived events
	 * @return a list of client ids
	 */
	public Pair<List<String>, Long> findAllIds(long serverVersion, int limit, boolean isArchived) {
		return allClients.findAllIds(serverVersion, limit, isArchived);
	}

	@PreAuthorize("hasRole('CLIENT_VIEW')")
	@PostFilter("hasPermission(filterObject, 'CLIENT_VIEW')")
	public List<Client> findByClientTypeAndLocationId(String clientType, String locationId) {
		return allClients.findByClientTypeAndLocationId(clientType,locationId);
	}

	/**
	 * This method searches for client using an id
	 *
	 * @param id server version for last client that was fetched
	 * @return a client object
	 */
	public Client findById(String id) {
		return allClients.findById(id);
	}
}
