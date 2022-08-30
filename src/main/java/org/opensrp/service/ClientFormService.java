package org.opensrp.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.opensrp.domain.IdVersionTuple;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.ClientFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientFormService {

    private static Logger logger = LogManager.getLogger(ClientFormService.class.toString());
    private ClientFormRepository clientFormRepository;

    @Autowired
    public void setClientFormRepository(ClientFormRepository clientFormRepository) {
        this.clientFormRepository = clientFormRepository;
    }

    public boolean isClientFormExists(String formIdentifier) {
        return isClientFormExists(formIdentifier, false);
    }

    public boolean isClientFormExists(String formIdentifier, boolean isJsonValidator) {
        return clientFormRepository.countClientFormByFormIdentifier(formIdentifier, isJsonValidator) > 0;
    }

    @Nullable
    public ClientFormMetadata getClientFormMetadataByIdentifierAndVersion(String formIdentifier, String formVersion) {
        return getClientFormMetadataByIdentifierAndVersion(formIdentifier, formVersion, false);
    }

    @Nullable
    public ClientFormMetadata getClientFormMetadataByIdentifierAndVersion(String formIdentifier, String formVersion, boolean isJsonValidator) {
        return clientFormRepository.getClientFormMetadata(formVersion, formIdentifier, isJsonValidator);
    }

    public ClientForm getClientFormById(Long id) {
        return clientFormRepository.get(id);
    }

    public List<IdVersionTuple> getAvailableClientFormMetadataVersionByIdentifier(String formIdentifier) {
        return getAvailableClientFormMetadataVersionByIdentifier(formIdentifier, false);
    }

    public List<IdVersionTuple> getAvailableClientFormMetadataVersionByIdentifier(String formIdentifier, boolean isJsonValidator) {
        return clientFormRepository.getAvailableClientFormVersions(formIdentifier, isJsonValidator);
    }

    public ClientFormMetadata getClientFormMetadataById(long formId) {
        return clientFormRepository.getFormMetadata(formId);
    }

    @Nullable
    public CompleteClientForm addClientForm(@NonNull ClientForm clientForm, @NonNull ClientFormMetadata clientFormMetadata) {
        // Check if the same client form with that version & identifier exists
        ClientFormMetadata clientFormMetadataResult = clientFormRepository
                .getClientFormMetadata(clientFormMetadata.getVersion(), clientFormMetadata.getIdentifier(), clientFormMetadata.getIsJsonValidator() == null ? false : clientFormMetadata.getIsJsonValidator());
        if (clientFormMetadataResult != null) {
            logger.error("ClientFormMetadata with version " + clientFormMetadata.getVersion() + " and Identifier "
                    + clientFormMetadata.getIdentifier() + " Already exists", new Exception());
            return null;
        }

        return clientFormRepository.create(clientForm, clientFormMetadata);
    }

    @NonNull
    public List<ClientFormMetadata> getDraftsClientFormMetadata(boolean isDraft) {
        List<ClientFormMetadata> clientFormMetadataList = clientFormRepository.getAllDraftsClientFormMetadata(isDraft);
        return clientFormMetadataList == null ? new ArrayList<>() : clientFormMetadataList;
    }

    @NonNull
    public List<ClientFormMetadata> getJsonWidgetValidatorClientFormMetadata(boolean isJsonValidator) {
        List<ClientFormMetadata> clientFormMetadataList = clientFormRepository.getAllJsonWidgetValidatorClientFormMetadata(isJsonValidator);
        return clientFormMetadataList == null ? new ArrayList<>() : clientFormMetadataList;
    }

    @NonNull
    public List<ClientFormMetadata> getAllClientFormMetadata() {
        List<ClientFormMetadata> clientFormMetadataList = clientFormRepository.getAllClientFormMetadata();
        return clientFormMetadataList == null ? new ArrayList<>() : clientFormMetadataList;
    }

    @Nullable
    public ClientForm getMostRecentFormValidator(@NonNull String formIdentifier) {
        return clientFormRepository.getMostRecentFormValidator(formIdentifier);
    }

    public void updateClientFormMetadataIsDraftValueByVersion(boolean isDraft, @NonNull String formVersion) {
        clientFormRepository.updateClientMetadataIsDraftByVersion(isDraft, formVersion);
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
