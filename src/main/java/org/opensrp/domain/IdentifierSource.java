package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.opensrp.util.IdentifierValidatorAlgorithm;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
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
	private String skipRegexFormat;
	
	public IdentifierSource(String identifier, String description, IdentifierValidatorAlgorithm identifierValidatorAlgorithm,
			String baseCharacterSet, String firstIdentifierBase, String prefix, String suffix, Integer minLength,
			Integer maxLength, String skipRegexFormat) {
		this.identifier = identifier;
		this.description = description;
		this.identifierValidatorAlgorithm = identifierValidatorAlgorithm;
		this.baseCharacterSet = baseCharacterSet;
		this.firstIdentifierBase = firstIdentifierBase;
		this.prefix = prefix;
		this.suffix = suffix;
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.skipRegexFormat = skipRegexFormat;
	}
}
