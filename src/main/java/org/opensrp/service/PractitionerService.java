package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.opensrp.domain.Organization;
import org.opensrp.domain.Practitioner;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.search.PractitionerSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PractitionerService {

	private PractitionerRepository practitionerRepository;

	private PractitionerRoleService practitionerRoleService;

	private OrganizationService organizationService;

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

	@Autowired
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}

	public PractitionerRepository getPractitionerRepository() {
		return practitionerRepository;
	}

	public Practitioner getPractitioner(String identifier) {
		return StringUtils.isBlank(identifier) ? null : getPractitionerRepository().get(identifier);
	}

	public org.opensrp.domain.postgres.Practitioner getPgPractitioner(String identifier) {
		return StringUtils.isBlank(identifier) ? null : getPractitionerRepository().getPractitioner(identifier);
	}

	public List<Practitioner> getAllPractitioners(PractitionerSearchBean practitionerSearchBean) {
		return getPractitionerRepository().getAllPractitioners(practitionerSearchBean);
	}

	public Practitioner addOrUpdatePractitioner(Practitioner practitioner) {
		if (StringUtils.isBlank(practitioner.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}

		if (getPractitionerRepository().get(practitioner.getIdentifier()) != null) {
			getPractitionerRepository().update(practitioner);
		} else {
			getPractitionerRepository().add(practitioner);
		}
		return practitioner;
	}

	public void deletePractitioner(Practitioner practitioner) {
		if (StringUtils.isBlank(practitioner.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}

		getPractitionerRepository().safeRemove(practitioner);

	}

	public void deletePractitioner(String identifier) {
		if (StringUtils.isBlank(identifier)) {
			throw new IllegalArgumentException("Identifier not specified");
		}

		getPractitionerRepository().safeRemove(identifier);

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

	/**
	 * Get practitioner using username
	 *
	 * @param username
	 * @return practitioner with the username
	 */
	public Practitioner getPractionerByUsername(String username) {
		return getPractitionerRepository().getPractitionerByUsername(username);

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

	public Long getPractitionerIdByIdentifier(String identifier) {
		org.opensrp.domain.postgres.Practitioner pgPractitioner = getPgPractitioner(identifier);
		return pgPractitioner != null ? pgPractitioner.getId() : null;
	}
}
