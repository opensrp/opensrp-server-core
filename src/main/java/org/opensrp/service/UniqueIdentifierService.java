package org.opensrp.service;


import org.opensrp.domain.IdentifierSource;
import org.opensrp.generator.UniqueIdGeneratorProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UniqueIdentifierService {

	@Autowired
	private UniqueIdGeneratorProcessor uniqueIdGeneratorProcessor;

	public UniqueIdentifierService(UniqueIdGeneratorProcessor uniqueIdGeneratorProcessor) {
		this.uniqueIdGeneratorProcessor = uniqueIdGeneratorProcessor;
	}

	@PreAuthorize("hasRole('IDENTIFIER_CREATE')")
	@Transactional
	public List<String> generateIdentifiers(IdentifierSource identifierSource, int numberOfIdsToGenerate, String usedBy) {

		try {
			List<String> identifiers = uniqueIdGeneratorProcessor
					.getIdentifiers(identifierSource, numberOfIdsToGenerate, usedBy);
			return identifiers;
		}
		catch (Exception ex) {
			throw new IllegalArgumentException(
					ex.getMessage());
		}
	}

	public void markIdentifierAsUsed (String identifier) {
		uniqueIdGeneratorProcessor.markIdentifierAsUsed(identifier);
	}
}
