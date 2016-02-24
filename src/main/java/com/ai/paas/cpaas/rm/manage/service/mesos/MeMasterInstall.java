package com.ai.paas.cpaas.rm.manage.service.mesos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ai.paas.cpaas.rm.util.AnsibleCommand;
import com.ai.paas.cpaas.rm.util.TaskUtil;
import com.ai.paas.cpaas.rm.vo.MesosInstance;
import com.ai.paas.cpaas.rm.vo.OpenResourceParamVo;

public class MeMasterInstall implements Tasklet {

  @Override
  public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    OpenResourceParamVo openParam = TaskUtil.createOpenParam(chunkContext);
    StringBuffer shellContext = TaskUtil.createBashFile();
    List<MesosInstance> mesosMaster = openParam.getMesosMaster();
    // 创建mesos用户
    MesosInstance mesosInstance = mesosMaster.get(0);
    // mesos master 安装
    List<String> installVars = new ArrayList<String>();
    String password = (String) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("password");
    installVars.add("ansible_ssh_pass=" + password);
    installVars.add("ansible_become_pass=" + password);
    StringBuffer zkMessage = new StringBuffer();
    zkMessage.append("zk=zk://");
    zkMessage.append(mesosInstance.getIp() + ":2181");
    for (int i = 1; i < mesosMaster.size(); i++) {
      MesosInstance mesos = mesosMaster.get(i);
      zkMessage.append("," + mesos.getIp() + ":2181");
    }
    zkMessage.append("/mesos");
    installVars.add(zkMessage.toString());
    installVars.add("quorum=1");
    AnsibleCommand mesosMasterCommand = new AnsibleCommand(TaskUtil.filepath + "/mesosmaster.yml", "rcmesos", installVars);
    shellContext.append(mesosMasterCommand.toString());
    shellContext.append(System.lineSeparator());
    TaskUtil.executeFile("mesosMasterInstall", shellContext.toString());
    return RepeatStatus.FINISHED;
  }

}
