package com.ai.paas.cpaas.rm.manage.service.mesos;

import java.io.InputStream;
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
    InputStream in = OpenPortUtil.class.getResourceAsStream("/playbook/mesos/messlaveinstall.yml");
    String content = TaskUtil.getFile(in);
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    Boolean useAgent = openParam.getUseAgent();
    TaskUtil.uploadFile("messlaveinstall.yml", content, useAgent);

    StringBuffer shellContext = TaskUtil.createBashFile();
    List<MesosSlave> slavenodes = openParam.getMesosSlave();
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    MesosInstance masternode = mesosMaster.get(0);

    List<String> vars = new ArrayList<String>();

    String password =
        (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
            .getExecutionContext().get("password");
    vars.add("ansible_ssh_pass=" + password);
    vars.add("ansible_become_pass=" + password);

    StringBuffer zkMessage = new StringBuffer();
    zkMessage.append("zk=zk://");
    zkMessage.append(masternode.getIp() + ":2181");
    for (MesosInstance mesos : mesosMaster) {
      zkMessage.append("," + mesos.getIp() + ":2181");
    }
    zkMessage.append("/mesos");
    vars.add(zkMessage.toString());
    vars.add("containerizers=docker,mesos");
    vars.add("timeout=5mins");

    for (MesosSlave node : slavenodes) {
      vars.add("hostname=" + node.getIp());
      vars.add("ip=" + node.getIp());
      vars.add("hosts" + node.getIp());
      AnsibleCommand command =
          new AnsibleCommand(TaskUtil.getSystemProperty("filepath") + "/messlaveinstall.yml",
              "rcmesos", vars);
      vars.remove(7);
      vars.remove(6);
      vars.remove(5);
      shellContext.append(command.toString());
    }
    TaskUtil.executeFile("mesosSlaveStep", shellContext.toString(), useAgent);
    return RepeatStatus.FINISHED;
  }

}
