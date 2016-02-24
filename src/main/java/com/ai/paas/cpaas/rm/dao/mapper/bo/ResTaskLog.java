package com.ai.paas.cpaas.rm.dao.mapper.bo;

import java.sql.Timestamp;

public class ResTaskLog {
    private Long logId;

    private Integer jobId;

    private Integer taskId;

    private String logCnt;

    private Timestamp logTime;

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getLogCnt() {
        return logCnt;
    }

    public void setLogCnt(String logCnt) {
        this.logCnt = logCnt == null ? null : logCnt.trim();
    }

    public Timestamp getLogTime() {
        return logTime;
    }

    public void setLogTime(Timestamp logTime) {
        this.logTime = logTime;
    }
}