package org.opensrp.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Inventory;
import org.opensrp.domain.ProductCatalogue;
import org.opensrp.domain.Stock;
import org.opensrp.repository.StocksRepository;
import org.opensrp.search.StockSearchBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {
	
	private final StocksRepository allStocks;

	private ProductCatalogueService productCatalogueService;

	private static Logger logger = LoggerFactory.getLogger(StockService.class.toString());
	
	@Autowired
	public StockService(StocksRepository allStocks, ProductCatalogueService productCatalogueService) {
		this.allStocks = allStocks;
		this.productCatalogueService = productCatalogueService;
	}
	
	public List<Stock> findAllByProviderid(String providerid) {
		return allStocks.findAllByProviderid(providerid);
	}
	
	public Stock getById(String id) {
		return allStocks.findById(id);
	}
	
	public List<Stock> getAll() {
		return allStocks.getAll();
	}
	
	public List<Stock> findStocks(StockSearchBean searchBean, String sortBy, String sortOrder, int limit) {
		return allStocks.findStocks(searchBean, sortBy, sortOrder, limit);
	}
	
	public List<Stock> findStocks(StockSearchBean searchBean) {
		return allStocks.findStocks(searchBean);
	}
	
	public List<Stock> findAllStocks() {
		return allStocks.findAllStocks();
	}
	
	public Stock find(Stock stock) {
		Stock st = allStocks.findById(stock.getId());
		if (st == null) {
			return null;
		} else {
			return stock;
		}
	}
	
	public synchronized Stock addStock(Stock stock) {
		Stock st = find(stock);
		if (st != null) {
			throw new IllegalArgumentException(
			        "A stock already exists with given id. Consider updating data.[" + st.getId() + "]");
		}
		stock.setServerVersion(allStocks.getNextServerVersion());
		allStocks.add(stock);
		return stock;
	}
	
	public synchronized Stock addorUpdateStock(Stock stock) {
		if (stock.getId() != null && getById(stock.getId()) != null) {
			stock.setDateEdited(DateTime.now());
			stock.setServerVersion(allStocks.getNextServerVersion());
			stock.setRevision(getById(stock.getId()).getRevision());
			allStocks.update(stock);
		} else {
			stock.setDateCreated(DateTime.now());
			stock.setServerVersion(allStocks.getNextServerVersion());
			allStocks.add(stock);
		}
		return stock;
	}
	
	public void updateStock(Stock updatedStock) {
		// If update is on original entity
		if (updatedStock.isNew()) {
			throw new IllegalArgumentException(
			        "Stock to be updated is not an existing and persisting domain object. Update database object instead of new pojo");
		}
		
		updatedStock.setDateEdited(DateTime.now());
		updatedStock.setServerVersion(allStocks.getNextServerVersion());
		allStocks.update(updatedStock);
	}
	
	public Stock find(String uniqueId) {
		List<Stock> sList = allStocks.findAllByProviderid(uniqueId);
		if (sList.size() > 1) {
			throw new IllegalArgumentException("Multiple events with identifier " + uniqueId + " exist.");
		} else if (sList.size() != 0) {
			return sList.get(0);
		}
		return null;
	}
	
	public Stock mergeStock(Stock updatedStock) {
		Stock original = find(updatedStock);
		if (original == null) {
			throw new IllegalArgumentException("No stock found with given id. Consider adding new!");
		}
		original.setDateEdited(DateTime.now());
		original.setServerVersion(allStocks.getNextServerVersion());
		allStocks.update(original);
		return original;
	}
	
	public List<Stock> findStocksBy(StockSearchBean searchBean) {
		return allStocks.findStocks(searchBean);
	}

	public void addInventory(Inventory inventory, String userName) {
		if(inventory == null) {
			return;
		}
		validateFields(inventory);

		ProductCatalogue productCatalogue = productCatalogueService.getProductCatalogueByName(inventory.getProductName());
		if(productCatalogue == null) {
			throw new IllegalArgumentException(
					"Invalid Product Name was selected");
		}
		Stock existingStock = findByIdentifierAndServicePointId(String.valueOf(productCatalogue.getUniqueId()),inventory.getServicePointId());
		if (existingStock != null) {
			throw new IllegalArgumentException(
					"A stock already exists with given id. Consider updating data.[" + existingStock.getId() + "]");
		}

		Stock stock = convertInventoryToStock(inventory, userName);
		if (stock == null) {
			return;
		}
		stock.setServerVersion(allStocks.getNextServerVersion());
		allStocks.add(stock);
	}

	public void updateInventory(Inventory inventory, String userName) {
		validateFields(inventory);
		Stock stock = convertInventoryToStock(inventory, userName);
		ProductCatalogue productCatalogue = inventory != null ? productCatalogueService.getProductCatalogueByName(inventory.getProductName()) : null;
		if(productCatalogue == null || inventory == null) {
			return;
		}
		Stock existingStock = findByIdentifierAndServicePointId(String.valueOf(productCatalogue.getUniqueId()),inventory.getServicePointId());

		if (existingStock == null) {
			throw new IllegalArgumentException(
					"Stock to be updated is not an existing and persisting domain object. Update database object instead of new pojo");
		}
        stock.setId(existingStock.getId());
		stock.setDateEdited(DateTime.now());
		stock.setServerVersion(allStocks.getNextServerVersion());
		allStocks.update(stock);
	}

	public Stock findByIdentifierAndServicePointId(String identifier, String locationId) {
		return allStocks.findByIdentifierAndServicePointId(identifier,locationId);
	}

	public void deleteStock(Long id) {
		if (id != null) {
			allStocks.delete(id);
		}
	}

	public List<Stock> getStocksByServicePointId(String servicePointId) {
	  return allStocks.findStocksByLocationId(servicePointId);
	}

	private Stock convertInventoryToStock(Inventory inventory, String username) {
		Stock stock = new Stock();
		ProductCatalogue productCatalogue = productCatalogueService.getProductCatalogueByName(inventory.getProductName());
		if (inventory == null || productCatalogue == null) {
			return null;
		}
		Date accountabilityEndDate = addMonthsToDate(inventory.getDeliveryDate(),
				productCatalogue.getAccountabilityPeriod());
		Map<String,String> customProperties = new HashMap<>();

		stock.setIdentifier(productCatalogue.getUniqueId());
		stock.setProviderid(username);
		stock.setValue(inventory.getQuantity());
		stock.setTransaction_type("Inventory");
		stock.setLocationId(inventory.getServicePointId());
		stock.setDeliveryDate(inventory.getDeliveryDate());
		stock.setAccountabilityEndDate(accountabilityEndDate);
		stock.setDonor(inventory.getDonor());
		stock.setSerialNumber(inventory.getSerialNumber());
		customProperties.put("UNICEF section", inventory.getUnicefSection());
		customProperties.put("PO Number", String.valueOf(inventory.getPoNumber()));
		stock.setCustomProperties(customProperties);
		return stock;
	}

	private Date addMonthsToDate(Date date, Integer monthsCount) {
		DateTime dateTime = new DateTime(date).plusMonths(monthsCount);
		return dateTime.toDate();
	}

	private void validateFields(Inventory inventory) {
		if (inventory.getQuantity() < 1) {
			throw new IllegalArgumentException("Quantity can not be less than 1");
		} else if (StringUtils.isBlank(inventory.getProductName())) {
			throw new IllegalArgumentException("Product Name not specified");
		} else {
			logger.info("All validations on fields passed");
		}
	}
	
}
