package org.opensrp.domain;

import java.io.Serializable;

public class TaskCount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String code;

    private long actualCount;

    private long expectedCount;

    private long missingCount;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getActualCount() {
        return actualCount;
    }

    public void setActualCount(long actualCount) {
        this.actualCount = actualCount;
    }

    public long getExpectedCount() {
        return expectedCount;
    }

    public void setExpectedCount(long expectedCount) {
        this.expectedCount = expectedCount;
    }

    public long getMissingCount() {
        return missingCount;
    }

    public void setMissingCount(long missingCount) {
        this.missingCount = missingCount;
    }
}
