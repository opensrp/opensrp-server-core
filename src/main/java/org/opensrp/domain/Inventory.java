package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Inventory {

	@JsonProperty
	private String productName;

	@JsonProperty
	private String unicefSection;

	@JsonProperty
	private int quantity;

	@JsonProperty
	private Date deliveryDate;

	@JsonProperty
	private String donor;

	@JsonProperty
	private String servicePointId;

	@JsonProperty
	private Integer poNumber;

	@JsonProperty
	private String serialNumber;

}
