package org.opensrp.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;

public class LocalDateSerializerTest {

    @Test
    public void testSerializer() throws IOException {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        LocalDate localDate = new LocalDate();
        LocalDateSerializer localDateSerializer = new LocalDateSerializer();
        SerializerProvider provider = Mockito.mock(SerializerProvider.class);
        JsonGenerator jsonGen = Mockito.mock(JsonGenerator.class);
        localDateSerializer.serialize(localDate, jsonGen, provider);
        Mockito.verify(jsonGen, Mockito.atLeastOnce()).writeString(argumentCaptor.capture());
    }
}
