package org.opensrp.generator;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.IdentifierSource;
import org.opensrp.domain.UniqueId;
import org.opensrp.repository.UniqueIdRepository;
import org.opensrp.util.IdGeneratorUtil;
import org.opensrp.util.IdentifierValidatorAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UniqueIdGeneratorProcessor {

	private static Logger logger = LoggerFactory.getLogger(UniqueIdGeneratorProcessor.class.toString());

	@Autowired
	private UniqueIdRepository uniqueIdRepository;

	public UniqueIdGeneratorProcessor(UniqueIdRepository uniqueIdRepository) {
		this.uniqueIdRepository = uniqueIdRepository;
	}

	public synchronized List<String> getIdentifiers(IdentifierSource identifierSource, int batchSize, String usedBy) {

		UniqueId lastUniqueId = uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(identifierSource.getId());

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

		if (identifierSource.getFirstIdentifierBase() != null) {
			numbersToGenerate = batchSize - 1;
			identifiers.add(luhnIdentifierValidator
					.getValidIdentifier(prefix + firstIdentifierBase + suffix, identifierSource));
		}

		String previousId = "";

		for (int i = 0; i < numbersToGenerate; ) {
			identifier = getIdentifierForSeed(sequenceValue, identifierSource);
			logger.info("Identifier from processor is " +  identifier);
			if(identifier == null) {
				break;
			}
			if (!reservedIdentifiers.contains(identifier)) {
				if (identifierSource.getIdentifierValidatorAlgorithm()
						.equals(IdentifierValidatorAlgorithm.LUHN_CHECK_DIGIT_ALGORITHM)) {
					identifier = luhnIdentifierValidator.getValidIdentifier(identifier, identifierSource);
				}
				if (!identifier.equals(previousId)) {
					identifiers.add(identifier);
					previousId = identifier;
				}
				i++;
			}
			sequenceValue++;
		}

		saveIds(identifiers, null, "not_used", new Date(), usedBy, new Date(), identifierSource.getId());

		return identifiers;
	}

	public String getIdentifierForSeed(long seed, IdentifierSource identifierSource) {
		// Convert the next sequence integer into a String with the appropriate Base characters
		int seqLength =
				identifierSource.getFirstIdentifierBase() == null ? 1 : identifierSource.getFirstIdentifierBase().length();

		if(seqLength == 1) {
			seqLength = identifierSource.getMinLength();
		}

		String identifier = IdGeneratorUtil
				.convertToBase(seed, identifierSource.getBaseCharacterSet().toCharArray(), seqLength,
						identifierSource.getMinLength());

		if (((identifierSource.getMinLength() != null && identifierSource.getMinLength() > 0)
				|| (identifierSource.getMaxLength() != null && identifierSource.getMaxLength() > 0))
				&& ((identifier.length() < identifierSource.getMinLength()) || (identifier.length() > identifierSource
				.getMaxLength()))) {
			return null;
		}

		identifier = identifierSource.getPrefix() == null ? identifier : identifierSource.getPrefix() + identifier;
		identifier = (identifierSource.getSuffix() == null ? identifier : identifier + identifierSource.getSuffix());


		if (identifierSource.getRegexFormat() != null && StringUtils.isNotEmpty(identifierSource.getRegexFormat())) {
			return identifier.matches(identifierSource.getRegexFormat()) ?
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
