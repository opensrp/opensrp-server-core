package org.opensrp.repository.postgres;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.ClientFormRepository;
import org.opensrp.service.ClientFormService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.InvalidTransactionException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ClientFormRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ClientFormRepository clientFormRepository;
    private Set<String> scripts = new HashSet<String>();

    @Override
    protected Set<String> getDatabaseScripts() {
        scripts.add("client_form.sql");
        return scripts;
    }

    @Test
    public void testGet() {
        assertNotNull(clientFormRepository.get(5));
    }

    @Test
    public void testCountClientFormByFormIdentifier() {
        assertEquals(5, clientFormRepository.countClientFormByFormIdentifier("json.form/adverse_event.json"));
    }

    @Test
    public void testGetClientFormMetadataFromIdentifierAndVersion() {
        ClientFormMetadata clientFormMetadata = clientFormRepository.getClientFormMetadata("0.0.1", "json.form/adverse_event.json");
        assertEquals("AEFI", clientFormMetadata.getLabel());
        assertEquals("child", clientFormMetadata.getModule());
    }

    @Test
    public void testGetAvailableClientFormVersions() {
        assertEquals(5, clientFormRepository.getAvailableClientFormVersions("json.form/adverse_event.json").size());
    }

    @Test
    public void testGetFormMetadata() {
        ClientFormMetadata clientFormMetadata = clientFormRepository.getFormMetadata(3);
        assertEquals("child", clientFormMetadata.getModule());
        assertEquals("0.1.0", clientFormMetadata.getVersion());
        assertEquals("json.form/adverse_event.json", clientFormMetadata.getIdentifier());
    }

    @Test
    public void testCreateShouldReturnNull() throws InvalidTransactionException {
        ClientForm clientForm = new ClientForm();
        clientForm.setId(2L);
        ClientFormService.CompleteClientForm completeClientForm = clientFormRepository.create(clientForm, new ClientFormMetadata());
        assertNull(completeClientForm);
    }

    @Test
    public void testCreateShouldReturnValidObject() throws InvalidTransactionException {
        ClientForm clientForm = new ClientForm();
        clientForm.setCreatedAt(new Date());
        clientForm.setJson("{'from': 'child'}");

        ClientFormMetadata clientFormMetadata = new ClientFormMetadata();
        clientFormMetadata.setModule("child");
        clientFormMetadata.setVersion("1.0.0");
        clientFormMetadata.setIdentifier("json.form/child/sample.json");
        clientFormMetadata.setLabel("SAMPLE FORM");
        clientFormMetadata.setCreatedAt(new Date());

        ClientFormService.CompleteClientForm completeClientForm = clientFormRepository.create(clientForm, clientFormMetadata);

        assertEquals(6, (long) completeClientForm.clientForm.getId());
        assertEquals(6, (long) completeClientForm.clientFormMetadata.getId());
    }

    @Test
    public void testGetAll() {
        assertEquals(5, clientFormRepository.getAll().size());
    }

    @Test
    public void safeRemove() {
        ClientForm clientForm = new ClientForm();
        clientForm.setCreatedAt(new Date());
        clientForm.setJson("{}");

        clientFormRepository.add(clientForm);

        assertNotNull(clientFormRepository.get(6));
        clientForm.setId(6L);

        clientFormRepository.safeRemove(clientForm);
        assertNull(clientFormRepository.get(6));
    }

}