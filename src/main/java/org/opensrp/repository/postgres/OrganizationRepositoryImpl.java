package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.CodeSystem;
import org.opensrp.domain.Organization;
import org.opensrp.domain.postgres.DateRange;
import org.opensrp.domain.postgres.OrganizationExample;
import org.opensrp.domain.postgres.OrganizationLocation;
import org.opensrp.domain.postgres.OrganizationLocationExample;
import org.opensrp.domain.postgres.OrganizationLocationExample.Criteria;
import org.opensrp.repository.OrganizationRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomOrganizationLocationMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomOrganizationMapper;
import org.opensrp.search.AssignedLocationAndPlanSearchBean;
import org.opensrp.search.OrganizationSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by Samuel Githengi on 8/30/19.
 */
@Repository
public class OrganizationRepositoryImpl extends BaseRepositoryImpl<Organization> implements OrganizationRepository {
	
	@Autowired
	private CustomOrganizationMapper organizationMapper;
	
	@Autowired
	private CustomOrganizationLocationMapper organizationLocationMapper;
	
	@Override
	public Organization get(String id) {
		return convert(findOrganizationByIdentifier(id));
	}
	
	@Override
	public Organization getByPrimaryKey(Long id) {
		if (id == null) {
			return null;
		}
		OrganizationExample example = new OrganizationExample();
		example.createCriteria().andIdEqualTo(id).andDateDeletedIsNull();
		List<org.opensrp.domain.postgres.Organization> organizations = organizationMapper.selectByExample(example);
		return organizations.isEmpty() ? null : convert(organizations.get(0));
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
		
		organizationMapper.insertSelective(pgOrganization);
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
		
		organizationMapper.updateByPrimaryKeySelective(pgOrganization);
		
	}
	
	@Override
	public List<Organization> getAll() {
		OrganizationExample example = new OrganizationExample();
		example.createCriteria().andDateDeletedIsNull();
		List<org.opensrp.domain.postgres.Organization> organizations = organizationMapper.selectMany(example, 0,
		    DEFAULT_FETCH_SIZE);
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
		
		organizationMapper.updateByPrimaryKeySelective(pgOrganization);
		
	}
	
	@Override
	public List<Organization> selectOrganizationsEncompassLocations(String location_id, Date activeDate) {
		List<org.opensrp.domain.postgres.Organization> organizations = organizationMapper
		        .selectOrganizationsEncompassLocations(location_id, activeDate);
		return convert(organizations);
	}
	
	@Override
	public void assignLocationAndPlan(Long organizationId, String jurisdictionIdentifier, Long jurisdictionId,
	        String planIdentifier, Long planId, Date fromDate, Date toDate) {
		List<OrganizationLocation> assignedLocations = getAssignedLocations(organizationId);
		for (OrganizationLocation organizationLocation : assignedLocations) {
			if (isExistingAssignment(jurisdictionId, planId, fromDate, organizationLocation)) {
				organizationLocation.setToDate(toDate);
				organizationLocation.setDuration(new DateRange(organizationLocation.getFromDate(), toDate));
				OrganizationLocationExample example = new OrganizationLocationExample();
				example.createCriteria().andIdEqualTo(organizationLocation.getId());
				organizationLocationMapper.updateByExample(organizationLocation, example);
				return;
			}
		}
		insertOrganizationLocation(organizationId, jurisdictionId, planId, fromDate, toDate);
		
	}
	
	private List<OrganizationLocation> getAssignedLocations(Long organizationId) {
		OrganizationLocationExample example = new OrganizationLocationExample();
		Date currentDate = new LocalDate().toDate();
		example.createCriteria().andOrganizationIdEqualTo(organizationId).andFromDateLessThanOrEqualTo(currentDate);
		return organizationLocationMapper.selectByExampleAndDateTo(example.getOredCriteria(), example.getOrderByClause(),
		    currentDate);
	}
	
