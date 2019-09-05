package org.opensrp.repository;

import org.opensrp.domain.postgres.PractitionerRole;

import java.util.List;

public interface PractitionerRoleRepository extends BaseRepository<PractitionerRole> {

    List<PractitionerRole> getRolesForPractitioner(Long practitionerId);
}
