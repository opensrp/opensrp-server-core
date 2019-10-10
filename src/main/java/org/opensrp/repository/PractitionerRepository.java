package org.opensrp.repository;

import org.opensrp.domain.Practitioner;

import java.util.List;

public interface PractitionerRepository extends BaseRepository<Practitioner> {

    org.opensrp.domain.postgres.Practitioner getPractitioner(String id);

	/**
	 * @param userId
	 * @return
	 */
	Practitioner getPractitionerByUserId(String userId);

	Practitioner getByPrimaryKey(Long id);

	List<Practitioner> getPractitionersByOrgId(Long orgId);

	void safeRemove(String identifier);
}
