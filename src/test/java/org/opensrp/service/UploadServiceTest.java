package org.opensrp.service;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.repository.SettingRepository;
import org.opensrp.search.SettingSearchBean;
import org.opensrp.search.UploadValidationBean;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;


@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*", "org.w3c.*"})
public class UploadServiceTest {

    private UploadService uploadService;

    private SettingRepository settingRepository;

    @Before
    public void setUp() {
        settingRepository = mock(SettingRepository.class);
        uploadService = new UploadService();
        uploadService.setSettingRepository(settingRepository);
    }

    @Test
    public void testValidateFieldValues() {

        List<SettingConfiguration> settingConfigurations = new ArrayList<>();
        SettingConfiguration configuration = new SettingConfiguration();
        configuration.setIdentifier(UploadService.CSV_UPLOAD_SETTING);

        Setting settings = new Setting();
        settings.setValues(new JSONArray("[{\"Child Registration\": [{\"regex\": \"\", \"required\": false, \"column_name\": \"opensrp_id\", \"field_mapping\": \"client.identifiers.opensrp_id\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"last_name\", \"field_mapping\": \"client.lastName\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"first_name\", \"field_mapping\": \"client.firstName\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"middle_name\", \"field_mapping\": \"client.middleName\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"gender\", \"field_mapping\": \"client.gender\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"reveal_id\", \"field_mapping\": \"client.identifiers.reveal_id\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"grade\", \"field_mapping\": \"client.attributes.grade\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"grade_class\", \"field_mapping\": \"client.attributes.grade_class\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"school_enrolled\", \"field_mapping\": \"client.attributes.school_enrolled\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"school_name\", \"field_mapping\": \"client.attributes.school_name\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"birthdate\", \"field_mapping\": \"client.birthdate\"}, {\"regex\": \"\", \"required\": false, \"column_name\": \"birthday_approximated\", \"field_mapping\": \"client.birthdateApprox\"}, {\"regex\": \"\", \"required\": true, \"column_name\": \"team_name\", \"field_mapping\": \"event.team\"}, {\"regex\": \"\", \"required\": true, \"column_name\": \"team_id\", \"field_mapping\": \"event.teamId\"}]}]"));

        configuration.setSettings(new ArrayList<>());
        configuration.getSettings().add(settings);

        settingConfigurations.add(configuration);


        Mockito.doReturn(settingConfigurations).when(settingRepository).findSettings(Mockito.any(SettingSearchBean.class),
                null);

        List<Map<String, String>> csvClients = new ArrayList<>();

        Map<String, String> values = new HashMap<>();
        values.put("opensrp_id", "");
        values.put("last_name", "Banda");
        values.put("first_name", "Lucy");
        values.put("middle_name", "F");
        values.put("gender", "Female");
        values.put("reveal_id", "4858791");
        values.put("grade", "Grade 1");
        values.put("grade_class", "");
        values.put("school_enrolled", "");
        values.put("school_name", "");
        values.put("birthdate", "2019-09-08T03:00:00.000+03:00");
        values.put("birthday_approximated", "false");
        values.put("team_name", "false");
        values.put("team_id", "0f38856a-6e0f-5e31-bf3c-a2ad8a53210d");

        csvClients.add(values);

        UploadValidationBean result = uploadService.validateFieldValues(csvClients, "Child Registration", "opensrp_id");

        Assert.assertEquals(1, (int) result.getTotalRows());
        Assert.assertEquals(14, (int) result.getHeaderColumns());
        Assert.assertNull(result.getErrors());
        Assert.assertEquals(0, (int) result.getRowsToUpdate());
        Assert.assertEquals(1, (int) result.getRowsToCreate());
    }
}
