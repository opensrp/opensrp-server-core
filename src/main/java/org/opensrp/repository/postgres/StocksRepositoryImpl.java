package org.opensrp.repository.postgres;

import static org.opensrp.util.Utils.isEmptyList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.opensrp.domain.postgres.StockExample;
import org.opensrp.domain.postgres.StockMetadata;
import org.opensrp.domain.postgres.StockMetadataExample;
import org.opensrp.repository.LocationRepository;
import org.opensrp.repository.StocksRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomStockMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomStockMetadataMapper;
import org.opensrp.search.StockSearchBean;
import org.smartregister.converters.StockConverter;
import org.smartregister.domain.PhysicalLocation;
import org.smartregister.domain.ProductCatalogue;
import org.smartregister.domain.Stock;
import org.smartregister.domain.StockAndProductDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.fhir.model.resource.Bundle;

@Repository("stocksRepositoryPostgres")
public class StocksRepositoryImpl extends BaseRepositoryImpl<Stock> implements StocksRepository {

	@Autowired
	private CustomStockMapper stockMapper;
	
	@Autowired
	private CustomStockMetadataMapper stockMetadataMapper;

	@Autowired
	private LocationRepository locationRepository;

	@Override
	public Stock get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		org.opensrp.domain.postgres.Stock pgStock = stockMetadataMapper.selectByDocumentId(id);
		
