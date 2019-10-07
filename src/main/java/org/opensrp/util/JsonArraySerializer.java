package org.opensrp.util;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.json.JSONArray;

import java.io.IOException;

public class JsonArraySerializer extends SerializerBase<JSONArray>  {


    public JsonArraySerializer() {
        super(JSONArray.class);
    }

    @Override
    public void serialize(JSONArray value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonGenerationException {
        gen.writeRawValue(value.toString());
    }
}
