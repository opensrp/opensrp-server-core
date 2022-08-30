package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.Location;
import org.opensrp.repository.postgres.mapper.LocationMapper;

public interface CustomLocationMapper extends LocationMapper {

    int insertSelectiveAndSetId(Location location);

    Long selectServerVersionByPrimaryKey(Long id);

    int updateByPrimaryKeyAndGenerateServerVersion(Location location);

}
