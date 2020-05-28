package org.opensrp.service;


import org.opensrp.domain.IdentifierSource;
import org.opensrp.domain.UniqueId;
import org.opensrp.generator.UniqueIdGeneratorProcessor;
import org.opensrp.repository.SettingRepository;
import org.opensrp.repository.UniqueIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class UniqueIdentifierService {

	@Autowired
	UniqueIdRepository uniqueIdRepository;

	@Autowired
	SettingRepository settingRepository;

	@Autowired
	private UniqueIdGeneratorProcessor uniqueIdGeneratorProcessor;

	@Transactional
	public List<String> generateIdentifiers(IdentifierSource identifierSource, int numberOfIdsToGenerate, String usedBy) {

		try {
			List<String> identifiers = uniqueIdGeneratorProcessor
					.getIdentifiers(identifierSource, numberOfIdsToGenerate, usedBy);
			return identifiers;
		}
		catch (Exception ex) {
			return null;
		}

	}

	private void saveIds(List<String> ids, String location, String status, Date updatedAt, String usedBy,
			Date createdAt, Long idSource) {
		for (int i = 0; i < ids.size(); i++) {
			UniqueId uniqueId = new UniqueId("", status, usedBy, location, createdAt, updatedAt, ids.get(i), idSource,
					Boolean.FALSE);
			uniqueIdRepository.add(uniqueId);
		}
	}
}
