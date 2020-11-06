package org.opensrp.service;

import java.text.ParseException;
import java.util.*;

import org.joda.time.DateTime;
import org.smartregister.domain.Inventory;
import org.opensrp.domain.ProductCatalogue;
import org.smartregister.domain.Stock;
import org.opensrp.dto.CsvBulkImportDataSummary;
import org.opensrp.dto.FailedRecordSummary;
import org.opensrp.repository.StocksRepository;
import org.opensrp.search.StockSearchBean;
import org.opensrp.validator.InventoryDataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.opensrp.util.constants.InventoryConstants.PRODUCT_ID;
import static org.opensrp.util.constants.InventoryConstants.SERVICE_POINT_ID;
import static org.opensrp.util.constants.InventoryConstants.PRODUCT_NAME;
import static org.opensrp.util.constants.InventoryConstants.DELIVERY_DATE;
import static org.opensrp.util.constants.InventoryConstants.QUANTITY;
import static org.opensrp.util.constants.InventoryConstants.PO_NUMBER;
import static org.opensrp.util.constants.InventoryConstants.SERIAL_NUMBER;
import static org.opensrp.util.constants.InventoryConstants.UNICEF_SECTION;
import static org.opensrp.util.constants.InventoryConstants.DONOR;
import static org.opensrp.util.constants.InventoryConstants.MISSING_REQUIRED_FIELDS_V2;
import static org.opensrp.util.constants.InventoryConstants.MISSING_SERIAL_NUMBER;
import static org.opensrp.util.constants.InventoryConstants.INVALID_DELIVERY_DATE;
import static org.opensrp.util.constants.InventoryConstants.INVALID_DONOR;
import static org.opensrp.util.constants.InventoryConstants.INVALID_UNICEF_SECTION;
import static org.opensrp.util.constants.InventoryConstants.INVALID_QUANTITY;
import static org.opensrp.util.constants.InventoryConstants.SERVICE_POINT_DOES_NOT_EXISTS;
import static org.opensrp.util.constants.InventoryConstants.INVALID_PO_NUMBER;
import static org.opensrp.validator.InventoryDataValidator.convertStringToDate;
import static org.opensrp.validator.InventoryDataValidator.isWholeNumber;

@Service
public class StockService {
	
	private final StocksRepository allStocks;

	private ProductCatalogueService productCatalogueService;

	private PhysicalLocationService physicalLocationService;

	private InventoryDataValidator inventoryDataValidator;

	private static Logger logger = LoggerFactory.getLogger(StockService.class.toString());

