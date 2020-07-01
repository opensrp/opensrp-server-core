package org.opensrp.util;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opensrp.domain.CSVRowConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONCSVUtil {

    /**
     * construct a json object given the configuration set and the object set
     *
     * @param csvValues
     * @param configGroup
     * @return
     */
    public static JSONObject toJSON(Map<String, String> csvValues, Map<String, List<CSVRowConfig>> configGroup) {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> value :
                csvValues.entrySet()) {
            List<CSVRowConfig> configs = configGroup.get(value.getKey());
            for(CSVRowConfig config : configs){
                if (config == null)
                    throw new IllegalStateException("Column" + value.getKey() + " is missing a config");

                if (config.validate(value.getValue())) {
                    if (StringUtils.isNotBlank(value.getValue()))
                        addNodeToJson(config.getFieldMapping(), value.getValue(), jsonObject);
                }else{
                    throw new IllegalStateException("CSV has an invalid value for field " + config.getColumnName() + " mapping " + config.getFieldMapping());
                }
            }
        }
        return jsonObject;
    }

    /**
     * Converts a json object to a string array
     *
     * @param jsonObject
     * @param fieldMapping
     * @return
     */
    public static List<String> jsonToString(JSONObject jsonObject, List<String> fieldMapping) {
        List<String> values = new ArrayList<>();
        for(String mapping :  fieldMapping){
            values.add(readNodeFromJson(mapping, jsonObject));
        }
        return values;
    }

    public static String readNodeFromJson(String nodePath, JSONObject jsonObject) {
        String[] nodeAddress = nodePath.split("\\.");

        JSONObject currentNode = jsonObject;
        int pos = 0;
        while (pos < nodeAddress.length) {
            String node = nodeAddress[pos];
            // check if array
            int arrayPosition = -1;
            if (node.contains("[") && node.contains("]")) {
                // get array position
                arrayPosition = getNodeIndex(node);
                node = node.substring(0, node.indexOf("["));
            }

            if (arrayPosition > -1) {
                // add a json array to the position and
                JSONArray jsonArray = currentNode.has(node) ? currentNode.getJSONArray(node) : null;
                if(jsonArray != null && jsonArray.length() > arrayPosition){

                    if (pos != (nodeAddress.length - 1)) {
                        currentNode = jsonArray.getJSONObject(arrayPosition);
                    }else{
                        return jsonArray.getString(arrayPosition);
                    }
                }else{
                    return "";
                }
            } else if (pos == (nodeAddress.length - 1)) {
                // add a string if its the very last element
                if (currentNode.has(node))
                    return currentNode.get(node).toString();

            } else {
                // add the object as a json node
                if(currentNode.has(node)) {
                    currentNode = currentNode.getJSONObject(node);
                }else{
                    return "";
                }
            }

            pos++;
        }
        return null;
    }

    private static int getNodeIndex(String node) {
        int arrayPosition = -1;
        Matcher m = Pattern.compile("\\[(.*?)\\]").matcher(node);
        if (m.find()) {
            String strPosition = m.group(1);
            if (StringUtils.isNotBlank(strPosition))
                arrayPosition = Integer.parseInt(strPosition);
        }

        return arrayPosition;
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
                arrayPosition = getNodeIndex(node);
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
