package org.opensrp.repository.postgres;

import static org.opensrp.util.Utils.isEmptyList;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.LocationTag;
import org.opensrp.domain.postgres.LocationTagExample;
import org.opensrp.repository.LocationTagRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
public class LocationTagRepositoryImpl extends BaseRepositoryImpl<LocationTag> implements LocationTagRepository {
	
	@Autowired
	private CustomLocationTagMapper locationTagMapper;
	
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
		org.opensrp.domain.postgres.LocationTag pgPractitioner = convert(locationTag);
		
		locationTagMapper.insertSelective(pgPractitioner);
		
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
	
}
