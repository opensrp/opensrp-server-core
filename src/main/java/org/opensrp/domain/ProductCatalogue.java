package org.opensrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.opensrp.util.ProductType;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductCatalogue {

	private Long uniqueId;

	private String productName;

	private ProductType productType;

	private ArrayList<String> sections;

	private String availability;

	private String condition;

	private String appropriateUsage;

	private Integer accountabilityPeriod;

	private String photoPath;

	private Long serverVersion;
}
