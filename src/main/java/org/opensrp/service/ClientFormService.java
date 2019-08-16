package org.opensrp.service;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.opensrp.domain.IdVersionTuple;
import org.opensrp.domain.postgres.ClientForm;
import org.opensrp.domain.postgres.ClientFormMetadata;
import org.opensrp.repository.postgres.mapper.ClientFormMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomClientFormMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientFormService {

    private final ClientFormMapper clientFormMapper;

    private final CustomClientFormMetadataMapper clientFormMetadataMapper;

    @Autowired
    public ClientFormService(ClientFormMapper clientFormMapper, CustomClientFormMetadataMapper clientFormMetadataMapper) {
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
}
