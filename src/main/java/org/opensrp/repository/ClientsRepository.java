package org.opensrp.repository;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.opensrp.domain.postgres.HouseholdClient;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;
import org.smartregister.domain.Client;
import org.smartregister.pathevaluator.dao.ClientDao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public interface ClientsRepository extends BaseRepository<Client>, ClientDao {

    Client findByBaseEntityId(String baseEntityId);

    Client findById(String id);

    List<Client> findAllClients();

    List<Client> findAllByIdentifier(String identifier);

    List<Client> findAllByIdentifier(String identifierType, String identifier);

    List<Client> findAllByAttribute(String attributeType, String attribute);

    List<Client> findAllByAttributes(String attributeType, List<String> attributes);

    List<Client> findAllByMatchingName(String nameMatches);

    /**
     * Find a client based on the relationship id and between a range of date created dates e.g
     * given mother's id get children born at a given time
     *
     * @param relationalId
     * @param dateFrom
     * @param dateTo
     * @return
     */
    List<Client> findByRelationshipIdAndDateCreated(String relationalId, String dateFrom, String dateTo);

    List<Client> findByRelationshipId(String relationshipType, String entityId);

    List<Client> findByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean);

    List<Client> findByDynamicQuery(String query);

    List<Client> findByCriteria(ClientSearchBean searchBean);

    List<Client> findByCriteria(AddressSearchBean addressSearchBean, DateTime lastEditFrom, DateTime lastEditTo);

    List<Client> findByRelationShip(String relationIndentier);

    List<Client> findByEmptyServerVersion();

    /**
     * This method searches for all clients ordered by serverVersion ascending
     * and return size determined by limit is available or default fetch size
     *
     * @param serverVersion server version for last client that was fetched
     * @param limit         number of records to fetch
     * @return
     */
    List<Client> findByServerVersion(long serverVersion, Integer limit);

    Long countAll(long serverVersion);

    List<Client> findByFieldValue(String field, List<String> ids);

    List<Client> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar);

    List<HouseholdClient> selectMemberCountHouseholdHeadProviderByClients(String field, List<String> ids, String clientType);

    HouseholdClient findTotalCountHouseholdByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean);

    List<Client> findMembersByRelationshipId(String relationshipId);

    List<Client> findAllClientsByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean);

    HouseholdClient findCountAllClientsByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean);

    List<Client> findHouseholdByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean);

    List<Client> findANCByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean);

    int findCountANCByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean);

    List<Client> findChildByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean);

    int findCountChildByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean);

    /**
     * Updates a client
     *
     * @param entity        the client to be updated
     * @param allowArchived a flag that allows update of archived clients
     */
    void update(Client entity, boolean allowArchived);

    /**
     * This method searches for client ids paginated by server version
     *
     * @param serverVersion server version for last client that was fetched
     * @param limit         upper limit on number of client ids to fetch
     * @param isArchived    whether to return archived events
     * @return a list of client ids and last server version
     */
    Pair<List<String>, Long> findAllIds(long serverVersion, int limit, boolean isArchived);

    /**
     * overloads {@link #findAllIds(long, int, boolean)} by adding date/time filters
     *
     * @param serverVersion
     * @param limit
     * @param fromDate
     * @param toDate
     * @return
     */
    Pair<List<String>, Long> findAllIds(long serverVersion, int limit, boolean isArchived, Date fromDate, Date toDate);

    List<Client> findByClientTypeAndLocationId(String clientType, String locationId);

    List<Client> findByLocationIdExclusiveOfType(String locationId, String clientType);

    /**
     * @param clients
     * @return
     */
    List<Client> convert(List<org.opensrp.domain.postgres.Client> clients);

}
