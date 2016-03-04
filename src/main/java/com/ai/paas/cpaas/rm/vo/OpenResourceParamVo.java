package com.ai.paas.cpaas.rm.vo;

import java.io.Serializable;
import java.util.List;

public class OpenResourceParamVo implements Serializable {


  private String clusterName;
  private String dataCenter;
  private String domain;
  private String externalDomain;
  private String loadVirtualIP;
  private String imagePath;
  private Boolean useAgent;
  private String aid;

  public String getClusterId() {
    return clusterId;
  }

  public void setClusterId(String clusterId) {
    this.clusterId = clusterId;
  }



  private String clusterId;

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public Boolean getUseAgent() {
    return useAgent;
  }

  public void setUseAgent(Boolean useAgent) {
    this.useAgent = useAgent;
  }



  private List<MesosInstance> mesosMaster;
  private List<MesosSlave> mesosSlave;
  private List<Attributes> attributesList;

  public String getClusterName() {
    return clusterName;
  }

  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  public String getDataCenter() {
    return dataCenter;
  }

  public void setDataCenter(String dataCenter) {
    this.dataCenter = dataCenter;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public String getExternalDomain() {
    return externalDomain;
  }

  public void setExternalDomain(String externalDomain) {
    this.externalDomain = externalDomain;
  }

  public String getLoadVirtualIP() {
    return loadVirtualIP;
  }

  public void setLoadVirtualIP(String loadVirtualIP) {
    this.loadVirtualIP = loadVirtualIP;
  }

  public List<MesosInstance> getMesosMaster() {
    return mesosMaster;
  }

  public void setMesosMaster(List<MesosInstance> mesosMaster) {
    this.mesosMaster = mesosMaster;
  }

  public List<MesosSlave> getMesosSlave() {
    return mesosSlave;
  }

  public void setMesosSlave(List<MesosSlave> mesosSlave) {
    this.mesosSlave = mesosSlave;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }



  public List<Attributes> getAttributesList() {
    return attributesList;
  }

  public void setAttributesList(List<Attributes> attributesList) {
    this.attributesList = attributesList;
  }



  public String getAid() {
    return aid;
  }

  public void setAid(String aid) {
    this.aid = aid;
  }



  private static final long serialVersionUID = 5671311406883071939L;



}
