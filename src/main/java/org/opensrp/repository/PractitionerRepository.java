package org.opensrp.repository;

import org.opensrp.domain.PractitionerDefinition;
import org.opensrp.domain.postgres.Practitioner;

public interface PractitionerRepository extends BaseRepository<PractitionerDefinition> {

    Practitioner getPractitioner(String id);
}
