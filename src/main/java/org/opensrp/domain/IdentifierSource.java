package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opensrp.util.IdentifierValidatorAlgorithm;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class IdentifierSource {

	@JsonProperty
	private Long id;

	@JsonProperty
	private String identifier;

	@JsonProperty
	private String description;

	@Enumerated(EnumType.STRING)
	@JsonProperty
	private IdentifierValidatorAlgorithm identifierValidatorAlgorithm;

	@JsonProperty
	private String baseCharacterSet;

	@JsonProperty
	private String firstIdentifierBase;

	@JsonProperty
	private String prefix;

	@JsonProperty
	private String suffix;

	@JsonProperty
	private Integer minLength;

	@JsonProperty
	private Integer maxLength;

	@JsonProperty
	private String blacklisted;

	public IdentifierSource() {

	}

	public IdentifierSource(String identifier, String description, IdentifierValidatorAlgorithm identifierValidatorAlgorithm,
			String baseCharacterSet, String firstIdentifierBase, String prefix, String suffix, Integer minLength,
			Integer maxLength, String blacklisted) {
		this.identifier = identifier;
		this.description = description;
		this.identifierValidatorAlgorithm = identifierValidatorAlgorithm;
		this.baseCharacterSet = baseCharacterSet;
		this.firstIdentifierBase = firstIdentifierBase;
		this.prefix = prefix;
		this.suffix = suffix;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.blacklisted = blacklisted;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public IdentifierValidatorAlgorithm getIdentifierValidatorAlgorithm() {
		return identifierValidatorAlgorithm;
	}

	public void setIdentifierValidatorAlgorithm(IdentifierValidatorAlgorithm identifierValidatorAlgorithm) {
		this.identifierValidatorAlgorithm = identifierValidatorAlgorithm;
	}

	public String getBaseCharacterSet() {
		return baseCharacterSet;
	}

	public void setBaseCharacterSet(String baseCharacterSet) {
		this.baseCharacterSet = baseCharacterSet;
	}

	public String getFirstIdentifierBase() {
		return firstIdentifierBase;
	}

	public void setFirstIdentifierBase(String firstIdentifierBase) {
		this.firstIdentifierBase = firstIdentifierBase;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Integer getMinLength() {
		return minLength;
	}

	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	public String getBlacklisted() {
		return blacklisted;
	}

	public void setBlacklisted(String blacklisted) {
		this.blacklisted = blacklisted;
	}
}
