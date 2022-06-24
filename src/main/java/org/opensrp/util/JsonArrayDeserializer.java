package org.opensrp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class JsonArrayDeserializer extends StdScalarDeserializer<JSONArray> {

    private static final long serialVersionUID = 428760301825551510L;

    private static Logger logger = LogManager.getLogger(JsonArrayDeserializer.class.toString());

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
