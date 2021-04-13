package org.opensrp.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Organization;
import org.opensrp.domain.postgres.Practitioner;
import org.smartregister.domain.PractitionerRole;
import org.opensrp.repository.PractitionerRoleRepository;
import org.opensrp.search.PractitionerRoleSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

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
	@Lazy
	public void setPractitionerService(PractitionerService practitionerService) {
		this.practitionerService = practitionerService;
	}
	
	/**
	 * @param organizationService the organizationService to set
	 */
	@Lazy
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	
	public PractitionerRoleRepository getPractitionerRoleRepository() {
		return practitionerRoleRepository;
	}
	
	@PreAuthorize("hasRole('PRACTITIONER_ROLE_VIEW')")
	@PostAuthorize("hasPermission(returnObject, 'PRACTITIONER_ROLE_VIEW')")
	public PractitionerRole getPractitionerRole(String identifier) {
		return StringUtils.isBlank(identifier) ? null : getPractitionerRoleRepository().get(identifier);
	}
	
	@PreAuthorize("hasRole('PRACTITIONER_ROLE_VIEW')")
	@PostFilter("hasPermission(returnObject, 'PRACTITIONER_ROLE_VIEW')")
	public List<PractitionerRole> getAllPractitionerRoles(PractitionerRoleSearchBean practitionerRoleSearchBean) {
		return getPractitionerRoleRepository().getAllPractitionerRoles(practitionerRoleSearchBean);
	}
	
	@PreAuthorize("(hasPermission(#practitionerRole,'PractitionerRole', 'PRACTITIONER_ROLE_VIEW') and "
	        + "hasPermission(#practitionerRole,'PractitionerRole', 'PRACTITIONER_ROLE_CREATE') and "
	        + "hasPermission(#practitionerRole,'PractitionerRole', 'PRACTITIONER_ROLE_UPDATE'))")
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
	
	@PreAuthorize("hasPermission(#practitionerIdentifier,'PractitionerRole', 'PRACTITIONER_ROLE_DELETE')")
	public void deletePractitionerRole(PractitionerRole practitionerRole) {
		if (StringUtils.isBlank(practitionerRole.getIdentifier())) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		getPractitionerRoleRepository().safeRemove(practitionerRole);
	}
	
	@PreAuthorize("hasPermission(#identifier,'PractitionerRole', 'PRACTITIONER_ROLE_DELETE')")
	public void deletePractitionerRole(String identifier) {
		if (StringUtils.isBlank(identifier)) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		getPractitionerRoleRepository().safeRemove(identifier);
	}
	
	@PreAuthorize("hasPermission(#practitionerIdentifier,'PractitionerRole', 'PRACTITIONER_ROLE_DELETE')")
	public void deletePractitionerRole(String organizationIdentifier, String practitionerIdentifier) {
		if (StringUtils.isBlank(organizationIdentifier) || StringUtils.isBlank(practitionerIdentifier)) {
			throw new IllegalArgumentException("Organization Identifier or Practitioner Identifier not specified");
		}
		
		Organization organization = organizationService.getOrganization(organizationIdentifier);
		
		Practitioner pgPractitioner = practitionerService.getPgPractitioner(practitionerIdentifier);
		
		if (organization == null || pgPractitioner == null) {
			return;
		}
		
		getPractitionerRoleRepository().safeRemove(organization.getId(), pgPractitioner.getId());
	}
	
	@PreAuthorize("hasRole('PRACTITIONER_ROLE_VIEW')")
	@PostFilter("hasPermission(filterObject, 'PRACTITIONER_ROLE_VIEW')")
	public List<PractitionerRole> getRolesForPractitioner(String practitionerIdentifier) {
		if (StringUtils.isBlank(practitionerIdentifier)) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		return getPractitionerRoleRepository().getRolesForPractitioner(practitionerIdentifier);
	}
	
	@PreAuthorize("hasRole('PRACTITIONER_ROLE_VIEW')")
	@PostFilter("hasPermission(filterObject, 'PRACTITIONER_ROLE_VIEW')")
	public List<org.opensrp.domain.postgres.PractitionerRole> getPgRolesForPractitioner(String practitionerIdentifier) {
		if (StringUtils.isBlank(practitionerIdentifier)) {
			throw new IllegalArgumentException("Identifier not specified");
		}
		
		return getPractitionerRoleRepository().getPgRolesForPractitioner(practitionerIdentifier);
	}
	
	@PreAuthorize("hasPermission(#identifier,'PractitionerRole', 'PRACTITIONER_ROLE_ASSIGN')")
	public void assignPractitionerRole(Long organizationId, String practitionerIdentifier, String code,
	        PractitionerRole practitionerRole) {
		validateIdentifier(practitionerRole.getIdentifier());
		Long practitionerId = practitionerService.getPractitionerIdByIdentifier(practitionerIdentifier);
		if (practitionerId == null) {
			throw new IllegalArgumentException("Practitioner not found by the identifier : " + practitionerIdentifier);
		}
		practitionerRoleRepository.assignPractitionerRole(organizationId, practitionerId, practitionerIdentifier, code,
		    practitionerRole);
	}
	
	public void validateIdentifier(String identifier) {
		if (StringUtils.isBlank(identifier))
			throw new IllegalArgumentException("Practitioner Role Identifier not specified");
	}
	

    public List<PractitionerRole> getPractitionerRolesByOrgIdAndCode(Long organizationId, String code) {
	    return practitionerRoleRepository.getPractitionerRolesByOrgIdAndCode(organizationId, code);
    }
}
