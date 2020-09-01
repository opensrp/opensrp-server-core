package org.opensrp.service;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.ProductCatalogue;
import org.opensrp.repository.ProductCatalogueRepository;
import org.opensrp.search.ProductCatalogueSearchBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCatalogueService {

	private ProductCatalogueRepository productCatalogueRepository;

	private static Logger logger = LoggerFactory.getLogger(ProductCatalogueService.class.toString());

	@Autowired
	public ProductCatalogueService(ProductCatalogueRepository productCatalogueRepository) {
		this.productCatalogueRepository = productCatalogueRepository;
	}

	public List<ProductCatalogue> findAllSupplyCatalogs() {
		return productCatalogueRepository.getAll();
	}

	public ProductCatalogue getProductCatalogue(Long uniqueId) {
		return uniqueId == 0 ? null : productCatalogueRepository.getById(uniqueId);
	}

	public List<ProductCatalogue> findProductCataloguesByVersion(Long lastSyncedServerVersion) {
		return productCatalogueRepository.findProductCataloguesByVersion(lastSyncedServerVersion);
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

	public List<ProductCatalogue> getProductCatalogues(ProductCatalogueSearchBean productCatalogueSearchBean) {
		if (StringUtils.isBlank(productCatalogueSearchBean.getProductName()) && productCatalogueSearchBean.getProductType() != null
				&& StringUtils.isBlank(productCatalogueSearchBean.getProductType().name()) && productCatalogueSearchBean.getUniqueId() == 0
				&& productCatalogueSearchBean.getServerVersion() == 0) {
			return findAllSupplyCatalogs();
		}
		else {
			return productCatalogueRepository.getProductCataloguesBySearchBean(productCatalogueSearchBean);
		}
	}

	private void validateFields(ProductCatalogue productCatalogue) {
		if (StringUtils.isBlank(productCatalogue.getProductName())) {
			throw new IllegalArgumentException("Product Name was not specified");
		} else if (productCatalogue.getProductType() != null && StringUtils.isBlank(
				productCatalogue.getProductType().name())) {
			throw new IllegalArgumentException("Supply Product Type was not specified");
		} else if (productCatalogue.getSections() == null || productCatalogue.getSections().size() == 0) {
			throw new IllegalArgumentException("Unicef sections was not specified");
		} else if (StringUtils.isBlank(productCatalogue.getAvailability())) {
			throw new IllegalArgumentException("The availability text was not specified");
		} else if (StringUtils.isBlank(productCatalogue.getWorking())) {
			throw new IllegalArgumentException("The working text was not specified");
		} else if (StringUtils.isBlank(productCatalogue.getAppropriateUsage())) {
			throw new IllegalArgumentException("The product approprate usage text was not specified");
		} else if (productCatalogue.getAccountabilityPeriod() == null) {
			throw new IllegalArgumentException("Accountability period was not specified");
		} else {
			logger.info("All validations on fields passed");
		}
	}
}
