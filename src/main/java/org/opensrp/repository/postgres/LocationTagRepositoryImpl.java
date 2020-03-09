package org.opensrp.repository.postgres;

import static org.opensrp.util.Utils.isEmptyList;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.LocationTag;
import org.opensrp.domain.postgres.LocationTagExample;
import org.opensrp.repository.LocationTagRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
			return; // location tag already added
		}
		
		if (getLocationTagByName(locationTag.getName()) != null) {
			return; // location tag with this name id already added
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
		Long id = getLocationTagByName(locationTag.getName()).getId();
		if (id == null) {
			return; // practitioner does not exist
		}
		
		org.opensrp.domain.postgres.LocationTag pgLocationTag = convert(locationTag);
		
		pgLocationTag.setId(id);
		locationTagMapper.updateByPrimaryKey(pgLocationTag);
		
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
		// TODO Auto-generated method stub
		return null;
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
	
}
