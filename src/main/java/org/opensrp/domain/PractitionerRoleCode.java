package org.opensrp.domain;

import java.io.Serializable;

public class PractitionerRoleCode implements Serializable {

    private static final long serialVersionUID = 5814439241291810987L;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
