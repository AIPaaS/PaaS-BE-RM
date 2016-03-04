package com.ai.paas.cpaas.rm.vo;

import java.io.Serializable;

public class MesosSlave extends MesosInstance implements Serializable {


  private String attributes;
  private int cpuTotal;
  private int cpuOffer;
  private long memTotal;
  private long memOffer;


  public String getAttributes() {
    return attributes;
  }

  public void setAttributes(String attributes) {
    this.attributes = attributes;
  }

  public int getCpuTotal() {
    return cpuTotal;
  }

  public void setCpuTotal(int cpuTotal) {
    this.cpuTotal = cpuTotal;
  }

  public int getCpuOffer() {
    return cpuOffer;
  }

  public void setCpuOffer(int cpuOffer) {
    this.cpuOffer = cpuOffer;
  }

  public long getMemTotal() {
    return memTotal;
  }

  public void setMemTotal(long memTotal) {
    this.memTotal = memTotal;
  }

  public long getMemOffer() {
    return memOffer;
  }

  public void setMemOffer(long memOffer) {
    this.memOffer = memOffer;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  private static final long serialVersionUID = -2768375632744004953L;



}
