package org.opensrp.repository.postgres;

import static org.opensrp.util.Utils.isEmptyList;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.LocationTag;
import org.opensrp.domain.LocationTagMap;
import org.opensrp.domain.postgres.LocationTagExample;
import org.opensrp.domain.postgres.LocationTagMapExample;
import org.opensrp.repository.LocationTagRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationTagMapMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
public class LocationTagRepositoryImpl extends BaseRepositoryImpl<LocationTag> implements LocationTagRepository {
	
	@Autowired
	private CustomLocationTagMapper locationTagMapper;
	
	@Autowired
	private CustomLocationTagMapMapper locationTagMapMapper;
	
	@Override
	public void add(LocationTag locationTag) {
		if (locationTag == null) {
			return;
		}
		if (getUniqueField(locationTag) == null) {
			return;
		}
		
		if (retrievePrimaryKey(locationTag) != null) {
			throw new DuplicateKeyException("Location tag name already exists");
			
		}
		
		if (getLocationTagByName(locationTag.getName()) != null) {
			throw new DuplicateKeyException("Location tag name already exists");
		}
		if (locationTag.getName().isEmpty()) {
			return;
		}
		org.opensrp.domain.postgres.LocationTag pgLocationTag = convert(locationTag);
		
		locationTagMapper.insertSelective(pgLocationTag);
		
	}
	
	@Override
	public void update(LocationTag locationTag) {
		if (locationTag == null) {
			return;
		}
		if (getUniqueField(locationTag) == null) {
			return;
		}
		Long id = locationTag.getId();
		if (id == null) {
			return; // location tag does not exist
		}
		if (locationTag.getName().isEmpty()) {
			return;
		}
		
		org.opensrp.domain.postgres.LocationTag getPgLocationTag = getLocationTagByNameAndNotEqualId(locationTag.getName(),
		    id);
		
		if (getPgLocationTag == null) {
			org.opensrp.domain.postgres.LocationTag pgLocationTag = convert(locationTag);
			pgLocationTag.setId(id);
			locationTagMapper.updateByPrimaryKey(pgLocationTag);
		} else {
			throw new DuplicateKeyException("Location tag name already exists");
			
		}
		
	}
	
	@Override
	public List<LocationTag> getAll() {
		LocationTagExample locationTagExample = new LocationTagExample();
		locationTagExample.createCriteria().andActiveIsNotNull();
		List<org.opensrp.domain.postgres.LocationTag> pgLocationTagList = locationTagMapper.selectMany(locationTagExample,
		    0, DEFAULT_FETCH_SIZE);
		return convert(pgLocationTagList);
	}
	
	@Override
	public void safeRemove(LocationTag locationTag) {
		if (locationTag == null) {
			return;
		}
		
		Long id = (Long) retrievePrimaryKey(locationTag);
		if (id == null) {
			return;
		}
		
		org.opensrp.domain.postgres.LocationTag pgLocationTag = convert(locationTag);
		pgLocationTag.setId(id);
		pgLocationTag.setActive(false);
		locationTagMapper.updateByPrimaryKey(pgLocationTag);
		
	}
	
	@Override
	public LocationTag getLocationTagByPrimaryKey(Long id) {
		if (id == null) {
			return null;
		}
		LocationTagExample example = new LocationTagExample();
		example.createCriteria().andIdEqualTo(id);
		List<org.opensrp.domain.postgres.LocationTag> locationTags = locationTagMapper.selectByExample(example);
		return locationTags.isEmpty() ? null : convert(locationTags.get(0));
	}
	
	@Override
	public void safeRemove(Long id) {
		LocationTag locationTag = getLocationTagByPrimaryKey(id);
		org.opensrp.domain.postgres.LocationTag pgLocationTag = convert(locationTag);
		if (pgLocationTag == null) {
			return;
		}
		pgLocationTag.setId(id);
		pgLocationTag.setActive(false);
		locationTagMapper.updateByPrimaryKey(pgLocationTag);
		
	}
	
	@Override
	public org.opensrp.domain.postgres.LocationTag getLocationTagByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		
		LocationTagExample locationTagExample = new LocationTagExample();
		locationTagExample.createCriteria().andNameEqualTo(name);
		List<org.opensrp.domain.postgres.LocationTag> locationTagList = locationTagMapper
		        .selectByExample(locationTagExample);
		
