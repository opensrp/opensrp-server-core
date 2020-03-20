package org.opensrp.domain;

import edu.umd.cs.findbugs.annotations.NonNull;

public class IdVersionTuple {
    public long id;
    public String version;

    public IdVersionTuple(long id, @NonNull String version) {
        this.id = id;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
