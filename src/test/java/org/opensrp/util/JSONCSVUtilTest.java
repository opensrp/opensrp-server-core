package org.opensrp.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.opensrp.domain.CSVRowConfig;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.smartregister.utils.DateTimeTypeConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.opensrp.service.UploadService.CLIENT;
import static org.opensrp.service.UploadService.EVENT;

public class JSONCSVUtilTest {

	@Test
	public void testCreateJSON() {
		final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
				.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

		String jsonConfigs = "[{\"column_name\":\"opensrp_id\",\"field_mapping\":\"client.identifiers.opensrp_id\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"has_national_ID\",\"field_mapping\":\"client.attributes.has_no_id\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"national_ID\",\"field_mapping\":\"client.identifiers.national_id\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"surname\",\"field_mapping\":\"client.lastName\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"given_name\",\"field_mapping\":\"client.firstName\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"sex\",\"field_mapping\":\"client.gender\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"reveal_id\",\"field_mapping\":\"client.identifiers.reveal_id\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"grade\",\"field_mapping\":\"client.attributes.grade\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"grade_class\",\"field_mapping\":\"client.attributes.grade_class\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"school_enrolled\",\"field_mapping\":\"client.attributes.school_enrolled\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"school_name\",\"field_mapping\":\"client.attributes.school_name\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"school_ID\",\"field_mapping\":\"client.attributes.default_residence\",\"required\":true,\"regex\":\"\"},{\"column_name\":\"school_ID\",\"field_mapping\":\"event.locationId\",\"required\":true,\"regex\":\"\"},{\"column_name\":\"birthdate\",\"field_mapping\":\"client.birthdate\",\"required\":false,\"regex\":\"\"},{\"column_name\":\"birthday_approximated\",\"field_mapping\":\"client.birthdateApprox\",\"required\":false,\"regex\":\"\"}]";

		TypeToken<List<CSVRowConfig>> token = new TypeToken<>() {

		};
		List<CSVRowConfig> configList = gson.fromJson(jsonConfigs, token.getType());

		Map<String, List<CSVRowConfig>> configGroup = configList
				.stream()
				.collect(Collectors.groupingBy(CSVRowConfig::getColumnName));

		Map<String, String> csvValues = new HashMap<>();
		csvValues.put("opensrp_id", "");
		csvValues.put("has_national_ID", "Yes");
		csvValues.put("national_ID", "234234234");
		csvValues.put("surname", "Dev");
		csvValues.put("given_name", "Ronald");
		csvValues.put("sex", "Male");
		csvValues.put("reveal_id", "");
		csvValues.put("grade", "Grade 2");
		csvValues.put("grade_class", "");
		csvValues.put("school_enrolled", "Yes");
		csvValues.put("school_name", "Makini");
		csvValues.put("school_ID", "f0de23e0-1e42-49e1-a60f-58d92040aaaa");
		csvValues.put("birthdate", "2020-01-01");
		csvValues.put("birthday_approximated", "true");

		JSONObject jsonObject = JSONCSVUtil.toJSON(csvValues, configGroup);

		Client client = gson.fromJson(jsonObject.get(CLIENT).toString(), Client.class);
		Assert.assertEquals(client.getFirstName(), "Ronald");
		Assert.assertEquals(client.getLastName(), "Dev");
		Assert.assertEquals(client.getGender(), "Male");
		Assert.assertTrue(client.getBirthdateApprox());
		Assert.assertEquals(client.getIdentifier("national_id"), "234234234");
		Assert.assertEquals(client.getAttributes().get("default_residence"), "f0de23e0-1e42-49e1-a60f-58d92040aaaa");
		Assert.assertEquals(client.getAttributes().get("grade"), "Grade 2");
		Assert.assertEquals(client.getAttributes().get("school_name"), "Makini");
		Assert.assertEquals(client.getAttributes().get("school_enrolled"), "Yes");
		Assert.assertEquals(client.getAttributes().get("has_no_id"), "Yes");

		Event event = gson.fromJson(jsonObject.get(EVENT).toString(), Event.class);
		Assert.assertEquals(event.getLocationId(), "f0de23e0-1e42-49e1-a60f-58d92040aaaa");
	}

	@Test
	public void testExtractData() {
		String expectedJSON = "{\"client\":{\"lastName\":\"Williams\",\"firstName\":\"Mark\",\"relationships\":{\"family\":[\"f785d89a-8c8c-4b92-91b4-f0a3bb160fed\"]},\"birthdate\":\"2013-05-15T00:00:00.000Z\",\"identifiers\":{\"opensrp_id\":\"2446768-0\",\"reveal_id\":\"2013051524467680\"},\"attributes\":{\"grade_class\":\"1\",\"grade\":\"Grade 2\",\"school_name\":\"St Christophers\",\"age_category\":\"\",\"school_enrolled\":\"Yes\",\"reveal_id\":\"12345\"},\"baseEntityId\":\"f785d89a-8c8c-4b92-91b4-f0a3bb160fed\",\"birthdateApprox\":\"false\"}}";
		JSONObject jsonObject = new JSONObject(expectedJSON);

		List<String> mapping = getAttributes().stream()
				.map(Pair::getLeft)
				.collect(Collectors.toList());

		List<String> string = JSONCSVUtil.jsonToString(jsonObject, mapping);

		String[] expected = { "12345", "", "Grade 2", "1", "Yes", "St Christophers", "f785d89a-8c8c-4b92-91b4-f0a3bb160fed",
				"2013-05-15T00:00:00.000Z", "false", "2446768-0", "2013051524467680", "Williams", "Mark",
				"f785d89a-8c8c-4b92-91b4-f0a3bb160fed" };

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
