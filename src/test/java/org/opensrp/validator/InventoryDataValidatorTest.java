package org.opensrp.validator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.service.PhysicalLocationService;
import org.opensrp.service.ProductCatalogueService;
import org.opensrp.service.SettingService;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.opensrp.util.constants.InventoryConstants.*;

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
        inventoryDataValidator = new InventoryDataValidator(physicalLocationService, productCatalogueService, settingService);
    }

    @Test
    public void testGetValidationErrors() throws ParseException {
        when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_DONOR_IDENTIFIER)).thenReturn(createSettingsDonors());
        when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_SECTIONS_IDENTIFIER)).thenReturn(createSettingsSections());
        when(productCatalogueService.getProductCatalogue(anyLong(), anyString())).thenReturn(null);
        when(physicalLocationService.getStructure(anyString(), anyBoolean())).thenReturn(null);
        List<String> validationErrors = inventoryDataValidator.getValidationErrors("90398", "1", "04-08-2020",
                "WASH", null, null, "1.8", "test Donor");
        assertEquals(7, validationErrors.size());
        assertEquals(MISSING_REQUIRED_FIELDS, validationErrors.get(0));
        assertEquals(PRODUCT_CATALOG_DOES_NOT_EXISTS, validationErrors.get(1));
        assertEquals(SERVICE_POINT_DOES_NOT_EXISTS, validationErrors.get(2));
        assertEquals(INVALID_FORMAT_OF_DELIVERY_DATE, validationErrors.get(3));
        assertEquals(INVALID_QUANTITY, validationErrors.get(4));
        assertEquals(INVALID_UNICEF_SECTION, validationErrors.get(5));
        assertEquals(INVALID_DONOR, validationErrors.get(6));
    }

    @Test
    public void testGetValidationErrorsV2() throws ParseException {
        when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_DONOR_IDENTIFIER)).thenReturn(createSettingsDonors());
        when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_SECTIONS_IDENTIFIER)).thenReturn(createSettingsSections());
        when(productCatalogueService.getProductCatalogue(anyLong(), anyString())).thenReturn(null);
        when(physicalLocationService.getStructure(anyString(), anyBoolean())).thenReturn(null);
        List<String> validationErrors = inventoryDataValidator.getValidationErrors("90398", "1", "04/08/20",
                "WASH", null, null, "1.8", "test Donor");
        assertEquals(7, validationErrors.size());
        assertEquals(MISSING_REQUIRED_FIELDS, validationErrors.get(0));
        assertEquals(PRODUCT_CATALOG_DOES_NOT_EXISTS, validationErrors.get(1));
        assertEquals(SERVICE_POINT_DOES_NOT_EXISTS, validationErrors.get(2));
        assertEquals(INVALID_FORMAT_OF_DELIVERY_DATE, validationErrors.get(3));
        assertEquals(INVALID_QUANTITY, validationErrors.get(4));
        assertEquals(INVALID_UNICEF_SECTION, validationErrors.get(5));
        assertEquals(INVALID_DONOR, validationErrors.get(6));
    }

    @Test
    public void testGetValidationErrorsV3() throws ParseException {
        when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_DONOR_IDENTIFIER)).thenReturn(createSettingsDonors());
        when(settingService.findSettingsByIdentifier(SETTINGS_CONFIGURATION_SECTIONS_IDENTIFIER)).thenReturn(createSettingsSections());
        when(productCatalogueService.getProductCatalogue(anyLong(), anyString())).thenReturn(null);
        when(physicalLocationService.getStructure(anyString(), anyBoolean())).thenReturn(null);
        List<String> validationErrors = inventoryDataValidator.getValidationErrors("90398", "1", "04/08/2020",
                "WASH", null, null, "1.8", "test Donor");
        assertEquals(6, validationErrors.size());
        assertEquals(MISSING_REQUIRED_FIELDS, validationErrors.get(0));
        assertEquals(PRODUCT_CATALOG_DOES_NOT_EXISTS, validationErrors.get(1));
        assertEquals(SERVICE_POINT_DOES_NOT_EXISTS, validationErrors.get(2));
        assertEquals(INVALID_QUANTITY, validationErrors.get(3));
        assertEquals(INVALID_UNICEF_SECTION, validationErrors.get(4));
        assertEquals(INVALID_DONOR, validationErrors.get(5));
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

}
