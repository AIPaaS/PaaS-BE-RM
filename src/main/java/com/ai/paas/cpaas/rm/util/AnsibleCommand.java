package com.ai.paas.cpaas.rm.util;

import java.io.Serializable;
import java.util.List;

public class AnsibleCommand implements Serializable{

	private String fileName;
	private String userName;
	private List<String> extraVars;
	@Override
	public String toString() {
		//ansibleList.add("ansible-playbook zookeeperuser.yml  --user=root --extra-vars \"ansible_ssh_pass=abc@123\" ");
		StringBuffer ansibleCommand=new StringBuffer();
		ansibleCommand.append("ansible-playbook ");
		ansibleCommand.append(this.getFileName()).append(" ");
		ansibleCommand.append("--user=").append(this.getUserName()).append(" ");
		ansibleCommand.append("--extra-vars \"");
		for(String var:this.getExtraVars())
		{
			ansibleCommand.append(var).append(" ");
		}
		ansibleCommand.append("\"");
		return ansibleCommand.toString();
	}
	public AnsibleCommand(String fileName, String userName,
			List<String> extraVars) {
		super();
		this.fileName = fileName;
		this.userName = userName;
		this.extraVars = extraVars;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public List<String> getExtraVars() {
		return extraVars;
	}
	public void setExtraVars(List<String> extraVars) {
		this.extraVars = extraVars;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	private static final long serialVersionUID = -9089855391140216498L;

}
