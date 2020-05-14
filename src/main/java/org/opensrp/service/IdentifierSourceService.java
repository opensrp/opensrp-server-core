package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.repository.IdentifierSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdentifierSourceService {

	private IdentifierSourceRepository identifierSourceRepository;

	@Autowired
	public IdentifierSourceService(IdentifierSourceRepository identifierSourceRepository) {
		this.identifierSourceRepository = identifierSourceRepository;
	}

	public List<IdentifierSource> findAllIdentifierSources() {
		return identifierSourceRepository.getAll();
	}

	public IdentifierSource findByIdentifier(String identifier) {
		return identifierSourceRepository.findByIdentifier(identifier);
	}

	//	public void add(IdentifierSource identifierSource) {
	//		identifierSourceRepository.add(identifierSource);
	//	}
	//
	//	public void update(IdentifierSource updatedIdentifierSource) throws JSONException {
	//		identifierSourceRepository.update(updatedIdentifierSource);
	//	}

	public void addOrUpdate(IdentifierSource identifierSource) {
		if (StringUtils.isBlank(identifierSource.getIdentifier())) {
			throw new IllegalArgumentException("Identifier value was not specified");
		}

		if (StringUtils.isBlank(identifierSource.getBaseCharacterSet())) {
			throw new IllegalArgumentException("Base character set was not specified");
		}

		if (identifierSource.getMinLength() == null || identifierSource.getMinLength() == 0) {
			throw new IllegalArgumentException("Minimum length was not specified");
		}

		if (identifierSource.getMaxLength() == null || identifierSource.getMaxLength() == 0) {
			throw new IllegalArgumentException("Maximum length was not specified");
		}

		if (identifierSource.getId() != null && identifierSource.getId() != 0) {
			identifierSourceRepository.update(identifierSource);
		} else {
			identifierSourceRepository.add(identifierSource);
		}
	}
}

