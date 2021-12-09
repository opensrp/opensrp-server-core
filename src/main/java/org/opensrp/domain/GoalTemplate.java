package org.opensrp.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalTemplate implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String description;
    private String priority;
    @JsonProperty("target")
    @SerializedName("target")
    private List<TargetTemplate> targets;

}
