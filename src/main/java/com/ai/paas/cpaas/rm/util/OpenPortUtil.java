package com.ai.paas.cpaas.rm.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;
import com.esotericsoftware.minlog.Log;

public class OpenPortUtil {

  public static RepeatStatus openPort(ChunkContext chunkContext, String portParam, String user,
      int typeId) throws ClientProtocolException, IOException, PaasException {
    // upload openport.yml
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/openport.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    TaskUtil.uploadFile("openport.yml", content, useAgent, aid);
    /*String password =
        (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getExecutionContext().get("password");*/
    String password=openParam.getMesosMaster().get(0).getPasswd();
    // open the port
    List<String> portVars = new ArrayList<String>();

    if (user.equals("rczkp01")) {
      user = "root";
      password = openParam.getMesosMaster().get(0).getPasswd();
    }
    portVars.add("ansible_ssh_pass=" + password);
    portVars.add("ansible_become_pass=" + password);
    // portVars.add("ports=[5050,8080]");
    portVars.add(portParam);
    AnsibleCommand openPortCommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/openport.yml","root", portVars);

    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    try {
      result =
          TaskUtil.executeFile("openportUtil", openPortCommand.toString(), openParam.getUseAgent(),
              aid);
    } catch (Exception e) {
      Log.error(e.toString());
      result = e.toString();
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), openPortCommand.toString(),
              typeId);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }

    return RepeatStatus.FINISHED;
  }
}
