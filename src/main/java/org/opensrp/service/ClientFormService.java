package org.opensrp.service;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.opensrp.domain.IdVersionTuple;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.postgres.mapper.custom.CustomClientFormMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomClientFormMetadataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientFormService {

    private final CustomClientFormMapper clientFormMapper;
    private final CustomClientFormMetadataMapper clientFormMetadataMapper;

    private static Logger logger = LoggerFactory.getLogger(ClientFormService.class.toString());


    @Autowired
    public ClientFormService(CustomClientFormMapper clientFormMapper, CustomClientFormMetadataMapper clientFormMetadataMapper) {
        this.clientFormMapper = clientFormMapper;
        this.clientFormMetadataMapper = clientFormMetadataMapper;
    }

    public boolean isClientFormExists(String formIdentifier) {
        return clientFormMetadataMapper.countClientFormMetadataByFormIdentifier(formIdentifier) > 0;
    }

    @Nullable
    public ClientFormMetadata getClientFormMetatdataByIdentifierAndVersion(String formIdentifier, String formVersion) {
        return clientFormMetadataMapper.selectClientFormMetadataByFormVersionAndIdentifier(formVersion, formIdentifier);
    }

    public ClientForm getClientFormById(Integer id) {
        return clientFormMapper.selectByPrimaryKey(id);
    }

    public List<IdVersionTuple> getAvailableClientFormMetadataVersionByIdentifier(String formIdentifier) {
        return clientFormMetadataMapper.getAvailableClientFormVersions(formIdentifier);
    }

    public ClientFormMetadata getClientFormMetadataById(int formId) {
        return clientFormMetadataMapper.selectByPrimaryKey(formId);
    }

    @Nullable
    public CompleteClientForm addClientForm(@NonNull ClientForm clientForm, @NonNull ClientFormMetadata clientFormMetadata) {
        Integer clientFormId = clientForm.getId();
        Integer clientFormMetadataId = clientFormMetadata.getId();
        if ((clientFormId != null && clientFormId != 0) || (clientFormMetadataId != null && clientFormMetadataId != 0)) {
            logger.error("ClientForm & ClientFormMetadata Id must be NULL or 0", new Exception());
            return null;
        }

        // Check if the same client form with that version & identifier exists
        ClientFormMetadata clientFormMetadataResult = clientFormMetadataMapper.selectClientFormMetadataByFormVersionAndIdentifier(clientFormMetadata.getVersion(), clientFormMetadata.getIdentifier());
        if (clientFormMetadataResult != null) {
            logger.error("ClientFormMetadata with version " + clientFormMetadata.getVersion() + " and Identifier "
                    + clientFormMetadata.getIdentifier() + " Already exists", new Exception());
            return null;
        }

        int rowsAffected = clientFormMapper.insertClientForm(clientForm);
        if (rowsAffected > 0) {
            logger.info("Generated id for Client form is " + clientForm.getId());

            clientFormMetadata.setId(clientForm.getId());
            int resultClientFormMetadataId = clientFormMetadataMapper.insertClientFormMetadata(clientFormMetadata);
            logger.info("Generated id for Client Form Metadata is " + resultClientFormMetadataId);

            return new CompleteClientForm(clientForm, clientFormMetadata);
        }

        return null;
    }

    public static class CompleteClientForm {

        public ClientForm clientForm;
        public ClientFormMetadata clientFormMetadata;

        public CompleteClientForm(@NonNull ClientForm clientForm, @NonNull ClientFormMetadata clientFormMetadata) {
            this.clientForm = clientForm;
            this.clientFormMetadata = clientFormMetadata;
        }
    }



}
