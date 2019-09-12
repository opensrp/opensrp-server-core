package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Practitioner;
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

    public Practitioner getPractitioner(String identifier) {
        return StringUtils.isBlank(identifier) ? null : getPractitionerRepository().get(identifier);
    }

    public List<Practitioner> getAllPractitioners() {
        return getPractitionerRepository().getAll();
    }

    public void addOrUpdatePractitioner(Practitioner practitioner) {
        if (StringUtils.isBlank(practitioner.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        if (getPractitionerRepository().get(practitioner.getIdentifier()) != null) {
            getPractitionerRepository().update(practitioner);
        } else {
            getPractitionerRepository().add(practitioner);
        }
    }

    public void deletePractitioner(Practitioner practitioner) {
        if (StringUtils.isBlank(practitioner.getIdentifier())) {
            throw new IllegalArgumentException("Identifier not specified");
        }

        getPractitionerRepository().safeRemove(practitioner);

    }
}
