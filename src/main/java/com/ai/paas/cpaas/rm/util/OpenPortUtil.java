package com.ai.paas.cpaas.rm.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;

public class OpenPortUtil {

  public static RepeatStatus openPort(ChunkContext chunkContext, String portParam, String user)
      throws ClientProtocolException, IOException, PaasException {
    // �ϴ�openport.yml
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/openport.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("openport.yml", content, useAgent);
    String password =
        (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getExecutionContext().get("mesosPassword");
    // ����ָ���˿�
    List<String> portVars = new ArrayList<String>();
    portVars.add("ansible_ssh_pass=" + password);
    portVars.add("ansible_become_pass=" + password);
    // portVars.add("ports=[5050,8080]");
    portVars.add(portParam);
    AnsibleCommand openPortCommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/openport.yml", user, portVars);
    TaskUtil.executeCommand(openPortCommand.toString(), useAgent);
    return RepeatStatus.FINISHED;
  }
}
