package org.opensrp.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.service.PhysicalLocationService;
import org.opensrp.service.ProductCatalogueService;
import org.opensrp.service.SettingService;
import org.smartregister.domain.ProductCatalogue;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.util.constants.EventDataExportConstants.SETTINGS_CONFIGURATION_EVENT_TYPE_TO_SETTINGS_IDENTIFIER;
import static org.opensrp.util.constants.InventoryConstants.SETTINGS_CONFIGURATION_DONOR_IDENTIFIER;
import static org.opensrp.util.constants.InventoryConstants.SETTINGS_CONFIGURATION_SECTIONS_IDENTIFIER;

public class InventoryDataValidatorTest {

	@Mock
	private PhysicalLocationService physicalLocationService;

	@Mock
	private ProductCatalogueService productCatalogueService;

	@Mock
	private SettingService settingService;

	private InventoryDataValidator inventoryDataValidator;

	@Before
	public void setUp() {
		initMocks(this);
		inventoryDataValidator = new InventoryDataValidator(physicalLocationService,productCatalogueService,settingService);
	}

	@Test
	public void testGetValidationErrors() throws ParseException {
		when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_DONOR_IDENTIFIER)).thenReturn(createSettingsDonors());
		when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_SECTIONS_IDENTIFIER)).thenReturn(createSettingsSections());
		when(productCatalogueService.getProductCatalogue(anyLong(), anyString())).thenReturn(null);
		when(physicalLocationService.getStructure(anyString(),anyBoolean())).thenReturn(null);
		List<String> validationErrors = inventoryDataValidator.getValidationErrors("90398","1","04/08/2020",
				"WASH", "-1", "", "1.8", "ADB");
	}

	private List<SettingsAndSettingsMetadataJoined> createSettingsDonors() {
		SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined = new SettingsAndSettingsMetadataJoined();
		SettingsMetadata settingsMetadata = new SettingsMetadata();
		settingsMetadata.setSettingKey("ADB");
		settingsMetadata.setSettingValue("ADB");
		settingsMetadata.setSettingLabel("ADB");
		settingsAndSettingsMetadataJoined.setSettingsMetadata(settingsMetadata);
		return Collections.singletonList(settingsAndSettingsMetadataJoined);
	}

	private List<SettingsAndSettingsMetadataJoined> createSettingsSections() {
		SettingsAndSettingsMetadataJoined settingsAndSettingsMetadataJoined = new SettingsAndSettingsMetadataJoined();
		SettingsMetadata settingsMetadata = new SettingsMetadata();
		settingsMetadata.setSettingKey("HEALTH");
		settingsMetadata.setSettingValue("Health");
		settingsMetadata.setSettingLabel("Health");
		settingsAndSettingsMetadataJoined.setSettingsMetadata(settingsMetadata);
		return Collections.singletonList(settingsAndSettingsMetadataJoined);
	}

	private ProductCatalogue createProductCatalogue() {
		ProductCatalogue productCatalogue = new ProductCatalogue();
		productCatalogue.setUniqueId(1l);
		productCatalogue.setProductName("Product A");
		productCatalogue.setIsAttractiveItem(Boolean.TRUE);
		productCatalogue.setMaterialNumber("MT-123");
		productCatalogue.setAvailability("available");
		productCatalogue.setCondition("good condition");
		productCatalogue.setAppropriateUsage("staff is trained to use it appropriately");
		productCatalogue.setAccountabilityPeriod(1);
		productCatalogue.setServerVersion(123456l);
		return productCatalogue;
	}


}
