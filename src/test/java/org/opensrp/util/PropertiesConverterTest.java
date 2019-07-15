package org.opensrp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.LocationProperty;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
public class PropertiesConverterTest {

    private String parentJson = "{\"name\":\"MKB_5\",\"status\":\"Active\",\"version\":0,\"parentId\":\"2953\",\"geographicLevel\":2}";
	private Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new DateTypeConverter()).serializeNulls()
            .registerTypeAdapter(LocationProperty.class, new PropertiesConverter()).create();
    @Test
    public void testSerializeCustomProperties() {

        LocationProperty locationProperty = gson.fromJson(parentJson, LocationProperty.class);
        Map<String, String> customProperties = new HashMap<>();
        customProperties.put("taskIdentifier", "0d15fcac-df64-4f53-b01a-b650d1e45252");
        customProperties.put("taskBusinessStatus", "Not Visited");
        customProperties.put("taskStatus", "In Progress");
        locationProperty.setCustomProperties(customProperties);
        String expected = "{\"status\":\"Active\",\"parentId\":\"2953\",\"name\":\"MKB_5\",\"geographicLevel\":2,\"version\":0,\"taskBusinessStatus\":\"Not Visited\",\"taskIdentifier\":\"0d15fcac-df64-4f53-b01a-b650d1e45252\",\"taskStatus\":\"In Progress\"}";
        assertEquals(expected, gson.toJson(locationProperty));

    }


    @Test
    public void testDeserializeCustomProperties() {

        LocationProperty locationProperty  = gson.fromJson("{\"status\":\"Active\",\"parentId\":\"2953\",\"name\":\"MKB_5\",\"geographicLevel\":2,\"version\":0,\"taskBusinessStatus\":\"Not Visited\",\"taskIdentifier\":\"0d15fcac-df64-4f53-b01a-b650d1e45252\",\"taskStatus\":\"In Progress\"}"
                        , LocationProperty.class);
        Map<String, String> customProperties = locationProperty.getCustomProperties();
        assertEquals(3, customProperties.size());
        assertEquals("0d15fcac-df64-4f53-b01a-b650d1e45252", customProperties.get("taskIdentifier"));
        assertEquals("Not Visited", customProperties.get("taskBusinessStatus"));
        assertEquals("In Progress", customProperties.get("taskStatus"));

    }

}
