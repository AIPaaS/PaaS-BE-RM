package com.ai.paas.cpaas.rm.manage.service.cluster;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.cpaas.rm.vo.WebHaproxy;
import com.ai.paas.ipaas.PaasException;

public class RestartKa implements Tasklet {
  private static Logger logger = Logger.getLogger(RestartKa.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    String[] files = {"restartKa.yml"};
    for (String file : files) {
      InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/base/" + file);
      String content = TaskUtil.getFile(in);
      TaskUtil.uploadFile(file, content, useAgent, aid);
    }
    StringBuffer shellContext = TaskUtil.createBashFile();
    WebHaproxy webHaproxy = openParam.getWebHaproxy();
    String password = webHaproxy.getHosts().get(0).getPasswd();
    List<String> configvars = new ArrayList<String>();
    configvars.add("ansible_ssh_pass=" + password);
    configvars.add("ansible_become_pass=" + password);

    AnsibleCommand command =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/restartKa.yml", "root",
            configvars);
    shellContext.append(command.toString()).append("\n");
    Timestamp start = new Timestamp(System.currentTimeMillis());
    String result = new String();
    int status = TaskUtil.FINISHED;
    try {
      result = TaskUtil.executeFile("restartKa", shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      logger.error(e.toString());
      result = e.toString();
      status = TaskUtil.FAILED;
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("restartKa"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }
    return RepeatStatus.FINISHED;
  }

}
