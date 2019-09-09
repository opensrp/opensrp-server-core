package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.CodeSystem;
import org.opensrp.domain.Organization;
import org.opensrp.domain.postgres.OrganizationExample;
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
		return convert(findOrganizationByIdentifier(id));
	}

	@Override
	public void add(Organization entity) {
		if (getUniqueField(entity) == null) {
			return;
		}

		if (retrievePrimaryKey(entity) != null) { // Organization already added
			return;
		}

		org.opensrp.domain.postgres.Organization pgOrganization = convert(entity, null);
		if (pgOrganization == null) {
			return;
		}

		organizationMapper.insert(pgOrganization);
	}

	@Override
	public void update(Organization entity) {
		if (getUniqueField(entity) == null) {
			return;
		}

		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Organization does not exist
			return;
		}

		org.opensrp.domain.postgres.Organization pgOrganization = convert(entity, id);
		if (pgOrganization == null) {
			return;
		}

		organizationMapper.updateByPrimaryKey(pgOrganization);

	}

	@Override
	public List<Organization> getAll() {
		List<org.opensrp.domain.postgres.Organization> organizations = organizationMapper
				.selectMany(new OrganizationExample(), 0, DEFAULT_FETCH_SIZE);
		return convert(organizations);
	}

	@Override
	public void safeRemove(Organization entity) {
		if (getUniqueField(entity) == null) {
			return;
		}

		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Organization does not exist
			return;
		}

		org.opensrp.domain.postgres.Organization pgOrganization = convert(entity, id);
		if (pgOrganization == null) {
			return;
		}
		pgOrganization.setDateDeleted(new Date());

		organizationMapper.updateByPrimaryKey(pgOrganization);

	}

	@Override
	public void assignLocationAndPlan(String organizationId, Long jurisdictionId, Long planId) {

	}

	@Override
	public void findAssignedLocations(String organizationIdentifier) {

	}

	@Override
	protected Long retrievePrimaryKey(Organization organization) {
		String identifier = getUniqueField(organization);
		if (identifier == null) {
			return null;
		}

		org.opensrp.domain.postgres.Organization pgEntity = findOrganizationByIdentifier(identifier);
		if (pgEntity == null) {
			return null;
		}
		return pgEntity.getId();
	}

	@Override
	protected String getUniqueField(Organization organization) {
		return organization.getIdentifier();
	}

	/**
	 * Get an Organization using an organization identifier
	 * 
	 * @param identifier
	 * @return the organization
	 */
	private org.opensrp.domain.postgres.Organization findOrganizationByIdentifier(String identifier) {
		if (StringUtils.isBlank(identifier)) {
			return null;
		}
		OrganizationExample example = new OrganizationExample();
		example.createCriteria().andIdentifierEqualTo(identifier).andDateDeletedIsNull();
		List<org.opensrp.domain.postgres.Organization> organizations = organizationMapper.selectByExample(example);
		return organizations.isEmpty() ? null : organizations.get(0);
	}

	@SuppressWarnings("unchecked")
	private Organization convert(org.opensrp.domain.postgres.Organization pgEntity) {
		if (pgEntity == null) {
			return null;
		}
		Organization organization = new Organization();
		organization.setIdentifier(pgEntity.getIdentifier());
		organization.setActive(pgEntity.getActive());
		organization.setName(pgEntity.getName());
		organization.setPartOf(pgEntity.getId());
		if (pgEntity.getType() instanceof List<?>) {
			organization.setType((List<CodeSystem>) pgEntity.getType());
		}

		return organization;
	}

	private List<Organization> convert(List<org.opensrp.domain.postgres.Organization> pgEntities) {
		List<Organization> organizations = new ArrayList<>();
		for (org.opensrp.domain.postgres.Organization pgEntity : pgEntities) {
			organizations.add(convert(pgEntity));
		}
		return organizations;
	}

	private org.opensrp.domain.postgres.Organization convert(Organization organization, Long id) {
		if (organization == null) {
			return null;
		}
		org.opensrp.domain.postgres.Organization pgOrganization = new org.opensrp.domain.postgres.Organization();
		pgOrganization.setId(id);
		pgOrganization.setIdentifier(organization.getIdentifier());
		pgOrganization.setActive(organization.isActive());
		pgOrganization.setName(organization.getName());
		pgOrganization.setType(organization.getType());
		pgOrganization.setParentId(organization.getPartOf());
		return pgOrganization;
	}

}
