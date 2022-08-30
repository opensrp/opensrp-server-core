package org.opensrp.domain.postgres;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PgStockAndProductDetails {

    private Stock stock;
    private ProductCatalogue productCatalogue;
}
