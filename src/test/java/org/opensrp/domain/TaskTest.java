package org.opensrp.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;
import org.opensrp.domain.Task.TaskStatus;
import org.opensrp.util.DateTimeTypeConverter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class TaskTest {

	private static Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new TaskDateTimeTypeConverter())
			.serializeNulls().create();

	protected static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HHmm");

	private String taskJson = "{\"identifier\":\"tsk11231jh22\",\"groupIdentifier\":\"2018_IRS-3734{\",\"status\":\"Ready\",\"businessStatus\":\"Not Visited\",\"priority\":3,\"code\":\"IRS\",\"description\":\"Spray House\",\"focus\":\"IRS Visit\",\"for\":\"location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc\",\"executionStartDate\":\"2018-11-10T2200\",\"executionEndDate\":null,\"authoredOn\":\"2018-10-31T0700\",\"lastModified\":\"2018-10-31T0700\",\"owner\":\"demouser\",\"note\":[{\"authorString\":\"demouser\",\"time\":\"2018-01-01T0800\",\"text\":\"This should be assigned to patrick.\"}]}";

	@Test
	public void testDeserialize() {
		Task task = gson.fromJson(taskJson, Task.class);
		assertEquals("tsk11231jh22", task.getIdentifier());
		assertEquals("2018_IRS-3734{", task.getGroupIdentifier());
		assertEquals(TaskStatus.READY, task.getStatus());
		assertEquals("Not Visited", task.getBusinessStatus());
		assertEquals(3, task.getPriority());
		assertEquals("IRS", task.getCode());
		assertEquals("Spray House", task.getDescription());
		assertEquals("IRS Visit", task.getFocus());
		assertEquals("location.properties.uid:41587456-b7c8-4c4e-b433-23a786f742fc", task.getForEntity());
		assertEquals("2018-11-10T2200", task.getExecutionStartDate().toString(formatter));
		assertNull(task.getExecutionEndDate());
		assertEquals("2018-10-31T0700", task.getAuthoredOn().toString(formatter));
		assertEquals("2018-10-31T0700", task.getLastModified().toString(formatter));
		assertEquals("demouser", task.getOwner());
		assertEquals(1, task.getNotes().size());
		assertEquals("demouser", task.getNotes().get(0).getAuthorString());
		assertEquals("2018-01-01T0800", task.getNotes().get(0).getTime().toString(formatter));
		assertEquals("This should be assigned to patrick.", task.getNotes().get(0).getText());
	}

	@Test
	public void testSerialize() {
		Task task = gson.fromJson(taskJson, Task.class);
		assertEquals(taskJson, gson.toJson(task));
	}

}

class TaskDateTimeTypeConverter extends DateTimeTypeConverter {

	@Override
	public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			return TaskTest.formatter.parseDateTime(json.getAsString());
		} catch (IllegalArgumentException e) {
			return new DateTime(json.getAsString());
		}
	}

	@Override
	public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString(TaskTest.formatter));
	}
}
