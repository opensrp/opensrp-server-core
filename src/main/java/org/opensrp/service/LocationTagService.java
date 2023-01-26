package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.domain.LocationTag;
import org.opensrp.domain.LocationTagMap;
import org.opensrp.domain.postgres.LocationTagExample;
import org.opensrp.repository.LocationTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class LocationTagService {
	
	private LocationTagRepository locationTagRepository;
	
	@Autowired
	public void setLocationTagRepository(LocationTagRepository locationTagRepository) {
		this.locationTagRepository = locationTagRepository;
	}
	
	public LocationTagRepository getLocationTagRepository() {
		return locationTagRepository;
	}

	@PreAuthorize("hasRole('LOCATIONTAG_VIEW')")
	public List<LocationTag> getAllLocationTags() {
		return getLocationTagRepository().getAll();
	}

	@PreAuthorize("hasRole('LOCATIONTAG_VIEW')")
	public LocationTag getLocationTagById(String id) {
		return getLocationTagRepository().getLocationTagByPrimaryKey(Long.valueOf(id));
	}
	
	@PreAuthorize("hasRole('LOCATIONTAG_CREATE') or hasRole('LOCATIONTAG_UPDATE')")
	public LocationTag addOrUpdateLocationTag(LocationTag locationTag) {
		if (StringUtils.isBlank(locationTag.getName())) {
			throw new IllegalArgumentException("Location tag name not specified");
		}
		
		if (locationTag.getId() != 0) {
			getLocationTagRepository().update(locationTag);
		} else {
			getLocationTagRepository().add(locationTag);
		}
		return locationTag;
	}

	@PreAuthorize("hasRole('LOCATIONTAG_DELETE')")
	public void deleteLocationTag(LocationTag locationTag) {
		if (StringUtils.isBlank(locationTag.getName())) {
			throw new IllegalArgumentException("Location tag name not specified");
		}
		
		getLocationTagRepository().safeRemove(locationTag);
		
	}

	@PreAuthorize("hasRole('LOCATIONTAG_DELETE')")
	public void deleteLocationTag(Long id) {
		if (id == 0) {
			throw new IllegalArgumentException("Id not specified");
		}
		
		getLocationTagRepository().safeRemove(id);
		
	}
	
	public List<LocationTag> findByLocationTagExample(LocationTagExample locationTagExample, int offset, int limit) {
		return getLocationTagRepository().findByLocationTagExample(locationTagExample, offset, limit);
	}
	
	public int addLocationTagMap(LocationTagMap locationTagMap) {
		return getLocationTagRepository().addLocationTagMap(locationTagMap);
		
	}
	
	public List<LocationTagMap> findLocationTagMapByCriteria(Long locationId, Long locationTagId) {
		
		return getLocationTagRepository().getLocationTagMapByExample(locationId, locationTagId);
	}
	
	public void deleteLocationTagMapByLocationIdAndLocationTagId(Long locationId, Long locationTagId) {
		getLocationTagRepository().deleteLocationTagMapByLocationIdAndLocationTagId(locationId, locationTagId);
	}
	
	public void deleteLocationTagMapByLocationId(Long locationId) {
		getLocationTagRepository().deleteLocationTagMapByLocationId(locationId);
	}
	
}