	@Autowired
	public StockService(StocksRepository allStocks, ProductCatalogueService productCatalogueService, PhysicalLocationService physicalLocationService,
			InventoryDataValidator inventoryDataValidator) {
		this.allStocks = allStocks;
		this.productCatalogueService = productCatalogueService;
		this.physicalLocationService = physicalLocationService;
		this.inventoryDataValidator = inventoryDataValidator;
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

		if (productCatalogue != null && productCatalogue.getIsAttractiveItem() && inventory.getSerialNumber() == null) {
			throw new IllegalArgumentException(MISSING_SERIAL_NUMBER);
		}

		Stock existingStock = inventory.getStockId() != null ? getById(inventory.getStockId()) : null;
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
		if(inventory.getStockId() == null) {
			return;
		}
		Stock stock = convertInventoryToStock(inventory, userName);
		Stock existingStock = getById(inventory.getStockId());
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

	public CsvBulkImportDataSummary convertandPersistInventorydata(List<Map<String, String>> csvStocks, String userName) {
		int rowCount = 0;
		CsvBulkImportDataSummary csvBulkImportDataSummary = new CsvBulkImportDataSummary();
		FailedRecordSummary failedRecordSummary;
		List<FailedRecordSummary> failedRecordSummaries = new ArrayList<>();
		Inventory inventory;
		Integer totalRows = csvStocks.size();
		Integer rowsProcessed = 0;

		try {
			failedRecordSummaries = validateInventoryData(csvStocks);
		}
		catch (ParseException e) {
			logger.error("Parse Exception occurred : " + e.getMessage(), e);
			failedRecordSummary = new FailedRecordSummary();
			List<String> validationError = new ArrayList<>();
			failedRecordSummary.setRowNumber(rowCount);
			validationError.add("Parse Exception occurred");
			failedRecordSummary.setReasonOfFailure(validationError);
			failedRecordSummaries.add(failedRecordSummary);
		}

		if (failedRecordSummaries.size() == 0) {
			for (Map<String, String> csvdata : csvStocks) {
				try {
					rowCount++;
					inventory = createInventoryObject(csvdata);
					addInventory(inventory, userName);
					rowsProcessed++;
				}
				catch (Exception e) {
					failedRecordSummary = new FailedRecordSummary();
					List<String> validationError = new ArrayList<>();
					failedRecordSummary.setRowNumber(rowCount);
					validationError.add("Unknown error occurred");
					failedRecordSummary.setReasonOfFailure(validationError);
					failedRecordSummaries.add(failedRecordSummary);
				}
			}
		}

		csvBulkImportDataSummary.setFailedRecordSummaryList(failedRecordSummaries);
		csvBulkImportDataSummary.setNumberOfCsvRows(totalRows);
		csvBulkImportDataSummary.setNumberOfRowsProcessed(rowsProcessed);
		return csvBulkImportDataSummary;

	}

	private List<FailedRecordSummary> validateInventoryData(List<Map<String, String>> csvRows) throws ParseException {

		List<FailedRecordSummary> failedRecordSummaries = new ArrayList<>();
		FailedRecordSummary failedRecordSummary;
		int rowNumber = 0;
		List<String> validationErrors;
		for (Map<String, String> csvdata : csvRows) {
			failedRecordSummary = new FailedRecordSummary();
			rowNumber++;
			String locationId = getValueFromMap(SERVICE_POINT_ID, csvdata);
			String productCatalogId = getValueFromMap(PRODUCT_ID, csvdata);
			String deliveryDateInString = getValueFromMap(DELIVERY_DATE, csvdata);
			String section = getValueFromMap(UNICEF_SECTION, csvdata);
			String poNumber = getValueFromMap(PO_NUMBER, csvdata);
			String serialNumber = getValueFromMap(SERIAL_NUMBER, csvdata);
			String quantity = getValueFromMap(QUANTITY, csvdata);
			String donor = getValueFromMap(DONOR, csvdata);

			validationErrors = inventoryDataValidator.getValidationErrors(locationId, productCatalogId, deliveryDateInString, section, poNumber,
					serialNumber, quantity, donor);

			if (validationErrors.size() > 0) {
				failedRecordSummary.setRowNumber(rowNumber);
				failedRecordSummary.setReasonOfFailure(validationErrors);
				failedRecordSummaries.add(failedRecordSummary);
			}
		}
		return failedRecordSummaries;
	}

	private Inventory createInventoryObject(Map<String, String> data) throws ParseException {
		Inventory inventory = new Inventory();
		String locationId = getValueFromMap(SERVICE_POINT_ID, data);
		String productCatalogId = getValueFromMap(PRODUCT_ID, data);
		String productName = getValueFromMap(PRODUCT_NAME, data);
		String deliveryDateInString = getValueFromMap(DELIVERY_DATE, data);
		String section = getValueFromMap(UNICEF_SECTION, data);
		String poNumberFromCsv = getValueFromMap(PO_NUMBER, data);
		String serialNumber = getValueFromMap(SERIAL_NUMBER, data);
		String quantityFromCsv = getValueFromMap(QUANTITY, data);
		String donor = getValueFromMap(DONOR, data);
		int quantity = Integer.valueOf(quantityFromCsv);
		int poNumber = Integer.valueOf(poNumberFromCsv);

		Long productId = Long.valueOf(productCatalogId);
		ProductCatalogue productCatalogue = productCatalogueService.getProductCatalogue(productId);
		String productCatalogueName = productCatalogue != null ? productCatalogue.getProductName() : productName;

		inventory.setProductName(productCatalogueName);
		inventory.setUnicefSection(section);
		inventory.setQuantity(quantity);
		inventory.setDeliveryDate(convertStringToDate(deliveryDateInString));
		inventory.setDonor(donor);
		inventory.setServicePointId(locationId);
		inventory.setPoNumber(poNumber);
		inventory.setSerialNumber(serialNumber);

		return inventory;
	}

	private Stock convertInventoryToStock(Inventory inventory, String username) {
		Stock stock = new Stock();
		ProductCatalogue productCatalogue = productCatalogueService.getProductCatalogueByName(inventory.getProductName());
		if (inventory == null || productCatalogue == null) {
			return null;
		}
		Date accountabilityEndDate = addMonthsToDate(inventory.getDeliveryDate(),
				productCatalogue.getAccountabilityPeriod());
		Map<String, String> customProperties = new HashMap<>();

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
		if (inventory.getStockId() != null) {
			stock.setId(inventory.getStockId());
		}
		return stock;
	}

	private Date addMonthsToDate(Date date, Integer monthsCount) {
		DateTime dateTime = new DateTime(date).plusMonths(monthsCount);
		return dateTime.toDate();
	}

	private void validateFields(Inventory inventory) {
		PhysicalLocation physicalLocation = inventory.getServicePointId() != null ?
				physicalLocationService.getLocation(inventory.getServicePointId(), true) :
				null;

		List<String> donors = inventoryDataValidator.getValidDonors();
		List<String> unicefSections = inventoryDataValidator.getValidUnicefSections();

		Date deliveryDate = inventory.getDeliveryDate();
		if (inventory.getServicePointId() == null || inventory.getDeliveryDate() == null
				|| inventory.getProductName() == null
				|| inventory.getPoNumber() == null || inventory.getUnicefSection() == null) {
			throw new IllegalArgumentException(MISSING_REQUIRED_FIELDS_V2);
		} else if (physicalLocation == null) {
			throw new IllegalArgumentException(SERVICE_POINT_DOES_NOT_EXISTS);
		} else if (deliveryDate.getTime() > new Date().getTime()) {
			throw new IllegalArgumentException(INVALID_DELIVERY_DATE);
		} else if (!isWholeNumber(String.valueOf(inventory.getQuantity())) || inventory.getQuantity() < 1) {
			throw new IllegalArgumentException(INVALID_QUANTITY);
		} else if (!unicefSections.contains(inventory.getUnicefSection())) {
			throw new IllegalArgumentException(INVALID_UNICEF_SECTION);
		} else if (inventory.getDonor() != null && !donors.contains(inventory.getDonor())) {
			throw new IllegalArgumentException(INVALID_DONOR);
		} else if (!isWholeNumber(String.valueOf(inventory.getPoNumber()))) {
			throw new IllegalArgumentException(INVALID_PO_NUMBER);
		} else {
			logger.info("All validations on fields passed");
		}
	}

	private String getValueFromMap(String key, Map<String, String> map) {
		return map.get(key);
	}
}
