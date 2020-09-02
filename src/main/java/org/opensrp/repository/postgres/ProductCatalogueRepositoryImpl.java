package org.opensrp.repository.postgres;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.ProductCatalogue;
import org.opensrp.domain.postgres.ProductCatalogueExample;
import org.opensrp.repository.ProductCatalogueRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomProductCatalogueMapper;
import org.opensrp.search.ProductCatalogueSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static org.opensrp.util.Utils.isEmptyList;

@Repository("productCatalogueRepositoryPostgres")
public class ProductCatalogueRepositoryImpl extends BaseRepositoryImpl<ProductCatalogue>
		implements ProductCatalogueRepository {

	@Autowired
	private CustomProductCatalogueMapper customProductCatalogueMapper;

	@Override
	public ProductCatalogue get(String id) {
		throw new NotImplementedException();
	}

	@Override
	public void add(ProductCatalogue entity) {

		if (entity == null) {
			return;
		}

		if (retrievePrimaryKey(entity) != null) { // ProductCatalogue already added
			return;
		}

		org.opensrp.domain.postgres.ProductCatalogue pgProductCatalogue = convert(entity, null);
		if (pgProductCatalogue == null) {
			return;
		}

		int rowsAffected = customProductCatalogueMapper.insertSelectiveAndSetId(pgProductCatalogue);
		if (rowsAffected < 1 || pgProductCatalogue.getUniqueId() == null) {
			return;
		}
	}

	@Override
	public void update(ProductCatalogue entity) {
		if (entity == null) {
			return;
		}
		Long id = retrievePrimaryKey(entity);

		if (id == null) { //Product Catalogues doesn't not exist
			return;
		}

		org.opensrp.domain.postgres.ProductCatalogue pgProductCatalogue = convert(entity, id);
		customProductCatalogueMapper.updateByPrimaryKey(pgProductCatalogue);
	}

	@Override
	public List<ProductCatalogue> getAll() {
		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		productCatalogueExample.createCriteria().andUniqueIdIsNotNull();
		List<org.opensrp.domain.postgres.ProductCatalogue> productCatalogues = customProductCatalogueMapper
				.selectMany(productCatalogueExample, 0, DEFAULT_FETCH_SIZE);
		return convert(productCatalogues);
	}

	@Override
	public void safeRemove(ProductCatalogue entity) {
		if (entity == null) {
			return;
		}

		Long uniqueId = retrievePrimaryKey(entity);
		if (uniqueId == null) {
			return;
		}

		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		productCatalogueExample.createCriteria().andUniqueIdEqualTo(uniqueId);
		//		int rowsAffected = customProductCatalogueMapper.deleteByExample(productCatalogueExample);
		int rowsAffected = customProductCatalogueMapper.deleteByPrimaryKey(uniqueId);
		if (rowsAffected < 1) {
			return;
		}

	}

	@Override
	protected Long retrievePrimaryKey(ProductCatalogue productCatalogue) {

		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		ProductCatalogueExample.Criteria criteria = productCatalogueExample.createCriteria();
		if (productCatalogue.getUniqueId() != null && productCatalogue.getUniqueId() != 0) {
			criteria.andUniqueIdEqualTo(productCatalogue.getUniqueId());
		} else {
			criteria.andProductNameEqualTo(productCatalogue.getProductName());
			if (productCatalogue.getProductType() != null) {
				criteria.andTypeEqualTo(productCatalogue.getProductType().name());
			}
		}

		org.opensrp.domain.postgres.ProductCatalogue pgProductCatalogue = customProductCatalogueMapper
				.selectOne(productCatalogueExample);
		if (pgProductCatalogue == null) {
			return null;
		}
		return pgProductCatalogue.getUniqueId();
	}

	@Override
	protected Object getUniqueField(ProductCatalogue productCatalogue) {
		if (productCatalogue == null) {
			return null;
		}
		return productCatalogue.getUniqueId();
	}

	@Override
	public ProductCatalogue getById(Long uniqueId) {
		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		productCatalogueExample.createCriteria().andUniqueIdEqualTo(uniqueId);

		List<org.opensrp.domain.postgres.ProductCatalogue> productCatalogues = customProductCatalogueMapper
				.selectByExample(productCatalogueExample);

		return isEmptyList(productCatalogues) ? null : convert(productCatalogues.get(0));
	}

	@Override
	public List<ProductCatalogue> getProductCataloguesBySearchBean(ProductCatalogueSearchBean productCatalogueSearchBean) {
		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		ProductCatalogueExample.Criteria criteria = populateProductCatalogueSearchCriteria(productCatalogueSearchBean,
				productCatalogueExample);
		return convert(customProductCatalogueMapper.selectMany(productCatalogueExample, 0, DEFAULT_FETCH_SIZE));
	}

	@Override
	public void safeRemove(Long uniqueId) {
		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		productCatalogueExample.createCriteria().andUniqueIdEqualTo(uniqueId);
		customProductCatalogueMapper.deleteByPrimaryKey(uniqueId);
	}

	private List<ProductCatalogue> convert(List<org.opensrp.domain.postgres.ProductCatalogue> productCatalogues) {
		if (productCatalogues == null || productCatalogues.isEmpty()) {
			return new ArrayList<>();
		}

		List<ProductCatalogue> convertedProductCatalogues = new ArrayList<>();
		for (org.opensrp.domain.postgres.ProductCatalogue productCatalogue : productCatalogues) {
			ProductCatalogue convertedProductCatalogue = convert(productCatalogue);
			if (convertedProductCatalogue != null) {
				convertedProductCatalogues.add(convertedProductCatalogue);
			}
		}

		return convertedProductCatalogues;
	}

	private ProductCatalogue convert(org.opensrp.domain.postgres.ProductCatalogue pgProductCatalogue) {
		if (pgProductCatalogue == null || pgProductCatalogue.getJson() == null || !(pgProductCatalogue
				.getJson() instanceof ProductCatalogue)) {
			return null;
		}
		ProductCatalogue productCatalogue = (ProductCatalogue) pgProductCatalogue.getJson();
		productCatalogue.setUniqueId(pgProductCatalogue.getUniqueId());
		return productCatalogue;
	}

	private org.opensrp.domain.postgres.ProductCatalogue convert(ProductCatalogue productCatalogue, Long primaryKey) {
		if (productCatalogue == null) {
			return null;
		}

		org.opensrp.domain.postgres.ProductCatalogue pgProductCatalogue = new org.opensrp.domain.postgres.ProductCatalogue();
		pgProductCatalogue.setUniqueId(primaryKey);
		pgProductCatalogue.setJson(productCatalogue);
		pgProductCatalogue.setProductName(productCatalogue.getProductName());
		pgProductCatalogue.setType(
				productCatalogue.getProductType() != null ? productCatalogue.getProductType().name() : null);
		pgProductCatalogue.setServerVersion(productCatalogue.getServerVersion());

		return pgProductCatalogue;
	}

	private ProductCatalogueExample.Criteria populateProductCatalogueSearchCriteria(
			ProductCatalogueSearchBean productCatalogueSearchBean,
			ProductCatalogueExample productCatalogueExample) {

		ProductCatalogueExample.Criteria criteria = productCatalogueExample.createCriteria();

		if (productCatalogueSearchBean.getServerVersion() != null && productCatalogueSearchBean.getServerVersion() != 0)
			criteria.andServerVersionGreaterThanOrEqualTo(productCatalogueSearchBean.getServerVersion());

		if (StringUtils.isNotEmpty(productCatalogueSearchBean.getProductName()))
			criteria.andProductNameEqualTo(productCatalogueSearchBean.getProductName());

		if (productCatalogueSearchBean.getProductType() != null && StringUtils
				.isNotEmpty(productCatalogueSearchBean.getProductType().name()))
			criteria.andTypeEqualTo(productCatalogueSearchBean.getProductType().name());

		if (productCatalogueSearchBean.getUniqueId() != null && productCatalogueSearchBean.getUniqueId() != 0)
			criteria.andUniqueIdEqualTo(productCatalogueSearchBean.getUniqueId());

		if (!criteria.isValid())
			throw new IllegalArgumentException("Atleast one search filter must be specified");

		return criteria;
	}
}
