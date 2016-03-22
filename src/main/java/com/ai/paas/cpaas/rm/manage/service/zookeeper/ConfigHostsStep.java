package com.ai.paas.cpaas.rm.manage.service.zookeeper;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
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

public class ConfigHostsStep implements Tasklet {
  private static Logger logger = Logger.getLogger(ConfigHostsStep.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in = ConfigHostsStep.class.getResourceAsStream("/playbook/confighosts.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    String aid = openParam.getAid();
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("confighosts.yml", content, useAgent, aid);

    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    List<MesosSlave> mesosSlave = openParam.getMesosSlave();
    StringBuffer shellContext = new StringBuffer();
    shellContext.append("lines=[");
    shellContext.append("'").append("127.0.0.1 localhost").append("'");

    HashMap<String, String> hosts = new HashMap<String, String>();

    for (int i = 0; i < mesosMaster.size(); i++) {
      MesosInstance instance = mesosMaster.get(i);
      String ip = instance.getIp();
      String name =
          (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
              .getExecutionContext().get(ip);
      if (!hosts.containsKey(ip)) {
        hosts.put(ip, name);
      }
    }
    for (int i = 0; i < mesosSlave.size(); i++) {
      MesosSlave instance = mesosSlave.get(i);
      String ip = instance.getIp();
      String name =
          (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
              .getExecutionContext().get(ip);
      if (!hosts.containsKey(ip)) {
        hosts.put(ip, name);
      }
    }

    for (String ip : hosts.keySet()) {
      String name = hosts.get(ip);
      this.genHostsLine(ip, name, shellContext);
    }

    shellContext.append("]");
    String password = mesosMaster.get(0).getPasswd();
    List<String> vars = new ArrayList<String>();
    vars.add("ansible_ssh_pass=" + password);
    vars.add("ansible_become_pass=" + password);
    vars.add(shellContext.toString());
    AnsibleCommand command =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/confighosts.yml", "root",
            vars);
    StringBuffer executeFile = TaskUtil.createBashFile();
    executeFile.append(command.toString());

    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    int status = TaskUtil.FINISHED;
    try {
      result = TaskUtil.executeFile("confighosts", executeFile.toString(), useAgent, aid);
    } catch (Exception e) {
      logger.error("config hosts:", e);
      result = e.toString();
      status = TaskUtil.FAILED;
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("configHostsStep"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }

    return RepeatStatus.FINISHED;
  }

  public void genHostsLine(String ip, String name, StringBuffer shellContext) {
    shellContext.append(",").append("'").append(ip + "  " + name).append("'");

  }
}
