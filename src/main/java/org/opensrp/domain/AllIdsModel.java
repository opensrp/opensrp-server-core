package org.opensrp.domain;

import java.util.List;

public class AllIdsModel {

    private List<String> identifiers;

    private Long lastServerVersion;

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
