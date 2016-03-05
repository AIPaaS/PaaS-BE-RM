package com.ai.paas.cpaas.rm.manage.service.mesos;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.OpenPortUtil;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.MesosSlave;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class MesosSlaveStep implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
      throws Exception {
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/mesos/meslaveinstall.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    String aid = openParam.getAid();
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("meslaveinstall.yml", content, useAgent, aid);

    StringBuffer shellContext = TaskUtil.createBashFile();
    List<MesosSlave> slavenodes = openParam.getMesosSlave();
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance masternode = mesosMaster.get(0);

    String password =
        (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getExecutionContext().get("password");


    StringBuffer zkMessage = new StringBuffer();
    zkMessage.append("zk=zk://");
    zkMessage.append(masternode.getIp() + ":2181");
    for (int i = 1; i < mesosMaster.size(); i++) {
      zkMessage.append("," + mesosMaster.get(i).getIp() + ":2181");
    }
    zkMessage.append("/mesos");
    for (MesosSlave node : slavenodes) {
      List<String> vars = new ArrayList<String>();
      vars.add("ansible_ssh_pass=" + password);
      vars.add("ansible_become_pass=" + password);
      vars.add(zkMessage.toString());
      vars.add("containerizers=docker,mesos");
      vars.add("timeout=5mins");
      vars.add("hostname=" + node.getIp());
      vars.add("ip=" + node.getIp());
      vars.add("hosts" + node.getIp());
      vars.add("attributes='zone:" + node.getZone() + "'");
      AnsibleCommand command =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/meslaveinstall.yml",
              "rcmesos", vars);
      shellContext.append(command.toString());
    }
    Timestamp start = new Timestamp(System.currentTimeMillis());

    String result = TaskUtil.executeFile("mesosSlaveStep", shellContext.toString(), useAgent, aid);

    // insert log and task record
    int taskId =
        TaskUtil.insertResJobDetail(start, openParam.getClusterId(), shellContext.toString(), 15);
    TaskUtil.insertResTaskLog(openParam.getClusterId(), taskId, result);
    return RepeatStatus.FINISHED;
  }

}
