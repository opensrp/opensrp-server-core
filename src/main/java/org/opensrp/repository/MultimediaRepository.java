package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.Multimedia;

public interface MultimediaRepository extends BaseRepository<Multimedia> {
	
	Multimedia findByCaseId(String entityId);
	
	List<Multimedia> all(String providerId);

	List<Multimedia> get(String entityId, String contentType, String fileCategory);
}
