package com.ai.paas.cpaas.rm.vo;

import java.io.Serializable;

public class MesosInstance implements Serializable{

	private int id;
	private String ip;
	private String root;
	private String passwd;
	private static final long serialVersionUID = -1657055000584908530L;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getRoot() {
		return root;
	}
	public void setRoot(String root) {
		this.root = root;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
