package org.opensrp.domain;

import java.io.Serializable;

public class LocationDetails implements Serializable {
    private static final long serialVersionUID = 7360003982578282029L;

    private String identifier;

    private String name;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
