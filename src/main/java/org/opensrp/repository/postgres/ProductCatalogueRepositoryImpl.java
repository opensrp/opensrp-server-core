package org.opensrp.repository.postgres;

import static org.opensrp.util.Utils.isEmptyList;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.postgres.ProductCatalogueExample;
import org.opensrp.repository.ProductCatalogueRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomProductCatalogueMapper;
import org.opensrp.search.ProductCatalogueSearchBean;
import org.smartregister.domain.ProductCatalogue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("productCatalogueRepositoryPostgres")
public class ProductCatalogueRepositoryImpl extends BaseRepositoryImpl<ProductCatalogue>
		implements ProductCatalogueRepository {

	@Autowired
	private CustomProductCatalogueMapper customProductCatalogueMapper;

	@Override
	public ProductCatalogue get(String id) {
		throw new NotImplementedException();
	}

	@Transactional
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
			throw new IllegalStateException();
		}

		int rowsAffected = customProductCatalogueMapper.insertSelectiveAndSetId(pgProductCatalogue);
		if (rowsAffected < 1 || pgProductCatalogue.getUniqueId() == null) {
			throw new IllegalStateException();
		}
		
		updateServerVersion(pgProductCatalogue, entity);
	}

	private void updateServerVersion(org.opensrp.domain.postgres.ProductCatalogue pgProductCatalog, ProductCatalogue entity) {
		Long serverVersion = customProductCatalogueMapper.selectServerVersionByPrimaryKey(pgProductCatalog.getUniqueId());
		entity.setServerVersion(serverVersion);
		pgProductCatalog.setJson(entity);
		pgProductCatalog.setServerVersion(serverVersion);
		int rowsAffected = customProductCatalogueMapper.updateByPrimaryKeySelective(pgProductCatalog);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
	}
	
	@Transactional
	@Override
	public void update(ProductCatalogue entity) {
		if (entity == null) {
			return;
		}
		Long id = retrievePrimaryKey(entity);

		if (id == null) { //Product Catalogues doesn't not exist
			throw new IllegalStateException();
		}

		org.opensrp.domain.postgres.ProductCatalogue pgProductCatalogue = convert(entity, id);
		int rowsAffected = customProductCatalogueMapper.updateByPrimaryKeyAndGenerateServerVersion(pgProductCatalogue);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
		updateServerVersion(pgProductCatalogue, entity);
	}

	@Override
	public List<ProductCatalogue> getAll() {
		throw new NotImplementedException();
	}

	@Override
	public List<ProductCatalogue> getAll(String baseUrl) {
		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		productCatalogueExample.createCriteria().andUniqueIdIsNotNull();
		List<org.opensrp.domain.postgres.ProductCatalogue> productCatalogues = customProductCatalogueMapper
				.selectMany(productCatalogueExample, 0, DEFAULT_FETCH_SIZE);
		return convert(productCatalogues, baseUrl);
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
	public ProductCatalogue getById(Long uniqueId, String baseUrl) {
		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		productCatalogueExample.createCriteria().andUniqueIdEqualTo(uniqueId);

		List<org.opensrp.domain.postgres.ProductCatalogue> productCatalogues = customProductCatalogueMapper
				.selectByExample(productCatalogueExample);

		return isEmptyList(productCatalogues) ? null : convert(productCatalogues.get(0), baseUrl);
	}

	@Override
	public List<ProductCatalogue> getProductCataloguesBySearchBean(ProductCatalogueSearchBean productCatalogueSearchBean, int limit, String baseUrl) {
		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		populateProductCatalogueSearchCriteria(productCatalogueSearchBean,
				productCatalogueExample);
		productCatalogueExample.setOrderByClause(this.getOrderByClause(SERVER_VERSION, ASCENDING));
		return convert(customProductCatalogueMapper.selectMany(productCatalogueExample, 0, limit), baseUrl);
	}

	@Override
	public List<ProductCatalogue> getProductCataloguesBySearchBean(ProductCatalogueSearchBean productCatalogueSearchBean, String baseUrl){
		return getProductCataloguesBySearchBean(productCatalogueSearchBean, DEFAULT_FETCH_SIZE, baseUrl);
	}

	@Override
	public void safeRemove(Long uniqueId) {
		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		productCatalogueExample.createCriteria().andUniqueIdEqualTo(uniqueId);
		customProductCatalogueMapper.deleteByPrimaryKey(uniqueId);
	}

	@Override
	public ProductCatalogue getProductCatalogueByName(String productName) {
		ProductCatalogueExample productCatalogueExample = new ProductCatalogueExample();
		productCatalogueExample.createCriteria().andProductNameEqualTo(productName);
		org.opensrp.domain.postgres.ProductCatalogue pgProductCatalogue = customProductCatalogueMapper
				.selectOne(productCatalogueExample);
		return convert(pgProductCatalogue, "");
	}

	private List<ProductCatalogue> convert(List<org.opensrp.domain.postgres.ProductCatalogue> productCatalogues, String baseUrl) {
		if (productCatalogues == null || productCatalogues.isEmpty()) {
			return new ArrayList<>();
		}

		List<ProductCatalogue> convertedProductCatalogues = new ArrayList<>();
		for (org.opensrp.domain.postgres.ProductCatalogue productCatalogue : productCatalogues) {
			ProductCatalogue convertedProductCatalogue = convert(productCatalogue, baseUrl);
			if (convertedProductCatalogue != null) {
				convertedProductCatalogues.add(convertedProductCatalogue);
			}
		}

		return convertedProductCatalogues;
	}

	private ProductCatalogue convert(org.opensrp.domain.postgres.ProductCatalogue pgProductCatalogue, String baseUrl) {
		if (pgProductCatalogue == null || pgProductCatalogue.getJson() == null || !(pgProductCatalogue
				.getJson() instanceof ProductCatalogue)) {
			return null;
		}
		ProductCatalogue productCatalogue = (ProductCatalogue) pgProductCatalogue.getJson();
		productCatalogue.setUniqueId(pgProductCatalogue.getUniqueId());
		String photoUrl = productCatalogue.getPhotoURL();
		if(!StringUtils.isBlank(photoUrl)) {
			productCatalogue.setPhotoURL(baseUrl + photoUrl);
		}
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
		pgProductCatalogue.setServerVersion(productCatalogue.getServerVersion());

		return pgProductCatalogue;
	}

	private ProductCatalogueExample.Criteria populateProductCatalogueSearchCriteria(
			ProductCatalogueSearchBean productCatalogueSearchBean,
			ProductCatalogueExample productCatalogueExample) {

		ProductCatalogueExample.Criteria criteria = productCatalogueExample.createCriteria();

		if (productCatalogueSearchBean.getServerVersion() != null && productCatalogueSearchBean.getServerVersion() >= 0) {
			criteria.andServerVersionGreaterThanOrEqualTo(productCatalogueSearchBean.getServerVersion());
		}

		if (StringUtils.isNotEmpty(productCatalogueSearchBean.getProductName()))
			criteria.andProductNameEqualTo(productCatalogueSearchBean.getProductName());

		if (productCatalogueSearchBean.getUniqueId() != null && productCatalogueSearchBean.getUniqueId() != 0)
			criteria.andUniqueIdEqualTo(productCatalogueSearchBean.getUniqueId());

		if (!criteria.isValid())
			throw new IllegalArgumentException("Atleast one search filter must be specified");

		return criteria;
	}

}
