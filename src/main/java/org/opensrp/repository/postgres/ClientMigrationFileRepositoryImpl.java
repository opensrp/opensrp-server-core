package org.opensrp.repository.postgres;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.ClientMigrationFile;
import org.opensrp.repository.ClientMigrationFileRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomClientMigrationFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClientMigrationFileRepositoryImpl extends BaseRepositoryImpl<ClientMigrationFile> implements ClientMigrationFileRepository {

    @Autowired
    private CustomClientMigrationFileMapper clientMigrationFileMapper;

    @Override
    public ClientMigrationFile get(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }

        int idInt = 0 ;

        try {
            idInt = Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            logger.error(ex.getLocalizedMessage(), ex);
            return null;
        }

        return getClientMigrationFileById(idInt);
    }

    @Override
    public void add(ClientMigrationFile entity) {
        if (getUniqueField(entity) == null) {
            throw new IllegalStateException("Missing identifier property or NULL");
        }

        if (retrievePrimaryKey(entity) != null) {
            throw new EntityExistsException();
        }

        org.opensrp.domain.postgres.ClientMigrationFile clientMigrationFile = convert(entity, null);
        if (clientMigrationFile == null) {
            return;
        }

        clientMigrationFileMapper.insertSelective(clientMigrationFile);
    }

    @Transactional
    @Override
    public void update(ClientMigrationFile entity) {
        if (getUniqueField(entity) == null) {
            return;
        }

        Long id = retrievePrimaryKey(entity);
        if (id == null) { // Manifest does not exist
            return;
        }

        org.opensrp.domain.postgres.ClientMigrationFile psqlClientMigrationFile = convert(entity, id);
        if (psqlClientMigrationFile == null) {
            return;
        }

        clientMigrationFileMapper.updateByPrimaryKey(psqlClientMigrationFile);
    }

    @Override
    public List<ClientMigrationFile> getAll() {
        return null;
    }

    @Override
    public void safeRemove(ClientMigrationFile entity) {
        if (entity == null) {
            return;
        }

        Long id = retrievePrimaryKey(entity);
        if (id == null) {
            return;
        }

        clientMigrationFileMapper.deleteByPrimaryKey(id);
    }

    @Nullable
    @Override
    public ClientMigrationFile getClientMigrationFileByFilename(String filename) {
        if (StringUtils.isBlank(filename)) {
            return null;
        }

        return convert(clientMigrationFileMapper.selectByFilename(filename));
    }

    @Override
    public List<ClientMigrationFile> getClientMigrationFileByVersion(int version) {
        if (version == 0) {
            return null;
        }

        ArrayList<ClientMigrationFile> convertedMigrationFiles = new ArrayList<>();
        List<org.opensrp.domain.postgres.ClientMigrationFile> psqlMigrationFiles = clientMigrationFileMapper.selectByVersion(version);

        for (org.opensrp.domain.postgres.ClientMigrationFile psqlMigrationFile: psqlMigrationFiles) {
            ClientMigrationFile migrationFile = convert(psqlMigrationFile);

            if (migrationFile != null) {
                convertedMigrationFiles.add(migrationFile);
            }
        }

        return convertedMigrationFiles;
    }

    @Nullable
    @Override
    public List<ClientMigrationFile> getClientMigrationFileByManifestId(int manifestId) {
        if (manifestId == 0) {
            return null;
        }

        ArrayList<ClientMigrationFile> convertedMigrationFiles = new ArrayList<>();
        List<org.opensrp.domain.postgres.ClientMigrationFile> psqlMigrationFiles = clientMigrationFileMapper.selectByManifestId(manifestId);

        for (org.opensrp.domain.postgres.ClientMigrationFile psqlMigrationFile: psqlMigrationFiles) {
            ClientMigrationFile migrationFile = convert(psqlMigrationFile);

            if (migrationFile != null) {
                convertedMigrationFiles.add(migrationFile);
            }
        }

        return convertedMigrationFiles;
    }

    @Nullable
    @Override
    public ClientMigrationFile getClientMigrationFileById(long id) {
        if (id == 0) {
            return null;
        }

        return convert(clientMigrationFileMapper.selectByPrimaryKey(id));
    }

    @Override
    public ClientMigrationFile getClientMigrationFileByIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }

        return convert(clientMigrationFileMapper.selectByIdentifier(identifier));
    }

    @Override
    public List<ClientMigrationFile> getAll(int limit) {
        if (limit == 0) {
            return new ArrayList<>();
        }

        ArrayList<ClientMigrationFile> convertedMigrationFiles = new ArrayList<>();
        List<org.opensrp.domain.postgres.ClientMigrationFile> psqlMigrationFiles = clientMigrationFileMapper.getAll(0, limit);

        for (org.opensrp.domain.postgres.ClientMigrationFile psqlMigrationFile: psqlMigrationFiles) {
            ClientMigrationFile migrationFile = convert(psqlMigrationFile);

            if (migrationFile != null) {
                convertedMigrationFiles.add(migrationFile);
            }
        }

        return convertedMigrationFiles;
    }

    private org.opensrp.domain.postgres.ClientMigrationFile convert(@Nullable ClientMigrationFile clientMigrationFile, Long primaryKey) {
        if (clientMigrationFile == null) {
            return null;
        }

        org.opensrp.domain.postgres.ClientMigrationFile psqlClientMigrationFile = new org.opensrp.domain.postgres.ClientMigrationFile();

        if (primaryKey != null) {
            psqlClientMigrationFile.setId(primaryKey);
        } else {
            psqlClientMigrationFile.setId(clientMigrationFile.getId());
        }

        psqlClientMigrationFile.setIdentifier(clientMigrationFile.getIdentifier());
        psqlClientMigrationFile.setFilename(clientMigrationFile.getFilename());
        psqlClientMigrationFile.setOnObjectStorage(clientMigrationFile.getOnObjectStorage());
        psqlClientMigrationFile.setObjectStoragePath(clientMigrationFile.getObjectStoragePath());
        psqlClientMigrationFile.setJurisdiction(clientMigrationFile.getJurisdiction());
        psqlClientMigrationFile.setVersion(clientMigrationFile.getVersion());
        psqlClientMigrationFile.setManifestId(clientMigrationFile.getManifestId());
        psqlClientMigrationFile.setFileContents(clientMigrationFile.getFileContents());

        if (clientMigrationFile.getCreatedAt() != null) {
            psqlClientMigrationFile.setCreatedAt(clientMigrationFile.getCreatedAt());
        }

        return psqlClientMigrationFile;
    }


    private ClientMigrationFile convert(@Nullable org.opensrp.domain.postgres.ClientMigrationFile clientMigrationFile) {
        if (clientMigrationFile == null) {
            return null;
        }

        ClientMigrationFile psqlClientMigrationFile = new ClientMigrationFile();

        psqlClientMigrationFile.setId(clientMigrationFile.getId());

        psqlClientMigrationFile.setIdentifier(clientMigrationFile.getIdentifier());
        psqlClientMigrationFile.setFilename(clientMigrationFile.getFilename());
        psqlClientMigrationFile.setOnObjectStorage(clientMigrationFile.getOnObjectStorage());
        psqlClientMigrationFile.setObjectStoragePath(clientMigrationFile.getObjectStoragePath());
        psqlClientMigrationFile.setJurisdiction(clientMigrationFile.getJurisdiction());
        psqlClientMigrationFile.setVersion(clientMigrationFile.getVersion());
        psqlClientMigrationFile.setManifestId(clientMigrationFile.getManifestId());
        psqlClientMigrationFile.setFileContents(clientMigrationFile.getFileContents());

        if (clientMigrationFile.getCreatedAt() != null) {
            psqlClientMigrationFile.setCreatedAt(clientMigrationFile.getCreatedAt());
        }

        return psqlClientMigrationFile;
    }

    @Override
    protected Long retrievePrimaryKey(ClientMigrationFile clientMigrationFile) {
        return getUniqueField(clientMigrationFile);
    }

    @Override
    protected Long getUniqueField(ClientMigrationFile clientMigrationFile) {
        if (clientMigrationFile == null) {
            return null;
        }

        return clientMigrationFile.getId();
    }
}

