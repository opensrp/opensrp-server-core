package org.opensrp.domain;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * CSV row configuration stored in the settings table that can be serialized
 */
public class CSVRowConfig {

    @SerializedName("column_name")
    private String columnName;

    @SerializedName("field_mapping")
    private String fieldMapping;

    private boolean required;

    private String regex;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getFieldMapping() {
        return fieldMapping;
    }

    public void setFieldMapping(String fieldMapping) {
        this.fieldMapping = fieldMapping;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public boolean validate(String value){
        if(required && StringUtils.isBlank(value))
            return false;

        return StringUtils.isBlank(value) || StringUtils.isBlank(regex) || Pattern.matches(regex, value);
    }
}
