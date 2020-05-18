package org.opensrp.util;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.domain.CSVRowConfig;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONCSVUtil {

    /**
     * construct a json object given the configuration set and the object set
     *
     * @param csvValues
     * @param configs
     * @return
     */
    public static JSONObject toJSON(Map<String, String> csvValues, Map<String, CSVRowConfig> configs) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> value :
                csvValues.entrySet()) {
            CSVRowConfig config = configs.get(value.getKey());
            if (config == null)
                throw new IllegalStateException("Column" + value.getKey() + " is missing a config");

            if (config.validate(value.getValue())) {
                if (StringUtils.isNotBlank(value.getValue()))
                    addNodeToJson(config.getFieldMapping(), value.getValue(), jsonObject);
            }
        }
        return jsonObject;
    }

    /**
     * inserts a node to the path of the variable
     *
     * @param nodePath
     * @param value
     * @param jsonObject
     */
    public static void addNodeToJson(String nodePath, String value, JSONObject jsonObject) {
        String[] nodeAddress = nodePath.split("\\.");

        JSONObject currentNode = jsonObject;
        int pos = 0;
        while (pos < nodeAddress.length) {
            String node = nodeAddress[pos];
            // check if array
            int arrayPosition = -1;
            if (node.contains("[") && node.contains("]")) {
                // get array position
                Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(node);
                if (m.find()) {
                    String strPosition = m.group(1);
                    if (StringUtils.isNotBlank(strPosition))
                        arrayPosition = Integer.parseInt(strPosition);
                }

                node = node.substring(0, node.indexOf("["));
            }

            if (arrayPosition > -1) {
                // add a json array to the position and
                JSONArray jsonArray = currentNode.has(node) ? currentNode.getJSONArray(node) : new JSONArray();

                // add an element in that position
                if (pos == (nodeAddress.length - 1)) {
                    // add a string on final node
                    jsonArray.put(arrayPosition, value);
                    currentNode.put(node, jsonArray);
                } else {
                    JSONObject object = (jsonArray.length() > arrayPosition) ? jsonArray.getJSONObject(arrayPosition) : new JSONObject();
                    jsonArray.put(arrayPosition, object);
                    currentNode.put(node, jsonArray);
                    currentNode = object;
                }

            } else if (pos == (nodeAddress.length - 1)) {
                // add a string if its the very last element
                currentNode.put(node, value);
            } else {
                // add the object as a json node
                JSONObject content = currentNode.has(node) ? currentNode.getJSONObject(node) : new JSONObject();
                currentNode.put(node, content);
                // add a new node
                currentNode = content;
            }
            pos++;
        }
    }
}
