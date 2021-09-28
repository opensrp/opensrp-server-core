package org.opensrp.repository;

import org.smartregister.domain.Practitioner;
import org.opensrp.search.PractitionerSearchBean;

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

	/** Get the practitioner using username
	 * @param username to get 
	 * @return practitioner
	 */
	Practitioner getPractitionerByUsername(String username);

	List<Practitioner> getAllPractitioners(PractitionerSearchBean practitionerSearchBean);

	List<Practitioner> getAllPractitionersByIdentifiers(List<String> practitionerIdentifiers);

	long countAllPractitioners();

	Practitioner getPractitionerByIdentifier(String practitionerIdentifier);
}
