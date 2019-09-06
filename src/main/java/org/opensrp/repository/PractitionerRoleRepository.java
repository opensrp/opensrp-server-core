package org.opensrp.repository;

import org.opensrp.domain.PractitionerRoleDefinition;
import org.opensrp.domain.postgres.PractitionerRole;

import java.util.List;

public interface PractitionerRoleRepository extends BaseRepository<PractitionerRoleDefinition> {

    List<PractitionerRoleDefinition>  getRolesForPractitioner(String practitionerIdentifier);

    public PractitionerRole getPractitionerRole(String id);
}
