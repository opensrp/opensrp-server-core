package org.opensrp.repository;

import org.opensrp.search.PractitionerRoleSearchBean;
import org.smartregister.domain.PractitionerRole;

import java.util.List;

public interface PractitionerRoleRepository extends BaseRepository<PractitionerRole> {

    List<PractitionerRole> getRolesForPractitioner(String practitionerIdentifier);

    List<org.opensrp.domain.postgres.PractitionerRole> getPgRolesForPractitioner(String practitionerIdentifier);

    org.opensrp.domain.postgres.PractitionerRole getPractitionerRole(String id);

    List<org.opensrp.domain.postgres.PractitionerRole> getPractitionerRole(Long organizationId, Long practitionerId);

    void safeRemove(String identifier);

    void safeRemove(Long organizationId, Long practitionerId);

    void assignPractitionerRole(Long organizationId, Long practitionerId, String practitionerIdentifier, String code,
                                PractitionerRole practitionerRole);

    List<PractitionerRole> getAllPractitionerRoles(PractitionerRoleSearchBean practitionerRoleSearchBean);

    List<PractitionerRole> getPractitionerRolesByOrgIdAndCode(Long organizationId, String code);

    long countAllPractitionerRoles();
}
