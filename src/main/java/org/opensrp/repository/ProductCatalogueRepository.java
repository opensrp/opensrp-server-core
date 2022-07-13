package org.opensrp.repository;

import org.opensrp.search.ProductCatalogueSearchBean;
import org.smartregister.domain.ProductCatalogue;

import java.util.List;

public interface ProductCatalogueRepository extends BaseRepository<ProductCatalogue> {

    ProductCatalogue getById(Long uniqueId, String baseUrl);

    @Deprecated(since = "2.11.4-SNAPSHOT", forRemoval = true)
    List<ProductCatalogue> getProductCataloguesBySearchBean(ProductCatalogueSearchBean productCatalogueSearchBean, String baseUrl);

    List<ProductCatalogue> getProductCataloguesBySearchBean(ProductCatalogueSearchBean productCatalogueSearchBean, int limit, String baseUrl);

    void safeRemove(Long id);

    ProductCatalogue getProductCatalogueByName(String productName);

    List<ProductCatalogue> getAll(String baseUrl);

}
