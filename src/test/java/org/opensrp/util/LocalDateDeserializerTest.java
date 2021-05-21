package org.opensrp.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class LocalDateDeserializerTest {

	@Test
	public void testDeserializer() throws IOException {
		LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer();
		DeserializationContext context = Mockito.mock(DeserializationContext.class);
		JsonParser jsonParser = Mockito.mock(JsonParser.class);
		Mockito.doReturn(JsonToken.VALUE_STRING).when(jsonParser).getCurrentToken();
		Mockito.doReturn("2021-05-21").when(jsonParser).getText();
		Assert.assertNotNull(localDateDeserializer.deserialize(jsonParser, context));
		Mockito.doReturn(null).when(jsonParser).getCurrentToken();
		Assert.assertNull(localDateDeserializer.deserialize(jsonParser, context));

	}
}
