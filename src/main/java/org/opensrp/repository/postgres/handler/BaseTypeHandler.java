package org.opensrp.repository.postgres.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;
import org.opensrp.util.DateTimeDeserializer;
import org.opensrp.util.DateTimeSerializer;

public class BaseTypeHandler {


    public static ObjectMapper mapper;

    protected BaseTypeHandler() {
        createObjectMapper();
    }

    public synchronized static ObjectMapper createObjectMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            applyDefaultConfiguration(mapper);
        }
        SimpleModule dateTimeModule = new SimpleModule("DateTimeModule");
        dateTimeModule.addDeserializer(DateTime.class, new DateTimeDeserializer());
        dateTimeModule.addSerializer(DateTime.class, new DateTimeSerializer());
        mapper.registerModule(dateTimeModule);
        return mapper;
    }

    private static void applyDefaultConfiguration(ObjectMapper om) {
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


}
