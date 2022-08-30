package org.opensrp.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class UtilTest extends TestResourceLoader {

    public String declaredFieldOne;

    public int declaredFieldTwo;

    @Test
    public void testGetStringFromJson() throws IOException {
        String formSubmission = "{\"_id\": \"1\",\n" + "   \"_rev\": \"2\"}";
        Map<String, String> map = Utils.getStringMapFromJSON(formSubmission);
        assertFalse(map.isEmpty());
        assertEquals(map.size(), 2);
        assertEquals(map.get("_id"), "1");
        assertEquals(map.get("_rev"), "2");
        assertNull(map.get("sdf"));
    }

    @Test(expected = Exception.class)
    public void testErrorForInvalidJsonInGetStringFromJson() {
        String formSubmission = "Invalid  JSON";
        Utils.getStringMapFromJSON(formSubmission);
    }

    @Test
    public void testGetFieldsAsList() {
        List<String> fieldList = Utils.getFieldsAsList(UtilTest.class);
        assertEquals(2, fieldList.size());
        assertEquals("declaredFieldOne", fieldList.get(0));
        assertEquals("declaredFieldTwo", fieldList.get(1));
    }


    @Test
    public void testGetXlsToJson() throws IOException, JSONException {
        String path = getFullPath("sampleXLS/validXLS.xls");
        JSONArray jsonArray = Utils.getXlsToJson(path);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        assertEquals(2, jsonArray.length());
        assertEquals("f", jsonObject.get("first"));
        assertEquals("s", jsonObject.get("second"));
        assertEquals("t", jsonObject.get("third"));

    }

    @Test
    public void testGetXlsToJsonForEmptyXls() throws IOException, JSONException {
        String path = getFullPath("sampleXLS/emptyXLS.xls");
        JSONArray jsonArray = Utils.getXlsToJson(path);
        assertEquals(0, jsonArray.length());
    }

    @Test(expected = Exception.class)
    public void testGetXlsToJsonForInvalidXls() throws IOException, JSONException {
        String path = getFullPath("sampleXLS/invalidXLS.xls");
        Utils.getXlsToJson(path);
    }

}
