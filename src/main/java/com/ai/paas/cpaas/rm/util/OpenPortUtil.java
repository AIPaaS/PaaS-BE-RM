package com.ai.paas.cpaas.rm.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.ipaas.PaasException;

public class OpenPortUtil {

  public static RepeatStatus openPort(ChunkContext chunkContext, String portParam, String user) throws ClientProtocolException, IOException,
      PaasException {
    String password = (String) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("mesosPassword");
    // 开放指定端口
    List<String> portVars = new ArrayList<String>();
    portVars.add("ansible_ssh_pass=" + password);
    portVars.add("ansible_become_pass=" + password);
    // portVars.add("ports=[5050,8080]");
    portVars.add(portParam);
    AnsibleCommand openPortCommand = new AnsibleCommand(TaskUtil.filepath + "/openport.yml", user, portVars);
    StringEntity entity = TaskUtil.genCommandParam(openPortCommand.toString());
    TaskUtil.executeCommand(entity);
    return RepeatStatus.FINISHED;
  }
}