		return isEmptyList(locationTagList) ? null : locationTagList.get(0);
	}
	
	@Override
	protected Object retrievePrimaryKey(LocationTag locationTag) {
		Object uniqueId = getUniqueField(locationTag);
		if (uniqueId == null) {
			return null;
		}
		
		String name = uniqueId.toString();
		org.opensrp.domain.postgres.LocationTag pgLocationTag = getLocationTagByName(name);
		
		return pgLocationTag == null ? null : pgLocationTag.getId();
	}
	
	@Override
	protected Object getUniqueField(LocationTag locationTag) {
		return locationTag == null ? null : locationTag.getName();
	}
	
	@Override
	public LocationTag get(String id) {
		throw new NotImplementedException();
	}
	
	private LocationTag convert(org.opensrp.domain.postgres.LocationTag pgLocationTag) {
		if (pgLocationTag == null) {
			return null;
		}
		LocationTag locationTag = new LocationTag();
		
		locationTag.setName(pgLocationTag.getName());
		locationTag.setId(pgLocationTag.getId());
		locationTag.setActive(pgLocationTag.getActive());
		locationTag.setDescription(pgLocationTag.getDescription());
		
		return locationTag;
	}
	
	private org.opensrp.domain.postgres.LocationTag convert(LocationTag locationTag) {
		if (locationTag == null) {
			return null;
		}
		org.opensrp.domain.postgres.LocationTag pgLocationTag = new org.opensrp.domain.postgres.LocationTag();
		pgLocationTag.setName(locationTag.getName());
		locationTag.setId(pgLocationTag.getId());
		pgLocationTag.setActive(locationTag.getActive());
		pgLocationTag.setDescription(locationTag.getDescription());
		
		return pgLocationTag;
	}
	
	private List<LocationTag> convert(List<org.opensrp.domain.postgres.LocationTag> pgLocationTags) {
		List<LocationTag> locationTags = new ArrayList<>();
		if (isEmptyList(pgLocationTags)) {
			return locationTags;
		}
		for (org.opensrp.domain.postgres.LocationTag pgLocationTag : pgLocationTags) {
			locationTags.add(convert(pgLocationTag));
		}
		return locationTags;
	}
	
	@Override
	public org.opensrp.domain.postgres.LocationTag getLocationTagByNameAndNotEqualId(String name, Long id) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		if (id != 0) {
			return null;
		}
		
		LocationTagExample locationTagExample = new LocationTagExample();
		locationTagExample.createCriteria().andNameEqualTo(name).andIdNotEqualTo(id);
		List<org.opensrp.domain.postgres.LocationTag> locationTagList = locationTagMapper
		        .selectByExample(locationTagExample);
		
		return isEmptyList(locationTagList) ? null : locationTagList.get(0);
	}
	
	@Override
	public List<LocationTag> findByLocationTagExample(LocationTagExample locationTagExample, int offset, int limit) {
		List<org.opensrp.domain.postgres.LocationTag> pgLocationTagList = locationTagMapper.selectMany(locationTagExample,
		    offset, limit);
		return convert(pgLocationTagList);
	}
	
	private LocationTagMap convertLocationTagMap(org.opensrp.domain.postgres.LocationTagMap pgLocationTagMap) {
		if (pgLocationTagMap == null) {
			return null;
		}
		LocationTagMap locationTagmap = new LocationTagMap();
		
		locationTagmap.setLocationId(pgLocationTagMap.getLocationId());
		locationTagmap.setLocationTagId(pgLocationTagMap.getLocationTagId());
		
		return locationTagmap;
	}
	
	private org.opensrp.domain.postgres.LocationTagMap convertLocationTagMap(LocationTagMap locationTagMap) {
		if (locationTagMap == null) {
			return null;
		}
		org.opensrp.domain.postgres.LocationTagMap pgLocationTagMap = new org.opensrp.domain.postgres.LocationTagMap();
		pgLocationTagMap.setLocationId(locationTagMap.getLocationId());
		pgLocationTagMap.setLocationTagId(locationTagMap.getLocationTagId());
		
		return pgLocationTagMap;
	}
	
	private List<LocationTagMap> convertLocationTagMap(List<org.opensrp.domain.postgres.LocationTagMap> pgLocationTagMaps) {
		List<LocationTagMap> locationTagMaps = new ArrayList<>();
		if (isEmptyList(pgLocationTagMaps)) {
			return locationTagMaps;
		}
		for (org.opensrp.domain.postgres.LocationTagMap pgLocationTagMap : pgLocationTagMaps) {
			locationTagMaps.add(convertLocationTagMap(pgLocationTagMap));
		}
		return locationTagMaps;
	}
	
	@Override
	public int addLocationTagMap(LocationTagMap locationTagMap) {
		org.opensrp.domain.postgres.LocationTagMap pgLocationTagMap = convertLocationTagMap(locationTagMap);
		validateLocationTagMap(locationTagMap.getLocationId(), locationTagMap.getLocationTagId());
		
		if (getLocationTagMapByExample(locationTagMap.getLocationId(), locationTagMap.getLocationTagId()).size() != 0) {
			throw new DuplicateKeyException("Location tag map  already exists");
			
		}
		
		return locationTagMapMapper.insertSelective(pgLocationTagMap);
		
	}
	
	@Override
	public void deleteLocationTagMapByLocationIdAndLocationTagId(Long locationId, Long locationTagId) {
		
		validateLocationTagMap(locationId, locationTagId);
		LocationTagMapExample example = new LocationTagMapExample();
		example.createCriteria().andLocationIdEqualTo(locationId).andLocationTagIdEqualTo(locationTagId);
		
		locationTagMapMapper.deleteByExample(example);
		
	}
	
	@Override
	public List<LocationTagMap> getLocationTagMapByExample(Long locationId, Long locationTagId) {
		LocationTagMapExample example = createLocationTagMapExample(locationId, locationTagId);
		return convertLocationTagMap(locationTagMapMapper.selectByExample(example));
	}
	
	private LocationTagMapExample createLocationTagMapExample(Long locationId, Long locationTagId) {
		LocationTagMapExample example = new LocationTagMapExample();
		if (locationId != null) {
			example.createCriteria().andLocationIdEqualTo(locationId);
		}
		if (locationTagId != null) {
			example.createCriteria().andLocationTagIdEqualTo(locationTagId);
			
		}
		return example;
		
	}
	
	private void validateLocationTagMap(Long locationId, Long locationTagId) {
		if (locationId == null || locationId == 0) {
			throw new IllegalArgumentException("Location tag id not specified");
		}
		if (locationTagId == null || locationTagId == 0) {
			throw new IllegalArgumentException("location id not specified");
		}
	}
	
}