	private boolean isExistingAssignment(Long jurisdictionId, Long planId, Date fromDate,
	        OrganizationLocation organizationLocation) {
		if (!LocalDate.fromDateFields(fromDate).equals(LocalDate.fromDateFields(organizationLocation.getFromDate()))) {
			logger.debug("from dates does not match");
			return false;
		} else if (jurisdictionId != null && planId != null) {
			return jurisdictionId.equals(organizationLocation.getLocationId())
			        && planId.equals(organizationLocation.getPlanId());
		} else if (jurisdictionId == null && planId != null) {
			return planId.equals(organizationLocation.getPlanId()) && organizationLocation.getLocationId() == null;
		} else if (jurisdictionId != null && planId == null) {
			return jurisdictionId.equals(organizationLocation.getLocationId()) && organizationLocation.getPlanId() == null;
		} else {
			return false;
		}
	}
	
	private void insertOrganizationLocation(Long organizationId, Long jurisdictionId, Long planId, Date fromDate,
	        Date toDate) {
		OrganizationLocation organizationLocation = new OrganizationLocation();
		organizationLocation.setOrganizationId(organizationId);
		organizationLocation.setLocationId(jurisdictionId);
		organizationLocation.setPlanId(planId);
		organizationLocation.setFromDate(fromDate);
		organizationLocation.setToDate(toDate);
		organizationLocation.setDuration(new DateRange(fromDate, toDate));
		organizationLocationMapper.insertSelective(organizationLocation);
	}
	
	@Override
	public List<AssignedLocations> findAssignedLocations(AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean) {
		Date currentDate = new LocalDate().toDate();
		OrganizationLocationExample example = new OrganizationLocationExample();
		Pair<Integer, Integer> pageSizeAndOffset = getPageSizeAndOffsetForAssignedLocAndPlans(assignedLocationAndPlanSearchBean);
		if(assignedLocationAndPlanSearchBean != null && assignedLocationAndPlanSearchBean.getOrganizationId() != null) {
			Criteria criteria = example.createCriteria()
					.andOrganizationIdEqualTo(assignedLocationAndPlanSearchBean.getOrganizationId());
			if (!assignedLocationAndPlanSearchBean.isReturnFutureAssignments()) {
				criteria.andFromDateLessThanOrEqualTo(currentDate);
			}
		}
		return organizationLocationMapper.findAssignedlocationsAndPlans(assignedLocationAndPlanSearchBean, pageSizeAndOffset.getRight() , pageSizeAndOffset.getLeft(), example.getOredCriteria(),
				currentDate);
	}
	
	@Override
	public List<AssignedLocations> findAssignedLocations(List<Long> organizationIds, boolean returnFutureAssignments) {
		Date currentDate = new LocalDate().toDate();
		OrganizationLocationExample example = new OrganizationLocationExample();
		example.createCriteria().andOrganizationIdIn(organizationIds).andFromDateLessThanOrEqualTo(currentDate);
		AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = new AssignedLocationAndPlanSearchBean();
		Pair<Integer,Integer> pageSizeAndOffset = getPageSizeAndOffsetForAssignedLocAndPlans(assignedLocationAndPlanSearchBean);
		return organizationLocationMapper.findAssignedlocationsAndPlans(assignedLocationAndPlanSearchBean,
				pageSizeAndOffset.getRight(), pageSizeAndOffset.getLeft(), example.getOredCriteria(),currentDate);
	}
	
