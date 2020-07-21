package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Organization;
import org.opensrp.domain.PractitionerRole;
import org.opensrp.domain.postgres.Practitioner;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.repository.PractitionerRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class PractitionerRoleService {
	
	private PractitionerRoleRepository practitionerRoleRepository;
	
	private PractitionerRepository practitionerRepository;
	
	private OrganizationService organizationService;
	
	@Autowired
	public PractitionerRoleService(PractitionerRoleRepository practitionerRoleRepository,
	    PractitionerRepository practitionerRepository, OrganizationService organizationService) {
		this.practitionerRoleRepository = practitionerRoleRepository;
		this.practitionerRepository = practitionerRepository;
		this.organizationService = organizationService;
	}

	@PreAuthorize("hasRole('PRACTITIONER_ROLE_VIEW')")
	@PostAuthorize("hasPermission(returnObject,'PractitionerRole', 'PRACTITIONER_ROLE_VIEW')")
	public PractitionerRole getPractitionerRole(String identifier) {
		return StringUtils.isBlank(identifier) ? null : practitionerRoleRepository.get(identifier);
	}

	@PreAuthorize("hasRole('PRACTITIONER_ROLE_VIEW')")
	@PostFilter("hasPermission(filterObject, 'PRACTITIONER_ROLE_VIEW')")
	public List<PractitionerRole> getAllPractitionerRoles() {
		return practitionerRoleRepository.getAll();
	}

	@PreAuthorize("(hasPermission(#practitionerRole,'PractitionerRole', 'PRACTITIONER_ROLE_VIEW') and "
			+ "hasPermission(#practitionerRole,'PractitionerRole', 'PRACTITIONER_ROLE_CREATE') and "
			+ "hasPermission(#practitionerRole,'PractitionerRole', 'PRACTITIONER_ROLE_UPDATE'))")
	public PractitionerRole addOrUpdatePractitionerRole(PractitionerRole practitionerRole) {
		if (StringUtils.isBlank(practitionerRole.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		if (practitionerRoleRepository.get(practitionerRole.getIdentifier()) != null) {
			practitionerRoleRepository.update(practitionerRole);
		} else {
			practitionerRoleRepository.add(practitionerRole);
		}
		return practitionerRole;
	}

	@PreAuthorize("hasRole('PRACTITIONER_ROLE_DELETE') and hasPermission(#practitionerRole,'PractitionerRole', 'PRACTITIONER_ROLE_DELETE')")
	public void deletePractitionerRole(PractitionerRole practitionerRole) {
		if (StringUtils.isBlank(practitionerRole.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		practitionerRoleRepository.safeRemove(practitionerRole);
	}

	@PreAuthorize("hasRole('PRACTITIONER_ROLE_DELETE')")
	public void deletePractitionerRole(String identifier) {
		if (StringUtils.isBlank(identifier)) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		practitionerRoleRepository.safeRemove(identifier);
	}

	@PreAuthorize("hasRole('PRACTITIONER_ROLE_DELETE')")
	public void deletePractitionerRole(String organizationIdentifier, String practitionerIdentifier) {
		if (StringUtils.isBlank(organizationIdentifier) || StringUtils.isBlank(practitionerIdentifier)) {
			throw new IllegalArgumentException("Organization Identifier or Practitioner Identifier not specified");
		}
		
		Organization organization = organizationService.getOrganization(organizationIdentifier);
		
		Practitioner pgPractitioner = practitionerRepository.getPractitioner(practitionerIdentifier);
		
		if (organization == null || pgPractitioner == null) {
			return;
		}
		
		practitionerRoleRepository.safeRemove(organization.getId(), pgPractitioner.getId());
	}

	@PreAuthorize("hasRole('PRACTITIONER_ROLE_VIEW')")
	@PostFilter("hasPermission(filterObject, 'PRACTITIONER_ROLE_VIEW')")
	public List<PractitionerRole> getRolesForPractitioner(String practitionerIdentifier) {
		if (StringUtils.isBlank(practitionerIdentifier)) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		return practitionerRoleRepository.getRolesForPractitioner(practitionerIdentifier);
	}
	
	public List<org.opensrp.domain.postgres.PractitionerRole> getPgRolesForPractitioner(String practitionerIdentifier) {
		if (StringUtils.isBlank(practitionerIdentifier)) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		return practitionerRoleRepository.getPgRolesForPractitioner(practitionerIdentifier);
	}
	
}
