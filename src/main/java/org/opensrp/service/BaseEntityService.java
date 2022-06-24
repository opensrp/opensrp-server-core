package org.opensrp.service;

import org.opensrp.repository.ClientsRepository;
import org.smartregister.domain.BaseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseEntityService {

    private final ClientsRepository allBaseEntities;

    @Autowired
    public BaseEntityService(ClientsRepository allBaseEntities) {
        this.allBaseEntities = allBaseEntities;
    }

    public List<? extends BaseEntity> getAllBaseEntities() {
        return allBaseEntities.findAllClients();
    }

    public BaseEntity findByBaseEntityId(String baseEntityId) {
        return allBaseEntities.findByBaseEntityId(baseEntityId);
    }

    public List<? extends BaseEntity> findByIdentifier(String identifier) {
        return allBaseEntities.findAllByIdentifier(identifier);
    }

    public List<? extends BaseEntity> findByIdentifier(String identifierType, String identifier) {
        return allBaseEntities.findAllByIdentifier(identifierType, identifier);
    }
}
