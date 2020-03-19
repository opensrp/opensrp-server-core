package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.LocationTag;
import org.opensrp.domain.postgres.LocationTagExample;

public interface LocationTagRepository extends BaseRepository<LocationTag> {
	
	LocationTag getLocationTagByPrimaryKey(Long id);
	
	void safeRemove(Long id);
	
	org.opensrp.domain.postgres.LocationTag getLocationTagByName(String name);
	
	org.opensrp.domain.postgres.LocationTag getLocationTagByNameAndNotEqualId(String name, Long id);
	
	List<LocationTag> findByLocationTagExample(LocationTagExample locationTagExample, int offset, int limit);
}
