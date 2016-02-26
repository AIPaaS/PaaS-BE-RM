package com.ai.paas.cpaas.rm.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.http.client.ClientProtocolException;

import com.ai.paas.ipaas.PaasException;

public class LocalEnv implements ExecuteEnv {


  @Override
  public String executeFile(String filename, String content) throws ClientProtocolException,
      IOException, PaasException {
    String filepath = TaskUtil.getSystemProperty("filepath");
    // 传输执行文件
    this.uploadFile(filename, content);

    // 更改文件权限
    this.executeCommand("chmod u+x " + filepath + "/" + filename);

    // 执行文件
    String result = this.executeCommand("bash " + filepath + "/" + filename);
    return result;
  }

  @Override
  public String executeCommand(String content) throws ClientProtocolException, IOException,
      PaasException {
    Runtime rt = Runtime.getRuntime();
    String[] commands = {"/bin/sh", "-c"};
    Process proc = rt.exec(commands);
    BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

    BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

    StringBuffer sbuffer = new StringBuffer();
    String s = null;
    while ((s = stdInput.readLine()) != null) {
      sbuffer.append(s);
    }
    while ((s = stdError.readLine()) != null) {
      throw new PaasException(s);
    }
    return sbuffer.toString();
  }

  @Override
  public void uploadFile(String filename, String content) throws ClientProtocolException,
      IOException, PaasException {
    String filepath = TaskUtil.getSystemProperty("filepath");
    try (Writer writer =
        new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath + "/" + filename),
            "utf-8"))) {
      writer.write(content);
    }

  }

}
