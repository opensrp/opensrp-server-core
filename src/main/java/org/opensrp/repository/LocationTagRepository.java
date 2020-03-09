package org.opensrp.repository;

import org.opensrp.domain.LocationTag;

public interface LocationTagRepository extends BaseRepository<LocationTag> {
	
	LocationTag getLocationTagByPrimaryKey(Long id);
	
	void safeRemove(Long id);
	
	org.opensrp.domain.postgres.LocationTag getLocationTagByName(String username);
}
