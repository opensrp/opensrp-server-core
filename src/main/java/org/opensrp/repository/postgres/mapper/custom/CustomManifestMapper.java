package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Manifest;
import org.opensrp.domain.postgres.ManifestExample;
import org.opensrp.repository.postgres.mapper.ManifestMapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public interface CustomManifestMapper extends ManifestMapper {

    Manifest selectByIdentifier(String identifier);

    int insertSelectiveAndSetId(Manifest manifest);

    List<Manifest> selectMany(@Param("example") ManifestExample manifestExample,
                              @Param("offset") int offset, @Param("limit") int limit);

    List<Manifest> selectByAppId(@Param("appId") String appId);

    @Nullable
    Manifest selectByAppIdAndAppVersion(@NonNull @Param("appId") String appId, @NonNull @Param("appVersion") String appVersion);
}
