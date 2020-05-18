package org.opensrp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.domain.CSVRowConfig;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.repository.SettingRepository;
import org.opensrp.search.SettingSearchBean;
import org.opensrp.util.DateTimeTypeConverter;
import org.opensrp.util.JSONCSVUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UploadService {

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();

    public static final String CLIENT = "client";
    public static final String EVENT = "event";

    private static Logger logger = LoggerFactory.getLogger(UploadService.class.toString());
    public static String CSV_UPLOAD_SETTING = "csv_upload_config";

    private SettingRepository settingRepository;

    @Autowired
    public void setSettingRepository(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    /**
     * Generates a list of clients by processing byte content
     *
     * @param csvClients
     * @param eventName
     * @return
     */
    public List<Pair<Client, Event>> getClientFromCSVBytes(List<Map<String, String>> csvClients, String eventName) {

        List<Pair<Client, Event>> totalRows = new ArrayList<>();
        Map<String, CSVRowConfig> configs = getCSVConfig(eventName)
                .stream()
                .collect(Collectors.toMap(CSVRowConfig::getColumnName,
                        Function.identity()));

        for (Map<String, String> csvValue : csvClients) {
            JSONObject jsonObject = JSONCSVUtil.toJSON(csvValue, configs);

            Client client = null;
            Event event = null;

            if (jsonObject.has(CLIENT))
                client = gson.fromJson(jsonObject.get(CLIENT).toString(), Client.class);

            if (jsonObject.has(EVENT))
                event = gson.fromJson(jsonObject.toString(), Event.class);

            totalRows.add(Pair.of(client, event));
        }

        return totalRows;
    }

    public List<CSVRowConfig> getCSVConfig(String eventName) {
        SettingSearchBean settingSearchBean = new SettingSearchBean();
        settingSearchBean.setIdentifier(CSV_UPLOAD_SETTING);
        settingSearchBean.setServerVersion(0L);
        List<SettingConfiguration> configurations = settingRepository.findSettings(settingSearchBean);
        if (configurations.size() == 1) {
            SettingConfiguration configuration = configurations.get(0);
            Setting settings = configuration.getSettings().get(0);
            JSONArray values = settings.getValues();
            int forms = 0;
            while (forms < values.length()) {
                JSONObject jsObject = values.getJSONObject(forms);
                if (jsObject.has(eventName)) {
                    return gson.fromJson(jsObject.get(eventName).toString(),
                            new TypeToken<ArrayList<CSVRowConfig>>() {
                            }.getType());
                }
                forms++;
            }
        }

        return null;
    }
}
