package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.PractitionerDefinition;
import org.opensrp.repository.PractitionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PractitionerService {

    private PractitionerRepository practitionerRepository;

    @Autowired
    public void setPractitionerRepository(PractitionerRepository practitionerRepository) {
        this.practitionerRepository = practitionerRepository;
    }

    public PractitionerRepository getPractitionerRepository() {
        return practitionerRepository;
    }

    public PractitionerDefinition getPractitioner(String identifier) {
        return StringUtils.isBlank(identifier) ? null : getPractitionerRepository().get(identifier);
    }

    public List<PractitionerDefinition> getAllPractitioners() {
        return getPractitionerRepository().getAll();
    }

    public void addOrUpdatePractitioner(PractitionerDefinition practitionerDefinition) {
        if (StringUtils.isBlank(practitionerDefinition.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        if (getPractitionerRepository().get(practitionerDefinition.getIdentifier()) != null) {
            getPractitionerRepository().update(practitionerDefinition);
        } else {
            getPractitionerRepository().add(practitionerDefinition);
        }
    }

    public void deletePractitioner(PractitionerDefinition practitionerDefinition) {
        if (StringUtils.isBlank(practitionerDefinition.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        getPractitionerRepository().safeRemove(practitionerDefinition);

    }
}
