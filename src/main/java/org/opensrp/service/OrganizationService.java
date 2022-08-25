/**
 *
 */
package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.joda.time.DateTime;
import org.opensrp.domain.AssignedLocations;
import org.opensrp.domain.Organization;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.OrganizationRepository;
import org.opensrp.repository.PlanRepository;
import org.opensrp.search.AssignedLocationAndPlanSearchBean;
import org.opensrp.search.BaseSearchBean;
import org.opensrp.search.OrganizationSearchBean;
import org.smartregister.domain.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author Samuel Githengi created on 09/09/19
 */
@Service
public class OrganizationService {

    private static final String ASSIGNED_LOCATIONS_HASH_KEY = "_assignedLocations";
    private OrganizationRepository organizationRepository;
    private LocationRepository locationRepository;
    private PlanRepository planRepository;
    @Autowired
    private PractitionerService practitionerService;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, List<AssignedLocations>> hashOps;

    @Autowired
    public OrganizationService(OrganizationRepository organizationRepository, LocationRepository locationRepository,
                               PlanRepository planRepository) {
        this.organizationRepository = organizationRepository;
        this.locationRepository = locationRepository;
        this.planRepository = planRepository;
    }

    /**
     * Get all organizations
     *
     * @return all organizations
     */
    @PreAuthorize("hasRole('ORGANIZATION_VIEW')")
    @PostFilter("hasPermission(filterObject, 'ORGANIZATION_VIEW')")
    public List<Organization> getAllOrganizations(OrganizationSearchBean organizationSearchBean) {
        return organizationRepository.getAllOrganizations(organizationSearchBean);
    }

    /**
     * Returns all organizations filtered by locations
     *
     * @return all organizations
     */
    @PreAuthorize("hasRole('ORGANIZATION_VIEW')")
    @PostFilter("hasPermission(filterObject, 'ORGANIZATION_VIEW')")
    public List<Organization> selectOrganizationsEncompassLocations(String location_id) {
        return organizationRepository.selectOrganizationsEncompassLocations(location_id, new Date());
    }

    /**
     * Get the organization that has the identifier
     *
     * @param identifier
     * @return organization with matching identifier
     */
    @PreAuthorize("hasRole('ORGANIZATION_VIEW')")
    @PostAuthorize("hasPermission(returnObject, 'ORGANIZATION_VIEW')")
    public Organization getOrganization(String identifier) {
        return organizationRepository.get(identifier);
    }

    /**
     * Get the organization that has the identifier
     *
     * @param id
     * @return organization with matching identifier
     */
    @PreAuthorize("hasRole('ORGANIZATION_VIEW')")
    @PostAuthorize("hasPermission(returnObject, ORGANIZATION_VIEW')")
    public Organization getOrganization(Long id) {
        return organizationRepository.getByPrimaryKey(id);
    }

    private void validateIdentifier(Organization organization) {
        validateIdentifier(organization.getIdentifier());
    }

    public void validateIdentifier(String identifier) {
        if (StringUtils.isBlank(identifier))
            throw new IllegalArgumentException("Organization Identifier not specified");
    }

    /**
     * Adds or Updates an Organization
     *
     * @param organization to add on update
     */
    @PreAuthorize("hasPermission(#organization,'Organization', 'ORGANIZATION_CREATE') and hasPermission(#organization,'Organization', 'ORGANIZATION_UPDATE')")
    public void addOrUpdateOrganization(Organization organization) {
        validateIdentifier(organization);
        Organization entity = organizationRepository.get(organization.getIdentifier());
        if (entity != null) {
            entity.setDateEdited(DateTime.now());
            organizationRepository.update(entity);
        } else {
            organization.setDateCreated(DateTime.now());
            organizationRepository.add(organization);
        }

    }

    /**
     * Adds an Organization
     *
     * @param organization to add
     */
    @PreAuthorize("hasPermission(#organization,'Organization', 'ORGANIZATION_CREATE')")
    public void addOrganization(Organization organization) {
        validateIdentifier(organization);
        organizationRepository.add(organization);

    }

    /**
     * Updates an Organization
     *
     * @param organization to update
     */
    @PreAuthorize("hasPermission(#organization,'Organization', 'ORGANIZATION_UPDATE')")
    public void updateOrganization(Organization organization) {
        validateIdentifier(organization);
        organizationRepository.update(organization);

    }

