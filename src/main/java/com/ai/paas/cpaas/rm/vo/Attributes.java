package com.ai.paas.cpaas.rm.vo;

import java.io.Serializable;

public class Attributes implements Serializable {

  private String zone;
  private String network;

  public String getZone() {
    return zone;
  }

  public void setZone(String zone) {
    this.zone = zone;
  }

  public String getNetwork() {
    return network;
  }

  public void setNetwork(String network) {
    this.network = network;
  }

  private static final long serialVersionUID = 1L;

}
