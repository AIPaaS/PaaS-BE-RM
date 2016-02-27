package com.ai.paas.cpaas.rm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import com.ai.paas.cpaas.rm.vo.TransResultVo;
import com.ai.paas.ipaas.PaasException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RemoteEnv implements ExecuteEnv {

  @Override
  public void uploadFile(String filename, String content) throws ClientProtocolException,
      IOException, PaasException {
    String filepath = TaskUtil.getSystemProperty("filepath");
    // 传输执行文件
    String url = TaskUtil.getSystemProperty("proxy.upload");
    StringEntity paramEntity = RemoteEnv.genFileParam(content, filename, filepath);
    RemoteEnv.sendRequest(url, paramEntity);
  }

  public static String sendRequest(String url, StringEntity paramEntity)
      throws ClientProtocolException, IOException, PaasException {
    HttpClient httpClient = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(url);
    httpPost.setEntity(paramEntity);
    HttpResponse response = httpClient.execute(httpPost);
    HttpEntity entity = response.getEntity();
    String result = new String();
    if (entity != null) {
      InputStream instream = entity.getContent();
      InputStreamReader inputStream = new InputStreamReader(instream, "UTF-8");
      try {
        BufferedReader br = new BufferedReader(inputStream);
        result = br.readLine();
      } finally {
        instream.close();
      }
    }
    Gson gson = new Gson();
    TransResultVo resultVo = gson.fromJson(result, TransResultVo.class);

    // TODO
    // 在ansible执行命令中，resultcode无法作为唯一判定结果，需要对stdout进行分析，
    if (!resultVo.getCode().equals(ExceptionCodeConstants.TransServiceCode.SUCCESS_CODE)) {
      // TODO
      String excResult = resultVo.getMsg();
      JsonParser parser = new JsonParser();
      JsonObject o = parser.parse(excResult).getAsJsonObject();
      String stderr = o.get("stderr").getAsString();
      String stdout = o.get("stdout").getAsString();
      System.out.println(stderr + " " + stdout);

      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          resultVo.getMsg());
    }
    return result;
  }

  @Override
  public String executeFile(String filename, String content) throws ClientProtocolException,
      IOException, PaasException {
    String filepath = TaskUtil.getSystemProperty("filepath");
    // TODO
    // TaskUtil.replaceIllegalCharacter(content);
    // 传输执行文件
    this.uploadFile(filename, content);

    // 更改文件权限
    this.executeCommand("chmod u+x " + filepath + "/" + filename);

    // 执行文件
    String result = this.executeCommand("bash " + filepath + "/" + filename);
    return result;
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

  @Override
  public String executeCommand(String content) throws ClientProtocolException, IOException,
      PaasException {
    String url = TaskUtil.getSystemProperty("proxy.exec");
    StringEntity paramEntity = RemoteEnv.genCommandParam(content);
    String result = RemoteEnv.sendRequest(url, paramEntity);
    return result;
  }
}
