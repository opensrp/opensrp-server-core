package org.opensrp.domain;

import java.io.Serializable;

public class PeriodTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    private String start;
    private String end;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
