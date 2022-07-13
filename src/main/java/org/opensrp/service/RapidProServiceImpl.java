package org.opensrp.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
public class RapidProServiceImpl implements RapidProService {

    private static Logger logger = LogManager.getLogger(RapidProServiceImpl.class.toString());
    HttpClient client = HttpClientBuilder.create().build();
    @Value("#{opensrp['rapidpro.url']}")
    private String rapidproUrl;
    @Value("#{opensrp['rapidpro.token']}")
    private String rapidproToken;

    /**
     * urns - JSON array of URNs to send the message to (array of strings, optional) contacts - JSON
     * array of contact UUIDs to send the message to (array of strings, optional) groups - JSON
     * array of group UUIDs to send the message to (array of strings, optional) text - the text of
     * the message to send (string, limit of 480 characters) channel - the id of the channel to use.
     * Contacts and URNs which can't be reached with this channel are ignored (int, optional)
     *
     * @param urns
     * @param contacts
     * @param groups
     * @param text
     * @param channel
     * @return
     */

    @Override
    public String sendMessage(List<String> urns, List<String> contacts, List<String> groups, String text, String channel) {
        try {
            HttpPost post = new HttpPost();
            if (text == null || text.isEmpty() || text.length() > 480) {
                logger.info("RapidPro: Message character limit of 480 exceeded");
                return "Empty text or text longer than 480 characters not allowed";
            }
            String uri = rapidproUrl + "/api/v1/broadcasts.json";
            post = setPostAuthHeader(uri, post);

            JSONObject jsonParams = new JSONObject();

            if (urns != null && !urns.isEmpty()) {
                jsonParams.put("urns", new JSONArray(urns));
            }
            if (contacts != null && !contacts.isEmpty()) {
                jsonParams.put("contacts", new JSONArray(contacts));
            }
            if (groups != null && !groups.isEmpty()) {
                jsonParams.put("groups", new JSONArray(groups));
            }
            if (channel != null && !channel.isEmpty()) {
                jsonParams.put("channel", channel);
            }

            if (!jsonParams.has("urns") && !jsonParams.has("contacts") && !jsonParams.has("groups")) {
                logger.info("RapidPro: No one to send message to!");
                return "No recipients specified";
            }

            jsonParams.put("text", text);

            StringEntity params = new StringEntity(jsonParams.toString());
            post.setEntity(params);

            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return responseString;
        } catch (Exception e) {
            logger.error("", e);
            return "Exception occurred";
        }
    }

    /**
     * This method creates a contact in rapidpro. The param fieldValues should contain a key urns(a
     * list of URNs you want associated with the contact (java.util.arraylist)) which is required by
     * rapidpro. uuid - the UUID of the contact to update (string) (optional, new contact created if
     * not present). name - the full name of the contact (string, optional).language - the preferred
     * language for the contact (3 letter iso code, optional). group_uuids - a list of the UUIDs of
     * any groups this contact is part of (string array, optional). fields - a hashmap of contact
     * fields you want to set or update on this contact (JSON, optional)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String createContact(Map<String, Object> fieldValues) {
        try {
            HttpPost post = new HttpPost();
            if (fieldValues == null || fieldValues.isEmpty() || !fieldValues.containsKey("urns")) {
                return "Field values cannot be empty and must have urns";
            }
            String uri = rapidproUrl + "/api/v1/contacts.json";
            post = setPostAuthHeader(uri, post);

            JSONObject jsonParams = new JSONObject();
            for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof List) {
                    value = new JSONArray((List<?>) value);
                }
                // create the fields just in case they don't exist first
                if (key.equalsIgnoreCase("fields")) {
                    for (Map.Entry<String, Object> fieldEntrySet : ((Map<String, Object>) value).entrySet()) {
                        String fieldName = fieldEntrySet.getKey();
                        addField(fieldName, null);
                        logger.info("Creating RapidPro field " + fieldName);
                    }
                }
                jsonParams.put(key, value);

            }
            StringEntity params = new StringEntity(jsonParams.toString());
            post.setEntity(params);
            logger.info("Creating RapidPro contact for " + (fieldValues.containsKey("name") ? fieldValues.get("name") : ""));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return responseString;
        } catch (Exception e) {
            logger.error("", e);
            return "Exception occurred";
        }

    }

    private HttpPost setPostAuthHeader(String url, HttpPost post) {
        post.setURI(URI.create(url));
        // add header Authorization: Token YOUR_API_TOKEN_GOES_HERE
        post.setHeader("Authorization", " Token " + rapidproToken);
        post.addHeader("content-type", "application/json");
        post.addHeader("Accept", "application/json");
        return post;
    }

    private HttpDelete setDeleteAuthHeader(String url, HttpDelete delete) {
        delete.setURI(URI.create(url));
        // add header Authorization: Token YOUR_API_TOKEN_GOES_HERE
        delete.setHeader("Authorization", " Token " + rapidproToken);
        delete.addHeader("content-type", "application/json");
        delete.addHeader("Accept", "application/json");
        return delete;
    }

    @Override
    public String createGroup(String name) {
        // FIXME Not currently supported in rapidpro
        return "Exception occurred";
    }

    /**
     * This method adds a field to rapidpro valuetype is a required field in rapidpro so if empty
     * the default is set to T-text other acceptable fieldtypes are: N - Decimal Number D - Datetime
     * S - State I - District label is the field name as it will appear in rapidpro and it's used to
     * generate the field key e.g a field label like woman name translates to woman_name key
     */
    @Override
    public String addField(String label, String valueType) {
        try {
            HttpPost post = new HttpPost();
            if (label == null || label.isEmpty()) {
                return "Field label is required";
            }
            String uri = rapidproUrl + "/api/v1/fields.json";
            post = setPostAuthHeader(uri, post);
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("label", label);
            jsonParams.put("value_type", valueType == null || valueType.isEmpty() ? "T" : valueType);

            StringEntity params = new StringEntity(jsonParams.toString());
            post.setEntity(params);
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return responseString;
        } catch (Exception e) {
            logger.error("", e);
            return "Exception occurred";
        }
    }

    @Override
    public int deleteContact(String uuid) {
        try {
            HttpDelete delete = new HttpDelete();
            String uri = rapidproUrl + "/api/v1/contacts.json?uuid=" + uuid;
            delete = setDeleteAuthHeader(uri, delete);
            HttpResponse response = client.execute(delete);
            int status = response.getStatusLine().getStatusCode();
            return status;

        } catch (Exception e) {
            logger.error("", e);
            return HttpStatus.SC_INTERNAL_SERVER_ERROR;
        }
    }
}