		return convert(pgStock);
	}
	
	@Transactional
	@Override
	public void add(Stock entity) {
		if (entity == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //Stock already added
			return;
		}
		
		if (StringUtils.isBlank(entity.getId()))
			entity.setId(UUID.randomUUID().toString());
		setRevision(entity);
		
		org.opensrp.domain.postgres.Stock pgStock = convert(entity, null);
		if (pgStock == null) {
			throw new IllegalStateException();
		}
		
		int rowsAffected = stockMapper.insertSelectiveAndSetId(pgStock);
		
		if (rowsAffected < 1 || pgStock.getId() == null) {
			throw new IllegalStateException();
		}
		
		updateServerVersion(pgStock, entity);
		
		StockMetadata stockMetadata = createMetadata(entity, pgStock.getId());
		if (stockMetadata != null) {
			stockMetadataMapper.insertSelective(stockMetadata);
		}
		
	}
	
	private void updateServerVersion(org.opensrp.domain.postgres.Stock pgStock, Stock entity) {
		long serverVersion = stockMapper.selectServerVersionByPrimaryKey(pgStock.getId());
		entity.setServerVersion(serverVersion);
		pgStock.setJson(entity);
		int rowsAffected = stockMapper.updateByPrimaryKeySelective(pgStock);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
	}
	
	@Transactional
	@Override
	public void update(Stock entity) {
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Stock not added
			throw new IllegalStateException();
		}
		
		setRevision(entity);
		org.opensrp.domain.postgres.Stock pgStock = convert(entity, id);
		if (pgStock == null) {
			throw new IllegalStateException();
		}
		
	
		
		int rowsAffected = stockMapper.updateByPrimaryKeyAndGenerateServerVersion(pgStock);
		if (rowsAffected < 1) {
			throw new IllegalStateException();
		}
		updateServerVersion(pgStock, entity);
		
		StockMetadata stockMetadata = createMetadata(entity, id);
		if (stockMetadata == null) {
			throw new IllegalStateException();
		}
		
		StockMetadataExample stockMetadataExample = new StockMetadataExample();
		stockMetadataExample.createCriteria().andStockIdEqualTo(id);
		stockMetadata.setId(stockMetadataMapper.selectByExample(stockMetadataExample).get(0).getId());
		stockMetadataMapper.updateByPrimaryKey(stockMetadata);
		
	}
	
	@Override
	public List<Stock> getAll() {
		List<org.opensrp.domain.postgres.Stock> stocks = stockMetadataMapper.selectMany(new StockMetadataExample(), 0,
		    DEFAULT_FETCH_SIZE);
		return convert(stocks);
	}
	
	@Override
	public void safeRemove(Stock entity) {
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		StockMetadataExample stockMetadataExample = new StockMetadataExample();
		stockMetadataExample.createCriteria().andStockIdEqualTo(id);
		int rowsAffected = stockMetadataMapper.deleteByExample(stockMetadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		stockMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public List<Stock> findAllByProviderid(String providerid) {
		StockMetadataExample stockMetadataExample = new StockMetadataExample();
		stockMetadataExample.createCriteria().andProviderIdEqualTo(providerid);
		return convert(stockMetadataMapper.selectMany(stockMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	/**
	 * implements the method equivalent in couch repository that return stocks matching stock type
	 * id
	 * 
	 * @param stockType the stock type
	 * @param stockTypeId the stock type id
	 * @return list of stock of a particluar stock type id
	 */
	@Override
	public List<Stock> findAllByIdentifier(String stockType, String stockTypeId) {
		return convert(stockMetadataMapper.selectByIdentifier(stockTypeId, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public Stock findById(String id) {
		return get(id);
	}
	
	@Override
	public List<Stock> findStocks(StockSearchBean searchBean, String sortBy, String sortOrder, int offset, int limit) {
		String orderByClause = getOrderByClause(sortBy, sortOrder);
		Date date = new Date();
		return convert(stockMetadataMapper.selectManyBySearchBean(searchBean, date, orderByClause, offset, limit));

	}
	
	@Override
	public List<Stock> findStocks(StockSearchBean searchBean) {
		String sortBy = searchBean.getOrderByFieldName() != null ? searchBean.getOrderByFieldName().name() : null;
		String sortOrder= searchBean.getOrderByType() != null ? searchBean.getOrderByType().name() : null;
		Pair<Integer, Integer> pageLimitAndOffSet = getPageSizeAndOffset(searchBean);
		searchBean.setOffset(pageLimitAndOffSet.getRight());
		searchBean.setLimit(pageLimitAndOffSet.getLeft());
		return findStocks(searchBean, sortBy, sortOrder, searchBean.getOffset(), searchBean.getLimit());
	}
	
	@Override
	public List<Stock> findAllStocks() {
		return getAll();
	}

	@Override
	public Stock findByIdentifierAndServicePointId(String identifier, String locationId) {
		return convert(stockMapper.selectByIdentifierAndLocationId(identifier,locationId));
	}

	@Override
	public Stock getById(Long id) {
		StockExample stockExample = new StockExample();
		stockExample.createCriteria().andIdEqualTo(id);

		List<org.opensrp.domain.postgres.Stock> stocks = stockMapper.selectByExample(stockExample);

		return isEmptyList(stocks) ? null : convert(stocks.get(0));
	}

	@Override
	public void delete(Long stockId) {
		if (stockId == null) {
			return;
		}

		Stock stock = getById(stockId);
		org.opensrp.domain.postgres.Stock pgStock = convert(stock, stockId);
		Date dateDeleted = new Date();
		if (pgStock == null) {
			return;
		}

		pgStock.setDateDeleted(dateDeleted);
		StockMetadata stockMetadata = findStockMetaDataByStockId(stockId);
		stockMetadata.setDateDeleted(dateDeleted);
		int rowsAffected = stockMetadataMapper.updateByPrimaryKey(stockMetadata);
		if (rowsAffected < 1) {
			return;
		}

		stockMapper.updateByPrimaryKeySelective(pgStock);
	}

	@Override
	public List<Stock> findStocksByLocationId(StockSearchBean stockSearchBean) {
		Pair<Integer, Integer> pageSizeAndOffset = getPageSizeAndOffset(stockSearchBean);
        stockSearchBean.setOffset(pageSizeAndOffset.getRight());
        stockSearchBean.setLimit(pageSizeAndOffset.getLeft());
		return findStocks(stockSearchBean);
	}

	public StockMetadata findStockMetaDataByStockId(Long stockId) {
		return stockMetadataMapper.selectByStockId(stockId);
	}

	@Override
	protected Long retrievePrimaryKey(Stock entity) {
		if (entity == null || entity.getId() == null) {
			return null;
		}
		String documentId = entity.getId();
		
		StockMetadataExample stockMetadataExample = new StockMetadataExample();
		stockMetadataExample.createCriteria().andDocumentIdEqualTo(documentId);
		
		org.opensrp.domain.postgres.Stock pgStock = stockMetadataMapper.selectByDocumentId(documentId);
		if (pgStock == null) {
			return null;
		}
		return pgStock.getId();
	}
	
	@Override
	protected Object getUniqueField(Stock entity) {
		return entity == null ? null : entity.getId();
	}
	
	//private methods
	private Stock convert(org.opensrp.domain.postgres.Stock pgStock) {
		if (pgStock == null || pgStock.getJson() == null || !(pgStock.getJson() instanceof Stock)) {
			return null;
		}
		return (Stock) pgStock.getJson();
	}
	
	private org.opensrp.domain.postgres.Stock convert(Stock entity, Long id) {
		if (entity == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Stock pgReport = new org.opensrp.domain.postgres.Stock();
		pgReport.setId(id);
		pgReport.setJson(entity);
		
		return pgReport;
	}
	
	private List<Stock> convert(List<org.opensrp.domain.postgres.Stock> stocks) {
		if (stocks == null || stocks.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Stock> convertedStocks = new ArrayList<>();
		for (org.opensrp.domain.postgres.Stock stock : stocks) {
			Stock convertedStock = convert(stock);
			if (convertedStock != null) {
				convertedStocks.add(convertedStock);
			}
		}
		return convertedStocks;
	}
	
	private StockMetadata createMetadata(Stock entity, Long id) {
		StockMetadata metadata = new StockMetadata();
		metadata.setStockId(id);
		metadata.setDocumentId(entity.getId());
		metadata.setProviderId(entity.getProviderid());
		metadata.setLocationId(entity.getLocationId());
		metadata.setServerVersion(entity.getServerVersion());
		return metadata;
	}

	private Pair<Integer, Integer> getPageSizeAndOffset(StockSearchBean stockSearchBean) {

		Integer pageSize;
		Integer offset = 0;
		if (stockSearchBean.getPageSize() == null || stockSearchBean.getPageSize() == 0) {
			pageSize = DEFAULT_FETCH_SIZE;
		} else {
			pageSize = stockSearchBean.getPageSize();
		}

		if (stockSearchBean.getPageNumber() != null && stockSearchBean.getPageNumber() != 0) {
			offset = (stockSearchBean.getPageNumber() - 1) * pageSize;
		}

		return Pair.of(pageSize, offset);
	}


	@Override
	public List<Bundle> findInventoryItemsInAJurisdiction(String jurisdictionId) {
		List<PhysicalLocation> childLocations =
				locationRepository.findStructuresByProperties(false, jurisdictionId, null);
		List<String> servicePointIds = new ArrayList<>();
		for (PhysicalLocation physicalLocation : childLocations) {
			servicePointIds.add(physicalLocation.getId());
		}

		if(servicePointIds != null && servicePointIds.size() > 0) {
			return convertToFHIR(getInventoryWithProductDetails(servicePointIds));
		}
		return convertToFHIR(new ArrayList<>());
	}


	@Override
	public List<Bundle> findInventoryInAServicePoint(String servicePointId) {
		List<String> locations = new ArrayList<>();
		locations.add(servicePointId);
		return convertToFHIR(getInventoryWithProductDetails(locations));
	}

	@Override
	public List<Bundle> getStockById(String stockId) {
		return convertToFHIR(getInventoryWithProductDetailsByStockId(stockId));
	}

	private List<StockAndProductDetails> convertStockAndProductDetails(List<org.opensrp.domain.postgres.PgStockAndProductDetails> stockAndProductDetails) {
		if (stockAndProductDetails == null || stockAndProductDetails.isEmpty()) {
			return new ArrayList<>();
		}

		List<StockAndProductDetails> convertedStocksAndProductDetails = new ArrayList<>();
		for (org.opensrp.domain.postgres.PgStockAndProductDetails stockAndProductDetail : stockAndProductDetails) {
			StockAndProductDetails convertedStocksAndProductDetail = convert(stockAndProductDetail);
			if (convertedStocksAndProductDetail != null) {
				convertedStocksAndProductDetails.add(convertedStocksAndProductDetail);
			}
		}
		return convertedStocksAndProductDetails;
	}

	private StockAndProductDetails convert(org.opensrp.domain.postgres.PgStockAndProductDetails pgStockAndProductDetails) {
		StockAndProductDetails stockAndProductDetails = new StockAndProductDetails();
		Stock stock = convert(pgStockAndProductDetails.getStock());
		ProductCatalogue productCatalogue = convert(pgStockAndProductDetails.getProductCatalogue(), "");
		stockAndProductDetails.setStock(stock);
		stockAndProductDetails.setProductCatalogue(productCatalogue);
		return stockAndProductDetails;
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

	@Override
	public List<StockAndProductDetails> getInventoryWithProductDetails(List<String> locations) {
		return convertStockAndProductDetails(stockMetadataMapper.selectManyStockAndProductDetailsByServicePointId(locations));
	}

	@Override
	public List<StockAndProductDetails> getInventoryWithProductDetailsByStockId(String stockId) {
		return convertStockAndProductDetails(stockMetadataMapper.selectStockAndProductDetailsByStockId(stockId));
	}

	private List<Bundle> convertToFHIR(List<StockAndProductDetails> stockAndProductDetails) {
		return stockAndProductDetails.stream().map(stockAndProductDetail -> StockConverter.convertStockToBundleResource(stockAndProductDetail))
				.collect(Collectors.toList());
	}

}
