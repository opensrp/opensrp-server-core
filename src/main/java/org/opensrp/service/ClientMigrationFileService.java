package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.ClientMigrationFile;
import org.opensrp.repository.ClientMigrationFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ClientMigrationFileService {

    private static Logger logger = LoggerFactory.getLogger(ClientMigrationFileService.class.toString());

    private ClientMigrationFileRepository clientMigrationFileRepository;

    @Autowired
    public void setClientMigrationFileRepository(ClientMigrationFileRepository clientMigrationFileRepository) {
        this.clientMigrationFileRepository = clientMigrationFileRepository;
    }

    public ClientMigrationFileRepository getClientMigrationFileRepository() {
        return clientMigrationFileRepository;
    }

    public List<ClientMigrationFile> getAllClientMigrationFiles() {
        return clientMigrationFileRepository.getAll();
    }

    public List<ClientMigrationFile> getAllClientMigrationFiles(int limit) { return clientMigrationFileRepository.getAll(limit);}

    public void addOrUpdateClientMigrationFile(ClientMigrationFile clientMigrationFile) {
        if (StringUtils.isBlank(clientMigrationFile.getIdentifier()))
            throw new IllegalArgumentException("identifier not specified");

        clientMigrationFile.setCreatedAt(new Date());
        if (clientMigrationFileRepository.get(clientMigrationFile.getIdentifier()) != null) {
            clientMigrationFileRepository.update(clientMigrationFile);
        } else {
            clientMigrationFile.setCreatedAt(new Date());
            clientMigrationFileRepository.add(clientMigrationFile);
        }
    }

    public ClientMigrationFile addClientMigrationFile(ClientMigrationFile clientMigrationFile) {
        if (StringUtils.isBlank(clientMigrationFile.getIdentifier()))
            throw new IllegalArgumentException("identifier not specified");

        clientMigrationFile.setCreatedAt(new Date());
        clientMigrationFileRepository.add(clientMigrationFile);
        return clientMigrationFile;
        
    }

    public ClientMigrationFile updateClientMigrationFile(ClientMigrationFile clientMigrationFile) {
        if (StringUtils.isBlank(clientMigrationFile.getIdentifier()))
            throw new IllegalArgumentException("identifier not specified");

        clientMigrationFileRepository.update(clientMigrationFile);
        return clientMigrationFile;
    }

    public ClientMigrationFile getClientMigrationFile(String identifier) {
        if (StringUtils.isBlank(identifier))
            return null;

        return getClientMigrationFileRepository().getClientMigrationFileByIdentifier(identifier);
    }

    public ArrayList<String> saveClientMigrationFiles(List<ClientMigrationFile> clientMigrationFiles) {
        ArrayList<String> clientMigrationFilesWithErrors = new ArrayList<>();

        for (ClientMigrationFile clientMigrationFile : clientMigrationFiles) {
            try {
                addOrUpdateClientMigrationFile(clientMigrationFile);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                clientMigrationFilesWithErrors.add(clientMigrationFile.getIdentifier() != null ? clientMigrationFile.getIdentifier() : "unknown file!!");
            }
        }

        return clientMigrationFilesWithErrors;
    }

    public void deleteClientMigrationFile(ClientMigrationFile clientMigrationFile) {
        if (StringUtils.isBlank(clientMigrationFile.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        clientMigrationFileRepository.safeRemove(clientMigrationFile);
    }

    public ClientMigrationFile getClientMigrationFileByFilename(String filename) {
        if (StringUtils.isBlank(filename))
            return null;

        return clientMigrationFileRepository.getClientMigrationFileByFilename(filename);
    }

    public List<ClientMigrationFile> getClientMigrationFileByVersion(int version) {
        return clientMigrationFileRepository.getClientMigrationFileByVersion(version);
    }

    public List<ClientMigrationFile> getClientMigrationFileByManifestId(int manifestId) {
        return clientMigrationFileRepository.getClientMigrationFileByManifestId(manifestId);
    }

    public ClientMigrationFile getClientMigrationFileById(int id) {
        return clientMigrationFileRepository.getClientMigrationFileById(id);
    }
}

