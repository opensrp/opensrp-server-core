package org.opensrp.repository;

import org.opensrp.domain.Manifest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public interface ManifestRepository extends BaseRepository<Manifest> {

	@Nullable
	Manifest getManifestByAppId(String appId);

	@Nullable
	Manifest getManifest(@NonNull String appId, @NonNull String appVersion);
}
