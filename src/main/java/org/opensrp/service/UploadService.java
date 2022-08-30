package org.opensrp.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.domain.CSVRowConfig;
import org.opensrp.repository.LocationRepository;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;
import org.opensrp.domain.setting.Setting;
import org.opensrp.domain.setting.SettingConfiguration;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.SettingRepository;
import org.opensrp.search.SettingSearchBean;
import org.opensrp.search.UploadValidationBean;
import org.smartregister.domain.PhysicalLocation;
import org.smartregister.utils.DateTimeTypeConverter;
import org.opensrp.util.JSONCSVUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UploadService {

    //TODO : Add annotations of spring-security

    public static final String CLIENT = "client";
    public static final String EVENT = "event";
    public static final String DEFAULT_RESIDENCE = "default_residence";
    private static final Logger logger = LogManager.getLogger(UploadService.class.toString());
    public static String CSV_UPLOAD_SETTING = "csv_upload_config";
    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
    private SettingRepository settingRepository;

    private ClientsRepository clientsRepository;

    private LocationRepository locationRepository;

    @Autowired
    public void setSettingRepository(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Autowired
    public void setClientsRepository(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    @Autowired
    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Validates the CSV fields provided by checking them against the config files. The code also verify that clients
     * with unique ID specified must exist in the DB
     *
     * @param csvClients
     * @param eventName
     * @param uniqueIDKey
     * @return
     */
    public UploadValidationBean validateFieldValues(List<Map<String, String>> csvClients, String eventName, String uniqueIDKey) {
        UploadValidationBean validationBean = new UploadValidationBean();
        validationBean.setTotalRows(csvClients.size());
        validationBean.setHeaderColumns(csvClients.size() > 0 ? csvClients.get(0).size() : 0);

        List<Pair<Client, Event>> totalRows = new ArrayList<>();
        Map<String, List<CSVRowConfig>> configs = getCSVConfig(eventName)
                .stream()
                .collect(Collectors.groupingBy(CSVRowConfig::getColumnName));

        if (csvClients.size() > 0 && csvClients.get(0).size() != configs.size())
            throw new IllegalArgumentException("The number of rows must be equal to the mappings size");

        int rowNumber = 1;
        for (Map<String, String> csvValue : csvClients) {
            try {
                JSONObject jsonObject = JSONCSVUtil.toJSON(csvValue, configs);

                Client client = null;
                Event event = null;
                PhysicalLocation dbStructure = null;

                String baseEntityID = null;
                if (jsonObject.has(CLIENT)) {
                    client = gson.fromJson(jsonObject.get(CLIENT).toString(), Client.class);
                    baseEntityID = getClientsBaseEntityID(client, uniqueIDKey);
                    client.setBaseEntityId(baseEntityID);
                    // Get structure by Id
                    String uploadedStructureId = (String) client.getAttribute(DEFAULT_RESIDENCE);
                    dbStructure = locationRepository.getStructure(uploadedStructureId, false);
                    if (dbStructure == null) {
                        throw new IllegalArgumentException("Structure with id " + uploadedStructureId + " does not exist");
                    }
                }

                if (jsonObject.has(EVENT)) {
                    event = gson.fromJson(jsonObject.get(EVENT).toString(), Event.class);
                    event.setBaseEntityId(baseEntityID);
                    if (StringUtils.isNotBlank(dbStructure.getProperties().getParentId())) {
                        event.setLocationId(dbStructure.getProperties().getParentId());
                    }
                }

                if (StringUtils.isBlank(baseEntityID)) {
                    validationBean.setRowsToCreate(validationBean.getRowsToCreate() + 1);
                } else {
                    validationBean.setRowsToUpdate(validationBean.getRowsToUpdate() + 1);
                }

                totalRows.add(Pair.of(client, event));
            } catch (Exception e) {
                validationBean.addError(e.getMessage() + " on row " + rowNumber);
                logger.error(e.getMessage());
            }
            rowNumber++;
        }

        validationBean.setAnalyzedData(totalRows);

        return validationBean;
    }

    public String getClientsBaseEntityID(Client client, String uniqueIDKey) {
        if (client == null || StringUtils.isBlank(client.getIdentifier(uniqueIDKey)))
            return null;

        String uniqueID = client.getIdentifier(uniqueIDKey);
        if (StringUtils.isNotBlank(uniqueID)) {
            List<Client> cl = clientsRepository.findAllByIdentifier(uniqueID);
            if (cl.size() == 1) {
                return cl.get(0).getBaseEntityId();
            } else if (cl.size() > 1) {
                throw new IllegalArgumentException("Multiple clients with identifier " + uniqueID + " exist.");
            } else {
                throw new IllegalStateException("Client not found for id " + uniqueID);
            }
        }
        return null;
    }

    public List<CSVRowConfig> getCSVConfig(String eventName) {
        SettingSearchBean settingSearchBean = new SettingSearchBean();
        settingSearchBean.setIdentifier(CSV_UPLOAD_SETTING);
        settingSearchBean.setServerVersion(0L);
        List<SettingConfiguration> configurations = settingRepository.findSettings(settingSearchBean, null);

        int count = 0;
        while (count < configurations.size()) {
            SettingConfiguration configuration = configurations.get(count);
            if (CSV_UPLOAD_SETTING.equals(configuration.getIdentifier())) {
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

            count++;
        }

        return new ArrayList<>();
    }
}
