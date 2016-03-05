package com.ai.paas.cpaas.rm.dao.mapper.bo;

import java.sql.Timestamp;

public class ResJobDetail {
    private Integer taskId;

    private String clusterId;

    private Integer typeId;

    private Integer taskState;

    private Integer taskOrder;

    private Timestamp taskStartTime;

    private Timestamp taskEndTime;

    private String taskPlaybook;

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId == null ? null : clusterId.trim();
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getTaskState() {
        return taskState;
    }

    public void setTaskState(Integer taskState) {
        this.taskState = taskState;
    }

    public Integer getTaskOrder() {
        return taskOrder;
    }

    public void setTaskOrder(Integer taskOrder) {
        this.taskOrder = taskOrder;
    }

    public Timestamp getTaskStartTime() {
        return taskStartTime;
    }

    public void setTaskStartTime(Timestamp taskStartTime) {
        this.taskStartTime = taskStartTime;
    }

    public Timestamp getTaskEndTime() {
        return taskEndTime;
    }

    public void setTaskEndTime(Timestamp taskEndTime) {
        this.taskEndTime = taskEndTime;
    }

    public String getTaskPlaybook() {
        return taskPlaybook;
    }

    public void setTaskPlaybook(String taskPlaybook) {
        this.taskPlaybook = taskPlaybook == null ? null : taskPlaybook.trim();
    }
}