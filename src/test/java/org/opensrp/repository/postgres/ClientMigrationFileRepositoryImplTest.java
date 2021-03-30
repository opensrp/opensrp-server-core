package org.opensrp.repository.postgres;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.domain.ClientMigrationFile;
import org.opensrp.repository.ClientMigrationFileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityExistsException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClientMigrationFileRepositoryImplTest extends BaseRepositoryTest {

	@Autowired
	private ClientMigrationFileRepository clientMigrationFileRepository;

	private Set<String> scripts = new HashSet<String>();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getShouldReturnMigrationFileWhenGivenAValidId() {
		ClientMigrationFile clientMigrationFile = clientMigrationFileRepository.get("1");

		Assert.assertEquals("cec563c2-d3e7-4b2a-866e-82d5902d44de", clientMigrationFile.getIdentifier());
		Assert.assertEquals("1.up.sql", clientMigrationFile.getFilename());
		Assert.assertEquals("CREATE TABLE vaccines(id INTEGER, vaccine_name VARCHAR);", clientMigrationFile.getFileContents());
		Assert.assertEquals(null, clientMigrationFile.getJurisdiction());
		Assert.assertEquals(Integer.valueOf(1), clientMigrationFile.getVersion());
		Assert.assertEquals(Integer.valueOf(1), clientMigrationFile.getManifestId());
		Assert.assertEquals(null, clientMigrationFile.getObjectStoragePath());
		Assert.assertEquals(false, clientMigrationFile.getOnObjectStorage());
	}

	@Test
	public void getShouldReturnNullWhenGivenBlankId() {
		Assert.assertNull(clientMigrationFileRepository.get(""));
	}

	@Test
	public void getShouldReturnNullWhenGivenNonIntegerId() {
		Assert.assertNull(clientMigrationFileRepository.get("98iu"));
	}

	@Test(expected = IllegalStateException.class)
	public void addShouldThrowExceptionWhenGiveNullClientMigrationFile() {
		clientMigrationFileRepository.add(null);
	}

	@Test(expected = IllegalStateException.class)
	public void addShouldThrowExceptionWhenGiveClientMigrationFileWithNullIdentifier() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
		clientMigrationFileRepository.add(clientMigrationFile);
	}

	@Test(expected = EntityExistsException.class)
	public void addShouldThrowExceptionWhenGiveClientMigrationFileWithId() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
		clientMigrationFile.setIdentifier("my-identifier");
		clientMigrationFile.setId(2398L);
		clientMigrationFileRepository.add(clientMigrationFile);
	}

	@Test
	public void addShouldCallInsertSelectiveOnMapper() {
		ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
		clientMigrationFile.setIdentifier("my-identifier");
		String randomFileName = "some-random-filename";
		clientMigrationFile.setFilename(randomFileName);
		clientMigrationFile.setVersion(89);
		clientMigrationFileRepository.add(clientMigrationFile);

		ClientMigrationFile actualRecord = clientMigrationFileRepository.getClientMigrationFileByIdentifier("my-identifier");
		Assert.assertEquals(randomFileName, actualRecord.getFilename());
	}

	@Test
	public void updateShouldUpdateClientMigrationFileDetails() {
		ClientMigrationFile clientMigrationFile = clientMigrationFileRepository.getClientMigrationFileByIdentifier("38bc7c3f-7439-4d62-bd9b-fa40867d0a44");
		clientMigrationFile.setFilename("a different file name");

		clientMigrationFileRepository.update(clientMigrationFile);

		ClientMigrationFile actualUpdatedRecord = clientMigrationFileRepository.getClientMigrationFileByIdentifier("38bc7c3f-7439-4d62-bd9b-fa40867d0a44");
		Assert.assertEquals("a different file name", actualUpdatedRecord.getFilename());
	}

	@Test
	public void updateShouldNotUpdateClientMigrationFileDetailsWhenIdentifierIsNull() {
		ClientMigrationFile clientMigrationFile = clientMigrationFileRepository.getClientMigrationFileByIdentifier("38bc7c3f-7439-4d62-bd9b-fa40867d0a44");
		clientMigrationFile.setFilename("a different file name");
		clientMigrationFile.setIdentifier(null);

		clientMigrationFileRepository.update(clientMigrationFile);

		// Assert that the file name is similar
		ClientMigrationFile actualUpdatedRecord = clientMigrationFileRepository.getClientMigrationFileByIdentifier("38bc7c3f-7439-4d62-bd9b-fa40867d0a44");
		Assert.assertEquals("3.up.sql", actualUpdatedRecord.getFilename());
	}

	@Test
	public void updateShouldNotUpdateClientMigrationFileDetailsWhenIdIsNull() {
		ClientMigrationFile clientMigrationFile = clientMigrationFileRepository.getClientMigrationFileByIdentifier("38bc7c3f-7439-4d62-bd9b-fa40867d0a44");
		clientMigrationFile.setFilename("a different file name");
		clientMigrationFile.setId(null);

		clientMigrationFileRepository.update(clientMigrationFile);

		// Assert that the file name is similar
		ClientMigrationFile actualUpdatedRecord = clientMigrationFileRepository.getClientMigrationFileByIdentifier("38bc7c3f-7439-4d62-bd9b-fa40867d0a44");
		Assert.assertEquals("3.up.sql", actualUpdatedRecord.getFilename());
	}

	@Test
	public void getAll() {
		List<ClientMigrationFile> migrationFiles = clientMigrationFileRepository.getAll();

		Assert.assertEquals(6, migrationFiles.size());
	}

	@Test
	public void safeRemove() {
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
	public void getClientMigrationFileById() {
	}

	@Test
	public void getClientMigrationFileByIdentifier() {
	}

	@Test
	public void testGetAll() {
	}

	@Test
	public void retrievePrimaryKey() {
	}

	@Test
	public void getUniqueField() {
	}

	@Override
	protected Set<String> getDatabaseScripts() {
		scripts.add("client_migration_file.sql");
		return scripts;
	}
}
