package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.joda.time.DateTime;
import org.opensrp.domain.Organization;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.search.PractitionerSearchBean;
import org.smartregister.domain.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PractitionerService {

    private PractitionerRepository practitionerRepository;

    private PractitionerRoleService practitionerRoleService;

    private OrganizationService organizationService;

    /**
     * @param practitionerRoleService the practitionerRoleService to set
     */
    @Autowired
    public void setPractitionerRoleService(PractitionerRoleService practitionerRoleService) {
        this.practitionerRoleService = practitionerRoleService;
    }

    @Autowired
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public PractitionerRepository getPractitionerRepository() {
        return practitionerRepository;
    }

    @Autowired
    public void setPractitionerRepository(PractitionerRepository practitionerRepository) {
        this.practitionerRepository = practitionerRepository;
    }

    @PreAuthorize("hasRole('PRACTITIONER_VIEW')")
    @PostAuthorize("hasPermission(returnObject, 'PRACTITIONER_VIEW')")
    public Practitioner getPractitioner(String identifier) {
        return StringUtils.isBlank(identifier) ? null : getPractitionerRepository().get(identifier);
    }

    @PreAuthorize("hasRole('PRACTITIONER_VIEW')")
    @PostAuthorize("hasPermission(returnObject, 'PRACTITIONER_VIEW')")
    public org.opensrp.domain.postgres.Practitioner getPgPractitioner(String identifier) {
        return StringUtils.isBlank(identifier) ? null : getPractitionerRepository().getPractitioner(identifier);
    }

    @PreAuthorize("hasRole('PRACTITIONER_VIEW')")
    @PostFilter("hasPermission(filterObject, 'PRACTITIONER_VIEW')")
    public List<Practitioner> getAllPractitioners(PractitionerSearchBean practitionerSearchBean) {
        return getPractitionerRepository().getAllPractitioners(practitionerSearchBean);
    }

    @PreAuthorize("hasPermission(#practitioner,'Practitioner','PRACTITIONER_CREATE') or hasPermission(#practitioner,'Practitioner','PRACTITIONER_UPDATE')")
    public Practitioner addOrUpdatePractitioner(Practitioner practitioner) {
        if (StringUtils.isBlank(practitioner.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        if (getPractitionerRepository().get(practitioner.getIdentifier()) != null) {
            practitioner.setDateEdited(DateTime.now());
            getPractitionerRepository().update(practitioner);
        } else {
            practitioner.setDateCreated(DateTime.now());
            getPractitionerRepository().add(practitioner);
        }
        return practitioner;
    }

    @PreAuthorize("hasPermission(#practitioner,'Practitioner','PRACTITIONER_DELETE')")
    public void deletePractitioner(Practitioner practitioner) {
        if (StringUtils.isBlank(practitioner.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        getPractitionerRepository().safeRemove(practitioner);

    }

    @PreAuthorize("hasPermission(#identifier,'Practitioner','PRACTITIONER_DELETE')")
    public void deletePractitioner(String identifier) {
        if (StringUtils.isBlank(identifier)) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        getPractitionerRepository().safeRemove(identifier);

    }

    @PreAuthorize("hasRole('PRACTITIONER_VIEW')")
    @PostFilter("hasPermission(filterObject, 'PRACTITIONER_VIEW')")
    public ImmutablePair<Practitioner, List<Long>> getOrganizationsByUserId(String userId) {
        Practitioner practioner = getPractitionerRepository().getPractitionerByUserId(userId);
        List<Long> organizationIds = new ArrayList<>();
        for (PractitionerRole practitionerRole : practitionerRoleService
                .getPgRolesForPractitioner(practioner.getIdentifier())) {
            organizationIds.add(practitionerRole.getOrganizationId());
        }
        return new ImmutablePair<>(practioner, organizationIds);

    }

    /**
     * Get practitioner using username
     *
     * @param username
     * @return practitioner with the username
     */
    @PreAuthorize("hasRole('PRACTITIONER_VIEW')")
    @PostFilter("hasPermission(filterObject, 'PRACTITIONER_VIEW')")
    public Practitioner getPractionerByUsername(String username) {
        return getPractitionerRepository().getPractitionerByUsername(username);

    }

    /**
     * Gets the organization ids that a user is assigned to according to the plan location
     * assignment
     *
     * @param username the username of user
     * @return the organization ids a user is assigned to
     */
    @PreAuthorize("hasPermission(#username,'User', 'PRACTITIONER_VIEW')")
    public List<Long> getOrganizationIdsByUserName(String username) {
        Practitioner practitioner = getPractionerByUsername(username);
        if (practitioner != null) {
            return practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier()).stream()
                    .map(role -> role.getOrganizationId()).collect(Collectors.toList());
        }

        return null;
    }

    /**
     * Get practitioner using the user id
     *
     * @param userId {@link String}, User id from keycloak
     * @return practitioner {@link Practitioner}
     */
    public Practitioner getPractitionerByUserId(String userId) {
        return getPractitionerRepository().getPractitionerByUserId(userId);
    }

    public List<Practitioner> getPractitionersByOrgIdentifier(String organizationIdentifier) {
        organizationService.validateIdentifier(organizationIdentifier);
        Organization organization = organizationService.getOrganization(organizationIdentifier);

        if (organization == null) {
            throw new IllegalArgumentException("Organization does not exist");
        }

        return getPractitionerRepository().getPractitionersByOrgId(organization.getId());
    }


    /**
     * Gets the organization ids that a user is assigned to according to the plan location
     * assignment
     *
     * @param userId the userId of user
     * @return the organization ids a user is assigned to
     */
    @PreAuthorize("hasPermission(#userId,'UserId', 'PRACTITIONER_VIEW')")
    public List<Long> getOrganizationIdsByUserId(String userId) {
        Practitioner practitioner = getPractitionerRepository().getPractitionerByUserId(userId);
        if (practitioner != null) {
            return practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier()).stream()
                    .map(role -> role.getOrganizationId()).collect(Collectors.toList());
        }

        return null;
    }

    public Long getPractitionerIdByIdentifier(String identifier) {
        org.opensrp.domain.postgres.Practitioner pgPractitioner = getPgPractitioner(identifier);
        return pgPractitioner != null ? pgPractitioner.getId() : null;
    }

    public List<Practitioner> getPractitionersByOrgId(Long orgId) {
        return practitionerRepository.getPractitionersByOrgId(orgId);
    }

    public List<Practitioner> getPractitionersByIdentifiers(List<String> practitionerIdentifiers) {
        List<Practitioner> practitioners = new ArrayList<>();
        if (practitionerIdentifiers != null && practitionerIdentifiers.size() > 0) {
            practitioners = practitionerRepository.getAllPractitionersByIdentifiers(practitionerIdentifiers);
        }
        return practitioners;
    }

    public List<Practitioner> getAssignedPractitionersByIdentifierAndCode(String practitionerIdentifier, String code) {
        List<Long> organizationIds = new ArrayList<>();
        List<PractitionerRole> practitionerRolesOfPractitioner = practitionerRoleService.getPgRolesForPractitioner(practitionerIdentifier);
        if (practitionerRolesOfPractitioner != null) {
            for (PractitionerRole practitionerRole : practitionerRolesOfPractitioner) {
                if (practitionerRole.getOrganizationId() != null) {
                    organizationIds.add(practitionerRole.getOrganizationId());
                }
            }
        }

        // Retrieved teams, now get all members of the team
        List<org.smartregister.domain.PractitionerRole> practitionerRoles = new ArrayList<>();
        List<String> practitionerIdentifiers = new ArrayList<>();
        for (Long organizationId : organizationIds) {
            practitionerRoles = practitionerRoleService.getPractitionerRolesByOrgIdAndCode(organizationId, code);
            practitionerRoles.stream().forEach(practitionerRole -> practitionerIdentifiers.add(practitionerRole.getPractitionerIdentifier()));
        }

        // Now get all practitioners by the given Ids
        return getPractitionersByIdentifiers(practitionerIdentifiers);
    }

    @PreAuthorize("hasRole('PRACTITIONER_VIEW')")
    public long countAllPractitioners() {
        return getPractitionerRepository().countAllPractitioners();
    }

    @PreAuthorize("hasRole('ORGANIZATION_VIEW')")
    @PostFilter("hasPermission(filterObject, 'ORGANIZATION_VIEW')")
    public ImmutablePair<Practitioner, List<Long>> getOrganizationsByPractitionerIdentifier(String practitionerIdentifier) {
        Practitioner practioner = getPractitionerRepository().getPractitionerByIdentifier(practitionerIdentifier);
        List<Long> organizationIds = new ArrayList<>();
        if (practioner != null && practioner.getIdentifier() != null) {
            for (PractitionerRole practitionerRole : practitionerRoleService
                    .getPgRolesForPractitioner(practioner.getIdentifier())) {
                organizationIds.add(practitionerRole.getOrganizationId());
            }
        }
        return new ImmutablePair<>(practioner, organizationIds);
    }

}
