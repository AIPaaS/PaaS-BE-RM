package com.ai.paas.cpaas.rm.manage.service.zookeeper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.ExceptionCodeConstants;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.MesosSlave;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;
import com.ai.paas.cpaas.rm.vo.WebHaproxy;
import com.ai.paas.ipaas.PaasException;

public class AnsibleHostsConfig implements Tasklet {

  private static Logger logger = Logger.getLogger(AnsibleHostsConfig.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws PaasException {
    logger.info("config ansible hosts step ");
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    logger.debug("the param is:" + openParam.toString());
    String aid = openParam.getAid();
    // create execute file
    StringBuffer shellContext = TaskUtil.createBashFile();
    shellContext.append("mv /etc/ansible/hosts /etc/ansible/hosts.bak");
    shellContext.append("\n");
    shellContext.append("cat >/etc/ansible/hosts <<-EOL");
    shellContext.append("\n");

    List<MesosInstance> mesosmaster = openParam.getMesosMaster();
    if (mesosmaster == null) {
      mesosmaster = new ArrayList<MesosInstance>();
    }

    List<MesosSlave> mesosSlave = openParam.getMesosSlave();
    if (mesosSlave == null) {
      mesosSlave = new ArrayList<MesosSlave>();
    }

    WebHaproxy webHaproxy = openParam.getWebHaproxy();
    List<MesosInstance> agents = new ArrayList<MesosInstance>();
    if (webHaproxy != null) {
      agents = openParam.getWebHaproxy().getHosts();
      if (agents == null) {
        agents = new ArrayList<MesosInstance>();
      }
    }


    HashMap<String, String> hosts = new HashMap<String, String>();
    for (MesosInstance instance : mesosmaster) {
      int id = instance.getId();
      String masterName = TaskUtil.genMasterName(id);
      String ip = instance.getIp();
      shellContext.append("[" + masterName + "]");
      shellContext.append("\n");
      shellContext.append(ip);
      shellContext.append("\n");

      hosts.put(ip, masterName);

    }

    shellContext.append("[master]");
    shellContext.append("\n");

    for (MesosInstance instance : mesosmaster) {
      String ip = instance.getIp();
      shellContext.append(ip);
      shellContext.append("\n");
    }

    for (MesosSlave slave : mesosSlave) {
      int id = slave.getId();
      String slaveName = TaskUtil.genSlaveName(id);
      String ip = slave.getIp();
      shellContext.append("[" + slaveName + "]");
      shellContext.append("\n");
      shellContext.append(ip);
      shellContext.append("\n");

      if (!hosts.containsKey(ip)) {
        hosts.put(ip, slaveName);
      }

    }

    shellContext.append("[slave]");
    shellContext.append("\n");
    for (MesosSlave slave : mesosSlave) {
      String ip = slave.getIp();
      shellContext.append(ip);
      shellContext.append("\n");
    }

    for (MesosInstance agent : agents) {
      int id = agent.getId();
      String agentName = TaskUtil.genAgentName(id);
      String ip = agent.getIp();
      shellContext.append("[" + agentName + "]");
      shellContext.append("\n");
      shellContext.append(ip);
      shellContext.append("\n");
      if (!hosts.containsKey(ip)) {
        hosts.put(ip, agentName);
      }

    }

    for (String ip : hosts.keySet()) {
      String name = hosts.get(ip);
      chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
          .put(ip, name);
    }
    shellContext.append("[agent]");
    shellContext.append("\n");
    for (MesosInstance agent : agents) {
      shellContext.append(agent.getIp());
      shellContext.append("\n");
    }

    shellContext.append("[nodes]");
    shellContext.append("\n");

    List<String> nodes = new ArrayList<String>();
    for (MesosInstance instance : mesosmaster) {
      nodes.add(instance.getIp());
    }

    for (MesosSlave slave : mesosSlave) {
      nodes.add(slave.getIp());
    }

    for (MesosInstance agent : agents) {
      nodes.add(agent.getIp());
    }
    // 去重
    Set<String> hs = new HashSet<>();
    hs.addAll(nodes);
    nodes.clear();
    nodes.addAll(hs);

    for (String ip : nodes) {
      shellContext.append(ip);
      shellContext.append("\n");
    }

    shellContext.append("EOL");
    shellContext.append("\n");
    logger.debug("the ansible hosts is" + shellContext.toString());
    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = new String();
    int status = TaskUtil.FINISHED;
    try {
      result =
          TaskUtil.executeFile("configAnsibleHosts", shellContext.toString(),
              openParam.getUseAgent(), aid);
    } catch (Exception e) {
      logger.error("config ansible hosts:", e);
      result = e.toString();
      status = TaskUtil.FAILED;
      throw new PaasException(ExceptionCodeConstants.DubboServiceCode.SYSTEM_ERROR_CODE,
          e.toString());
    } finally {
      // insert log and task record
      int taskId =
          TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(),
              TaskUtil.getTypeId("ansibleHostsConfig"), status);
      TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    }
    return RepeatStatus.FINISHED;
  }
}
