package com.ai.paas.cpaas.rm.util;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.ai.paas.ipaas.PaasException;

public interface ExecuteEnv {



  public String executeFile(String filename, String content, String aid)
      throws ClientProtocolException, IOException, PaasException;

  public void uploadFile(String filename, String content, String aid)
      throws ClientProtocolException, IOException, PaasException;

  public String executeCommand(String content, String aid) throws ClientProtocolException,
      IOException, PaasException;

}
