package org.opensrp.util;

import com.google.gson.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.lang.reflect.Type;

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
