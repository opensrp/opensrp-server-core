package org.opensrp.util;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class JsonArrayDeserializer extends StdScalarDeserializer<JSONArray> {

    public JsonArrayDeserializer() {
        super(JsonArrayDeserializer.class);
    }

    @Override
    public JSONArray deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        try {
            return new JSONArray(jsonParser.readValueAsTree().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
