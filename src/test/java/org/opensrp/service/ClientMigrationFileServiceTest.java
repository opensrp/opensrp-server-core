package org.opensrp.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.domain.ClientMigrationFile;
import org.opensrp.domain.postgres.Client;
import org.opensrp.repository.ClientFormRepository;
import org.opensrp.repository.ClientMigrationFileRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	public void getClientMigrationFileShouldReturnClientMigrationFile() {
		String identifier = "my-identifier";

		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
		clientMigrationFile.setIdentifier(identifier);
		Mockito.doReturn(clientMigrationFile).when(clientMigrationFileRepository).getClientMigrationFileByIdentifier(identifier);

		// call the method under test
		Assert.assertEquals(clientMigrationFile, clientMigrationFileService.getClientMigrationFile(identifier));

		Mockito.verify(clientMigrationFileRepository).getClientMigrationFileByIdentifier(identifier);
	}

	@Test
	public void getClientMigrationFileShouldReturnNull() {
		// call the method under test
		Assert.assertNull(clientMigrationFileService.getClientMigrationFile(null));

		Mockito.verify(clientMigrationFileRepository, Mockito.never()).getClientMigrationFileByIdentifier(identifier);
	}

	@Test
	public void saveClientMigrationFilesShouldCallAddOrUpdateClientMigrationFileAndReturnEmptyErrorSet() {
		ClientMigrationFileService clientMigrationFileService1 = Mockito.spy(clientMigrationFileService);

		List<ClientMigrationFile> clientMigrationFileList = new ArrayList<>();

		for (int i = 0; i < 8; i++) {
			clientMigrationFileList.add(new ClientMigrationFile());
		}

		ArgumentCaptor<List<ClientMigrationFile>> listArgumentCaptor = ArgumentCaptor.forClass(List.class);

		// Call the method under test
		Set<String> errorSet = clientMigrationFileService1.saveClientMigrationFiles(listArgumentCaptor.capture());

		Assert.assertEquals(0, errorSet.size());
		Assert.assertEquals(8, listArgumentCaptor.getValue().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteClientMigrationFileShouldThrowExceptionWhenGivenClientMigrationFileWithoutIdentifier() {
		clientMigrationFileService.deleteClientMigrationFile(new ClientMigrationFile());
	}

	@Test
	public void deleteClientMigrationFileShouldCallRepositorySafeRemove() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
		clientMigrationFile.setIdentifier("my-identifier");

		clientMigrationFileService.deleteClientMigrationFile(clientMigrationFile);

		Mockito.verify(clientMigrationFileRepository).safeRemove(clientMigrationFile);
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
