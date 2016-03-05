package com.ai.paas.cpaas.rm.manage.service.zookeeper;

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
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.MesosSlave;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.ipaas.PaasException;
import com.esotericsoftware.minlog.Log;

public class ChangeHostNameStep implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    // upload hostnamectl.yml
    InputStream in = ChangeHostNameStep.class.getResourceAsStream("/playbook/hostnamectl.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    String aid = openParam.getAid();
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("hostnamectl.yml", content, useAgent, aid);
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    List<MesosSlave> mesosSlave = openParam.getMesosSlave();
    StringBuffer shellContext = TaskUtil.createBashFile();
    for (int i = 0; i < mesosMaster.size(); i++) {
      MesosInstance instance = mesosMaster.get(i);
      String ip = instance.getIp();
      String name =
          (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
              .getExecutionContext().get(ip);
      this.genCommand(instance, shellContext, name);
    }
    for (int i = 0; i < mesosSlave.size(); i++) {
      MesosSlave instance = mesosSlave.get(i);
      String ip = instance.getIp();
      String name =
          (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
              .getExecutionContext().get(ip);
      this.genCommand(instance, shellContext, name);
    }

    Timestamp start = new Timestamp(System.currentTimeMillis());
    // upload shellContext and execute it

    String result = new String();
    try {
      result = TaskUtil.executeFile("changehostnames", shellContext.toString(), useAgent, aid);
    } catch (Exception e) {
      Log.error(e.toString());
      result = e.toString();
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(), 2);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }


    return RepeatStatus.FINISHED;
  }

  public void genCommand(MesosInstance instance, StringBuffer shellContext, String hosts) {
    List<String> vars = new ArrayList<String>();
    StringBuffer hostname = new StringBuffer();
    hostname.append("hostname=");
    hostname.append(hosts);
    String hostsParam = "hosts=" + hosts;
    String password = "ansible_ssh_pass=" + instance.getPasswd();
    vars.add(hostname.toString());
    vars.add(password);
    vars.add(hostsParam);
    AnsibleCommand ansibleCommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/hostnamectl.yml",
            instance.getRoot(), vars);
    shellContext.append(ansibleCommand.toString());
    shellContext.append("\n");
  }
}
