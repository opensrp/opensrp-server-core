package org.opensrp.scheduler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.opensrp.util.Utils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class ScheduleTest {

    @Test(expected = JSONException.class)
    public void testConstructorInvalidString() throws JSONException {
        new Schedule(" dsf");
    }

    @Test(expected = JSONException.class)
    public void testWithInvalidJsonObject() throws JSONException {
        new Schedule(new JSONObject());
    }

    @Test(expected = Exception.class)
    public void testCompilerExceptionInPassesValidation() throws IOException, JSONException {
        String scheduleConfigPath = "/schedules/schedule-config.xls";
        ResourceLoader loader = new DefaultResourceLoader();
        scheduleConfigPath = loader.getResource(scheduleConfigPath).getURI().getPath();

        JSONArray xlsToJsonScheduleArray = Utils.getXlsToJson(scheduleConfigPath);

        String duplicatePassLogic = "${fs.pentavalent_2} !=s ${fs.pentavalent_2}";
        Schedule schedule = new Schedule(xlsToJsonScheduleArray.getJSONObject(0).put("passLogic", duplicatePassLogic));

        Map<String, String> flvl = new HashMap<>();

        flvl.put("pentavalent_2", null);
        schedule.passesValidations(flvl);

    }
}
