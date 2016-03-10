package com.ai.paas.cpaas.rm.manage.service.chronos;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;
import com.esotericsoftware.minlog.Log;

public class VerifyChronos implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    String aid = openParam.getAid();
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/chronos/verifychronos.yml");
    String content = TaskUtil.getFile(in);
    TaskUtil.uploadFile("verifychronos.yml", content, useAgent, aid);

    MesosInstance master = openParam.getMesosMaster().get(0);
    String password = master.getPasswd();
    List<MesosInstance> masterlist = openParam.getMesosMaster();
    StringBuffer shellContext = TaskUtil.createBashFile();
    for (int i = 0; i < masterlist.size(); i++) {
      List<String> configvars = new ArrayList<String>();
      configvars.add("ansible_ssh_pass=" + password);
      configvars.add("ansible_become_pass=" + password);
      configvars.add("chronos_bind_address=" + masterlist.get(i).getIp());
      configvars.add("chronos_port=4400");
      AnsibleCommand command =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/verifychronos.yml", "root",
              configvars);
      shellContext.append(command.toString()).append("\n");
    }
    Timestamp start = new Timestamp(System.currentTimeMillis());
    String result = new String();
    try {
      result = TaskUtil.executeFile("verifychronos", shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      Log.error(e.toString());
      result = e.toString();
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("verifychronos"));
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }
    return RepeatStatus.FINISHED;
  }

}
