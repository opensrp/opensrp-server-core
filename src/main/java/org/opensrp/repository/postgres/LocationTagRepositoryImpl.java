package org.opensrp.repository.postgres;

import java.util.List;

import org.opensrp.domain.LocationTag;
import org.opensrp.repository.LocationTagRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomLocationTagMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LocationTagRepositoryImpl extends BaseRepositoryImpl<LocationTag> implements LocationTagRepository {
	
	@Autowired
	private CustomLocationTagMapper locationTagMapper;
	
	@Override
	public void add(LocationTag entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(LocationTag entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<LocationTag> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void safeRemove(LocationTag entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public LocationTag getLocationTagByPrimaryKey(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void safeRemove(Long id) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public LocationTag getLocationTagByName(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected Object retrievePrimaryKey(LocationTag t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected Object getUniqueField(LocationTag t) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public LocationTag get(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
