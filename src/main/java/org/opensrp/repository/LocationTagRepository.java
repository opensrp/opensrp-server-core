package org.opensrp.repository;

import org.opensrp.domain.LocationTag;

public interface LocationTagRepository extends BaseRepository<LocationTag> {
	
	LocationTag getLocationTagByPrimaryKey(Long id);
	
	void safeRemove(Long id);
	
	LocationTag getLocationTagByName(String username);
}