    /**
     * Assigns the jurisdiction and /or plan to the organization with organizationId
     *
<<<<<<< HEAD
<<<<<<< HEAD
     * @param identifier     the id of the organization
     * @param jurisdictionId the identifier of the jurisdiction
     * @param planId         the identifier of the plan
=======
     * @param identifier the id of the organization
     * @param jurisdictionId the identifier of the jurisdiction
     * @param planId the identifier of the plan
>>>>>>> 4dd439d2 (reformat server-core code)
=======
     * @param identifier     the id of the organization
     * @param jurisdictionId the identifier of the jurisdiction
     * @param planId         the identifier of the plan
>>>>>>> 342d08ef (update charges to fix merge conflict)
     * @param fromDate
     * @param toDate
     */
    @PreAuthorize("hasPermission(#identifier,'OrganizationLocation','ORGANIZATION_ASSIGN_LOCATION')")
    public void assignLocationAndPlan(String identifier, String jurisdictionId, String planId, Date fromDate, Date toDate) {
        validateIdentifier(identifier);
        if (StringUtils.isBlank(identifier))
            throw new IllegalArgumentException("identifier cannot be null or empty");
        if (StringUtils.isBlank(jurisdictionId) && StringUtils.isBlank(planId))
            throw new IllegalArgumentException("jurisdictionId and planId cannot be null");
        Organization organization = getOrganization(identifier);
        if (organization == null)
            throw new IllegalArgumentException("Organization not found");
        revokeFromCache(identifier);
        organizationRepository.assignLocationAndPlan(organization.getId(), jurisdictionId,
                locationRepository.retrievePrimaryKey(jurisdictionId, true), planId, planRepository.retrievePrimaryKey(planId),
                fromDate == null ? new Date() : fromDate, toDate);

    }

    /**
     * Gets the locations and Plans assigned to an organization
     *
<<<<<<< HEAD
<<<<<<< HEAD
     * @param identifier              the organization identifier
=======
     * @param identifier the organization identifier
>>>>>>> 4dd439d2 (reformat server-core code)
=======
     * @param identifier              the organization identifier
>>>>>>> 342d08ef (update charges to fix merge conflict)
     * @param returnFutureAssignments flag to control if future assignments are returned
     * @return the assigned locations and plans
     */

