package org.opensrp.repository.postgres.handler;

import org.ektorp.impl.StdObjectMapperFactory;
import org.joda.time.DateTime;
import org.opensrp.util.DateTimeDeserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class BaseTypeHandler {
	
	public static final ObjectMapper mapper = new StdObjectMapperFactory().createObjectMapper();;
	
	protected BaseTypeHandler() {
		SimpleModule dateTimeModule = new SimpleModule("DateTimeModule");
		dateTimeModule.addDeserializer(DateTime.class, new DateTimeDeserializer());
		mapper.registerModule(dateTimeModule);
	}
	
}
