package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartregister.domain.ProductCatalogue;
import org.opensrp.repository.ProductCatalogueRepository;
import org.opensrp.search.ProductCatalogueSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCatalogueService {

    private static Logger logger = LogManager.getLogger(ProductCatalogueService.class.toString());
    private ProductCatalogueRepository productCatalogueRepository;

    @Autowired
    public ProductCatalogueService(ProductCatalogueRepository productCatalogueRepository) {
        this.productCatalogueRepository = productCatalogueRepository;
    }

    public List<ProductCatalogue> findAllProductCatalogues(String baseUrl) {
        return productCatalogueRepository.getAll(baseUrl);
    }

    public ProductCatalogue getProductCatalogue(Long uniqueId, String baseUrl) {
        return uniqueId == 0 ? null : productCatalogueRepository.getById(uniqueId, baseUrl);
    }

    public void add(ProductCatalogue productCatalogue) {
        validateFields(productCatalogue);
        productCatalogueRepository.add(productCatalogue);
    }

    public void update(ProductCatalogue productCatalogue) {
        validateFields(productCatalogue);
        productCatalogueRepository.update(productCatalogue);
    }

    public void deleteProductCatalogue(ProductCatalogue productCatalogue) {
        if (StringUtils.isBlank(productCatalogue.getProductName())) {
            throw new IllegalArgumentException("Product name not specified");
        }

        productCatalogueRepository.safeRemove(productCatalogue);

    }

    public void deleteProductCatalogueById(Long uniqueId) {
        if (uniqueId == 0) {
            throw new IllegalArgumentException("Id not specified");
        }

        productCatalogueRepository.safeRemove(uniqueId);

    }

    public List<ProductCatalogue> getProductCatalogues(ProductCatalogueSearchBean productCatalogueSearchBean, int limit, String baseUrl) {
        if (StringUtils.isBlank(productCatalogueSearchBean.getProductName()) &&
                productCatalogueSearchBean.getUniqueId() == 0
                && productCatalogueSearchBean.getServerVersion() == null) {
            return findAllProductCatalogues(baseUrl);
        } else {
            return productCatalogueRepository.getProductCataloguesBySearchBean(productCatalogueSearchBean, limit, baseUrl);
        }
    }

    @Deprecated(since = "2.11.4-SNAPSHOT")
    public List<ProductCatalogue> getProductCatalogues(ProductCatalogueSearchBean productCatalogueSearchBean, String baseUrl) {
        return getProductCatalogues(productCatalogueSearchBean, Integer.MAX_VALUE, baseUrl);
    }

    public ProductCatalogue getProductCatalogueByName(String productName) {
        return productCatalogueRepository.getProductCatalogueByName(productName);
    }

    private void validateFields(ProductCatalogue productCatalogue) {
        if (StringUtils.isBlank(productCatalogue.getProductName())) {
            throw new IllegalArgumentException("Product Name was not specified");
        } else if (productCatalogue.getIsAttractiveItem() == null) {
            throw new IllegalArgumentException("Is attractive item selection was not made");
        } else if (productCatalogue.getIsAttractiveItem() && StringUtils.isBlank(productCatalogue.getMaterialNumber())) {
            throw new IllegalArgumentException("Material Number was not specified");
        } else if (StringUtils.isBlank(productCatalogue.getAvailability())) {
            throw new IllegalArgumentException("The availability text was not specified");
        } else if (productCatalogue.getAccountabilityPeriod() == null) {
            throw new IllegalArgumentException("Accountability period was not specified");
        } else {
            logger.info("All validations on fields passed");
        }
    }
}
