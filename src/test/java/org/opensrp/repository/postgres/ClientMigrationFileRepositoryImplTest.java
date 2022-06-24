package org.opensrp.repository.postgres;

import org.junit.Assert;
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
        ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
        clientMigrationFile.setId(5L);

        clientMigrationFileRepository.safeRemove(clientMigrationFile);

        Assert.assertEquals(5, clientMigrationFileRepository.getAll().size());
        Assert.assertNull(clientMigrationFileRepository.get("5"));
    }

    @Test
    public void safeRemoveShouldFailWhenEntityPrimaryKeyIsNull() {
        ClientMigrationFile clientMigrationFile = new ClientMigrationFile();
        clientMigrationFile.setId(null);

        clientMigrationFileRepository.safeRemove(clientMigrationFile);

        Assert.assertEquals(6, clientMigrationFileRepository.getAll().size());
    }

    @Test
    public void safeRemoveShouldFailWhenEntityIsNull() {
        clientMigrationFileRepository.safeRemove(null);

        Assert.assertEquals(6, clientMigrationFileRepository.getAll().size());
    }

    @Test
    public void getClientMigrationFileByFilename() {
        ClientMigrationFile clientMigrationFile = clientMigrationFileRepository.getClientMigrationFileByFilename("3.up.sql");

        Assert.assertEquals(Integer.valueOf(3), clientMigrationFile.getVersion());
        Assert.assertEquals(Integer.valueOf(3), clientMigrationFile.getManifestId());
        Assert.assertEquals("CREATE TABLE alerts(id INTEGER, alert_name VARCHAR, is_offline BOOLEAN);", clientMigrationFile.getFileContents());
        Assert.assertEquals("38bc7c3f-7439-4d62-bd9b-fa40867d0a44", clientMigrationFile.getIdentifier());
    }

    @Test
    public void getClientMigrationFileByFilenameShouldReturnNullWhenFileNameisEmpty() {
        Assert.assertNull(clientMigrationFileRepository.getClientMigrationFileByFilename(""));
    }

    @Test
    public void getClientMigrationFileByVersion() {
        List<ClientMigrationFile> migrationFiles = clientMigrationFileRepository.getClientMigrationFileByVersion(3);

        Assert.assertEquals(2, migrationFiles.size());
        Assert.assertEquals(Integer.valueOf(3), migrationFiles.get(0).getVersion());

        Assert.assertEquals("3.up.sql", migrationFiles.get(0).getFilename());
        Assert.assertEquals("3.down.sql", migrationFiles.get(1).getFilename());
    }

    @Test
    public void getClientMigrationFileByManifestId() {
        List<ClientMigrationFile> migrationFiles = clientMigrationFileRepository.getClientMigrationFileByManifestId(3);

        Assert.assertEquals(2, migrationFiles.size());
        Assert.assertEquals(Integer.valueOf(3), migrationFiles.get(0).getVersion());

        Assert.assertEquals(Integer.valueOf(3), migrationFiles.get(0).getManifestId());
        Assert.assertEquals("3.up.sql", migrationFiles.get(0).getFilename());
        Assert.assertEquals("3.down.sql", migrationFiles.get(1).getFilename());
    }

    @Test
    public void getClientMigrationFileByManifestIdShouldReturnNull() {
        Assert.assertNull(clientMigrationFileRepository.getClientMigrationFileByManifestId(0));
    }

    @Test
    public void getClientMigrationFileByIdShouldReturnNull() {
        Assert.assertNull(clientMigrationFileRepository.getClientMigrationFileById(0));
    }

    @Test
    public void getClientMigrationFileByIdShouldReturnClientMigrationFile() {
        ClientMigrationFile clientMigrationFile = clientMigrationFileRepository.getClientMigrationFileById(1);
        Assert.assertEquals("cec563c2-d3e7-4b2a-866e-82d5902d44de", clientMigrationFile.getIdentifier());
        Assert.assertEquals("1.up.sql", clientMigrationFile.getFilename());
    }

    @Test
    public void getClientMigrationFileByIdentifierShouldReturnNull() {
        Assert.assertNull(clientMigrationFileRepository.getClientMigrationFileByIdentifier("an-identifier"));
    }

    @Test
    public void getClientMigrationFileByIdentifierShouldReturnClientMigrationFile() {
        ClientMigrationFile clientMigrationFile = clientMigrationFileRepository.getClientMigrationFileByIdentifier("70cbd363-5dd7-4e92-8364-bcc093cb4962");
        Assert.assertEquals("2.down.sql", clientMigrationFile.getFilename());
        Assert.assertEquals(Integer.valueOf(2), clientMigrationFile.getVersion());
        Assert.assertEquals("ALTER TABLE vaccines RENAME TO vaccines_old;\\nCREATE TABLE vaccines(id INTEGER, vaccine_name VARCHAR);\\nINSERT INTO vaccines(id, vaccine_name) SELECT id, vaccine_name FROM vaccines_old;\\nDROP TABLE vaccines_old", clientMigrationFile.getFileContents());
    }

    @Test
    public void testGetAllWhenGivenLimit() {
        List<ClientMigrationFile> migrationFiles = clientMigrationFileRepository.getAll(3);

        Assert.assertEquals(3, migrationFiles.size());
        Assert.assertEquals("1.up.sql", migrationFiles.get(0).getFilename());
        Assert.assertEquals("1.down.sql", migrationFiles.get(1).getFilename());
        Assert.assertEquals("2.up.sql", migrationFiles.get(2).getFilename());
    }

    @Override
    protected Set<String> getDatabaseScripts() {
        scripts.add("client_migration_file.sql");
        return scripts;
    }
}
