package org.opensrp.domain;

import java.io.Serializable;
import java.util.List;

public class PlanTaskCount implements Serializable {

    private static final long serialVersionUID = 1L;

    private String planIdentifier;

    private List<TaskCount> taskCounts;

    public String getPlanIdentifier() {
        return planIdentifier;
    }

    public void setPlanIdentifier(String planIdentifier) {
        this.planIdentifier = planIdentifier;
    }

    public List<TaskCount> getTaskCounts() {
        return taskCounts;
    }

    public void setTaskCounts(List<TaskCount> taskCounts) {
        this.taskCounts = taskCounts;
    }
}
