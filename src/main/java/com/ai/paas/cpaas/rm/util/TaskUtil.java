package com.ai.paas.cpaas.rm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.springframework.batch.core.scope.context.ChunkContext;

import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class TaskUtil {

  // public static String filepath = "/root/test";
  private static final AtomicInteger counter = new AtomicInteger();

  public static int nextValue() {
    return counter.getAndIncrement();
  }


  public static OpenResourceParamVo createOpenParam(ChunkContext chunkContext) {
    String openParameter =
        (String) chunkContext.getStepContext().getJobParameters().get("openParameter");
    Gson gson = new Gson();
    OpenResourceParamVo openParam = gson.fromJson(openParameter, OpenResourceParamVo.class);
    return openParam;
  }

  public static StringBuffer createBashFile() {
    StringBuffer shellContext = new StringBuffer();
    shellContext.append("#!/bin/bash");
    shellContext.append("\n");
    return shellContext;
  }

  // ÃÜÂëÉú³É´ý¸Ä
  public static String generatePasswd() {
    return new SessionIdentifierGenerator().nextSessionId();
  }

  public static void uploadFile(String filename, String content, Boolean useAgent)
      throws ClientProtocolException, IOException, PaasException {
    ExecuteEnv executeEnv = TaskUtil.genEnv(useAgent);
    executeEnv.uploadFile(filename, content);
  }

  public static void executeFile(String filename, String content, Boolean useAgent)
      throws ClientProtocolException, IOException, PaasException {
    ExecuteEnv executeEnv = TaskUtil.genEnv(useAgent);
    executeEnv.executeFile(filename, content);
  }

  public static void executeCommand(String command, Boolean useAgent)
      throws ClientProtocolException, IOException, PaasException {
    ExecuteEnv executeEnv = TaskUtil.genEnv(useAgent);
    executeEnv.executeCommand(command);
  }

  public static ExecuteEnv genEnv(Boolean useAgent) {
    ExecuteEnv executeEnv = null;
    if (useAgent) {
      executeEnv = new RemoteEnv();
    } else {
      executeEnv = new LocalEnv();
    }
    return executeEnv;
  }

  public static StringEntity genFileParam(String content, String filename, String path)
      throws UnsupportedEncodingException {
    JsonObject object = new JsonObject();
    object.addProperty("aid", "dev");
    object.addProperty("content", content);
    object.addProperty("fileName", filename);;
    object.addProperty("path", path);
    StringEntity entity = new StringEntity(object.toString(), "application/json", "UTF-8");
    return entity;
  }

  public static StringEntity genCommandParam(String command) throws UnsupportedEncodingException {
    JsonObject object = new JsonObject();
    object.addProperty("aid", "dev");
    object.addProperty("command", command);
    StringEntity entity = new StringEntity(object.toString(), "application/json", "UTF-8");
    return entity;
  }



  public static String genMasterName(int i) {
    return "mesos-master" + i;
  }

  public static String genSlaveName(int i) {
    return "mesos-slave" + i;
  }

  public static String getFile(InputStream in) throws IOException {
    // InputStream in = TaskUtil.class.getResourceAsStream("/batch/river.yml");
    // InputStream in = TaskUtil.class.getResourceAsStream(path);
    BufferedReader br = new BufferedReader(new InputStreamReader(in));
    StringBuilder sb = new StringBuilder();
    try {
      String line = br.readLine();

      while (line != null) {
        sb.append(line);
        sb.append("\n");
        line = br.readLine();
      }
    } finally {
      br.close();
    }
    return sb.toString();
  }

  public static String getSystemProperty(String param) {
    Properties prop = new Properties();
    String property = new String();
    try {
      prop.load(TaskUtil.class.getClassLoader().getResourceAsStream("batch/config.properties"));
      property = prop.getProperty(param);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return property;
  }

  // Ìæ»»windows»»ÐÐ·û
  public static String replaceIllegalCharacter(String source) {
    if (source == null) return source;
    /*
     * String reg = "[\n-\r]"; Pattern p = Pattern.compile(reg); Matcher m = p.matcher(source);
     */
    return source.replaceAll("\r\n", "\n");
  }
}
