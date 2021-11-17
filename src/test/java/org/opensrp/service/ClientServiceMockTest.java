package org.opensrp.service;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;
import org.smartregister.domain.Client;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class ClientServiceMockTest {

    @Mock
    private ClientsRepository clientsRepository;

    @InjectMocks
    private ClientService clientService;

    private final String fakeRelationshipId = "2cdb85bb-b601-4bc7-a0e1-a806491081a8";
    private final String fakeEntityId = "6432b376-97f4-4628-9b48-71688829b624";
    private final long fakeServerVersion = 36L;


    @Test
    public void testFindByRelationshipIdAndDateCreatedCallsRepository() {
        String dateFrom = "2019-01-10";
        String dateTo = "2020-16-30";
        clientService.findByRelationshipIdAndDateCreated(fakeRelationshipId, dateFrom, dateTo);
        verify(clientsRepository).findByRelationshipIdAndDateCreated(eq(fakeRelationshipId), eq(dateFrom), eq(dateTo));
    }

    @Test
    public void testFindByRelationship() {
        clientService.findByRelationship(fakeRelationshipId);
        verify(clientsRepository).findByRelationShip(eq(fakeRelationshipId));
    }

    @Test
    public void testFindByRelationshipIdAndType() {
        String relationshipType = "family";
        clientService.findByRelationshipIdAndType(relationshipType, fakeEntityId);
        verify(clientsRepository).findByRelationshipId(eq(relationshipType), eq(fakeEntityId));
    }

    @Test
    public void testFindAllByAttributes() {
        String attributeType = "test_attribute_type";
        List<String> attributes = new ArrayList<>();
        clientService.findAllByAttributes(attributeType, attributes);
        verify(clientsRepository).findAllByAttributes(eq(attributeType), eq(attributes));
    }

    @Test
    public void testFindAllByMatchingName() {
        String olga = "olga";
        clientService.findAllByMatchingName(olga);
        verify(clientsRepository).findAllByMatchingName(eq(olga));
    }

    @Test
    public void testFindByDynamicQuery() {
        String query = "query";
        clientService.findByDynamicQuery(query);
        verify(clientsRepository).findByDynamicQuery(eq(query));
    }

    @Test(expected = RuntimeException.class)
    public void testAddClientNoBaseEntityIdThrowsError() {
        Client client = spy(Client.class);
        client.setBaseEntityId(null);

        clientService.addClient(client);
    }

    @Test(expected = RuntimeException.class)
    public void testAddOrUpdateNoBaseEntityIdThrowsError() {
        Client client = spy(Client.class);
        client.setBaseEntityId(null);

        clientService.addClient(client);
    }

    @Test
    public void testFindServerByVersion() {
        int limit = Integer.MAX_VALUE;
        clientService.findByServerVersion(fakeServerVersion, limit);
        verify(clientsRepository).findByServerVersion(eq(fakeServerVersion), eq(limit));
    }

    @Test
    public void testCountAll() {
        clientService.countAll(fakeServerVersion);
        verify(clientsRepository).countAll(eq(fakeServerVersion));
    }

    @Test
    public void testNotInOpenMRSByServerVersion() {
        Calendar calendar = Calendar.getInstance();
        clientService.notInOpenMRSByServerVersion(fakeServerVersion, calendar);
        verify(clientsRepository).notInOpenMRSByServerVersion(eq(fakeServerVersion), eq(calendar));
    }

    @Test
    public void testFindByFieldValueForFieldIds() {
        String field = "field";
        List<String> ids = new ArrayList<>();
        clientService.findByFieldValue(field, ids);
        verify(clientsRepository).findByFieldValue(eq(field), eq(ids));
    }

    @Test
    public void testFindByFieldValueId() {
        String id = fakeRelationshipId;
        clientService.findByFieldValue(id);
        verify(clientsRepository).findByRelationShip(eq(id));
    }

    @Test
    public void testFindTotalCountHouseholdByCriteria() {
        ClientSearchBean clientSearchBean = mock(ClientSearchBean.class);
        AddressSearchBean addressSearchBean = mock(AddressSearchBean.class);
        clientService.findTotalCountHouseholdByCriteria(clientSearchBean, addressSearchBean);
        verify(clientsRepository).findTotalCountHouseholdByCriteria(eq(clientSearchBean), eq(addressSearchBean));
    }

    @Test
    public void testFindMembersByRelationshipId() {
        clientService.findMembersByRelationshipId(fakeRelationshipId);
        verify(clientsRepository).findMembersByRelationshipId(eq(fakeRelationshipId));
    }

    @Test
    public void testFindAllClientsByCriteria() {
        ClientSearchBean clientSearchBean = mock(ClientSearchBean.class);
        AddressSearchBean addressSearchBean = mock(AddressSearchBean.class);
        clientService.findAllClientsByCriteria(clientSearchBean, addressSearchBean);
        verify(clientsRepository).findAllClientsByCriteria(eq(clientSearchBean), eq(addressSearchBean));
    }

    @Test
    public void testFindTotalCountAllClientsByCriteria() {
        ClientSearchBean clientSearchBean = mock(ClientSearchBean.class);
        AddressSearchBean addressSearchBean = mock(AddressSearchBean.class);
        clientService.findTotalCountAllClientsByCriteria(clientSearchBean, addressSearchBean);
        verify(clientsRepository).findCountAllClientsByCriteria(clientSearchBean, addressSearchBean);
    }

    @Test
    public void testFindHouseholdByCriteria() {
        ClientSearchBean clientSearchBean = spy(ClientSearchBean.class);
        AddressSearchBean addressSearchBean = spy(AddressSearchBean.class);
        DateTime lastEditFrom = DateTime.now();
        DateTime lastEditTo = DateTime.now();
        clientService.findHouseholdByCriteria(clientSearchBean, addressSearchBean, lastEditFrom, lastEditTo);
        verify(clientSearchBean).setLastEditFrom(eq(lastEditFrom));
        verify(clientSearchBean).setLastEditTo(eq(lastEditTo));
        verify(clientsRepository).findHouseholdByCriteria(eq(clientSearchBean), eq(addressSearchBean));
    }

    @Test
    public void testFindAllANCByCriteria() {
        ClientSearchBean clientSearchBean = mock(ClientSearchBean.class);
        AddressSearchBean addressSearchBean = mock(AddressSearchBean.class);
        clientService.findAllANCByCriteria(clientSearchBean, addressSearchBean);
        verify(clientsRepository).findANCByCriteria(eq(clientSearchBean), eq(addressSearchBean));
    }

    @Test
    public void testFindCountANCByCriteria() {
        ClientSearchBean clientSearchBean = mock(ClientSearchBean.class);
        AddressSearchBean addressSearchBean = mock(AddressSearchBean.class);
        clientService.findCountANCByCriteria(clientSearchBean, addressSearchBean);
        verify(clientsRepository).findCountANCByCriteria(eq(clientSearchBean), eq(addressSearchBean));
    }

    @Test
    public void findAllChildByCriteria() {
        ClientSearchBean clientSearchBean = mock(ClientSearchBean.class);
        AddressSearchBean addressSearchBean = mock(AddressSearchBean.class);
        clientService.findAllChildByCriteria(clientSearchBean, addressSearchBean);
        verify(clientsRepository).findChildByCriteria(eq(clientSearchBean), eq(addressSearchBean));
    }

    @Test
    public void testFindCountChildByCriteria() {
        ClientSearchBean clientSearchBean = mock(ClientSearchBean.class);
        AddressSearchBean addressSearchBean = mock(AddressSearchBean.class);
        clientService.findCountChildByCriteria(clientSearchBean, addressSearchBean);
        verify(clientsRepository).findCountChildByCriteria(eq(clientSearchBean), eq(addressSearchBean));
    }

    @Test
    public void testFindAllIds() {
        int limit = Integer.MAX_VALUE;
        boolean isArchived = false;
        clientService.findAllIds(fakeServerVersion, limit, isArchived);
        verify(clientsRepository).findAllIds(eq(fakeServerVersion), eq(limit), eq(isArchived));
    }

    @Test
    public void testFindAllIdsWithDates() {
        Date dateFrom = mock(Date.class);
        Date dateTo = mock(Date.class);
        int limit = Integer.MAX_VALUE;
        boolean isArchived = false;
        clientService.findAllIds(fakeServerVersion, limit, isArchived, dateFrom, dateTo);
        verify(clientsRepository).findAllIds(eq(fakeServerVersion), eq(limit), eq(isArchived), eq(dateFrom), eq(dateTo));
    }

    @Test
    public void testFindByClientTypeAndLocationId() {
        String clientType = "family";
        String locationId = "f6154a29-2f11-4d7d-bc82-001264550b2f";
        clientService.findByClientTypeAndLocationId(clientType, locationId);
        verify(clientsRepository).findByClientTypeAndLocationId(eq(clientType), eq(locationId));
    }

    @Test
    public void testFindById(){
        String id = fakeEntityId;
        clientService.findById(id);
        verify(clientsRepository).findById(eq(id));
    }
}
