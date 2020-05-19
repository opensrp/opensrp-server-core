package org.opensrp.util;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JSONCSVUtilTest {

    @Test
    public void testExtractData() {
        String expectedJSON = "{\"client\":{\"lastName\":\"Williams\",\"firstName\":\"Mark\",\"relationships\":{\"family\":[\"f785d89a-8c8c-4b92-91b4-f0a3bb160fed\"]},\"birthdate\":\"2013-05-15T00:00:00.000Z\",\"identifiers\":{\"opensrp_id\":\"2446768-0\",\"reveal_id\":\"2013051524467680\"},\"attributes\":{\"grade_class\":\"1\",\"grade\":\"Grade 2\",\"school_name\":\"St Christophers\",\"age_category\":\"\",\"school_enrolled\":\"Yes\",\"reveal_id\":\"12345\"},\"baseEntityId\":\"f785d89a-8c8c-4b92-91b4-f0a3bb160fed\",\"birthdateApprox\":\"false\"}}";
        JSONObject jsonObject = new JSONObject(expectedJSON);

        List<String> mapping = getAttributes().stream()
                .map(Pair::getLeft)
                .collect(Collectors.toList());

        List<String> string = JSONCSVUtil.jsonToString(jsonObject, mapping);

        String[] expected = {"12345", "", "Grade 2", "1", "Yes", "St Christophers", "f785d89a-8c8c-4b92-91b4-f0a3bb160fed",
                "2013-05-15T00:00:00.000Z", "false", "2446768-0", "2013051524467680", "Williams", "Mark", "f785d89a-8c8c-4b92-91b4-f0a3bb160fed"};

        for (String s : expected) {
            Assert.assertTrue(string.contains(s));
        }

        Assert.assertEquals(expected.length, string.size());
    }

    @Test
    public void testAddJsonNode() {
        JSONObject jsonObject = new JSONObject();
        for (Pair<String, String> pa :
                getAttributes()) {
            JSONCSVUtil.addNodeToJson(pa.getKey(), pa.getValue(), jsonObject);
        }

        String expectedJSON = "{\"client\":{\"lastName\":\"Williams\",\"firstName\":\"Mark\",\"relationships\":{\"family\":[\"f785d89a-8c8c-4b92-91b4-f0a3bb160fed\"]},\"birthdate\":\"2013-05-15T00:00:00.000Z\",\"identifiers\":{\"opensrp_id\":\"2446768-0\",\"reveal_id\":\"2013051524467680\"},\"attributes\":{\"grade_class\":\"1\",\"grade\":\"Grade 2\",\"school_name\":\"St Christophers\",\"age_category\":\"6-10\",\"school_enrolled\":\"Yes\",\"reveal_id\":\"12345\"},\"baseEntityId\":\"f785d89a-8c8c-4b92-91b4-f0a3bb160fed\",\"birthdateApprox\":\"false\"}}";
        Assert.assertEquals(expectedJSON, jsonObject.toString());
    }

    private List<Pair<String, String>> getAttributes() {
        List<Pair<String, String>> result = new ArrayList<>();

        result.add(Pair.of("client.attributes.reveal_id", "12345"));
        result.add(Pair.of("client.attributes.age_category", "6-10"));
        result.add(Pair.of("client.attributes.grade", "Grade 2"));
        result.add(Pair.of("client.attributes.grade_class", "1"));
        result.add(Pair.of("client.attributes.school_enrolled", "Yes"));
        result.add(Pair.of("client.attributes.school_name", "St Christophers"));

        result.add(Pair.of("client.baseEntityId", "f785d89a-8c8c-4b92-91b4-f0a3bb160fed"));
        result.add(Pair.of("client.birthdate", "2013-05-15T00:00:00.000Z"));
        result.add(Pair.of("client.birthdateApprox", "false"));
        result.add(Pair.of("client.identifiers.opensrp_id", "2446768-0"));
        result.add(Pair.of("client.identifiers.reveal_id", "2013051524467680"));
        result.add(Pair.of("client.lastName", "Williams"));
        result.add(Pair.of("client.firstName", "Mark"));
        result.add(Pair.of("client.relationships.family[0]", "f785d89a-8c8c-4b92-91b4-f0a3bb160fed"));

        return result;
    }

}
