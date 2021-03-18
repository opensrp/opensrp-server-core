package org.opensrp.repository;

import org.smartregister.domain.PractitionerRole;
import org.opensrp.search.PractitionerRoleSearchBean;

import java.util.List;

public interface PractitionerRoleRepository extends BaseRepository<PractitionerRole> {

    List<PractitionerRole>  getRolesForPractitioner(String practitionerIdentifier);

    List<org.opensrp.domain.postgres.PractitionerRole>  getPgRolesForPractitioner(String practitionerIdentifier);

    public org.opensrp.domain.postgres.PractitionerRole getPractitionerRole(String id);

    public List<org.opensrp.domain.postgres.PractitionerRole> getPractitionerRole(Long organizationId, Long practitionerId);

    void safeRemove(String identifier);

    void safeRemove(Long organizationId, Long practitionerId);

    void assignPractitionerRole(Long organizationId,Long practitionerId, String practitionerIdentifier, String code,
            PractitionerRole practitionerRole);

    List<PractitionerRole> getAllPractitionerRoles(PractitionerRoleSearchBean practitionerRoleSearchBean);

	public List<PractitionerRole> getPractitionerRolesByOrgIdAndCode(Long organizationId, String code);
}
