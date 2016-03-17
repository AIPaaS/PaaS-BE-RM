package com.ai.paas.cpaas.rm.manage.service.zookeeper;

import java.sql.Timestamp;
import java.util.List;

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
import com.ai.paas.ipaas.PaasException;

public class AnsibleHostsConfig implements Tasklet {

  private static Logger logger = Logger.getLogger(AnsibleHostsConfig.class);

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws PaasException {
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
    List<MesosSlave> mesosSlave = openParam.getMesosSlave();
    List<MesosInstance> agents = openParam.getWebHaproxy().getHosts();
    for (int i = 0; i < mesosmaster.size(); i++) {
      int id = mesosmaster.get(i).getId();
      String masterName = TaskUtil.genMasterName(id);
      String ip = mesosmaster.get(i).getIp();

      shellContext.append("[" + masterName + "]");
      shellContext.append("\n");
      shellContext.append(ip);
      shellContext.append("\n");
      chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
          .put(ip, masterName);

    }
    shellContext.append("[master]");
    shellContext.append("\n");
    for (int i = 0; i < mesosmaster.size(); i++) {
      shellContext.append(mesosmaster.get(i).getIp());
      shellContext.append("\n");
    }

    for (int i = 0; i < mesosSlave.size(); i++) {
      int id = mesosSlave.get(i).getId();
      String slaveName = TaskUtil.genSlaveName(id);
      String ip = mesosSlave.get(i).getIp();

      shellContext.append("[" + slaveName + "]");
      shellContext.append("\n");
      shellContext.append(ip);
      shellContext.append("\n");
      chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
          .put(ip, slaveName);
    }
    shellContext.append("[slave]");
    shellContext.append("\n");
    for (int i = 0; i < mesosSlave.size(); i++) {
      shellContext.append(mesosSlave.get(i).getIp());
      shellContext.append("\n");
    }


    for (int i = 0; i < agents.size(); i++) {
      int id = agents.get(i).getId();
      String agentName = TaskUtil.genAgentName(id);
      String ip = agents.get(i).getIp();
      shellContext.append("[" + agentName + "]");
      shellContext.append("\n");
      shellContext.append(ip);
      shellContext.append("\n");
      chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext()
          .put(ip, agentName);

    }

    shellContext.append("[agent]");
    shellContext.append("\n");
    for (int i = 0; i < agents.size(); i++) {
      shellContext.append(agents.get(i).getIp());
      shellContext.append("\n");
    }


    shellContext.append("[nodes]");
    shellContext.append("\n");
    for (int i = 0; i < mesosmaster.size(); i++) {
      shellContext.append(mesosmaster.get(i).getIp());
      shellContext.append("\n");
    }
    for (int i = 0; i < mesosSlave.size(); i++) {
      shellContext.append(mesosSlave.get(i).getIp());
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
      logger.error(e.toString());
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
