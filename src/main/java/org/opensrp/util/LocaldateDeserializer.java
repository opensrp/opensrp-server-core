package org.opensrp.util;

import java.io.IOException;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class LocaldateDeserializer extends StdScalarDeserializer<LocalDate> {
	
	private static final long serialVersionUID = -805075518081134882L;
	
	public LocaldateDeserializer() {
		super(LocalDate.class);
	}
	
	@Override
	public LocalDate deserialize(JsonParser jsonParser, DeserializationContext ctxt)
	        throws IOException, JsonProcessingException {
		JsonToken currentToken = jsonParser.getCurrentToken();
		if (currentToken == JsonToken.VALUE_STRING) {
			String dateTimeAsString = jsonParser.getText().trim();
			return new LocalDate(dateTimeAsString);
		}
		return null;
	}
}
