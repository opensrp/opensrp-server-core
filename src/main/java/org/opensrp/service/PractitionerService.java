package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.opensrp.domain.Practitioner;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.repository.PractitionerRepository;
import org.opensrp.search.PractitionerSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class PractitionerService {
	
	private PractitionerRepository practitionerRepository;
	
	private PractitionerRoleService practitionerRoleService;
	
	@Autowired
	public PractitionerService(PractitionerRepository practitionerRepository,
	    PractitionerRoleService practitionerRoleService) {
		this.practitionerRepository = practitionerRepository;
		this.practitionerRoleService = practitionerRoleService;
	}
	
	public PractitionerRepository getPractitionerRepository() {
		return practitionerRepository;
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
			getPractitionerRepository().update(practitioner);
		} else {
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
	 * <<<<<<< HEAD ======= Gets practitioners in an organization
	 * 
	 * @param organizationId the identifier for organization
	 * @return practitioners in an organization
	 */

	
	/**
	 * Gets the organization ids that a user is assigned to according to the plan location
	 * assignment
	 *
	 * @param username the username of user
	 * @return the organization ids a user is assigned to
	 */
	@PreAuthorize("hasPermission(#username,'User', 'PRACTITIONER_VIEW')")
	public List<Long> getOrganizationIdsByUserName(String username) {
		org.opensrp.domain.Practitioner practitioner = getPractionerByUsername(username);
		if (practitioner != null) {
			return practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier()).stream()
			        .map(role -> role.getOrganizationId()).collect(Collectors.toList());
		}
		
		return null;
	}
	
	/**
	 * Gets the organization ids that a user is assigned to according to the plan location
	 * assignment
	 *
	 * @param username the username of user
	 * @return the organization ids a user is assigned to
	 */
	@PreAuthorize("hasPermission(#userId,'UserId', 'PRACTITIONER_VIEW')")
	public List<Long> getOrganizationIdsByUserId(String userId) {
		org.opensrp.domain.Practitioner practitioner = getPractitionerRepository().getPractitionerByUserId(userId);
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
}
