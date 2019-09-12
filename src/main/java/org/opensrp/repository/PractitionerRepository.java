package org.opensrp.repository;

import org.opensrp.domain.Practitioner;

public interface PractitionerRepository extends BaseRepository<Practitioner> {

    org.opensrp.domain.postgres.Practitioner getPractitioner(String id);

	/**
	 * @param userId
	 * @return
	 */
	Practitioner getPractitionerByUserId(String userId);
}
