package com.ai.paas.cpaas.rm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

import com.ai.paas.cpaas.rm.vo.TransResultVo;
import com.ai.paas.ipaas.PaasException;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RemoteEnv implements ExecuteEnv {

  private static Logger logger = Logger.getLogger(RemoteEnv.class);

  @Override
  public void uploadFile(String filename, String content, String aid)
      throws ClientProtocolException, IOException, PaasException {
    String filepath = TaskUtil.getSystemProperty("filepath");
    // upload execute shell
    String url = TaskUtil.getSystemProperty("upload");
    StringEntity paramEntity = RemoteEnv.genFileParam(content, filename, filepath, aid);
    RemoteEnv.sendRequest(url, paramEntity);
  }

  public static String sendRequest(String url, StringEntity paramEntity)
      throws ClientProtocolException, IOException, PaasException {
    // HttpClient httpClient = HttpClients.createDefault();
    int timeout = 5000;
    RequestConfig config =
        RequestConfig.custom().setConnectTimeout(timeout * 1000)
            .setConnectionRequestTimeout(timeout * 1000).setSocketTimeout(timeout * 1000).build();
    CloseableHttpClient httpClient =
        HttpClientBuilder.create().setDefaultRequestConfig(config).build();


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

    if (!url.contains("upload")) {
      String excResult = resultVo.getMsg();
      JsonParser parser = new JsonParser();
      JsonObject o = parser.parse(excResult).getAsJsonObject();
      String stderr = o.get("stderr").getAsString();
      String stdout = o.get("stdout").getAsString();
      // judge the result with stderr and stdout
      if (!resultVo.getCode().equals(ExceptionCodeConstants.TransServiceCode.SUCCESS_CODE)) {
        System.out.println(stderr);
        logger.error(stderr);
        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
            resultVo.getMsg());
      }
      // analyze stdout
      if (stdout.contains("unreachable")) {
        String pattern = "unreachable=[1-9]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(stdout);
        if (m.find()) {
          System.out.println(stdout);
          logger.error(stdout);
          throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
              resultVo.getMsg());
        }
      }
      if (stdout.contains("failed")) {
        String pattern = "failed=[1-9]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(stdout);
        if (m.find()) {
          System.out.println(stdout);
          logger.error(stdout);
          throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
              resultVo.getMsg());
        }
      }
      if (!StringUtils.isEmpty(stderr)) {
        System.out.println(stderr);
        logger.error(stderr);
        throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
            resultVo.getMsg());
      }
      System.out.println("code:" + resultVo.getCode() + ";stderr:" + stdout + ";stderr:" + stderr);
      logger.debug("code:" + resultVo.getCode() + ";stderr:" + stdout + ";stderr:" + stderr);
    }

    return result;
  }

  @Override
  public String executeFile(String filename, String content, String aid)
      throws ClientProtocolException, IOException, PaasException {
    String filepath = TaskUtil.getSystemProperty("filepath");
    // upload execute file
    this.uploadFile(filename, content, aid);

    // change the permission of file
    this.executeCommand("chmod u+x " + filepath + "/" + filename, aid);

    // execute shell file
    String result = this.executeCommand("bash " + filepath + "/" + filename, aid);
    return result;
  }

  public static StringEntity genFileParam(String content, String filename, String path, String aid)
      throws UnsupportedEncodingException {
    JsonObject object = new JsonObject();
    object.addProperty("aid", aid);
    object.addProperty("content", content);
    object.addProperty("fileName", filename);;
    object.addProperty("path", path);
    StringEntity entity = new StringEntity(object.toString(), "application/json", "UTF-8");
    return entity;
  }

  public static StringEntity genCommandParam(String command, String aid)
      throws UnsupportedEncodingException {
    JsonObject object = new JsonObject();
    object.addProperty("aid", aid);
    object.addProperty("command", command);
    StringEntity entity = new StringEntity(object.toString(), "application/json", "UTF-8");
    return entity;
  }

  @Override
  public String executeCommand(String content, String aid) throws ClientProtocolException,
      IOException, PaasException {
    String url = TaskUtil.getSystemProperty("exec");
    StringEntity paramEntity = RemoteEnv.genCommandParam(content, aid);
    String result = RemoteEnv.sendRequest(url, paramEntity);
    System.out.println("command content:" + content);
    logger.debug("command content:" + content);
    return result;
  }
}
