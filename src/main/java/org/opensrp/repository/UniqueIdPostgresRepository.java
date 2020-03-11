package org.opensrp.repository;

import java.util.List;
import org.opensrp.domain.UniqueId;

public interface UniqueIdPostgresRepository extends BaseRepository<UniqueId> {

    List<UniqueId> getNotUsedIds(int limit);

    List<String> getNotUsedIdsAsString(int limit);

    Long totalUnUsedIds();

    int markAsUsed(final List<String> ids);

}
