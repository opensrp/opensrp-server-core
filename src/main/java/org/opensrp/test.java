package org.opensrp;

import org.springframework.stereotype.Component;

@Component
public class test {
/*
    public static void main(String arg[]) {

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

        SimpleModule jsonArrayDeserializerModule = new SimpleModule("JsonArrayDeserializerModule", new Version(0, 0, 0, null));
        jsonArrayDeserializerModule.addDeserializer(JSONArray.class, new JsonArrayDeserializer());
        mapper.registerModule(jsonArrayDeserializerModule);

        SimpleModule jsonArraySerializerModule = new SimpleModule("JsonArraySerializerModule", new Version(0, 0, 0, null));
        jsonArrayDeserializerModule.addSerializer(JSONArray.class, new JsonArraySerializer());
        mapper.registerModule(jsonArrayDeserializerModule);

        String settingJsonString = "{\n" +
                "  \"_id\": \"1\",\n" +
                "  \"_rev\": \"v1\",\n" +
                "  \"type\": \"SettingConfiguration\",\n" +
                "  \"teamId\": \"09962f7c-8dab-4dee-96e2-354382aec76a\",\n" +
                "  \"settings\": [\n" +
                "    {\n" +
                "      \"key\": \"location_buffer_radius_in_metres\",\n" +
                "      \"label\": \"Location Buffer\",\n" +
                "      \"value\": \"40\",\n" +
                "      \"description\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"validate_far_structures\",\n" +
                "      \"label\": \"Validate Far Structures\",\n" +
                "      \"value\": false,\n" +
                "      \"description\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"sprayops\",\n" +
                "      \"label\": \"List of Spray Operator names and codes\",\n" +
                "      \"values\": [\n" +
                "        {\n" +
                "          \"SVSOP1\": \"SVSOP Name 1\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"SVSOP2\": \"SVSOP Name 1\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"description\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"key\": \"datacollectors\",\n" +
                "      \"label\": \"List of datacollector names and codes\",\n" +
                "      \"values\": [\n" +
                "        {\n" +
                "          \"SV1\": \"Siavonga Data Collector 1\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"SV2\": \"Siavonga Data Collector 2\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"description\": \"\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"identifier\": \"global_configs\",\n" +
                "  \"serverVersion\": 1551182946512\n" +
                "}";


        String debugstr = "{\"_id\": \"1\", \"_rev\": \"v2\", \"type\": \"SettingConfiguration\", \"teamId\": \"09962f7c-8dab-4dee-96e2-354382aec76a\", \"settings\": [{\"key\": \"location_buffer_radius_in_metres\", \"label\": \"Location Buffer\", \"value\": \"40\", \"description\": \"\"}, {\"key\": \"validate_far_structures\", \"label\": \"Validate Far Structures\", \"value\": \"false\", \"description\": \"\"}, {\"key\": \"sprayops\", \"label\": \"List of Spray Operator names and codes\", \"values\": \"[{\\\"SVSOP1\\\":\\\"SVSOP Name 1\\\"},{\\\"SVSOP2\\\":\\\"SVSOP Name 1\\\"}]\", \"description\": \"\"}, {\"key\": \"datacollectors\", \"label\": \"List of datacollector names and codes\", \"values\": \"[{\\\"SV1\\\":\\\"Siavonga Data Collector 1\\\"},{\\\"SV2\\\":\\\"Siavonga Data Collector 2\\\"}]\", \"description\": \"\"}], \"identifier\": \"global_configs\", \"serverVersion\": 1569944066092}";

        //mapper.readValue(settingJsonString, SettingConfiguration.class);

        //SettingTypeHandler settingTypeHandler = new SettingTypeHandler();
        try {
            SettingConfiguration settingConfiguration = mapper.readValue(settingJsonString, SettingConfiguration.class);
            System.out.println(settingConfiguration.getSettings().get(2).getValues().toString());

            String output =  mapper.writeValueAsString(settingConfiguration);
            System.out.println(output);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

 */
}