    @PreAuthorize("hasRole('ORGANIZATION_VIEW_LOCATIONS')")
    @PostFilter("hasPermission(filterObject ,'ORGANIZATION_VIEW_LOCATIONS')")
    public List<AssignedLocations> findAssignedLocationsAndPlans(String identifier, boolean returnFutureAssignments,
                                                                 Integer pageNumber, Integer pageSize, String orderByType, String orderByFieldName) {
        validateIdentifier(identifier);
        Organization organization = getOrganization(identifier);
        if (organization == null)
            throw new IllegalArgumentException("Organization not found");

        BaseSearchBean.OrderByType orderByTypeEnum = orderByType != null ? BaseSearchBean.OrderByType.valueOf(orderByType)
                : BaseSearchBean.OrderByType.DESC;
        BaseSearchBean.FieldName fieldName = orderByFieldName != null ? BaseSearchBean.FieldName.valueOf(orderByFieldName)
                : BaseSearchBean.FieldName.id;

        AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .pageNumber(pageNumber).pageSize(pageSize).orderByType(orderByTypeEnum).orderByFieldName(fieldName)
                .organizationId(organization.getId()).returnFutureAssignments(returnFutureAssignments).build();
        return organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);

    }

    /**
     * Gets the locations and Plans assigned to a list of organizations
     *
<<<<<<< HEAD
<<<<<<< HEAD
     * @param organizationIds         the organization ids
=======
     * @param organizationIds the organization ids
>>>>>>> 4dd439d2 (reformat server-core code)
=======
     * @param organizationIds         the organization ids
>>>>>>> 342d08ef (update charges to fix merge conflict)
     * @param returnFutureAssignments flag to control if future assignments are returned
     * @return the assigned locations and plans
     */
    public List<AssignedLocations> findAssignedLocationsAndPlans(List<Long> organizationIds,
                                                                 boolean returnFutureAssignments) {
        return organizationRepository.findAssignedLocations(organizationIds, returnFutureAssignments);

    }

    /**
     * Gets the active locations and Plans assigned to a list of organizations
     *
     * @param organizationIds the organization ids
     * @return the assigned locations and plans
     */
    @PreAuthorize("hasRole('ORGANIZATION_VIEW_LOCATIONS')")
    @PostFilter("hasPermission(filterObject,'ORGANIZATION_VIEW_LOCATIONS')")
    public List<AssignedLocations> findAssignedLocationsAndPlans(List<Long> organizationIds) {
        return findAssignedLocationsAndPlans(organizationIds, false);
    }

    /**
     * Gets the locations and Plans using the Plan Identifier
     *
     * @param planIdentifier the plan identifier
     * @return the assigned locations and plans
     */
    @PreAuthorize("hasRole('ORGANIZATION_VIEW_LOCATIONS')")
    @PostFilter("hasPermission(filterObject,'ORGANIZATION_VIEW_LOCATIONS')")
    public List<AssignedLocations> findAssignedLocationsAndPlansByPlanIdentifier(String planIdentifier, Integer pageNumber,
                                                                                 Integer pageSize, String orderByType, String orderByFieldName) {
        if (StringUtils.isBlank(planIdentifier))
            throw new IllegalArgumentException("PlanIdentifier Identifier not specified");

        Long planId = planRepository.retrievePrimaryKey(planIdentifier);

        if (planId == null)
            throw new IllegalArgumentException("Plan not found");

        BaseSearchBean.OrderByType orderByTypeEnum = orderByType != null ? BaseSearchBean.OrderByType.valueOf(orderByType)
                : BaseSearchBean.OrderByType.DESC;
        BaseSearchBean.FieldName fieldName = orderByFieldName != null ? BaseSearchBean.FieldName.valueOf(orderByFieldName)
                : BaseSearchBean.FieldName.id;

        AssignedLocationAndPlanSearchBean assignedLocationAndPlanSearchBean = AssignedLocationAndPlanSearchBean.builder()
                .pageNumber(pageNumber).pageSize(pageSize).orderByType(orderByTypeEnum).orderByFieldName(fieldName)
                .planId(planId).build();
        return organizationRepository.findAssignedLocations(assignedLocationAndPlanSearchBean);

    }

    /**
     * Set the Organization repository
     *
     * @param organizationRepository the organizationRepository to set
     */
    @Autowired
    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * set the location repository
     *
     * @param locationRepository the locationRepository to set
     */
    @Autowired
    /**
     * @param locationRepository the locationRepository to set
     */
    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * set the plan Repository
     *
     * @param planRepository the planRepository to set
     */
    @Autowired
    public void setPlanRepository(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    /**
     * @param practitionerService the practitionerService to set
     */
    public void setPractitionerService(PractitionerService practitionerService) {
        this.practitionerService = practitionerService;
    }

    @PreAuthorize("hasRole('ORGANIZATION_VIEW')")
    @PostFilter("hasPermission(filterObject, 'ORGANIZATION_VIEW')")
    public List<Organization> getSearchOrganizations(OrganizationSearchBean organizationSearchBean) {
        return organizationRepository.findSearchOrganizations(organizationSearchBean);
    }

    public Integer findOrganizationCount(OrganizationSearchBean organizationSearchBean) {
        return organizationRepository.findOrganizationCount(organizationSearchBean);
    }

    private void revokeFromCache(String organizationIdentifier) {
        List<Practitioner> practitioners = getPractitionersByOrgIdentifier(organizationIdentifier);
        for (Practitioner practitioner : practitioners) {
            hashOps.delete(practitioner.getUsername(), ASSIGNED_LOCATIONS_HASH_KEY);
        }
    }

    /**
     * Gets practitioners in an organization
     *
     * @param organizationIdentifier the identifier for organization
     * @return practitioners in an organization
     */

    public List<Practitioner> getPractitionersByOrgIdentifier(String organizationIdentifier) {
        validateIdentifier(organizationIdentifier);
        Organization organization = getOrganization(organizationIdentifier);

        if (organization == null) {
            throw new IllegalArgumentException("Organization does not exist");
        }

        return practitionerService.getPractitionersByOrgId(organization.getId());
    }

    public Organization findOrganizationByName(String organizationName) {
        return organizationRepository.findOrganizationByName(organizationName);
    }

    /**
     * This method will revoke all the team assignments including future assignments as well
     * fetched from the plan Id
     * by setting to_date param to the current date
     */
    public void unassignLocationAndPlan(String planIdentifier) {
        if (StringUtils.isBlank(planIdentifier))
            throw new IllegalArgumentException("PlanIdentfier cannot be null");

        Long planId = planRepository.retrievePrimaryKey(planIdentifier);
        if (planId == null)
            throw new IllegalArgumentException("Plan not found");

        organizationRepository.unassignLocationAndPlan(planId);
    }

    @PreAuthorize("hasRole('ORGANIZATION_VIEW')")
    public long countAllOrganizations() {
        return organizationRepository.countAllOrganizations();
    }

    @PreAuthorize("hasRole('ORGANIZATION_VIEW')")
    @PostFilter("hasPermission(filterObject, 'ORGANIZATION_VIEW')")
    public List<Organization> getAllOrganizationsByOrganizationIds(List<Long> organizationIds) {
        return organizationRepository.getOrganizationsByIds(organizationIds);
    }

    @PreAuthorize("hasRole('ORGANIZATION_VIEW')")
    @PostFilter("hasPermission(filterObject, 'ORGANIZATION_VIEW')")
    public List<Organization> getOrganizationsByPractitionerIdentifier(String practitionerIdentifier) {
        ImmutablePair<Practitioner, List<Long>> practitionerOrganizationIds = practitionerService.getOrganizationsByPractitionerIdentifier(practitionerIdentifier);
        return practitionerOrganizationIds != null && practitionerOrganizationIds.getRight() != null && practitionerOrganizationIds.getRight().size() > 0 ? getAllOrganizationsByOrganizationIds(practitionerOrganizationIds.getRight()) : null;
    }
}
