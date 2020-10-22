package org.opensrp.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.domain.Inventory;
import org.opensrp.domain.ProductCatalogue;
import org.opensrp.domain.Stock;
import org.opensrp.dto.CsvBulkImportDataSummary;
import org.opensrp.dto.FailedRecordSummary;
import org.opensrp.repository.StocksRepository;
import org.opensrp.search.StockSearchBean;
import org.opensrp.util.Donor;
import org.opensrp.util.UNICEFSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockService {
	
	private final StocksRepository allStocks;

	private ProductCatalogueService productCatalogueService;

	private PhysicalLocationService physicalLocationService;

	private static Logger logger = LoggerFactory.getLogger(StockService.class.toString());

	private static final String SERVICE_POINT_ID = "service_point_id";

	private static final String PRODUCT_ID = "product_id";

	private static final String QUANTITY = "quantity";

	private static final String DELIVERY_DATE = "delivery_date";

	private static final String UNICEF_SECTION = "unicef_section";

	private static final String SERIAL_NUMBER = "serial_number";

	private static final String DONOR = "donor";

	private static final String PO_NUMBER = "po_number";

	private static final String PRODUCT_NAME = "product_name";

	@Autowired
	public StockService(StocksRepository allStocks, ProductCatalogueService productCatalogueService, PhysicalLocationService physicalLocationService) {
		this.allStocks = allStocks;
		this.productCatalogueService = productCatalogueService;
		this.physicalLocationService = physicalLocationService;
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
			e.printStackTrace();
		}

		String productId;

		if (failedRecordSummaries.size() == 0) {
			for (Map<String, String> csvdata : csvStocks) {
				try {
					rowCount++;
					inventory = createInventoryObject(csvdata);
					productId = getValueFromMap(PRODUCT_ID, csvdata);
					Stock existingStock = findByIdentifierAndServicePointId(String.valueOf(productId),
							inventory.getServicePointId());
					if (existingStock == null)
						addInventory(inventory, userName);
					else
						updateInventory(inventory, userName);
					rowsProcessed++;
				}
				catch (Exception e) {
					failedRecordSummary = new FailedRecordSummary();
					failedRecordSummary.setRowNumber(rowCount);
					failedRecordSummary.setReasonOfFailure(
							"Exception occurred while converting and persisting of Stock data : " + e.getMessage());
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
		for (Map<String, String> csvdata : csvRows) {
			rowNumber++;
			String locationId = getValueFromMap(SERVICE_POINT_ID, csvdata);
			String productCatalogId = getValueFromMap(PRODUCT_ID, csvdata);
			String deliveryDateInString = getValueFromMap(DELIVERY_DATE, csvdata);
			String section = getValueFromMap(UNICEF_SECTION, csvdata);
			String poNumber = getValueFromMap(PO_NUMBER, csvdata);
			String serialNumber = getValueFromMap(SERIAL_NUMBER, csvdata);
			String quantity = getValueFromMap(QUANTITY, csvdata);
			String donor = getValueFromMap(DONOR, csvdata);
			ProductCatalogue productCatalogue;
			PhysicalLocation physicalLocation;
			Date deliveryDate;
			productCatalogue = productCatalogId != null ?
					productCatalogueService.getProductCatalogue(Long.valueOf(productCatalogId)) :
					null;
			physicalLocation = locationId != null ? physicalLocationService.getLocation(locationId, true) : null;
			deliveryDate = deliveryDateInString != null ? convertStringToDate(deliveryDateInString) : null;

			if (locationId == null || productCatalogId == null || deliveryDateInString == null || section == null
					|| poNumber == null) {
				logger.error("Required fields are missing");
				failedRecordSummary = new FailedRecordSummary();
				failedRecordSummary.setReasonOfFailure("Required fields are missing");
				failedRecordSummary.setRowNumber(rowNumber);
				failedRecordSummaries.add(failedRecordSummary);
			} else if (productCatalogue != null && productCatalogue.getIsAttractiveItem() && serialNumber == null) {
				logger.error("Serial Number is missing");
				failedRecordSummary = new FailedRecordSummary();
				failedRecordSummary.setReasonOfFailure("Serial Number is missing");
				failedRecordSummary.setRowNumber(rowNumber);
				failedRecordSummaries.add(failedRecordSummary);
			} else if (productCatalogue == null) {
				logger.error("Product Catalog does not exists against this Id");
				failedRecordSummary = new FailedRecordSummary();
				failedRecordSummary.setReasonOfFailure("Product Catalog does not exists against this Id");
				failedRecordSummary.setRowNumber(rowNumber);
				failedRecordSummaries.add(failedRecordSummary);
			} else if (physicalLocation == null) {
				logger.error("Physical Location does not exists against this Id");
				failedRecordSummary = new FailedRecordSummary();
				failedRecordSummary.setReasonOfFailure("Physical Location does not exists against this Id");
				failedRecordSummary.setRowNumber(rowNumber);
				failedRecordSummaries.add(failedRecordSummary);
			} else if (deliveryDate.getTime() > new Date().getTime()) {
				logger.error("Delivery Date can not be of future");
				failedRecordSummary = new FailedRecordSummary();
				failedRecordSummary.setReasonOfFailure("Delivery Date can not be of future");
				failedRecordSummary.setRowNumber(rowNumber);
				failedRecordSummaries.add(failedRecordSummary);
			} else if (quantity != null && Integer.valueOf(quantity) < 1) {
				logger.error("Quantity can not be less than 1");
				failedRecordSummary = new FailedRecordSummary();
				failedRecordSummary.setReasonOfFailure("Quantity can not be less than 1");
				failedRecordSummary.setRowNumber(rowNumber);
				failedRecordSummaries.add(failedRecordSummary);
			} else if (!UNICEFSection.containsString(section)) {
				logger.error("Selected UNICEF section is not among the list");
				failedRecordSummary = new FailedRecordSummary();
				failedRecordSummary.setReasonOfFailure("Selected UNICEF section is not among the list");
				failedRecordSummary.setRowNumber(rowNumber);
				failedRecordSummaries.add(failedRecordSummary);
			} else if (donor != null && !Donor.containsString(donor)) {
				logger.error("Selected donor is not among the list");
				failedRecordSummary = new FailedRecordSummary();
				failedRecordSummary.setReasonOfFailure("Selected donor is not among the list");
				failedRecordSummary.setRowNumber(rowNumber);
				failedRecordSummaries.add(failedRecordSummary);
			}
			//TODO : Add whole number validation for PO number
			else {
				logger.info("All validations passed");
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

	private String getValueFromMap(String key, Map<String, String> map) {
		return map.get(key);
	}

	private Date convertStringToDate(String stringDate) throws ParseException {
		return (stringDate != null) ? new SimpleDateFormat("dd/MM/yyyy").parse(stringDate) : null;
	}
	
}
