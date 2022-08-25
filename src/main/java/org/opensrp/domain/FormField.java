package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class FormField {

    private String name;

    private String bind;

    private boolean shouldLoadValue;

    public FormField() {
    }

    public FormField(String name, String bind) {
        this.name = name;

        this.bind = bind;
    }

    public String name() {
        return name;
    }

    public String bind() {
        return bind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    public boolean isShouldLoadValue() {
        return shouldLoadValue;
    }

    public void setShouldLoadValue(boolean shouldLoadValue) {
        this.shouldLoadValue = shouldLoadValue;
    }

}
