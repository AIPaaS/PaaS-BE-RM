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

public class ChangeHostNameStep implements Tasklet {

  private static Logger logger = Logger.getLogger(ChangeHostNameStep.class);

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
    List<MesosInstance> agents = openParam.getWebHaproxy().getHosts();

    HashMap<String, String> hosts = new HashMap<String, String>();
    String password = mesosMaster.get(0).getPasswd();
    StringBuffer shellContext = TaskUtil.createBashFile();
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

    for (int i = 0; i < agents.size(); i++) {
      MesosInstance instance = agents.get(i);
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
      this.genCommand(password, shellContext, name);
    }

    Timestamp start = new Timestamp(System.currentTimeMillis());
    // upload shellContext and execute it

    String result = new String();
    int status = TaskUtil.FINISHED;
    try {
      result = TaskUtil.executeFile("changehostnames", shellContext.toString(), useAgent, aid);
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
              TaskUtil.getTypeId("changeHostNameStep"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }


    return RepeatStatus.FINISHED;
  }

  public void genCommand(String password, StringBuffer shellContext, String hosts) {
    List<String> vars = new ArrayList<String>();
    StringBuffer hostname = new StringBuffer();
    hostname.append("hostname=");
    hostname.append(hosts);
    String hostsParam = "hosts=" + hosts;
    vars.add(hostname.toString());
    vars.add("ansible_ssh_pass=" + password);
    vars.add(hostsParam);
    AnsibleCommand ansibleCommand =
        new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/hostnamectl.yml", "root",
            vars);
    shellContext.append(ansibleCommand.toString());
    shellContext.append("\n");
  }
}
