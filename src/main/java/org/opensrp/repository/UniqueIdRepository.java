package org.opensrp.repository;

import org.opensrp.domain.UniqueId;

import java.util.List;
import java.util.Set;

public interface UniqueIdRepository extends BaseRepository<UniqueId> {

    List<UniqueId> getNotUsedIds(int limit);

    List<String> getNotUsedIdsAsString(int limit);

    Long totalUnUsedIds();

    Long[] markAsUsed(final List<String> ids);

    void clearTable();

    boolean checkIfClientExists(String usedBy, String location);

    UniqueId findByIdentifierSourceOrderByIdDesc(Long identifierSource);

    Set<String> findReservedIdentifiers();


}
