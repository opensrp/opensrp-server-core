package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Organization;
import org.opensrp.domain.postgres.Practitioner;
import org.opensrp.domain.PractitionerRole;
import org.opensrp.repository.PractitionerRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PractitionerRoleService {

    private PractitionerRoleRepository practitionerRoleRepository;

    private PractitionerService practitionerService;

    private OrganizationService organizationService;

    @Autowired
    public void setPractitionerRoleRepository(PractitionerRoleRepository practitionerRoleRepository) {
        this.practitionerRoleRepository = practitionerRoleRepository;
    }

    /**
     * @param practitionerService the practitionerService to set
     */
    @Autowired
    public void setPractitionerService(PractitionerService practitionerService) {
        this.practitionerService = practitionerService;
    }

    /**
     *
     * @param organizationService the organizationService to set
     */
    @Autowired
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    public PractitionerRoleRepository getPractitionerRoleRepository() {
        return practitionerRoleRepository;
    }

    public PractitionerRole getPractitionerRole(String identifier) {
        return StringUtils.isBlank(identifier) ? null : getPractitionerRoleRepository().get(identifier);
    }

    public List<PractitionerRole> getAllPractitionerRoles() {
        return  getPractitionerRoleRepository().getAll();
    }

    public PractitionerRole addOrUpdatePractitionerRole(PractitionerRole practitionerRole) {
        if (StringUtils.isBlank(practitionerRole.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        if (getPractitionerRoleRepository().get(practitionerRole.getIdentifier()) != null) {
            getPractitionerRoleRepository().update(practitionerRole);
        } else {
            getPractitionerRoleRepository().add(practitionerRole);
        }
        return practitionerRole;
    }

    public void deletePractitionerRole(PractitionerRole practitionerRole) {
        if (StringUtils.isBlank(practitionerRole.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        getPractitionerRoleRepository().safeRemove(practitionerRole);
    }

    public void deletePractitionerRole(String identifier) {
        if (StringUtils.isBlank(identifier)) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        getPractitionerRoleRepository().safeRemove(identifier);
    }

    public void deletePractitionerRole(String organizationIdentifier, String practitionerIdentifier) {
        if (StringUtils.isBlank(organizationIdentifier) || StringUtils.isBlank(practitionerIdentifier) ) {
            throw new IllegalArgumentException("Organization Identifier or Practitioner Identifier not specified");
        }

        Organization organization = organizationService.getOrganization(organizationIdentifier);

        Practitioner pgPractitioner= practitionerService.getPgPractitioner(practitionerIdentifier);

        if (organization == null || pgPractitioner == null) {
            return;
        }

        getPractitionerRoleRepository().safeRemove(organization.getId(), pgPractitioner.getId());
    }

    public List<PractitionerRole> getRolesForPractitioner(String practitionerIdentifier) {
        if (StringUtils.isBlank(practitionerIdentifier)) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        return getPractitionerRoleRepository().getRolesForPractitioner(practitionerIdentifier);
    }

    public List<org.opensrp.domain.postgres.PractitionerRole> getPgRolesForPractitioner(String practitionerIdentifier) {
        if (StringUtils.isBlank(practitionerIdentifier)) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        return getPractitionerRoleRepository().getPgRolesForPractitioner(practitionerIdentifier);
    }

}
