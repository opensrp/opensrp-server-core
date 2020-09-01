package org.opensrp.repository;

import org.opensrp.domain.ProductCatalogue;
import org.opensrp.search.ProductCatalogueSearchBean;

import java.util.List;

public interface ProductCatalogueRepository extends BaseRepository<ProductCatalogue> {

	ProductCatalogue getById(Long uniqueId);

	List<ProductCatalogue> findProductCataloguesByVersion(Long lastSyncedServerVersion);

	List<ProductCatalogue> getProductCataloguesBySearchBean(ProductCatalogueSearchBean productCatalogueSearchBean);

	void safeRemove(Long id);

}
