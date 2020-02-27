package org.opensrp.domain;

import java.util.List;

public class AllIdsModel {

    List<String> identifiers;

    Long lastServerVersion;

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public Long getLastServerVersion() {
        return lastServerVersion;
    }

    public void setLastServerVersion(Long lastServerVersion) {
        this.lastServerVersion = lastServerVersion;
    }
}
