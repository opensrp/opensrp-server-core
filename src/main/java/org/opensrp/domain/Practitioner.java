package org.opensrp.domain;

import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Date;

public class Practitioner {

    @JsonProperty
    private String identifier;

    @JsonProperty
    private Boolean active;

    @JsonProperty
    private String name;

    @JsonProperty
    @SerializedName("user_id")
    private String userId;

    @JsonProperty
    @SerializedName("username")
    private String userName;

    @JsonProperty
    @SerializedName("date_deleted")
    private Date dateDeleted;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Date dateDeleted) {
        this.dateDeleted = dateDeleted;
    }
}
