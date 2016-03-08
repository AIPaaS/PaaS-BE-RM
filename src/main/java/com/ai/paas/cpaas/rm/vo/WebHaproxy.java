package com.ai.paas.cpaas.rm.vo;

import java.io.Serializable;
import java.util.List;

public class WebHaproxy implements Serializable {

  private String virtualIp;
  private Boolean loadOnly;
  private List<MesosInstance> hosts;

  public String getVirtualIp() {
    return virtualIp;
  }

  public void setVirtualIp(String virtualIp) {
    this.virtualIp = virtualIp;
  }

  public Boolean getLoadOnly() {
    return loadOnly;
  }

  public void setLoadOnly(Boolean loadOnly) {
    this.loadOnly = loadOnly;
  }

  public List<MesosInstance> getHosts() {
    return hosts;
  }

  public void setHosts(List<MesosInstance> hosts) {
    this.hosts = hosts;
  }

  private static final long serialVersionUID = 1L;

}
