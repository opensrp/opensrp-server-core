package org.opensrp.service;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;
import org.opensrp.api.domain.Location;
import org.opensrp.domain.setting.Setting;
import org.opensrp.repository.SettingRepository;
import org.opensrp.search.SettingSearchBean;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

public class SettingServiceTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private SettingRepository settingRepository;

    @Captor
    private ArgumentCaptor<SettingSearchBean> settingSearchBeanArgumentCaptor;

    @Captor
    private ArgumentCaptor<Map<String, TreeNode<String, Location>>> mapArgumentCaptor;

    private SettingService settingService;

    @Before
    public void setUp() {
        settingService = new SettingService();
        settingService.setSettingRepository(this.settingRepository);
    }

    @Test
    public void testFindSettingsCallsRepositoryFindSettingsMethod() {
        SettingSearchBean settingSearchBean = new SettingSearchBean();
        settingSearchBean.setIdentifier("id-1");
        LocationTree locationTree = new Gson().fromJson(
                "{\"locationsHierarchy\":{\"map\":{\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":{\"id\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"label\":\"Uganda\",\"node\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"tags\":[\"Country\"],\"voided\":false},\"children\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":{\"id\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"label\":\"Kampala\",\"node\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false},\"tags\":[\"District\"],\"voided\":false},\"children\":{\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":{\"id\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"label\":\"KCCA\",\"node\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"parentLocation\":{\"locationId\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\",\"name\":\"Uganda\",\"voided\":false},\"voided\":false},\"tags\":[\"County\"],\"voided\":false},\"children\":{\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":{\"id\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"label\":\"Central Division\",\"node\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"parentLocation\":{\"locationId\":\"8340315f-48e4-4768-a1ce-414532b4c49b\",\"name\":\"Kampala\",\"voided\":false},\"voided\":false},\"tags\":[\"Sub-county\"],\"voided\":false},\"children\":{\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":{\"id\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"label\":\"Bukesa Urban Health Centre\",\"node\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"parentLocation\":{\"locationId\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\",\"name\":\"KCCA\",\"voided\":false},\"voided\":false},\"tags\":[\"Health Facility\"],\"voided\":false},\"children\":{\"982eb3f3-b7e3-450f-a38e-d067f2345212\":{\"id\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"label\":\"Jambula Girls School\",\"node\":{\"locationId\":\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"name\":\"Jambula Girls School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false},\"voided\":false},\"tags\":[\"School\"],\"voided\":false},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"},\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\":{\"id\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"label\":\"Nsalo Secondary School\",\"node\":{\"locationId\":\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\",\"name\":\"Nsalo Secondary School\",\"parentLocation\":{\"locationId\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\",\"name\":\"Bukesa Urban Health Centre\",\"parentLocation\":{\"locationId\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\",\"name\":\"Central Division\",\"voided\":false},\"voided\":false},\"tags\":[\"School\"],\"voided\":false},\"parent\":\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"}},\"parent\":\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"}},\"parent\":\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"}},\"parent\":\"8340315f-48e4-4768-a1ce-414532b4c49b\"}},\"parent\":\"02ebbc84-5e29-4cd5-9b79-c594058923e9\"}}}},\"parentChildren\":{\"8340315f-48e4-4768-a1ce-414532b4c49b\":[\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\"],\"02ebbc84-5e29-4cd5-9b79-c594058923e9\":[\"8340315f-48e4-4768-a1ce-414532b4c49b\"],\"b1ef8a0b-275b-43fc-a580-1e21ceb34c78\":[\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\"],\"4e188e6d-2ffb-4b25-85f9-b9fbf5010d40\":[\"44de66fb-e6c6-4bae-92bb-386dfe626eba\"],\"44de66fb-e6c6-4bae-92bb-386dfe626eba\":[\"982eb3f3-b7e3-450f-a38e-d067f2345212\",\"ee08a6e0-3f73-4c28-b186-64d5cd06f4ce\"]}}}",
                LocationTree.class);
        Map<String, TreeNode<String, Location>> treeNodeHashMap = locationTree.getLocationsHierarchy();
        settingService.findSettings(settingSearchBean,treeNodeHashMap);
        verify(settingRepository).findSettings(settingSearchBeanArgumentCaptor.capture(), mapArgumentCaptor.capture());
        assertNotNull(settingSearchBeanArgumentCaptor.getValue());
        assertEquals("id-1", settingSearchBeanArgumentCaptor.getValue().getIdentifier());
    }

    @Test
    public void testAddOrUpdateSettingsCallsRepositoryAddOrUpdateMethod() {
        Setting setting = new Setting();
        setting.setSettingsId("id-1");
        settingService.addOrUpdateSettings(setting);
        verify(settingRepository).addOrUpdate(setting);
    }

    @Test
    public void testDeleteSettingCallsRepositoryDeleteMethod() {
        settingService.deleteSetting(1l);
        verify(settingRepository).delete(1l);
    }

    @Test
    public void testFindSettingsByIdentifierCallsRepositoryMethod() {
        settingService.findSettingsByIdentifier("identifier-1");
        verify(settingRepository).findSettingsAndSettingsMetadataByIdentifier("identifier-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveSettingsThrowsExceptionWhenJsonSettingConfigurationIsNull() {
        settingService.saveSetting(null);
    }
}
