package com.ai.paas.cpaas.rm.dao.mapper.bo;

public class ResInstanceProps {
    private Integer keyId;

    private String keyValue;

    private String clusterId;

    private Integer state;

    private Byte[] keyCode;

    public Integer getKeyId() {
        return keyId;
    }

    public void setKeyId(Integer keyId) {
        this.keyId = keyId;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue == null ? null : keyValue.trim();
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId == null ? null : clusterId.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Byte[] getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(Byte[] keyCode) {
        this.keyCode = keyCode;
    }
}