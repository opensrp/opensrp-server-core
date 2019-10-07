package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Manifest;
import org.opensrp.domain.postgres.ManifestExample;
import org.opensrp.repository.postgres.mapper.ManifestMapper;

import java.util.List;

public interface CustomManifestMapper extends ManifestMapper {

    Manifest selectByIdentifier(Long identifier);

    int insertSelectiveAndSetId(Manifest task);

    List<Manifest> selectMany(@Param("example") ManifestExample manifestExample,
                              @Param("offset") int offset, @Param("limit") int limit);
}
