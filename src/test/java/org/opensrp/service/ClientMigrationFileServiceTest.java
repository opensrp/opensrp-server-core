package org.opensrp.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.opensrp.domain.ClientMigrationFile;
import org.opensrp.repository.ClientMigrationFileRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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

		Mockito.verify(clientMigrationFileRepository).update(clientMigrationFile);
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

		Mockito.verify(clientMigrationFileRepository, Mockito.never()).getClientMigrationFileByIdentifier(Mockito.nullable(String.class));
	}

	@Test
	public void saveClientMigrationFilesShouldCallAddOrUpdateClientMigrationFileAndReturnEmptyErrorSet() {
		ClientMigrationFileService clientMigrationFileService1 = Mockito.spy(clientMigrationFileService);

		List<ClientMigrationFile> clientMigrationFileList = new ArrayList<>();

		for (int i = 0; i < 8; i++) {
			ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
			clientMigrationFile.setIdentifier(UUID.randomUUID().toString());
			clientMigrationFileList.add(clientMigrationFile);
		}

		ArgumentCaptor<ClientMigrationFile> clientMigrationFileArgumentCaptor = ArgumentCaptor.forClass(ClientMigrationFile.class);

		// Call the method under test
		ArrayList<String> errorSet = clientMigrationFileService1.saveClientMigrationFiles(clientMigrationFileList);

		Mockito.verify(clientMigrationFileService1, Mockito.times(8)).addOrUpdateClientMigrationFile(
				clientMigrationFileArgumentCaptor.capture());

		Assert.assertEquals(0, errorSet.size());
		Assert.assertEquals(8, clientMigrationFileArgumentCaptor.getAllValues().size());
	}

	@Test
	public void saveClientMigrationFilesShouldReturnSaveErrors() {
		ClientMigrationFileService clientMigrationFileService1 = Mockito.spy(clientMigrationFileService);

		List<ClientMigrationFile> clientMigrationFileList = new ArrayList<>();

		for (int i = 0; i < 8; i++) {
			clientMigrationFileList.add(new ClientMigrationFile());
		}

		ArgumentCaptor<ClientMigrationFile> clientMigrationFileArgumentCaptor = ArgumentCaptor.forClass(ClientMigrationFile.class);

		// Call the method under test
		ArrayList<String> errorSet = clientMigrationFileService1.saveClientMigrationFiles(clientMigrationFileList);

		Mockito.verify(clientMigrationFileService1, Mockito.times(8)).addOrUpdateClientMigrationFile(
				clientMigrationFileArgumentCaptor.capture());

		Assert.assertEquals(8, errorSet.size());
		Assert.assertEquals(8, clientMigrationFileArgumentCaptor.getAllValues().size());
		Assert.assertEquals("unknown file!!", errorSet.get(0));
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
	public void getClientMigrationFileByFilenameShouldCallRepositoryMethod() {
		String filename = "89.up.sql";
		clientMigrationFileService.getClientMigrationFileByFilename(filename);

		Mockito.verify(clientMigrationFileRepository).getClientMigrationFileByFilename(filename);
	}

	@Test
	public void getClientMigrationFileByFilenameShouldReturnNullWhenFilenamebIsBlank() {
		Assert.assertNull(clientMigrationFileService.getClientMigrationFileByFilename(null));
	}

	@Test
	public void getClientMigrationFileByVersion() {
		int version = 7;
		clientMigrationFileService.getClientMigrationFileByVersion(version);

		Mockito.verify(clientMigrationFileRepository).getClientMigrationFileByVersion(version);
	}

	@Test
	public void getClientMigrationFileByManifestId() {
		int manifestId = 7;
		clientMigrationFileService.getClientMigrationFileByManifestId(manifestId);

		Mockito.verify(clientMigrationFileRepository).getClientMigrationFileByManifestId(manifestId);
	}

	@Test
	public void getClientMigrationFileById() {
		int id = 7;
		clientMigrationFileService.getClientMigrationFileById(id);

		Mockito.verify(clientMigrationFileRepository).getClientMigrationFileById(id);
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		scripts.add("client_form.sql");
		return scripts;
	}
}
