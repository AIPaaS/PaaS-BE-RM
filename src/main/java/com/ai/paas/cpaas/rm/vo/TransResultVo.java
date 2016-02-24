package com.ai.paas.cpaas.rm.vo;

import java.io.Serializable;

public class TransResultVo implements Serializable{

	private static final long serialVersionUID = 4363271629585918800L;
	
	private String code;
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	private String msg;
}
