package org.opensrp.search;

import lombok.Getter;
import lombok.Setter;
import org.opensrp.util.ProductType;

@Getter
@Setter
public class ProductCatalogueSearchBean {

	private Long uniqueId;

	private String productName;

	private ProductType productType;

	private Long serverVersion;
}
