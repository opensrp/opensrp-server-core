package org.opensrp.repository;

import java.util.List;

import org.smartregister.domain.LocationTag;
import org.opensrp.domain.postgres.LocationTagExample;

public interface LocationTagRepository extends BaseRepository<LocationTag> {
	
	LocationTag getLocationTagByPrimaryKey(Long id);
	
	void safeRemove(Long id);
	
	org.opensrp.domain.postgres.LocationTag getLocationTagByName(String name);
	
	org.opensrp.domain.postgres.LocationTag getLocationTagByNameAndNotEqualId(String name, Long id);
	
	List<LocationTag> findByLocationTagExample(LocationTagExample locationTagExample, int offset, int limit);
	
	int addLocationTagMap(org.opensrp.domain.LocationTagMap locationTagMap);
	
	void deleteLocationTagMapByLocationIdAndLocationTagId(Long locationId, Long locationTagId);
	
	void deleteLocationTagMapByLocationId(Long locationId);
	
	List<org.opensrp.domain.LocationTagMap> getLocationTagMapByExample(Long locationid, Long locationtagId);
	
}
