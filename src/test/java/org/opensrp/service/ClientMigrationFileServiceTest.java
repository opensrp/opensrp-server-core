package org.opensrp.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.domain.ClientMigrationFile;
import org.opensrp.repository.ClientFormRepository;
import org.opensrp.repository.ClientMigrationFileRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ClientMigrationFileServiceTest extends BaseRepositoryTest {


	private ClientMigrationFileService clientMigrationFileService;

	private ClientMigrationFileRepository clientMigrationFileRepository;

	private Set<String> scripts = new HashSet<String>();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		clientMigrationFileService = Mockito.mock(ClientMigrationFileService.class);
		clientMigrationFileRepository = Mockito.mock(ClientMigrationFileRepository.class);

		clientMigrationFileService = new ClientMigrationFileService();
		clientMigrationFileService.setClientMigrationFileRepository(clientMigrationFileRepository);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void setClientMigrationFileRepository() {
		clientMigrationFileService.setClientMigrationFileRepository(clientMigrationFileRepository);

		Assert.assertEquals(clientMigrationFileRepository, clientMigrationFileService.getClientMigrationFileRepository());
	}

	@Test
	public void getClientMigrationFileRepository() {
		Whitebox.setInternalState(clientMigrationFileService, "clientMigrationFileRepository", clientMigrationFileRepository);

		Assert.assertEquals(clientMigrationFileRepository, clientMigrationFileService.getClientMigrationFileRepository());
	}

	@Test
	public void getAllClientMigrationFiles() {
		clientMigrationFileService.getAllClientMigrationFiles();

		Mockito.verify(clientMigrationFileRepository).getAll();
	}

	@Test
	public void testGetAllClientMigrationFiles() {
		int limit = 78;
		clientMigrationFileService.getAllClientMigrationFiles(limit);

		Mockito.verify(clientMigrationFileRepository).getAll(limit);
	}

	@Test
	public void addOrUpdateClientMigrationFileShouldCallRepositoryAddMethod() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
		clientMigrationFile.setIdentifier("my-identifier");

		clientMigrationFileService.addOrUpdateClientMigrationFile(clientMigrationFile);

		Mockito.verify(clientMigrationFileRepository).add(clientMigrationFile);
	}

	@Test
	public void addOrUpdateClientMigrationFileShouldCallRepositoryUpdateMethod() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
		clientMigrationFile.setIdentifier("my-identifier");

		Mockito.doReturn(clientMigrationFile).when(clientMigrationFileRepository).get("my-identifier");

		// Call the method under test
		clientMigrationFileService.addOrUpdateClientMigrationFile(clientMigrationFile);

		Mockito.verify(clientMigrationFileRepository).update(clientMigrationFile);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addOrUpdateClientMigrationFileShouldThrowExceptionWhenGivenClientMigrationFileWithoutIdentifier() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();

		// Call the method under test
		clientMigrationFileService.addOrUpdateClientMigrationFile(clientMigrationFile);
	}

	@Test(expected = IllegalArgumentException.class)
	public void addClientMigrationFileShouldThrowExceptionWhenGivenClientMigrationFileWithoutIdentifier() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();

		clientMigrationFileService.addClientMigrationFile(clientMigrationFile);
	}

	@Test
	public void addClientMigrationFileShouldCallRepositoryAddMethod() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
		clientMigrationFile.setIdentifier("my-identifier");

		// call the method under test
		Assert.assertNotNull(clientMigrationFileService.addClientMigrationFile(clientMigrationFile));

		Mockito.verify(clientMigrationFileRepository).add(clientMigrationFile);
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateClientMigrationFileShouldThrowExceptionWhenGivenClientMigrationFileWithoutIdentifier() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();

		clientMigrationFileService.updateClientMigrationFile(clientMigrationFile);
	}

	@Test
	public void updateClientMigrationFileShouldCallRepositoryUpdateClientMigrationFile() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
		clientMigrationFile.setIdentifier("my-identifier");

		clientMigrationFileService.updateClientMigrationFile(clientMigrationFile);
	}

	@Test
	public void getClientMigrationFile() {
	}

	@Test
	public void saveClientMigrationFiles() {
	}

	@Test
	public void deleteClientMigrationFile() {
	}

	@Test
	public void getClientMigrationFileByFilename() {
	}

	@Test
	public void getClientMigrationFileByVersion() {
	}

	@Test
	public void getClientMigrationFileByManifestId() {
	}

	@Test
	public void getClientMigrationFileByFileId() {
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		scripts.add("client_form.sql");
		return scripts;
	}
}
