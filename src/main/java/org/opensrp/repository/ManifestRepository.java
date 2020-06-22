package org.opensrp.repository;

import org.opensrp.domain.Manifest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

public interface ManifestRepository extends BaseRepository<Manifest> {

    @Nullable
    Manifest getManifestByAppId(String appId);

    @Nullable
    List<Manifest> getManifestsByAppId(String appId);

    @Nullable
    Manifest getManifest(@NonNull String appId, @NonNull String appVersion);

    /**
     * Gets all the manifests
     *
     * @param limit - {@link Integer} -- Identifies the limit number of rows.
     * @return manifests - {@link List<Manifest>}
     */
    @Nullable
    List<Manifest> getAll(int limit);
}
