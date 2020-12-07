package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductCatalogue {

	@JsonProperty(value ="uniqueId",required = true)
	private Long uniqueId;

	@JsonProperty(value ="productName",required = true)
	private String productName;

	@JsonProperty(value ="isAttractiveItem",required = true)
	private Boolean isAttractiveItem;

	@JsonProperty(value ="materialNumber",required = false)
	private String materialNumber;

	@JsonProperty(value ="availability",required = true)
	private String availability;

	@JsonProperty(value ="condition",required = false)
	private String condition;

	@JsonProperty(value ="appropriateUsage",required = false)
	private String appropriateUsage;

	@JsonProperty(value ="accountabilityPeriod",required = true)
	private Integer accountabilityPeriod;

	@JsonProperty(value ="photoURL",required = false)
	private String photoURL = "";

	@JsonProperty(value ="serverVersion",required = false)
	private Long serverVersion;
}
