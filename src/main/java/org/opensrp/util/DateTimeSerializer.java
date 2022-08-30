/**
 *
 */
package org.opensrp.util;

import java.io.IOException;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;

/**
 * @author Samuel Githengi created on 02/20/20
 */
public class DateTimeSerializer extends StdScalarSerializer<DateTime> {

    private static final long serialVersionUID = -6797135374503717200L;

    public DateTimeSerializer() {
        super(DateTime.class);
    }

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(value.toString());
    }

}
