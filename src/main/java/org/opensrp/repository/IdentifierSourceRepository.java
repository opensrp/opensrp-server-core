package org.opensrp.repository;

import org.opensrp.domain.IdentifierSource;


public interface IdentifierSourceRepository extends BaseRepository<IdentifierSource> {

    IdentifierSource findByIdentifier(String identifier);

    void update(IdentifierSource identifierSource);

    void updateIdSourceWithSequenceValue(IdentifierSource identifierSource, Long sequenceValue);

}
