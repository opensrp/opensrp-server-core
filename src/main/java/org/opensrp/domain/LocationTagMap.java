package org.opensrp.domain;

import java.io.Serializable;

public class LocationTagMap implements Serializable {

    private static final long serialVersionUID = -8367551045898354954L;

    private Long locationId;

    private Long locationTagId;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Long getLocationTagId() {
        return locationTagId;
    }

    public void setLocationTagId(Long locationTagId) {
        this.locationTagId = locationTagId;
    }

}
