package org.opensrp.repository;

import org.smartregister.domain.ProductCatalogue;
import org.opensrp.search.ProductCatalogueSearchBean;

import java.util.List;

public interface ProductCatalogueRepository extends BaseRepository<ProductCatalogue> {

	ProductCatalogue getById(Long uniqueId, String baseUrl);

	List<ProductCatalogue> getProductCataloguesBySearchBean(ProductCatalogueSearchBean productCatalogueSearchBean, String baseUrl);

	void safeRemove(Long id);

	ProductCatalogue getProductCatalogueByName(String productName);

	List<ProductCatalogue> getAll(String baseUrl);

}
