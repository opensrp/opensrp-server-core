package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Organization;
import org.opensrp.domain.PractitionerRole;
import org.opensrp.domain.postgres.Practitioner;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.repository.PractitionerRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	public PractitionerRole getPractitionerRole(String identifier) {
		return StringUtils.isBlank(identifier) ? null : practitionerRoleRepository.get(identifier);
	}
	
	public List<PractitionerRole> getAllPractitionerRoles() {
		return practitionerRoleRepository.getAll();
	}
	
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
	
	public void deletePractitionerRole(PractitionerRole practitionerRole) {
		if (StringUtils.isBlank(practitionerRole.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		practitionerRoleRepository.safeRemove(practitionerRole);
	}
	
	public void deletePractitionerRole(String identifier) {
		if (StringUtils.isBlank(identifier)) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		practitionerRoleRepository.safeRemove(identifier);
	}
	
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
