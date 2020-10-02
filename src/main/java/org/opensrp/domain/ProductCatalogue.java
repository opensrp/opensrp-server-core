package org.opensrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductCatalogue {

	private Long uniqueId;

	private String productName;

	private Boolean isAttractiveItem;

	private String serialId;

	private String availability;

	private String condition;

	private String appropriateUsage;

	private Integer accountabilityPeriod;

	private String photoPath;

	private Long serverVersion;
}
