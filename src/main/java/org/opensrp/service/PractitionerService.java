package org.opensrp.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.opensrp.domain.Organization;
import org.opensrp.domain.Practitioner;
import org.opensrp.domain.postgres.PractitionerRole;
import org.opensrp.repository.PractitionerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class PractitionerService {
	
	private PractitionerRepository practitionerRepository;
	
	private PractitionerRoleService practitionerRoleService;
	
	private OrganizationService organizationService;
	
	@Autowired
	public PractitionerService(PractitionerRepository practitionerRepository,
	    PractitionerRoleService practitionerRoleService, OrganizationService organizationService) {
		this.practitionerRepository = practitionerRepository;
		this.practitionerRoleService = practitionerRoleService;
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
	
	public List<Practitioner> getAllPractitioners() {
		return getPractitionerRepository().getAll();
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
	 * Gets practitioners in an organization
	 * 
	 * @param organizationId the identifier for organization
	 * @return practitioners in an organization
	 */
	
	public List<Practitioner> getPractitionersByOrgIdentifier(String organizationIdentifier) {
		organizationService.validateIdentifier(organizationIdentifier);
		Organization organization = organizationService.getOrganization(organizationIdentifier);
		
		if (organization == null) {
			throw new IllegalArgumentException("Organization does not exist");
		}
		
		return getPractitionerRepository().getPractitionersByOrgId(organization.getId());
	}
	
	/**
	 * Gets the organization ids that a user is assigned to according to the plan location
	 * assignment
	 *
	 * @param username the username of user
	 * @return the organization ids a user is assigned to
	 */
	@PreAuthorize("hasRole('PLAN_GET') and hasPermission(#username,'User', 'GET')")
	public List<Long> getOrganizationIdsByUserName(String username) {
		org.opensrp.domain.Practitioner practitioner = getPractionerByUsername(username);
		if (practitioner != null) {
			List<PractitionerRole> roles = practitionerRoleService.getPgRolesForPractitioner(practitioner.getIdentifier());
			if (roles.isEmpty())
				return null;
			List<Long> organizationIds = new ArrayList<>();
			for (PractitionerRole role : roles)
				organizationIds.add(role.getOrganizationId());
			return organizationIds;
		}
		
		return null;
	}
	
}