	@Override
	public List<AssignedLocations> findAssignedLocationsByPlanId(AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean) {
		Date currentDate = new LocalDate().toDate();
		OrganizationLocationExample example = new OrganizationLocationExample();
		Pair<Integer,Integer> pageSizeAndOffset = getPageSizeAndOffsetForAssignedLocAndPlans(assignedLocationAndPlanSearchBean);
		if(assignedLocationAndPlanSearchBean != null  && assignedLocationAndPlanSearchBean.getPlanId() != null) {
			example.createCriteria().andPlanIdEqualTo(assignedLocationAndPlanSearchBean.getPlanId()).andFromDateLessThanOrEqualTo(currentDate);
		}
		return organizationLocationMapper.findAssignedlocationsAndPlans(assignedLocationAndPlanSearchBean, pageSizeAndOffset.getRight(), pageSizeAndOffset.getLeft(),
				example.getOredCriteria(),currentDate);
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
	
	private Organization convert(org.opensrp.domain.postgres.Organization pgEntity) {
		if (pgEntity == null) {
			return null;
		}
		Organization organization = new Organization();
		organization.setIdentifier(pgEntity.getIdentifier());
		organization.setActive(pgEntity.getActive());
		organization.setName(pgEntity.getName());
		organization.setId(pgEntity.getId());
		organization.setPartOf(pgEntity.getParentId());
		if (pgEntity.getType() instanceof CodeSystem) {
			organization.setType((CodeSystem) pgEntity.getType());
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
	
	@Override
	public List<Organization> findSearchOrganizations(OrganizationSearchBean organizationSearchBean) {
		Pair<Integer, Integer> pageSizeAndOffset = getPageSizeAndOffset(organizationSearchBean);
		return organizationMapper.selectSearchOrganizations(organizationSearchBean, pageSizeAndOffset.getRight(),
		    pageSizeAndOffset.getLeft());
	}
	
	private Pair<Integer, Integer> getPageSizeAndOffset(OrganizationSearchBean organizationSearchBean) {
		
		Integer pageSize = 0;
		Integer offset = 0;
		if (organizationSearchBean.getPageSize() == null || organizationSearchBean.getPageSize() == 0) {
			pageSize = DEFAULT_FETCH_SIZE;
		} else {
			pageSize = organizationSearchBean.getPageSize();
		}
		
		if (organizationSearchBean.getPageNumber() != null && organizationSearchBean.getPageNumber() != 0) {
			offset = (organizationSearchBean.getPageNumber() - 1) * pageSize;
		}
		
		return Pair.of(pageSize, offset);
	}
	
	@Override
	public int findOrganizationCount(OrganizationSearchBean organizationSearchBean) {
		return organizationMapper.selectOrganizationCount(organizationSearchBean);
	}

	@Override
	public Organization findOrganizationByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		OrganizationExample example = new OrganizationExample();
		example.createCriteria().andNameEqualTo(name).andDateDeletedIsNull();
		List<org.opensrp.domain.postgres.Organization> organizations = organizationMapper.selectByExample(example);
		return organizations.isEmpty() ? null : convert(organizations.get(0));
	}

	@Override
	public List<Organization> getAllOrganizations(OrganizationSearchBean organizationSearchBean) {
		Pair<Integer, Integer> pageSizeAndOffset = getPageSizeAndOffset(organizationSearchBean);
		OrganizationExample example = new OrganizationExample();
		example.createCriteria().andDateDeletedIsNull();
		List<org.opensrp.domain.postgres.Organization> organizations = organizationMapper.selectManyBySearchBean(organizationSearchBean,example, pageSizeAndOffset.getRight(), pageSizeAndOffset.getLeft());
		return convert(organizations);
	}

	private Pair<Integer, Integer> getPageSizeAndOffsetForAssignedLocAndPlans(AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean) {

		Integer pageSize = 0;
		Integer offset = 0;
		if (assignedLocationAndPlanSearchBean.getPageSize() == null || assignedLocationAndPlanSearchBean.getPageSize() == 0) {
			pageSize = DEFAULT_FETCH_SIZE;
		} else {
			pageSize = assignedLocationAndPlanSearchBean.getPageSize();
		}

		if (assignedLocationAndPlanSearchBean.getPageNumber() != null && assignedLocationAndPlanSearchBean.getPageNumber() != 0) {
			offset = (assignedLocationAndPlanSearchBean.getPageNumber() - 1) * pageSize;
		}

		return Pair.of(pageSize, offset);
	}

}
