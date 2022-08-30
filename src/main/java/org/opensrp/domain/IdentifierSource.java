package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.opensrp.util.IdentifierValidatorAlgorithm;


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
    private String regexFormat;

    @JsonIgnore
    private Long sequenceValue;

}
