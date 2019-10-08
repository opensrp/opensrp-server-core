package org.opensrp.repository;

import org.opensrp.domain.PractitionerRole;

import java.util.List;

public interface PractitionerRoleRepository extends BaseRepository<PractitionerRole> {

    List<PractitionerRole>  getRolesForPractitioner(String practitionerIdentifier);

    List<org.opensrp.domain.postgres.PractitionerRole>  getPgRolesForPractitioner(String practitionerIdentifier);

    public org.opensrp.domain.postgres.PractitionerRole getPractitionerRole(String id);

    public List<org.opensrp.domain.postgres.PractitionerRole> getPractitionerRole(Long organizationId, Long practitionerId);

    void safeRemove(String identifier);

    void safeRemove(Long organizationId, Long practitionerId);
}
