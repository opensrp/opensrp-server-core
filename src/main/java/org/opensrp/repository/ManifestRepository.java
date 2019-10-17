package org.opensrp.repository;


import org.opensrp.domain.Manifest;

public interface ManifestRepository extends BaseRepository<Manifest>{
	
   org.opensrp.domain.postgres.Manifest getManifest(String id);

    Manifest getManifestByAppId(String appId);
}
