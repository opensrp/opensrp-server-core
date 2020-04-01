package org.opensrp.repository;

import org.opensrp.domain.Manifest;
import org.springframework.lang.Nullable;

public interface ManifestRepository extends BaseRepository<Manifest> {

    @Nullable
	org.opensrp.domain.postgres.Manifest getManifest(String id);

	@Nullable
	Manifest getManifestByAppId(String appId);
}
