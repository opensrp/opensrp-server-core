package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.opensrp.domain.Practitioner;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.repository.PractitionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PractitionerService {

	private PractitionerRepository practitionerRepository;

	private PractitionerRoleService practitionerRoleService;

	@Autowired
	public void setPractitionerRepository(PractitionerRepository practitionerRepository) {
		this.practitionerRepository = practitionerRepository;
	}

	/**
	 * @param practitionerRoleService the practitionerRoleService to set
	 */
	@Autowired
	public void setPractitionerRoleService(PractitionerRoleService practitionerRoleService) {
		this.practitionerRoleService = practitionerRoleService;
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

	public ImmutablePair<Practitioner, List<Long>> getOrganizationsByUserId(String userId) {
		Practitioner practioner = getPractitionerRepository().getPractitionerByUserId(userId);
		List<Long> organizationIds = new ArrayList<>();
		for (PractitionerRole practitionerRole : practitionerRoleService
				.getPgRolesForPractitioner(practioner.getIdentifier())) {
			organizationIds.add(practitionerRole.getOrganizationId());
		}
		return new ImmutablePair<>(practioner, organizationIds);

	}
}
