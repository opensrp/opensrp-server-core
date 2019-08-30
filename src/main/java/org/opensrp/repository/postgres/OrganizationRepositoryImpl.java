package org.opensrp.repository.postgres;

import java.util.List;

import org.opensrp.domain.Organization;
import org.opensrp.repository.OrganizationRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomOrganizationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Samuel Githengi on 8/30/19.
 */
@Repository
public class OrganizationRepositoryImpl extends BaseRepositoryImpl<Organization> implements OrganizationRepository {

	@Autowired
	private CustomOrganizationMapper organizationMapper;
	
	@Override
	public Organization get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Organization entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Organization entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Organization> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void safeRemove(Organization entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Object retrievePrimaryKey(Organization t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getUniqueField(Organization t) {
		// TODO Auto-generated method stub
		return null;
	}

}
