package org.opensrp.generator;

import org.opensrp.domain.IdentifierSource;
import org.opensrp.domain.UniqueId;
import org.opensrp.repository.UniqueIdRepository;
import org.opensrp.util.IdGeneratorUtil;
import org.opensrp.util.IdentifierValidatorAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UniqueIdGeneratorProcessor {
	
	@Autowired
	private UniqueIdRepository uniqueIdRepository;

	public synchronized List<String> getIdentifiers(IdentifierSource identifierSource, int batchSize, String usedBy) {

		UniqueId lastUniqueId = uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(identifierSource.getIdentifier());

		Long sequenceValue = lastUniqueId != null ? lastUniqueId.getId() : null;
		if (sequenceValue == null || sequenceValue < 0) {
			if (identifierSource.getFirstIdentifierBase() != null) {
				sequenceValue = IdGeneratorUtil.convertFromBase(identifierSource.getFirstIdentifierBase(),
						identifierSource.getBaseCharacterSet().toCharArray());
			} else {
				sequenceValue = 1L;
			}
		}

		Set<String> reservedIdentifiers = uniqueIdRepository.findReservedIdentifiers();

		List<String> identifiers = new ArrayList<String>();
		int numbersToGenerate = batchSize;
		String identifier = "";
		LuhnIdentifierValidator luhnIdentifierValidator = new LuhnIdentifierValidator();
		String prefix = identifierSource.getPrefix() != null ? identifierSource.getPrefix() : "";
		String suffix = identifierSource.getSuffix() != null ? identifierSource.getSuffix() : "";
		String firstIdentifierBase = identifierSource.getFirstIdentifierBase()!= null ? identifierSource.getFirstIdentifierBase() : "";

		if (identifierSource.getFirstIdentifierBase() != null || !identifierSource.getFirstIdentifierBase().equals("")) {
			numbersToGenerate = batchSize - 1;
			identifiers.add(luhnIdentifierValidator
					.getValidIdentifier(prefix + firstIdentifierBase + suffix, identifierSource));
		}

		for (int i = 0; i < numbersToGenerate; ) {
			identifier = getIdentifierForSeed(sequenceValue, identifierSource);
			if (!reservedIdentifiers.contains(identifier)) {
				if (identifierSource.getIdentifierValidatorAlgorithm()
						.equals(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM)) {
					identifier = luhnIdentifierValidator.getValidIdentifier(identifier, identifierSource);
				}
				identifiers.add(identifier);
				i++;
			}
			sequenceValue++;
		}

		if (identifiers.size() == batchSize) {
			saveIds(identifiers, null, "not_used", new Date(), usedBy, new Date(), identifierSource.getId());
		}

		return identifiers;
	}

	public String getIdentifierForSeed(long seed, IdentifierSource identifierSource) {
		// Convert the next sequence integer into a String with the appropriate Base characters
		int seqLength =
				identifierSource.getFirstIdentifierBase() == null ? 1 : identifierSource.getFirstIdentifierBase().length();

		String identifier = IdGeneratorUtil
				.convertToBase(seed, identifierSource.getBaseCharacterSet().toCharArray(), seqLength,
						identifierSource.getMinLength());

		identifier = identifierSource.getPrefix() == null ? identifier : identifierSource.getPrefix() + identifier;
		identifier = (identifierSource.getSuffix() == null ? identifier : identifier + identifierSource.getSuffix());

		if (identifierSource.getMinLength() != null && identifierSource.getMinLength() > 0) {
			if (identifier.length() < identifierSource.getMinLength()) {
				throw new RuntimeException(
						"Invalid configuration for IdentifierSource. Length minimum set to " + identifierSource
								.getMinLength() + " but generated " + identifier);
			}
		}

		if (identifierSource.getMaxLength() != null && identifierSource.getMaxLength() > 0) {
			if (identifier.length() > identifierSource.getMaxLength()) {
				throw new RuntimeException(
						"Invalid configuration for IdentifierSource. Length maximum set to " + identifierSource
								.getMaxLength() + " but generated " + identifier);
			}
		}
		if (identifierSource.getSkipRegexFormat() != null) {
			return identifier.matches(identifierSource.getSkipRegexFormat()) ?
					identifier :
					getIdentifierForSeed(seed + 1, identifierSource);
		}
		return identifier;
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
