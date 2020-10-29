package org.opensrp.validator;

import org.opensrp.domain.ProductCatalogue;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.service.PhysicalLocationService;
import org.opensrp.service.ProductCatalogueService;
import org.opensrp.service.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smartregister.domain.PhysicalLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.opensrp.util.constants.InventoryConstants.MISSING_SERIAL_NUMBER;
import static org.opensrp.util.constants.InventoryConstants.INVALID_DELIVERY_DATE;
import static org.opensrp.util.constants.InventoryConstants.INVALID_DONOR;
import static org.opensrp.util.constants.InventoryConstants.INVALID_UNICEF_SECTION;
import static org.opensrp.util.constants.InventoryConstants.INVALID_QUANTITY;
import static org.opensrp.util.constants.InventoryConstants.SERVICE_POINT_DOES_NOT_EXISTS;
import static org.opensrp.util.constants.InventoryConstants.INVALID_PO_NUMBER;
import static org.opensrp.util.constants.InventoryConstants.PRODUCT_CATALOG_DOES_NOT_EXISTS;
import static org.opensrp.util.constants.InventoryConstants.MISSING_REQUIRED_FIELDS;
import static org.opensrp.util.constants.InventoryConstants.SETTINGS_CONFIGURATION_DONOR_IDENTIFIER;
import static org.opensrp.util.constants.InventoryConstants.SETTINGS_CONFIGURATION_SECTIONS_IDENTIFIER;


@Component
public class InventoryDataValidator {

	@Autowired
	private PhysicalLocationService physicalLocationService;

	@Autowired
	private ProductCatalogueService productCatalogueService;

	@Autowired
	private SettingService settingService;

	private List<String> validationErrors;

	private List<String> validDonors;

	private List<String> validUnicefSections;

	private static Logger logger = LoggerFactory.getLogger(InventoryDataValidator.class.toString());

	public InventoryDataValidator(List<String> validationErrors) {
		this.validationErrors = validationErrors;
	}

	public List<String> getValidationErrors(String locationId, String productCatalogId, String deliveryDateInString,
			String section, String poNumber, String serialNumber, String quantity, String donor)
			throws ParseException {
		validationErrors = new ArrayList<>();

		validDonors = getValidDonors();
		validUnicefSections = getValidUnicefSections();

		Date deliveryDate = deliveryDateInString != null ? convertStringToDate(deliveryDateInString) : null;
		ProductCatalogue productCatalogue;
		PhysicalLocation physicalLocation;
		productCatalogue = productCatalogId != null ?
				productCatalogueService.getProductCatalogue(Long.valueOf(productCatalogId)) :
				null;
		physicalLocation = locationId != null ? physicalLocationService.getLocation(locationId, true) : null;

		validateRequiredFields(locationId, productCatalogId, deliveryDateInString, section, poNumber);
		validateSerialNumber(productCatalogue, serialNumber);
		validateProductCatalogue(productCatalogue);
		validateLocation(physicalLocation);
		validateDeliveryDate(deliveryDate);
		validateQuantity(quantity);
		validateUnicefSection(section);
		validateDonor(donor);
		validatePoNumber(poNumber);
		return validationErrors;
	}

	private void validateRequiredFields(String locationId, String productCatalogId, String deliveryDateInString,
			String section, String poNumber) {
		if (locationId == null || productCatalogId == null || deliveryDateInString == null || section == null
				|| poNumber == null) {
			logger.error(MISSING_REQUIRED_FIELDS);
			validationErrors.add(MISSING_REQUIRED_FIELDS);
		}
	}

	private void validateSerialNumber(ProductCatalogue productCatalogue, String serialNumber) {
		if (productCatalogue != null && productCatalogue.getIsAttractiveItem() && serialNumber == null) {
			logger.error(MISSING_SERIAL_NUMBER);
			validationErrors.add(MISSING_SERIAL_NUMBER);
		}
	}

	private void validateProductCatalogue(ProductCatalogue productCatalogue) {
		if (productCatalogue == null) {
			logger.error(PRODUCT_CATALOG_DOES_NOT_EXISTS);
			validationErrors.add(PRODUCT_CATALOG_DOES_NOT_EXISTS);
		}
	}

	private void validateLocation(PhysicalLocation physicalLocation) {
		if (physicalLocation == null) {
			logger.error(SERVICE_POINT_DOES_NOT_EXISTS);
			validationErrors.add(SERVICE_POINT_DOES_NOT_EXISTS);
		}
	}

	private void validateDeliveryDate(Date deliveryDate) {
		if (deliveryDate.getTime() > new Date().getTime()) {
			logger.error(INVALID_DELIVERY_DATE);
			validationErrors.add(INVALID_DELIVERY_DATE);
		}
	}

	private void validateQuantity(String quantity) {
		if (quantity != null && (!isWholeNumber(quantity) || Integer.valueOf(quantity) < 1)) {
			logger.error(INVALID_QUANTITY);
			validationErrors.add(INVALID_QUANTITY);
		}

	}

	private void validateUnicefSection(String section) {
		if (!validUnicefSections.contains(section)) {
			logger.error(INVALID_UNICEF_SECTION);
			validationErrors.add(INVALID_UNICEF_SECTION);
		}
	}

	private void validateDonor(String donor) {
		if (donor != null && !validDonors.contains(donor)) {
			logger.error(INVALID_DONOR);
			validationErrors.add(INVALID_DONOR);
		}
	}

	private void validatePoNumber(String poNumber) {
		if (!isWholeNumber(poNumber)) {
			logger.error(INVALID_PO_NUMBER);
			validationErrors.add(INVALID_PO_NUMBER);
		}
	}

	public List<String> getValidDonors() {
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = settingService
				.findSettingsByIdentifier(SETTINGS_CONFIGURATION_DONOR_IDENTIFIER);
		List<String> donors = new ArrayList<>();

		if (settingsAndSettingsMetadataJoinedList != null) {
			for (SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined : settingsAndSettingsMetadataJoinedList) {
				if (settingsAndSettingsMetadataJoined.getSettingsMetadata() != null
						&& settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingValue() != null) {
					donors.add(settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingValue());
				}
			}
		}
		return donors;
	}

	public List<String> getValidUnicefSections() {
		List<SettingsAndSettingsMetadataJoined> settingsAndSettingsMetadataJoinedList = settingService
				.findSettingsByIdentifier(SETTINGS_CONFIGURATION_SECTIONS_IDENTIFIER);
		List<String> unicefSections = new ArrayList<>();

		if (settingsAndSettingsMetadataJoinedList != null) {
			for (SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined : settingsAndSettingsMetadataJoinedList) {
				if (settingsAndSettingsMetadataJoined.getSettingsMetadata() != null
						&& settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingValue() != null) {
					unicefSections.add(settingsAndSettingsMetadataJoined.getSettingsMetadata().getSettingValue());
				}
			}
		}
		return unicefSections;
	}

	public static Date convertStringToDate(String stringDate) throws ParseException {
		return (stringDate != null) ? new SimpleDateFormat("dd/MM/yyyy").parse(stringDate) : null;
	}

	public static Boolean isWholeNumber(String number) {
		try {
			Integer parsedNumber = Integer.parseInt(number);
			logger.info("Parsed Integer is : " + parsedNumber);
			return true;
		}
		catch (NumberFormatException numberFormatException) {
			logger.error("Number Format Exception occurred : ", numberFormatException.getMessage());
			return false;
		}
	}
}
