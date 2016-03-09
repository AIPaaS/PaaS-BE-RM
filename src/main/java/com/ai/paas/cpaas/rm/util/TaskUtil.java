package com.ai.paas.cpaas.rm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.client.ClientProtocolException;
import org.springframework.batch.core.scope.context.ChunkContext;

import com.ai.paas.cpaas.rm.dao.interfaces.ResClusterInfoMapper;
import com.ai.paas.cpaas.rm.dao.interfaces.ResInstancePropsMapper;
import com.ai.paas.cpaas.rm.dao.interfaces.ResJobDetailMapper;
import com.ai.paas.cpaas.rm.dao.interfaces.ResTaskLogMapper;
import com.ai.paas.cpaas.rm.dao.interfaces.SysCodesMapper;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResClusterInfo;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResInstanceProps;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResJobDetail;
import com.ai.paas.cpaas.rm.dao.mapper.bo.ResTaskLog;
import com.ai.paas.cpaas.rm.dao.mapper.bo.SysCodes;
import com.ai.paas.cpaas.rm.dao.mapper.bo.SysCodesCriteria;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;
import com.ai.paas.ipaas.ServiceUtil;
import com.google.gson.Gson;

public class TaskUtil {

  // public static String filepath = "/root/test";
  private static final AtomicInteger counter = new AtomicInteger();

  public static String SECURITY_KEY = "byjsy7!@#bjwqt7!";

  public static int AVAILABLE = 0;

  public static int DISABLED = 1;

  public static int NOTSTART = 0;

  public static int EXECUTING = 1;

  public static int FINISHED = 2;

  public static int FAILED = 3;

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

  // gen password
  public static String generatePasswd() {
    return new SessionIdentifierGenerator().nextSessionId();
  }

  public static void uploadFile(String filename, String content, Boolean useAgent, String aid)
      throws ClientProtocolException, IOException, PaasException {
    ExecuteEnv executeEnv = TaskUtil.genEnv(useAgent);
    executeEnv.uploadFile(filename, content, aid);
  }

  public static String executeFile(String filename, String content, Boolean useAgent, String aid)
      throws ClientProtocolException, IOException, PaasException {
    ExecuteEnv executeEnv = TaskUtil.genEnv(useAgent);
    return executeEnv.executeFile(filename, content, aid);
  }

  public static void executeCommand(String command, Boolean useAgent, String aid)
      throws ClientProtocolException, IOException, PaasException {
    ExecuteEnv executeEnv = TaskUtil.genEnv(useAgent);
    executeEnv.executeCommand(command, aid);
  }

  public static ExecuteEnv genEnv(Boolean useAgent) {
    ExecuteEnv executeEnv = null;
    if (useAgent) {
      executeEnv = new RemoteEnv();
    }
    return executeEnv;
  }



  public static String genMasterName(int i) {
    // return "mesos-master" + i;
    return "master" + i;
  }

  public static String genSlaveName(int i) {
    // return "mesos-slave" + i;
    return "slave" + i;
  }

  public static String genAgentName(int i) {
    // return "mesos-slave" + i;
    return "agent" + i;
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
    /*
     * Properties prop = new Properties(); String property = new String(); try {
     * prop.load(TaskUtil.class.getClassLoader().getResourceAsStream("batch/config.properties"));
     * property = prop.getProperty(param); } catch (IOException ex) { ex.printStackTrace(); }
     */
    SysCodesCriteria instance = new SysCodesCriteria();
    SysCodesCriteria.Criteria criteria = instance.createCriteria();
    criteria.andCodeKeyEqualTo(param);
    criteria.andSysCodeEqualTo("PROXY");
    // instance.setSysCode("PROXY");
    // instance.setCodeKey(param);
    SysCodesMapper mapper = ServiceUtil.getMapper(SysCodesMapper.class);
    List<SysCodes> list = mapper.selectByExample(instance);
    return list.get(0).getCodeValue();
  }

  public static String replaceIllegalCharacter(String source) {
    if (source == null) return source;
    /*
     * String reg = "[\n-\r]"; Pattern p = Pattern.compile(reg); Matcher m = p.matcher(source);
     */
    return source.replaceAll("\r\n", "\n");
  }

  public static String genEtcdParam(OpenResourceParamVo openParam, String zone) {
    return "/" + openParam.getClusterId() + "/" + zone + "/config";
  }

  public static int insertResJobDetail(Timestamp start, String clusterId, String shellContext,
      int typeId) {
    ResJobDetail resJobDetail = new ResJobDetail();
    ResJobDetailMapper mapper = ServiceUtil.getMapper(ResJobDetailMapper.class);
    resJobDetail.setClusterId(clusterId);
    resJobDetail.setTaskPlaybook(shellContext);
    resJobDetail.setTaskState(TaskUtil.FINISHED);
    resJobDetail.setTaskStartTime(start);
    resJobDetail.setTaskEndTime(new Timestamp(System.currentTimeMillis()));
    resJobDetail.setTypeId(typeId);
    mapper.insert(resJobDetail);
    int taskId = resJobDetail.getTaskId();
    return taskId;
  }

  public static void updateResClusterInfo(ResClusterInfo instance) {
    ResClusterInfoMapper mapper = ServiceUtil.getMapper(ResClusterInfoMapper.class);
    mapper.updateByPrimaryKey(instance);
  }

  public static void insertResInstanceProps(String clusterId, String keyCode, String keyValue) {
    ResInstanceProps instance = new ResInstanceProps();
    instance.setState(TaskUtil.AVAILABLE);
    instance.setKeyCode(keyCode);
    instance.setKeyValue(keyValue);
    instance.setClusterId(clusterId);
    ResInstancePropsMapper mapper = ServiceUtil.getMapper(ResInstancePropsMapper.class);
    mapper.insert(instance);
  }

  public static void insertResTaskLog(String clusterId, int taskId, String result) {
    ResTaskLog resTaskLog = new ResTaskLog();
    resTaskLog.setClusterId(new Integer(clusterId));
    resTaskLog.setTaskId(taskId);
    resTaskLog.setLogCnt(result);
    ResTaskLogMapper mapper = ServiceUtil.getMapper(ResTaskLogMapper.class);
    resTaskLog.setLogTime(new Timestamp(System.currentTimeMillis()));
    mapper.insert(resTaskLog);
  }


}
