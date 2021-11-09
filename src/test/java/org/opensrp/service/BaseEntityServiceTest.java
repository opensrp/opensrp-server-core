package org.opensrp.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opensrp.repository.ClientsRepository;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*","org.w3c.*"})
public class BaseEntityServiceTest {

    private BaseEntityService baseEntityService;
    private ClientsRepository allBaseEntities;

    @Before
    public void setUp() {
        allBaseEntities = Mockito.mock(ClientsRepository.class);
        baseEntityService = new BaseEntityService(allBaseEntities);
    }

    @Test
    public void testGetAllBaseEntities() {
        baseEntityService.getAllBaseEntities();
        Mockito.verify(allBaseEntities).findAllClients();
    }

    @Test
    public void testFindByBaseEntityId() {
        String identifier = "identifier-a";
        baseEntityService.findByIdentifier(identifier);
        Mockito.verify(allBaseEntities).findAllByIdentifier(identifier);
    }

    @Test
    public void testFindByIdentifierAndIdentifierType() {
        String identifier = "identifier-a";
        String identifierType = "client-identifier";
        baseEntityService.findByIdentifier(identifierType, identifier);
        Mockito.verify(allBaseEntities).findAllByIdentifier(identifierType, identifier);
    }
}
