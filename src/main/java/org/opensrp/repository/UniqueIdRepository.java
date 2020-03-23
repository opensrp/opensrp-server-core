package org.opensrp.repository;

import java.util.List;
import org.opensrp.domain.UniqueId;

public interface UniqueIdRepository extends BaseRepository<UniqueId> {

    List<UniqueId> getNotUsedIds(int limit);

    List<String> getNotUsedIdsAsString(int limit);

    Long totalUnUsedIds();

    Long[] markAsUsed(final List<String> ids);

    void clearTable();

    boolean checkIfClientExists(String usedBy, String location);

}
