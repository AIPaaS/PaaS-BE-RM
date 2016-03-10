package com.ai.paas.cpaas.rm.vo;

import java.io.Serializable;

public class HaproxyInfo implements Serializable {

  private String ip;
  private int port;
  private String user;
  private String pwd;

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPwd() {
    return pwd;
  }

  public void setPwd(String pwd) {
    this.pwd = pwd;
  }

  private static final long serialVersionUID = -5977499979961864076L;

}
