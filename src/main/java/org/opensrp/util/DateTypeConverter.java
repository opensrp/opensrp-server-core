package org.opensrp.util;

import com.google.gson.*;
import org.joda.time.LocalDate;

import java.lang.reflect.Type;

public class DateTypeConverter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
	
	@Override
	public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
	        throws JsonParseException {
		return new LocalDate(json.getAsString());
	}
	
	@Override
	public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}
}
