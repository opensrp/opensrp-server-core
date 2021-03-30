package org.opensrp.repository.postgres;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensrp.domain.ClientMigrationFile;
import org.opensrp.repository.ClientMigrationFileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityExistsException;
import java.util.HashSet;
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
	public void update() {
	}

	@Test
	public void getAll() {
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
