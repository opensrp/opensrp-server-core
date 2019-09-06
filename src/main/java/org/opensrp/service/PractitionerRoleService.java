package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.PractitionerRoleDefinition;
import org.opensrp.repository.PractitionerRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PractitionerRoleService {

    private PractitionerRoleRepository practitionerRoleRepository;

    @Autowired
    public void setPractitionerRoleRepository(PractitionerRoleRepository practitionerRoleRepository) {
        this.practitionerRoleRepository = practitionerRoleRepository;
    }

    public PractitionerRoleRepository getPractitionerRoleRepository() {
        return practitionerRoleRepository;
    }

    public PractitionerRoleDefinition getPractitionerRole(String identifier) {
        return StringUtils.isBlank(identifier) ? null : getPractitionerRoleRepository().get(identifier);
    }

    public List<PractitionerRoleDefinition> getAllPractitionerRoles() {
        return  getPractitionerRoleRepository().getAll();
    }

    public void addOrUpdatePractitionerRole(PractitionerRoleDefinition practitionerRoleDefinition) {
        if (StringUtils.isBlank(practitionerRoleDefinition.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        if (getPractitionerRoleRepository().get(practitionerRoleDefinition.getIdentifier()) != null) {
            getPractitionerRoleRepository().update(practitionerRoleDefinition);
        } else {
            getPractitionerRoleRepository().add(practitionerRoleDefinition);
        }
    }

    public void deletePractitionerRole(PractitionerRoleDefinition practitionerRoleDefinition) {
        if (StringUtils.isBlank(practitionerRoleDefinition.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        getPractitionerRoleRepository().safeRemove(practitionerRoleDefinition);
    }

    public void getRolesForPractitioner(String practitionerIdentifier) {
        if (StringUtils.isBlank(practitionerIdentifier)) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        getPractitionerRoleRepository().getRolesForPractitioner(practitionerIdentifier);
    }

}
