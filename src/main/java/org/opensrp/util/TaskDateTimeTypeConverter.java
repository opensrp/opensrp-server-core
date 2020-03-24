package org.opensrp.util;

import java.lang.reflect.Type;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

public class TaskDateTimeTypeConverter extends DateTimeTypeConverter {

	private DateTimeFormatter dateTimeFormatter;

	public TaskDateTimeTypeConverter() {
		this("yyyy-MM-dd'T'HHmm");
	}

	public TaskDateTimeTypeConverter(String dateFormat) {
		dateTimeFormatter = DateTimeFormat.forPattern(dateFormat);
	}

	@Override
	public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		try {
			return dateTimeFormatter.parseDateTime(json.getAsString());
		} catch (IllegalArgumentException e) {
			return new DateTime(json.getAsString());
		}
	}

	@Override
	public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString(dateTimeFormatter));
	}
}
