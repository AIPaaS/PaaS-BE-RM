package com.ai.paas.cpaas.rm.dao.mapper.bo;

public class ResTaskType extends ResTaskTypeKey {
    private String typeName;

    private Integer typeOrder;

    private String typeDesc;

    private String typeTemplete;

    private Integer typeState;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName == null ? null : typeName.trim();
    }

    public Integer getTypeOrder() {
        return typeOrder;
    }

    public void setTypeOrder(Integer typeOrder) {
        this.typeOrder = typeOrder;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc == null ? null : typeDesc.trim();
    }

    public String getTypeTemplete() {
        return typeTemplete;
    }

    public void setTypeTemplete(String typeTemplete) {
        this.typeTemplete = typeTemplete == null ? null : typeTemplete.trim();
    }

    public Integer getTypeState() {
        return typeState;
    }

    public void setTypeState(Integer typeState) {
        this.typeState = typeState;
    }
}