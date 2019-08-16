package org.opensrp.domain;

import edu.umd.cs.findbugs.annotations.NonNull;

public class IdVersionTuple {
    public int id;
    public String version;

    public IdVersionTuple(int id, @NonNull String version) {
        this.id = id;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
