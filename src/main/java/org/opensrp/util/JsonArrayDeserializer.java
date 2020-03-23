package org.opensrp.util;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

public class JsonArrayDeserializer extends StdScalarDeserializer<JSONArray> {
	
	private static final long serialVersionUID = 428760301825551510L;
	
	private static Logger logger = LoggerFactory.getLogger(JsonArrayDeserializer.class.toString());

    public JsonArrayDeserializer() {
        super(JsonArrayDeserializer.class);
    }

	@Override
	public JSONArray deserialize(com.fasterxml.jackson.core.JsonParser jsonParser, DeserializationContext ctxt)
	        throws IOException, JsonProcessingException {
		 try {
	            return new JSONArray(jsonParser.readValueAsTree().toString());
	        } catch (JSONException e) {
	        	logger.error("error deserialize JSONArray");
	        }
	        return null;
	}
}
